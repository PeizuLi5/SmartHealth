package edu.cmpe277.smarthealth.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "SleepEntry")
public class SleepEntry {
    @PrimaryKey
    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "hours")
    public int hours;

    @ColumnInfo(name = "minutes")
    public int minutes;
}
