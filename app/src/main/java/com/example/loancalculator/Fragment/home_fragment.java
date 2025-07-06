package com.example.loancalculator.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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

    private EditText goldWeightEditText, stoneWeightEditText;
    private TextView actualWeightTextView, btnBasePrice;
    private DBHelper dbHelper;
    private LTVSettingsDao LTVSettingsDao;
    private float current75LtvPrice = 0f;
    private List<TextView> purityButtons = new ArrayList<>();

    public home_fragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home_fragment, container, false);

        LTVSettingsDao = LTVDB.getInstance(requireContext()).ltvSettingsDao();
        dbHelper = new DBHelper(requireContext());

        goldWeightEditText = root.findViewById(R.id.goldweight);
        stoneWeightEditText = root.findViewById(R.id.stoneweight);
        actualWeightTextView = root.findViewById(R.id.actualweight);
        btnBasePrice = root.findViewById(R.id.baseprice);

        // Purity buttons
        TextView btn70 = root.findViewById(R.id.seventypersent);
        TextView btn75 = root.findViewById(R.id.seventyfive);
        TextView btn79 = root.findViewById(R.id.seventynine);
        TextView btn83 = root.findViewById(R.id.eightythreee);
        TextView btn87 = root.findViewById(R.id.eightyseven);
        TextView btn91 = root.findViewById(R.id.ninetyone);
        TextView btn99 = root.findViewById(R.id.ninetynine);

        purityButtons.add(btn70);
        purityButtons.add(btn75);
        purityButtons.add(btn79);
        purityButtons.add(btn83);
        purityButtons.add(btn87);
        purityButtons.add(btn91);
        purityButtons.add(btn99);

        for (TextView btn : purityButtons) {
            btn.setBackgroundResource(R.drawable.bg_circle_purity);
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

            btn.setOnClickListener(v -> {
                highlightSelectedButton(btn);
                calculatePureGold(convertKaratToPercentage(btn.getText().toString()));
            });
        }

        loadBaseLTV();
        FirebaseSyncHelper.silentSyncSchemes(requireContext());

        return root;
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
            LTVSettings settings = LTVSettingsDao.getBase75LTV();
            if (settings != null) {
                current75LtvPrice = settings.getBase75LTV();
                requireActivity().runOnUiThread(() ->
                        btnBasePrice.setText("Base LTV : " + (int) current75LtvPrice));
            }
        }).start();
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

                    if (originalLoanAmount < scheme.getMinLimit()) {
                        continue;
                    } else if (originalLoanAmount > scheme.getMaxLimit()) {
                        loanAmountToShow = scheme.getMaxLimit();
                    } else {
                        loanAmountToShow = originalLoanAmount;
                    }

                    double monthlyInterest = (loanAmountToShow * scheme.getInterest()) / 100 / 12;
                    float interestPerMonth = (float) (scheme.getInterest() / 12);

                    foundAny = true;
                    View cardView = LayoutInflater.from(getContext()).inflate(R.layout.scheme_card, schemeContainer, false);

                    TextView schemeNameView = cardView.findViewById(R.id.tvSchemeName);
                    TextView loanAmountView = cardView.findViewById(R.id.tvLoanAmount);
                    TextView interestView = cardView.findViewById(R.id.tvMonthlyInterest);
                    TextView interestRateView = cardView.findViewById(R.id.tvInterestRate);

                    schemeNameView.setText(scheme.getName());

                    animeffect.animateLoanAmount(loanAmountView, (int) ((int) loanAmountToShow-(loanAmountToShow/2)), (int) loanAmountToShow);
                    animeffect.animateMonthlyInterest(interestView, 0, monthlyInterest);
                    animeffect.animateInterestRate(interestRateView, 0, interestPerMonth);

                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_fade_in);
                    cardView.startAnimation(animation);
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
            Log.e("findmulti", "Multiplier: " + purityMultiplier + " purity: " + purityLabel);
            double actualGold = (netWeight * purityMultiplier) / 100;
            actualWeightTextView.setText(String.format("Net Weight : %.2f g", actualGold));

            showEligibleSchemes(actualGold);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    private String convertKaratToPercentage(String karatText) {
        if (karatText.contains("70")) return "70%";
        else if (karatText.contains("75")) return "75%";
        else if (karatText.contains("79")) return "79%";
        else if (karatText.contains("83")) return "83%";
        else if (karatText.contains("87")) return "87%";
        else if (karatText.contains("91N")) return "91%";
        else if (karatText.contains("91")) return "99%";
        else return "0"; // fallback
    }
}
