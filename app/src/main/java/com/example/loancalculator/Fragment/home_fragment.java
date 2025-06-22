package com.example.loancalculator.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loancalculator.BaseLTV.LTVDB;
import com.example.loancalculator.BaseLTV.LTVSettings;
import com.example.loancalculator.BaseLTV.LTVSettingsDao;
import com.example.loancalculator.Scheme;
import com.example.loancalculator.SchemeDao;
import com.example.loancalculator.SchemeDatabase;
import com.example.loancalculator.purity.DBHelper;
import com.example.loancalculator.R;

import java.util.ArrayList;
import java.util.List;

public class home_fragment extends Fragment {

    private EditText goldWeightEditText, stoneWeightEditText;
    private TextView actualWeightTextView, btnBasePrice;
    private DBHelper dbHelper;
    private LTVSettingsDao LTVSettingsDao;
    private float current75LtvPrice = 0f;


    public home_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home_fragment, container, false);

        LTVSettingsDao = LTVDB.getInstance(requireContext()).ltvSettingsDao();
        goldWeightEditText = root.findViewById(R.id.goldweight);
        stoneWeightEditText = root.findViewById(R.id.stoneweight);
        actualWeightTextView = root.findViewById(R.id.actualweight);

        dbHelper = new DBHelper(requireContext());

        // Purity Buttons
        Button btn70 = root.findViewById(R.id.seventypersent);
        Button btn75 = root.findViewById(R.id.seventyfive);
        Button btn83 = root.findViewById(R.id.eightythreee);
        Button btn87 = root.findViewById(R.id.eightyseven);
        Button btn91 = root.findViewById(R.id.ninetyone);
        Button btn99 = root.findViewById(R.id.ninetynine);
        btnBasePrice = root.findViewById(R.id.baseprice);

        // Set Click Listeners to use dynamic DB values
        btn70.setOnClickListener(v -> calculatePureGold("70%"));
        btn75.setOnClickListener(v -> calculatePureGold("75%"));
        btn83.setOnClickListener(v -> calculatePureGold("83%"));
        btn87.setOnClickListener(v -> calculatePureGold("87%"));
        btn91.setOnClickListener(v -> calculatePureGold("91%"));
        btn99.setOnClickListener(v -> calculatePureGold("99%"));

        loadBaseLTV();

        return root;
    }

    private void showEligibleSchemes(double netWeight) {
        new Thread(() -> {
            List<Scheme> schemes = SchemeDatabase.getInstance(requireContext()).schemeDao().getAllSchemes();

            requireActivity().runOnUiThread(() -> {
                LinearLayout schemeContainer = requireView().findViewById(R.id.schemeContainer);
                schemeContainer.removeAllViews();

                boolean foundAny = false;

                for (Scheme scheme : schemes) {
                    double originalLoanAmount = netWeight * scheme.getPrice();
                    double loanAmountToShow;

                    // Apply logic
                    if (originalLoanAmount < scheme.getMinLimit()) {
                        continue; // Skip if below min
                    } else if (originalLoanAmount > scheme.getMaxLimit()) {
                        loanAmountToShow = scheme.getMaxLimit(); // Cap to max
                    } else {
                        loanAmountToShow = originalLoanAmount;
                    }

                    double monthlyInterest = (loanAmountToShow * scheme.getInterest()) / 100 / 12;

                    // UI setup
                    foundAny = true;
                    View cardView = LayoutInflater.from(getContext()).inflate(R.layout.scheme_card, schemeContainer, false);

                    TextView schemeNameView = cardView.findViewById(R.id.tvSchemeName);
                    TextView loanAmountView = cardView.findViewById(R.id.tvLoanAmount);
                    TextView interestView = cardView.findViewById(R.id.tvMonthlyInterest);
                    TextView interestRateView = cardView.findViewById(R.id.tvInterestRate);

                    Float intrestRatePerMonth = scheme.getInterest() / 12;

                    schemeNameView.setText(scheme.getName());
                    loanAmountView.setText("Loan Amount: ₹" + (int) loanAmountToShow);
                    interestView.setText("Monthly Interest: ₹" + (int) monthlyInterest);
                    interestRateView.setText(String.format("%.2f%% Interest", intrestRatePerMonth));


                    schemeContainer.addView(cardView);
                }

                if (!foundAny) {
                    TextView noSchemeView = new TextView(getContext());
                    noSchemeView.setText("No eligible schemes found.");
                    noSchemeView.setPadding(16, 16, 16, 16);
                    schemeContainer.addView(noSchemeView);
                }
            });
        }).start();
    }





    private void loadBaseLTV() {
        new Thread(() -> {
            LTVSettings settings = LTVSettingsDao.getBase75LTV();
            if (settings != null) {
                current75LtvPrice = settings.getBase75LTV();
                requireActivity().runOnUiThread(() ->
                        btnBasePrice.setText("Base LTV : "+String.valueOf((int) current75LtvPrice)));
            }
        }).start();
    }

    private void calculatePureGold(String purityLabel) {
        String goldWeightStr = goldWeightEditText.getText().toString().trim();
        String stoneWeightStr = stoneWeightEditText.getText().toString().trim();

        if (stoneWeightStr.isEmpty()) {
            stoneWeightStr = "0";
        }

        if (goldWeightStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter Gold weight", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double goldWeight = Double.parseDouble(goldWeightStr);
            double stoneWeight = Double.parseDouble(stoneWeightStr);
            double netWeight = goldWeight - stoneWeight;

            if (netWeight < 0) {
                Toast.makeText(getContext(), "Stone weight cannot exceed gold weight", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get multiplier from DB using label
            double purityMultiplier = dbHelper.getMultiplierByLabel(purityLabel);
            Log.e("findmulti", "Multiplier: " + purityMultiplier);
            double actualGold = (netWeight * purityMultiplier) / 100;
            actualWeightTextView.setText(String.format("Net Weight : %.2f g", actualGold));

            showEligibleSchemes(actualGold);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }
}
