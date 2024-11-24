package edu.cmpe277.smarthealth.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StepEntry {
    @PrimaryKey
    public long date;

    public int steps;
}
