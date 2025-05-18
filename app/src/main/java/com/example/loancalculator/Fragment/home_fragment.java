package com.example.loancalculator.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loancalculator.purity.DBHelper;
import com.example.loancalculator.R;

public class home_fragment extends Fragment {

    private EditText goldWeightEditText, stoneWeightEditText;
    private TextView actualWeightTextView;
    private DBHelper dbHelper;

    public home_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home_fragment, container, false);

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

        // Set Click Listeners to use dynamic DB values
        btn70.setOnClickListener(v -> calculatePureGold("70%"));
        btn75.setOnClickListener(v -> calculatePureGold("75%"));
        btn83.setOnClickListener(v -> calculatePureGold("83%"));
        btn87.setOnClickListener(v -> calculatePureGold("87%"));
        btn91.setOnClickListener(v -> calculatePureGold("91%"));
        btn99.setOnClickListener(v -> calculatePureGold("99%"));

        return root;
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

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }
}
