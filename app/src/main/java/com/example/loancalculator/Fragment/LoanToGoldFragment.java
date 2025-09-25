package com.example.loancalculator.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loancalculator.R;
import com.example.loancalculator.Scheme;
import com.example.loancalculator.SchemeDatabase;
import com.example.loancalculator.Utility.NumberFormatUtil;
import com.example.loancalculator.adapter.LoanToGoldAdapter;
import java.util.List;

public class LoanToGoldFragment extends Fragment {

  private EditText loanInput;
  private Button calculateBtn;
  private RecyclerView recyclerView;
  private LoanToGoldAdapter adapter;
  private TextView loanAmountText; // ✅ new textview for showing loan amount

  public LoanToGoldFragment() {}

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_loantogold, container, false);

    loanInput = view.findViewById(R.id.loanInput);
    calculateBtn = view.findViewById(R.id.calculateBtn);
    recyclerView = view.findViewById(R.id.recyclerLoanToGold);
    loanAmountText = view.findViewById(R.id.loanAmountText); // ✅ bind view

    recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
    adapter = new LoanToGoldAdapter();
    recyclerView.setAdapter(adapter);

    calculateBtn.setOnClickListener(
        v -> {
          String input = loanInput.getText().toString().trim();
          if (!input.isEmpty()) {
            double loanAmount = Double.parseDouble(input);

            // ✅ Format and show loan amount on UI
            String formatted = NumberFormatUtil.formatIndianNumber(loanAmount);
            loanAmountText.setText("Loan Amount: " + formatted);

            loadSchemes(loanAmount);
          }
        });

    return view;
  }

  private void loadSchemes(double loanAmount) {
    new Thread(
            () -> {
              List<Scheme> schemes =
                  SchemeDatabase.getInstance(requireContext()).schemeDao().getAllSchemes();

              requireActivity().runOnUiThread(() -> adapter.setData(schemes, loanAmount));
            })
        .start();
  }
}
