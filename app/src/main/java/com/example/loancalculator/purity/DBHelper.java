package com.example.loancalculator.purity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "goldLoan.db";
  private static final int DATABASE_VERSION = 1;

  private static final String TABLE_NAME = "purity_multipliers";
  private static final String COLUMN_ID = "id";
  private static final String COLUMN_PURITY_70 = "purity_70";
  private static final String COLUMN_PURITY_75 = "purity_75";
  private static final String COLUMN_PURITY_79 = "purity_79";
  private static final String COLUMN_PURITY_83 = "purity_83";
  private static final String COLUMN_PURITY_87 = "purity_87";
  private static final String COLUMN_PURITY_91 = "purity_91";
  private static final String COLUMN_PURITY_99 = "purity_99";

  private static final String CREATE_TABLE_QUERY =
      "CREATE TABLE "
          + TABLE_NAME
          + " ("
          + COLUMN_ID
          + " INTEGER PRIMARY KEY, "
          + COLUMN_PURITY_70
          + " REAL, "
          + COLUMN_PURITY_75
          + " REAL, "
          + COLUMN_PURITY_79
          + " REAL, "
          + COLUMN_PURITY_83
          + " REAL, "
          + COLUMN_PURITY_87
          + " REAL, "
          + COLUMN_PURITY_91
          + " REAL, "
          + COLUMN_PURITY_99
          + " REAL);";

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_TABLE_QUERY);

    // Insert default values with fixed ID = 1
    ContentValues values = new ContentValues();
    values.put(COLUMN_ID, 1);
    values.put(COLUMN_PURITY_70, 75);
    values.put(COLUMN_PURITY_75, 80);
    values.put(COLUMN_PURITY_79, 85);
    values.put(COLUMN_PURITY_83, 90);
    values.put(COLUMN_PURITY_87, 94);
    values.put(COLUMN_PURITY_91, 97);
    values.put(COLUMN_PURITY_99, 99);
    db.insert(TABLE_NAME, null, values);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    onCreate(db);
  }

  public boolean savePurityMultipliers(
      String purity70,
      String purity75,
      String purity79,
      String purity83,
      String purity87,
      String purity91,
      String purity99) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_PURITY_70, purity70);
    values.put(COLUMN_PURITY_75, purity75);
    values.put(COLUMN_PURITY_79, purity79);
    values.put(COLUMN_PURITY_83, purity83);
    values.put(COLUMN_PURITY_87, purity87);
    values.put(COLUMN_PURITY_91, purity91);
    values.put(COLUMN_PURITY_99, purity99);

    int rowsUpdated = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[] {"1"});

    if (rowsUpdated == 0) {
      values.put(COLUMN_ID, 1);
      long insertResult = db.insert(TABLE_NAME, null, values);
      db.close();
      return insertResult != -1;
    }

    db.close();
    return true;
  }

  @SuppressLint("Range")
  public double getMultiplierByLabel(String label) {
    SQLiteDatabase db = this.getReadableDatabase();
    double multiplier = -1;
    Cursor cursor =
        db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = 1", null);

    if (cursor != null && cursor.moveToFirst()) {
      switch (label) {
        case "70%":
          multiplier = cursor.getDouble(cursor.getColumnIndex(COLUMN_PURITY_70));
          break;
        case "75%":
          multiplier = cursor.getDouble(cursor.getColumnIndex(COLUMN_PURITY_75));
          break;
        case "79%":
          multiplier = cursor.getDouble(cursor.getColumnIndex(COLUMN_PURITY_79));
          break;
        case "83%":
          multiplier = cursor.getDouble(cursor.getColumnIndex(COLUMN_PURITY_83));
          break;
        case "87%":
          multiplier = cursor.getDouble(cursor.getColumnIndex(COLUMN_PURITY_87));
          break;
        case "91%":
          multiplier = cursor.getDouble(cursor.getColumnIndex(COLUMN_PURITY_91));
          break;
        case "99%":
          multiplier = cursor.getDouble(cursor.getColumnIndex(COLUMN_PURITY_99));
          break;
      }
      cursor.close();
    }

    db.close();
    return multiplier;
  }
}
