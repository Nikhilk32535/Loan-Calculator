package com.example.loancalculator.BaseLTV;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
    entities = {LTVSettings.class},
    version = 1)
public abstract class LTVDB extends RoomDatabase {

  private static volatile LTVDB INSTANCE;

  public abstract LTVSettingsDao ltvSettingsDao();

  // Singleton pattern to get instance
  public static LTVDB getInstance(Context context) {
    if (INSTANCE == null) {
      synchronized (LTVDB.class) {
        if (INSTANCE == null) {
          INSTANCE =
              Room.databaseBuilder(context.getApplicationContext(), LTVDB.class, "ltv_db").build();
        }
      }
    }
    return INSTANCE;
  }
}
