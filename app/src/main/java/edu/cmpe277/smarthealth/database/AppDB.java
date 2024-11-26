package edu.cmpe277.smarthealth.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {StepEntry.class, SleepEntry.class}, version = 1, exportSchema = false)
public abstract class AppDB extends RoomDatabase {
    private static volatile AppDB instance;

    public abstract StepDao stepDao();
    public abstract SleepDao sleepDao();

    public static AppDB getInstance(Context context){
        if(instance == null){
            synchronized (AppDB.class){
                if(instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDB.class,
                                    "records_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
