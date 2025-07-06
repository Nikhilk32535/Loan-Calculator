package com.example.loancalculator.purity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.loancalculator.R;

public class set_purity extends Fragment {

    private EditText edit70, edit75, edit79, edit83, edit87, edit91, edit99;
    private Button btnSave;
    private DBHelper dbHelper;

    public set_purity() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_purity, container, false);

        dbHelper = new DBHelper(requireContext());

        edit70 = view.findViewById(R.id.edit70);
        edit75 = view.findViewById(R.id.edit75);
        edit79 = view.findViewById(R.id.edit79);
        edit83 = view.findViewById(R.id.edit83);
        edit87 = view.findViewById(R.id.edit87);
        edit91 = view.findViewById(R.id.edit91);
        edit99 = view.findViewById(R.id.edit99);
        btnSave = view.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> savePurityValues());

        loadSavedMultipliers();
        return view;
    }

    @SuppressLint("DefaultLocale")
    private void loadSavedMultipliers() {
        edit70.setHint(String.format("%.2f", dbHelper.getMultiplierByLabel("70%")));
        edit75.setHint(String.format("%.2f", dbHelper.getMultiplierByLabel("75%")));
        edit79.setHint(String.format("%.2f", dbHelper.getMultiplierByLabel("79%")));
        edit83.setHint(String.format("%.2f", dbHelper.getMultiplierByLabel("83%")));
        edit87.setHint(String.format("%.2f", dbHelper.getMultiplierByLabel("87%")));
        edit91.setHint(String.format("%.2f", dbHelper.getMultiplierByLabel("91%")));
        edit99.setHint(String.format("%.2f", dbHelper.getMultiplierByLabel("99%")));
    }

    private void savePurityValues() {
        double current70 = dbHelper.getMultiplierByLabel("70%");
        double current75 = dbHelper.getMultiplierByLabel("75%");
        double current79 = dbHelper.getMultiplierByLabel("79%");
        double current83 = dbHelper.getMultiplierByLabel("83%");
        double current87 = dbHelper.getMultiplierByLabel("87%");
        double current91 = dbHelper.getMultiplierByLabel("91%");
        double current99 = dbHelper.getMultiplierByLabel("99%");

        String input70 = edit70.getText().toString().trim();
        String input75 = edit75.getText().toString().trim();
        String input79 = edit79.getText().toString().trim();
        String input83 = edit83.getText().toString().trim();
        String input87 = edit87.getText().toString().trim();
        String input91 = edit91.getText().toString().trim();
        String input99 = edit99.getText().toString().trim();

        String purity70 = input70.isEmpty() ? String.valueOf(current70) : input70;
        String purity75 = input75.isEmpty() ? String.valueOf(current75) : input75;
        String purity79 = input79.isEmpty() ? String.valueOf(current79) : input79;
        String purity83 = input83.isEmpty() ? String.valueOf(current83) : input83;
        String purity87 = input87.isEmpty() ? String.valueOf(current87) : input87;
        String purity91 = input91.isEmpty() ? String.valueOf(current91) : input91;
        String purity99 = input99.isEmpty() ? String.valueOf(current99) : input99;

        boolean isSaved = dbHelper.savePurityMultipliers(
                purity70, purity75, purity79, purity83, purity87, purity91, purity99);

        if (isSaved) {
            Toast.makeText(getContext(), "Purity values updated successfully.", Toast.LENGTH_SHORT).show();
            cleardata();
        } else {
            Toast.makeText(getContext(), "Failed to update purity values.", Toast.LENGTH_SHORT).show();
        }
    }

    public void cleardata() {
        edit70.setText("");
        edit75.setText("");
        edit79.setText("");
        edit83.setText("");
        edit87.setText("");
        edit91.setText("");
        edit99.setText("");
        loadSavedMultipliers();
    }
}
