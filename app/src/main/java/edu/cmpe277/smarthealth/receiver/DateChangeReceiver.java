package edu.cmpe277.smarthealth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.cmpe277.smarthealth.services.StepCounterService;

public class DateChangeReceiver extends BroadcastReceiver {
    private StepCounterService stepCounterService;

    public DateChangeReceiver(StepCounterService service) {
        this.stepCounterService = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_DATE_CHANGED.equals(action)
                    || Intent.ACTION_TIME_CHANGED.equals(action)
                    || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {

                stepCounterService.onDateChanged();
            }
        }
    }
}
