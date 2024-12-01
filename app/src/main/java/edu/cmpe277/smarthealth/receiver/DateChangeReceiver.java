package edu.cmpe277.smarthealth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Random;

import edu.cmpe277.smarthealth.database.AppDB;
import edu.cmpe277.smarthealth.database.SleepEntry;
import edu.cmpe277.smarthealth.services.StepCounterService;

public class DateChangeReceiver extends BroadcastReceiver {
    private StepCounterService stepCounterService;

    private AppDB appDB;

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

            if(Intent.ACTION_DATE_CHANGED.equals(action)){
                appDB = AppDB.getInstance(context);
                SleepEntry sleepEntry = new SleepEntry();
                sleepEntry.date = getStartOfDay(System.currentTimeMillis() - 10000);
                Random random = new Random();
                int min = 6;
                int max = 8;
                sleepEntry.hours = random.nextInt(max - min + 1) + min;
                sleepEntry.minutes = random.nextInt(60);
                appDB.sleepDao().insertOrUpdate(sleepEntry);
            }
        }
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
}
