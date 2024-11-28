package edu.cmpe277.smarthealth.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.SleepSegmentRequest;
import com.google.android.gms.tasks.Task;

import edu.cmpe277.smarthealth.R;
import edu.cmpe277.smarthealth.receiver.SleepReceiver;

public class SleepService extends Service {
    private ActivityRecognitionClient client;
    private PendingIntent pendingIntent;

    private static final String TAG = "SleepService";
    private static final String CHANNEL_ID = "SleepServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        startForegroundService();

        client = ActivityRecognition.getClient(this);
        pendingIntent = getSleepSegmentPendingIntent();

        requestSleepSegmentUpdates();
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

    private PendingIntent getSleepSegmentPendingIntent() {
        Intent intent = new Intent(this, SleepReceiver.class);
        intent.setAction(SleepReceiver.ACTION_SLEEP_SEGMENT_EVENT);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void requestSleepSegmentUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Void> task = client.requestSleepSegmentUpdates(pendingIntent, SleepSegmentRequest.getDefaultSleepSegmentRequest());
        task.addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully requested sleep updates"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to request sleep updates: " + e.getMessage()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeSleepSegmentUpdates();
    }

    private void removeSleepSegmentUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Void> task = client.removeSleepSegmentUpdates(pendingIntent);
        task.addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully removed sleep updates"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to remove sleep updates: " + e.getMessage()));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}