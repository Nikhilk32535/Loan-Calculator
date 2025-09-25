package com.example.loancalculator.Utility;

import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

public class animeffect {

  public static void animateLoanAmount(TextView textView, int start, int end) {
    ValueAnimator animator = ValueAnimator.ofInt(start, end);
    animator.setDuration(1500);
    animator.setInterpolator(new DecelerateInterpolator());

    animator.addUpdateListener(
        animation -> {
          int value = (int) animation.getAnimatedValue();
          textView.setText("Loan Amount: ₹" + String.format("%,d", value));
        });

    animator.start();
  }

  public static void animateMonthlyInterest(TextView textView, double start, double end) {
    ValueAnimator animator = ValueAnimator.ofFloat((float) start, (float) end);
    animator.setDuration(1500);
    animator.setInterpolator(new DecelerateInterpolator());

    animator.addUpdateListener(
        animation -> {
          float value = (float) animation.getAnimatedValue();
          textView.setText(String.format("Monthly Interest: ₹%,.2f", value));
        });

    animator.start();
  }

  public static void animateExistingLoan(TextView textView, int from, int to) {
    ValueAnimator animator = ValueAnimator.ofInt(from, to);
    animator.setDuration(800); // milliseconds
    animator.addUpdateListener(
        animation -> {
          int value = (int) animation.getAnimatedValue();
          textView.setText("Eligible Extra: ₹" + String.format("%,d", value));
        });
    animator.start();
  }

  public static void animateInterestRate(TextView textView, double start, double end) {
    ValueAnimator animator = ValueAnimator.ofFloat((float) start, (float) end);
    animator.setDuration(1500);
    animator.setInterpolator(new DecelerateInterpolator());

    animator.addUpdateListener(
        animation -> {
          float value = (float) animation.getAnimatedValue();
          textView.setText(String.format("%.2f%% Interest", value));
        });

    animator.start();
  }
}
