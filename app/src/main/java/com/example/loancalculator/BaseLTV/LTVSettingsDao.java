package com.example.loancalculator.BaseLTV;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface LTVSettingsDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertOrUpdate(LTVSettings settings);

  @Query("SELECT * FROM ltv_settings LIMIT 1")
  LTVSettings getBase75LTV();
}
