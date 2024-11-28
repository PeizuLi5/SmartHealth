package edu.cmpe277.smarthealth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.SleepSegmentEvent;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.cmpe277.smarthealth.database.AppDB;
import edu.cmpe277.smarthealth.database.SleepEntry;

public class SleepReceiver extends BroadcastReceiver{
    public static final String ACTION_SLEEP_SEGMENT_EVENT = "edu.cmpe277.smarthealth.ACTION_SLEEP_SEGMENT_EVENT";
    private static final String TAG = "SleepReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(SleepSegmentEvent.hasEvents(intent)){
            List<SleepSegmentEvent> events = SleepSegmentEvent.extractEvents(intent);
            for (SleepSegmentEvent event : events){
                long sleepStartTime = event.getStartTimeMillis();
                long sleepEndTime = event.getEndTimeMillis();
                int status = event.getStatus();

                Log.d("SleepReceiver", "Sleep segment: Start=" + sleepStartTime + ", End=" + sleepEndTime + ", Status=" + status);

                saveData(context, sleepStartTime, sleepEndTime);
            }
        }
    }

    private void saveData(Context context, long sleepStartTime, long sleepEndTime) {
        AppDB appDB = AppDB.getInstance(context.getApplicationContext());
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        long sleepDuration = sleepEndTime - sleepStartTime;
        int sleepMinutes = (int) (sleepDuration / (1000 * 60));

        SleepEntry sleepEntry = new SleepEntry();
        sleepEntry.date = getStartOfDay(sleepStartTime);
        sleepEntry.hours = sleepMinutes / 60;
        sleepEntry.minutes = sleepMinutes % 60;

        executorService.execute(() -> {
            try{
                SleepEntry findEntry = appDB.sleepDao().getSleepEntryByDate(sleepEntry.date);
                if (findEntry != null) {
                    int totalMinutes = findEntry.hours * 60 + findEntry.minutes + sleepMinutes;
                    findEntry.hours = totalMinutes / 60;
                    findEntry.minutes = totalMinutes % 60;

                    appDB.sleepDao().insertOrUpdate(findEntry);
                }
                else {
                    appDB.sleepDao().insertOrUpdate(sleepEntry);
                }
            }
            catch (Exception e){
                Log.e(TAG, "Error saving sleep data: " + e.getMessage());
                e.printStackTrace();
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
}
