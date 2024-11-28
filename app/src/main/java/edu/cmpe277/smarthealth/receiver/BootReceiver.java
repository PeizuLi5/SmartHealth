package edu.cmpe277.smarthealth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import edu.cmpe277.smarthealth.services.SleepService;
import edu.cmpe277.smarthealth.services.StepCounterService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent stepServiceIntent = new Intent(context, StepCounterService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(stepServiceIntent);
            } else {
                context.startService(stepServiceIntent);
            }

            Intent sleepServiceIntent = new Intent(context, SleepService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(sleepServiceIntent);
            } else {
                context.startService(sleepServiceIntent);
            }
        }
    }
}
