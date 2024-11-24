package edu.cmpe277.smarthealth.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.cmpe277.smarthealth.R;
import edu.cmpe277.smarthealth.database.AppDB;
import edu.cmpe277.smarthealth.database.StepEntry;

public class StepCounterService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int initialStep = -1;

    private static final String CHANNEL_ID = "StepCounterChannel";
    private static final String TAG = "StepCounterService";

    @Override
    public void onCreate(){
        super.onCreate();

        createNotificationChannel();
        startForegroundService();
        initializeStepCounter();
        Log.d(TAG, "StepCounterService started.");
    }

    private void initializeStepCounter(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepCountSensor != null) {
                sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
            else {
                Log.e(TAG, "Step Counter sensor not available!");
            }
        } else {
            Log.e(TAG, "SensorManager not available!");
        }
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
            }
            int stepsTaken = stepCount - initialStep;
            Log.d(TAG, "Steps taken: " + stepsTaken);

            saveStepData(stepsTaken);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void saveStepData(int totalSteps) {
        AppDB appDB = AppDB.getInstance(getApplicationContext());
        long date = getStartOfDay(System.currentTimeMillis());

        executorService.execute(() -> {
            StepEntry existingEntry = appDB.stepDao().getStep(date);
            if (existingEntry != null) {
                existingEntry.steps = totalSteps;
                appDB.stepDao().insertOrUpdate(existingEntry);
            }
            else {
                StepEntry newEntry = new StepEntry();
                newEntry.date = date;
                newEntry.steps = totalSteps;
                appDB.stepDao().insertOrUpdate(newEntry);
            }
        });
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
