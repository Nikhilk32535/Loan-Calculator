package com.example.loancalculator.Fragment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loancalculator.R;
import com.example.loancalculator.Scheme;
import com.example.loancalculator.SchemeDao;
import com.example.loancalculator.SchemeDatabase;
import com.example.loancalculator.adapter.SchemeAdapter;

import java.util.List;
import java.util.concurrent.Executors;

public class addscheme extends Fragment {

    private EditText et75LtvPrice, etSchemeName, etInterest, etMinLimit, etMaxLimit;
    private RadioGroup ltvRadioGroup;
    private Button btnSave, btnUpdate75Ltv;
    private RecyclerView recyclerViewSchemes;

    private float current75LtvPrice = 0f;

    private SchemeAdapter schemeAdapter;
    private SchemeDatabase appDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addscheme, container, false);

        // Initialize UI elements
        et75LtvPrice = rootView.findViewById(R.id.et_ltv_75rate);
        btnUpdate75Ltv = rootView.findViewById(R.id.btn_update75ltv);
        etSchemeName = rootView.findViewById(R.id.et_scheme_name);
        etInterest = rootView.findViewById(R.id.et_interest);
        etMinLimit = rootView.findViewById(R.id.et_min_limit);
        etMaxLimit = rootView.findViewById(R.id.et_max_limit);
        ltvRadioGroup = rootView.findViewById(R.id.ltvRadioGroup);
        btnSave = rootView.findViewById(R.id.btn_save);
        recyclerViewSchemes = rootView.findViewById(R.id.recyclerViewSchemes);

        appDatabase = SchemeDatabase.getInstance(getContext());

        setupRecyclerView();
        loadSchemes();

        btnUpdate75Ltv.setOnClickListener(v -> {
            String priceStr = et75LtvPrice.getText().toString().trim();
            if (TextUtils.isEmpty(priceStr)) {
                Toast.makeText(getContext(), "Enter 75LTV Price first", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                current75LtvPrice = Float.parseFloat(priceStr);

                new Thread(() -> {
                    appDatabase.schemeDao().updatePricesForAllSchemes(current75LtvPrice);
                    List<Scheme> updatedList = appDatabase.schemeDao().getAllSchemes();

                    requireActivity().runOnUiThread(() -> {
                        schemeAdapter.setSchemeList(updatedList);
                        Toast.makeText(getContext(), "Schemes updated with new 75LTV price", Toast.LENGTH_SHORT).show();
                    });
                }).start();

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid price", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> saveScheme());


        ItemTouchHelper itemTouchHelper = getItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(recyclerViewSchemes);



        return rootView;
    }

    private @NonNull ItemTouchHelper getItemTouchHelper() {

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                SchemeAdapter adapter = (SchemeAdapter) recyclerViewSchemes.getAdapter();
                Scheme schemeToDelete = adapter.getSchemeAt(position);

                // Show confirmation dialog
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this scheme?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            // Delete from database in background thread
                            SchemeDao schemeDao = appDatabase.schemeDao();
                            new Thread(() -> schemeDao.deleteScheme(schemeToDelete)).start();

                            // Remove from list and notify adapter
                            adapter.removeSchemeAt(position);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Restore the item (cancel swipe)
                            adapter.notifyItemChanged(position);
                        })
                        .setCancelable(false)
                        .show();
            }
        });


        return itemTouchHelper;
    }

    private void saveScheme() {
        String name = etSchemeName.getText().toString().toUpperCase().trim();
        String interestStr = etInterest.getText().toString().trim();
        String minLimitStr = etMinLimit.getText().toString().trim();
        String maxLimitStr = etMaxLimit.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(interestStr) ||
                TextUtils.isEmpty(minLimitStr) || TextUtils.isEmpty(maxLimitStr)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (current75LtvPrice <= 0) {
            Toast.makeText(getContext(), "Update 75LTV price first", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedLtvId = ltvRadioGroup.getCheckedRadioButtonId();
        if (selectedLtvId == -1) {
            Toast.makeText(getContext(), "Select LTV type", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = ltvRadioGroup.findViewById(selectedLtvId);
        String ltvType = selectedRadioButton.getText().toString();

        float interest, minLimit, maxLimit;
        try {
            interest = Float.parseFloat(interestStr);
            minLimit = Float.parseFloat(minLimitStr);
            maxLimit = Float.parseFloat(maxLimitStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid numeric values", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = calculatePriceByLtvType(ltvType, current75LtvPrice);

        Scheme scheme = new Scheme(name, ltvType, price, minLimit, maxLimit, interest);

        new Thread(() -> {
            appDatabase.schemeDao().insert(scheme);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Scheme saved", Toast.LENGTH_SHORT).show();
                clearInputs();
                loadSchemes();
            });
        }).start();
    }

    private int calculatePriceByLtvType(String ltvType, float base75Price) {
        switch (ltvType) {
            case "50LTV":
                return (int) ((base75Price / 75f) * 50f);
            case "60LTV":
                return (int) ((base75Price / 75f) * 60f);
            case "65LTV":
                return (int) ((base75Price / 75f) * 65f);
            case "75LTV":
            default:
                return (int) base75Price;
        }
    }

    private void clearInputs() {
        etSchemeName.setText("");
        etInterest.setText("");
        etMinLimit.setText("");
        etMaxLimit.setText("");
        ltvRadioGroup.clearCheck();
    }

    private void setupRecyclerView() {
        recyclerViewSchemes.setLayoutManager(new LinearLayoutManager(getContext()));
        schemeAdapter = new SchemeAdapter();
        recyclerViewSchemes.setAdapter(schemeAdapter);

        schemeAdapter.setOnDeleteClickListener(scheme -> {
            new Thread(() -> {
                appDatabase.schemeDao().deleteScheme(scheme);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Scheme deleted", Toast.LENGTH_SHORT).show();
                    loadSchemes();
                });
            }).start();
        });
    }

    private void loadSchemes() {
        new Thread(() -> {
            List<Scheme> schemes = appDatabase.schemeDao().getAllSchemes();
            requireActivity().runOnUiThread(() -> schemeAdapter.setSchemeList(schemes));
        }).start();
    }
    
}
