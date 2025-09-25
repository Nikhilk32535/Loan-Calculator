package com.example.loancalculator.Utility;

public class NumberFormatUtil {
  public static String formatIndianNumber(double amount) {
    if (amount >= 10000000) {
      return trimZero(amount / 10000000) + "Cr";
    } else if (amount >= 100000) {
      return trimZero(amount / 100000) + "L";
    } else if (amount >= 1000) {
      return trimZero(amount / 1000) + "K";
    } else {
      return String.valueOf((int) amount);
    }
  }

  private static String trimZero(double value) {
    if (value == (long) value) return String.format("%d", (long) value);
    else return String.format("%.1f", value);
  }
}
