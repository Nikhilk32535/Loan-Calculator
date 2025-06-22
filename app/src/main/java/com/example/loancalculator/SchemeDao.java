package com.example.loancalculator;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SchemeDao {

    @Insert
    void insert(Scheme scheme);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Scheme> schemes); // ✅ For syncing from Firebase

    @Update
    void update(Scheme scheme);

    @Delete
    void deleteScheme(Scheme scheme);

    @Query("SELECT * FROM schemes ORDER BY name ASC")
    List<Scheme> getAllSchemes();


    @Query("SELECT * FROM schemes WHERE name = :schemeName LIMIT 1")
    Scheme getSchemeByName(String schemeName);

    @Query("DELETE FROM schemes")
    void clearAll(); // ✅ Clear local DB before fresh Firebase sync

    @Query("UPDATE schemes SET price = " +
            "CASE " +
            "WHEN ltvType = '50LTV' THEN CAST((:price75 / 75.0) * 50 AS INTEGER) " +
            "WHEN ltvType = '60LTV' THEN CAST((:price75 / 75.0) * 60 AS INTEGER) " +
            "WHEN ltvType = '65LTV' THEN CAST((:price75 / 75.0) * 65 AS INTEGER) " +
            "WHEN ltvType = '75LTV' THEN CAST(:price75 AS INTEGER) " +
            "ELSE price END")
    void updatePricesForAllSchemes(float price75);
}
