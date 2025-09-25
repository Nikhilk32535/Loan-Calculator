package com.example.loancalculator.purity;

public class PurityModel {
  private int id;
  private String label;
  private double multiplier;
  private int LTV;

  public PurityModel(int id, String label, double multiplier) {
    this.id = id;
    this.label = label;
    this.multiplier = multiplier;
  }

  public int getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

  public double getMultiplier() {
    return multiplier;
  }

  public void setMultiplier(double multiplier) {
    this.multiplier = multiplier;
  }

  public int getLTV() {
    return LTV;
  }

  public void setLTV(int LTV) {
    this.LTV = LTV;
  }
}
