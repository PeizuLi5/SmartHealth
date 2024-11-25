package edu.cmpe277.smarthealth.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.cmpe277.smarthealth.R;
import edu.cmpe277.smarthealth.database.AppDB;
import edu.cmpe277.smarthealth.database.StepEntry;

public class StepCounterService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepCountSensor;

    private int initialStep = -1;
    private int totalStepCurrentDay = 0;
    private long currentDateStartTime;

    private SharedPreferences sharedPreferences;

    private Handler handler;
    private Runnable dayChangeRunnable;

    private static final String CHANNEL_ID = "StepCounterChannel";
    private static final String TAG = "StepCounterService";

    @Override
    public void onCreate(){
        super.onCreate();

        sharedPreferences = getSharedPreferences("StepCountPref", MODE_PRIVATE);

        createNotificationChannel();

        startForegroundService();

        initializeStepCounter();

        setupDayChangeHandler();
    }

    private void setupDayChangeHandler() {
        handler = new Handler();
        dayChangeRunnable = new Runnable() {
            @Override
            public void run() {
                long current = System.currentTimeMillis();
                if(currentDateStartTime < getStartOfDay(current)){
                    saveSteps();
                    resetCounter();

                    currentDateStartTime = getStartOfDay(current);
                    sharedPreferences.edit().putLong("currentDate", currentDateStartTime).apply();
                }

                handler.postDelayed(this, 60 * 60 * 1000);
            }
        };

        handler.post(dayChangeRunnable);
    }

    private void initializeStepCounter(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepCountSensor != null) {
                sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);

                initialStep = sharedPreferences.getInt("initialStep", -1);
                totalStepCurrentDay = sharedPreferences.getInt("totalStep", 0);
                currentDateStartTime = sharedPreferences.getLong("currentDate", System.currentTimeMillis());

                if(currentDateStartTime < getStartOfDay(System.currentTimeMillis())){
                    saveSteps();
                    resetCounter();
                }
            }
        }
    }

    private void resetCounter() {
        initialStep = -1;
        totalStepCurrentDay = 0;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("initialStep");
        editor.putInt("totalStep", 0);
        editor.apply();
    }

    private void saveSteps() {
        AppDB appDB = AppDB.getInstance(getApplicationContext());
        long date = currentDateStartTime;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            StepEntry stepEntry = new StepEntry(date, totalStepCurrentDay);
            appDB.stepDao().insertOrUpdate(stepEntry);
        });
    }

    private void startForegroundService(){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Step Counter")
                .setContentText("Counting steps")
                .setSmallIcon(R.drawable.walk)
                .build();
        startForeground(1, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Step Counter Channel";
            String description = "Channel for step counter service";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null && this != null) {
            sensorManager.unregisterListener(this);
        }
        Log.d(TAG, "StepCounterService stopped.");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == stepCountSensor) {
            int stepCount = (int) event.values[0];

            if (initialStep == -1) {
                initialStep = stepCount;
                saveInitialValues();
            }

            totalStepCurrentDay = stepCount - initialStep;
            saveTotalSteps();

            broadcastCurrentStep();
        }
    }

    private void broadcastCurrentStep() {
        Intent intent = new Intent("StepCountUpdate");
        intent.putExtra("totalStepsCurrent", totalStepCurrentDay);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void saveTotalSteps() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("totalStep", totalStepCurrentDay);
        editor.apply();
    }

    private void saveInitialValues() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("initialStep", initialStep);
        editor.apply();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
