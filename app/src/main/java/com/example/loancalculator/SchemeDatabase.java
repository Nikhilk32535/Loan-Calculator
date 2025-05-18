package com.example.loancalculator;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Scheme.class}, version = 2)  // <-- increment version here
public abstract class SchemeDatabase extends RoomDatabase {

    public abstract SchemeDao schemeDao();

    private static volatile SchemeDatabase INSTANCE;

    public static SchemeDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SchemeDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    SchemeDatabase.class, "scheme_database")
                            .fallbackToDestructiveMigration() // <-- use only in dev or if data loss is acceptable
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
