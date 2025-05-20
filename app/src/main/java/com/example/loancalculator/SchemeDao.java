package com.example.loancalculator;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SchemeDao {

    @Insert
    void insert(Scheme scheme);

    @Update
    void update(Scheme scheme);

    @Delete
    void deleteScheme(Scheme scheme);


    @Query("SELECT * FROM schemes")
    List<Scheme> getAllSchemes();

    @Query("UPDATE schemes SET price = " +
            "CASE " +
            "WHEN ltvType = '50LTV' THEN CAST((:price75 / 75.0) * 50 AS INTEGER) " +
            "WHEN ltvType = '60LTV' THEN CAST((:price75 / 75.0) * 60 AS INTEGER) " +
            "WHEN ltvType = '65LTV' THEN CAST((:price75 / 75.0) * 65 AS INTEGER) " +
            "WHEN ltvType = '75LTV' THEN CAST(:price75 AS INTEGER) " +
            "ELSE price END")
    void updatePricesForAllSchemes(float price75);


}
