package edu.cmpe277.smarthealth.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.cmpe277.smarthealth.R;
import edu.cmpe277.smarthealth.database.AppDB;
import edu.cmpe277.smarthealth.database.SleepEntry;

public class SleepService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, light;

    private ActivityRecognitionClient client;
    private PendingIntent pendingIntent;

    private AppDB appDB;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final String TAG = "SleepService";
    private static final String CHANNEL_ID = "SleepServiceChannel";
    private static final String TRANSITIONS_RECEIVER_ACTION = "edu.cmpe277.smarthealth.services.TRANSITIONS_RECEIVER_ACTION";

    private long noMotionStartTime = -1;
    private long lowLightStartTime = -1;
    private static final int NO_ACTION_DURATION_MAY_SLEEP = 30 * 60 * 1000;
    private static final float MOTION_THRESHOLD = 0.5f;
    private static final float LIGHT_THRESHOLD = 10.0f;
    private boolean isNoMotion = false, isLowLight = false, isStill = false, isSleeping = false;

    private long sleepTime = -1;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        startForegroundService();

        appDB = AppDB.getInstance(getApplicationContext());

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        client = ActivityRecognition.getClient(this);

        Intent intent = new Intent(TRANSITIONS_RECEIVER_ACTION);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(transitionReceiver, new IntentFilter(TRANSITIONS_RECEIVER_ACTION), Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(transitionReceiver, new IntentFilter(TRANSITIONS_RECEIVER_ACTION));
        }

        requestTransition();
    }

    private void startForegroundService() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sleep Tracking")
                .setContentText("Monitoring your sleep")
                .setSmallIcon(R.drawable.sleep)
                .build();
        startForeground(2, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sleep Service Channel";
            String description = "Channel for sleep tracking service";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void requestTransition() {
        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER).build());

        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT).build());

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.requestActivityTransitionUpdates(request, pendingIntent)
                .addOnSuccessListener(message -> Log.d(TAG, "Activity transition registered"))
                .addOnFailureListener(e -> Log.e(TAG, "Activity transition failed: " + e.getMessage()));
    }

    private final BroadcastReceiver transitionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ActivityTransitionResult.hasResult(intent))
                return;

            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents())
                handleTransitionEvent(event);
        }
    };

    private void handleTransitionEvent(ActivityTransitionEvent event) {
        int aType = event.getActivityType();
        int tType = event.getTransitionType();

        if (aType == DetectedActivity.STILL) {
            if (tType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                isStill = true;
                registerSensors();
            }
            else if (tType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                isStill = false;
                unregisterSensors();
                resetSleepDetection();
            }
        }
    }

    private void registerSensors(){
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensors(){
        sensorManager.unregisterListener(this);
    }

    private void resetSleepDetection(){
        isNoMotion = false;
        isLowLight = false;
        noMotionStartTime = -1;
        lowLightStartTime = -1;
        if(isSleeping){
            long currentTime = System.currentTimeMillis();
            endSleepSession(currentTime);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(!isStill)
            return;

        long currentTime = System.currentTimeMillis();
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_GYROSCOPE:
                handleMotionSensor(event, currentTime);
                break;
            case Sensor.TYPE_LIGHT:
                handleLightSensor(event, currentTime);
                break;
            default:
                break;
        }

        evaluateSleepingState(currentTime);
    }

    private void handleMotionSensor(SensorEvent event, long currentTime){
        float x = event.values[0], y = event.values[1], z = event.values[2];
        float movement = (float) Math.sqrt(x * x + y * y + z * z);

        if(movement < MOTION_THRESHOLD){
            if(!isNoMotion){
                isNoMotion = true;
                noMotionStartTime = currentTime;
            }
        }
        else{
            if(isNoMotion){
                isNoMotion = false;
                noMotionStartTime = -1;
                if(isSleeping){
                    endSleepSession(currentTime);
                }
            }
        }
    }

    private void handleLightSensor(SensorEvent event, long currentTime){
        float lightLevel = event.values[0];

        if (lightLevel < LIGHT_THRESHOLD) {
            if (!isLowLight) {
                isLowLight = true;
                lowLightStartTime = currentTime;
            }
        } else {
            if (isLowLight) {
                isLowLight = false;
                lowLightStartTime = -1;
                if (isSleeping) {
                    endSleepSession(currentTime);
                }
            }
        }
    }

    private void evaluateSleepingState(long currentTime) {
        if (isNoMotion && isLowLight) {
            long motionlessDuration = currentTime - noMotionStartTime;
            long lowLightDuration = currentTime - lowLightStartTime;

            if (motionlessDuration >= NO_ACTION_DURATION_MAY_SLEEP && lowLightDuration >= NO_ACTION_DURATION_MAY_SLEEP) {
                if (!isSleeping) {
                    long sleepStart = currentTime - Math.max(motionlessDuration, lowLightDuration);
                    startSleepSession(sleepStart);
                }
            }
        } else {
            if (isSleeping) {
                endSleepSession(currentTime);
            }
        }
    }

    private void startSleepSession(long startTime) {
        sleepTime = startTime;
        isSleeping = true;
    }

    private void endSleepSession(long endTime) {
        if (sleepTime != -1) {
            long sleepDuration = endTime - sleepTime;
            saveData(sleepTime, sleepDuration);
            sleepTime = -1;
            isSleeping = false;
        }
    }

    private void saveData(long sleepTime, long duration) {
        executorService.execute(() -> {
            long date = getStartOfDay(sleepTime);

            SleepEntry existingEntry = appDB.sleepDao().getSleepEntryByDate(date);

            int sleepMinutes = (int) (duration / (1000 * 60));

            if (existingEntry != null) {
                int totalMinutes = existingEntry.hours * 60 + existingEntry.minutes + sleepMinutes;
                existingEntry.hours = totalMinutes / 60;
                existingEntry.minutes = totalMinutes % 60;

                appDB.sleepDao().insertOrUpdate(existingEntry);
            } else {
                SleepEntry newEntry = new SleepEntry();
                newEntry.date = date;
                newEntry.hours = sleepMinutes / 60;
                newEntry.minutes = sleepMinutes % 60;

                appDB.sleepDao().insertOrUpdate(newEntry);
            }
        });
    }

    private long getStartOfDay(long sleepTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(sleepTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(transitionReceiver);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.removeActivityTransitionUpdates(pendingIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}