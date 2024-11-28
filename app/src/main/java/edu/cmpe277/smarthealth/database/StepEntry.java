package edu.cmpe277.smarthealth.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "StepEntry")
public class StepEntry {
    @PrimaryKey
    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "steps")
    public int steps;
}
