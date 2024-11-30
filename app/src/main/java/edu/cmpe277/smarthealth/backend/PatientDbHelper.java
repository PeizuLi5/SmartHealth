package edu.cmpe277.smarthealth.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PatientDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "patient_info.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation SQL statements
    private static final String CREATE_PATIENT_TABLE = "CREATE TABLE patients (" +
            "_id TEXT PRIMARY KEY," +
            "name TEXT," +
            "date_of_birth TEXT," +
            "weight REAL," +
            "height REAL," +
            "id_number TEXT," +
            "email TEXT," +
            "phone TEXT);";

    public PatientDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PATIENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Placeholder
    }
}
