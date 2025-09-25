package com.example.loancalculator.BaseLTV;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ltv_settings")
public class LTVSettings {
  @PrimaryKey public int id = 1; // Always one row only

  public float base75LTV;

  // ✅ No-arg constructor (required by Room)
  public LTVSettings() {}

  // Optional convenience constructor
  public LTVSettings(float baseLTVPrice) {
    this.base75LTV = baseLTVPrice;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public float getBase75LTV() {
    return base75LTV;
  }

  public void setBase75LTV(float base75LTV) {
    this.base75LTV = base75LTV;
  }
}
