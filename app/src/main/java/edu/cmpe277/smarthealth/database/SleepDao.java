package edu.cmpe277.smarthealth.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SleepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(SleepEntry sleepEntry);

    @Query("SELECT * FROM SleepEntry WHERE date BETWEEN :startDate AND :endDate")
    LiveData<List<SleepEntry>> getSleepEntriesBetweenDates(long startDate, long endDate);

    @Query("SELECT * FROM SleepEntry WHERE date = :date LIMIT 1")
    SleepEntry getSleepEntryByDate(long date);

    @Query("SELECT * FROM SleepEntry WHERE date BETWEEN :startDate AND :endDate")
    List<SleepEntry> getSleepEntriesBetweenDatesNonLive (long startDate, long endDate);
}
