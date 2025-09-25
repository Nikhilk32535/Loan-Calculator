package com.example.loancalculator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loancalculator.R;
import com.example.loancalculator.Scheme;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoanToGoldAdapter extends RecyclerView.Adapter<LoanToGoldAdapter.ViewHolder> {

  private final List<Scheme> visibleSchemes = new ArrayList<>();
  private double desiredLoan = 0.0;

  // 2-decimal grams format (e.g., 14.71 g)
  private static final DecimalFormat GRAM_FMT = new DecimalFormat("#,##0.00");

  /** Call this to refresh list based on user-entered desiredLoan */
  public void setData(List<Scheme> allSchemes, double desiredLoan) {
    this.desiredLoan = desiredLoan;
    visibleSchemes.clear();

    if (allSchemes != null) {
      for (Scheme s : allSchemes) {
        double pricePerGram = s.getPrice(); // loan per gram (LTV)
        double min = s.getMinLimit();
        double max = s.getMaxLimit();

        // Skip invalid or out-of-range schemes
        if (pricePerGram <= 0) continue;
        if (desiredLoan < min) continue;
        if (desiredLoan > max) continue;

        visibleSchemes.add(s);
      }
    }
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loan_to_gold, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder h, int position) {
    Scheme s = visibleSchemes.get(position);

    float monthlyintrest = s.getInterest() / 12;
    h.schemeName.setText(s.getName());

    // 🔹 Format interest with 2 decimals (e.g., 1.25%)
    double monthlyInterest = s.getInterest() / 12;
    String interestText = String.format(Locale.US, "%.2f%%", monthlyInterest);
    h.intrstrate.setText(interestText);

    double gramsNeeded = desiredLoan / s.getPrice(); // price = loan per gram
    // If you prefer to round up to 2 decimals:
    // gramsNeeded = Math.ceil(gramsNeeded * 100.0) / 100.0;

    String gramsText = GRAM_FMT.format(gramsNeeded) + " g";
    h.goldAmount.setText("Gold: " + gramsText);
  }

  @Override
  public int getItemCount() {
    return visibleSchemes.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    TextView schemeName, goldAmount, intrstrate;

    ViewHolder(@NonNull View itemView) {
      super(itemView);
      schemeName = itemView.findViewById(R.id.tvSchemeName);
      goldAmount = itemView.findViewById(R.id.tvgoldrequired);
      intrstrate = itemView.findViewById(R.id.tvSchemeInt);
    }
  }
}
