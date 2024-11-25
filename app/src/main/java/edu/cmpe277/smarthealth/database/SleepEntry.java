package edu.cmpe277.smarthealth.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SleepEntry {
    @PrimaryKey
    private long date;

    private int hours;

    private int minutes;

    public SleepEntry(){
        date = 0;
        hours = 0;
        minutes = 0;
    }

    public SleepEntry(long date, int hour, int minutes){
        this.date = date;
        this.hours = hour;
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public long getDate() {
        return date;
    }
}
