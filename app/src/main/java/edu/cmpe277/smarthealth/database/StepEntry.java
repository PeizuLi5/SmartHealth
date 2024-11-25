package edu.cmpe277.smarthealth.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StepEntry {
    @PrimaryKey
    private long date;

    private int steps;

    public StepEntry(){
        date = 0;
        steps = 0;
    }

    public StepEntry(long date, int steps){
        this.date = date;
        this.steps = steps;
    }

    public long getDate() {
        return date;
    }

    public int getSteps() {
        return steps;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
