package com.example.loancalculator;

import androidx.room.Dao;
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

    @Query("SELECT * FROM schemes")
    List<Scheme> getAllSchemes();
}
