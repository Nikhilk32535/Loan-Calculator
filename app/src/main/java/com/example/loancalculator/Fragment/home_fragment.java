package com.example.loancalculator.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.loancalculator.BaseLTV.LTVDB;
import com.example.loancalculator.BaseLTV.LTVSettings;
import com.example.loancalculator.BaseLTV.LTVSettingsDao;
import com.example.loancalculator.R;
import com.example.loancalculator.Scheme;
import com.example.loancalculator.SchemeDatabase;
import com.example.loancalculator.Utility.FirebaseSyncHelper;
import com.example.loancalculator.Utility.animeffect;
import com.example.loancalculator.purity.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class home_fragment extends Fragment {

    private EditText goldWeightEditText, stoneWeightEditText, existingLoanEditText;
    private TextView actualWeightTextView, btnBasePrice;
    private DBHelper dbHelper;
    private LTVSettingsDao ltvSettingsDao;
    private float baseLtvPrice = 0f;
    private final List<TextView> purityButtons = new ArrayList<>();

    public home_fragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_fragment, container, false);

        ltvSettingsDao = LTVDB.getInstance(requireContext()).ltvSettingsDao();
        dbHelper = new DBHelper(requireContext());

        goldWeightEditText = root.findViewById(R.id.goldweight);
        stoneWeightEditText = root.findViewById(R.id.stoneweight);
        actualWeightTextView = root.findViewById(R.id.actualweight);
        btnBasePrice = root.findViewById(R.id.baseprice);
        existingLoanEditText = root.findViewById(R.id.et_existing_loan);

        initPurityButtons(root);
        loadBaseLTV();
        FirebaseSyncHelper.silentSyncSchemes(requireContext());

        return root;
    }

    private void initPurityButtons(View root) {
        int[] ids = {
                R.id.seventypersent, R.id.seventyfive, R.id.seventynine,
                R.id.eightythreee, R.id.eightyseven, R.id.ninetyone, R.id.ninetynine
        };

        for (int id : ids) {
            TextView btn = root.findViewById(id);
            purityButtons.add(btn);
            btn.setBackgroundResource(R.drawable.bg_circle_purity);
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

            btn.setOnClickListener(v -> {
                highlightSelectedButton(btn);
                calculatePureGold(convertKaratToPercentage(btn.getText().toString()));
            });
        }
    }

    private void highlightSelectedButton(TextView selectedButton) {
        for (TextView btn : purityButtons) {
            btn.setBackgroundResource(R.drawable.bg_circle_purity);
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        }

        selectedButton.setBackgroundResource(R.drawable.bg_circle_selected);
        selectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
    }

    private void loadBaseLTV() {
        new Thread(() -> {
            LTVSettings settings = ltvSettingsDao.getBase75LTV();
            if (settings != null) {
                baseLtvPrice = settings.getBase75LTV();
                requireActivity().runOnUiThread(() ->
                        btnBasePrice.setText("Base LTV : ₹" + (int) baseLtvPrice));
            }
        }).start();
    }

    private void calculatePureGold(String purityLabel) {
        String goldWeightStr = goldWeightEditText.getText().toString().trim();
        String stoneWeightStr = stoneWeightEditText.getText().toString().trim();
        if (stoneWeightStr.isEmpty()) stoneWeightStr = "0";

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

            double purityMultiplier = dbHelper.getMultiplierByLabel(purityLabel);
            double actualGold = (netWeight * purityMultiplier) / 100;

            actualWeightTextView.setText(String.format("Net Weight : %.2f g", actualGold));
            showEligibleSchemes(actualGold);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEligibleSchemes(double netWeight) {
        new Thread(() -> {
            List<Scheme> schemes = SchemeDatabase.getInstance(requireContext()).schemeDao().getAllSchemes();

            String existingLoanStr = existingLoanEditText.getText().toString().trim();
            double existingLoan = 0;
            try {
                if (!existingLoanStr.isEmpty()) {
                    existingLoan = Double.parseDouble(existingLoanStr);
                }
            } catch (NumberFormatException ignored) {}

            double finalExistingLoan = existingLoan;

            requireActivity().runOnUiThread(() -> {
                LinearLayout schemeContainer = requireView().findViewById(R.id.schemeContainer);
                schemeContainer.removeAllViews();
                boolean foundAny = false;

                for (Scheme scheme : schemes) {
                    double baseLoan = netWeight * scheme.getPrice();
                    double cappedLoan = Math.min(Math.max(baseLoan, scheme.getMinLimit()), scheme.getMaxLimit());

                    // Skip if below min
                    if (baseLoan < scheme.getMinLimit()) continue;

                    // Total loan depends on whether existing loan exists
                    double additionalLoan = Math.max(0, cappedLoan - finalExistingLoan);
                    double totalLoan = finalExistingLoan > 0 ? finalExistingLoan + additionalLoan : cappedLoan;

                    // Interest calculations
                    float monthlyRate = (float) (scheme.getInterest() / 12);
                    double monthlyInterest = (totalLoan * scheme.getInterest()) / 100 / 12;

                    foundAny = true;
                    View cardView = LayoutInflater.from(getContext()).inflate(R.layout.scheme_card, schemeContainer, false);

                    TextView tvSchemeName = cardView.findViewById(R.id.tvSchemeName);
                    TextView tvLoanAmount = cardView.findViewById(R.id.tvLoanAmount);
                    TextView tvMonthlyInterest = cardView.findViewById(R.id.tvMonthlyInterest);
                    TextView tvInterestRate = cardView.findViewById(R.id.tvInterestRate);
                    TextView tvExistingLoan = cardView.findViewById(R.id.tvExistingLoan);

                    // Set text values
                    tvSchemeName.setText(scheme.getName());

                    animeffect.animateLoanAmount(tvLoanAmount, (int) (totalLoan / 2), (int) totalLoan);
                    animeffect.animateMonthlyInterest(tvMonthlyInterest, 0, monthlyInterest);
                    animeffect.animateInterestRate(tvInterestRate, 0, monthlyRate);

                    if (finalExistingLoan > 0) {
                        tvExistingLoan.setVisibility(View.VISIBLE);

                        // Animate extra eligible loan
                        animeffect.animateExistingLoan(tvExistingLoan, 0, (int) additionalLoan);
                    } else {
                        tvExistingLoan.setVisibility(View.GONE);
                    }

                    // Animation
                    Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_fade_in);
                    cardView.startAnimation(anim);
                    schemeContainer.addView(cardView);
                }

                if (!foundAny) {
                    TextView noScheme = new TextView(getContext());
                    noScheme.setText("No eligible schemes available.");
                    noScheme.setPadding(24, 40, 24, 40);
                    noScheme.setTextSize(16);
                    schemeContainer.addView(noScheme);
                }
            });
        }).start();
    }



    private String convertKaratToPercentage(String karatText) {
        if (karatText.contains("70")) return "70%";
        if (karatText.contains("75")) return "75%";
        if (karatText.contains("79")) return "79%";
        if (karatText.contains("83")) return "83%";
        if (karatText.contains("87")) return "87%";
        if (karatText.contains("91N")) return "91%";
        if (karatText.contains("91")) return "99%";
        return "0";
    }
}
