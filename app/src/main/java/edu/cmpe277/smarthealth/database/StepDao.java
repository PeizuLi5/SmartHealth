package edu.cmpe277.smarthealth.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(StepEntry stepEntry);

    @Query("SELECT * FROM StepEntry WHERE date BETWEEN :startDate AND :endDate")
    List<StepEntry> getStepsBetween(long startDate, long endDate);

    @Query("SELECT * FROM StepEntry WHERE date = :date LIMIT 1")
    StepEntry getStep(long date);

    @Query("SELECT * FROM StepEntry WHERE date = :date LIMIT 1")
    LiveData<StepEntry> getStepCountLive(long date);

    @Query("SELECT * FROM StepEntry ORDER BY date DESC")
    List<StepEntry> getAllStepData();
}
