package com.example.loancalculator.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loancalculator.BaseLTV.LTVDB;
import com.example.loancalculator.BaseLTV.LTVSettings;
import com.example.loancalculator.BaseLTV.LTVSettingsDao;
import com.example.loancalculator.R;
import com.example.loancalculator.Scheme;
import com.example.loancalculator.SchemeDao;
import com.example.loancalculator.SchemeDatabase;
import com.example.loancalculator.adapter.SchemeAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class addscheme extends Fragment {

    private EditText et75LtvPrice, etSchemeName, etInterest, etMinLimit, etMaxLimit;
    private RadioGroup ltvRadioGroup;
    private Button btnSave, btnUpdateBaseLTV;
    private RecyclerView recyclerView;
    private float current75LtvPrice = 0f;

    private SchemeAdapter schemeAdapter;
    private SchemeDatabase schemeDb;
    private LTVSettingsDao ltvSettingsDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addscheme, container, false);
        initViews(view);
        initDb();
        setupRecyclerView();
        loadSchemes();
        uploadAllLocalSchemesToFirebase();
        syncFromFirebaseToLocal();
        loadSchemes();
        setListeners();
        return view;
    }

    private void initViews(View view) {
        et75LtvPrice = view.findViewById(R.id.et_ltv_75rate);
        btnUpdateBaseLTV = view.findViewById(R.id.btn_update75ltv);
        etSchemeName = view.findViewById(R.id.et_scheme_name);
        etInterest = view.findViewById(R.id.et_interest);
        etMinLimit = view.findViewById(R.id.et_min_limit);
        etMaxLimit = view.findViewById(R.id.et_max_limit);
        ltvRadioGroup = view.findViewById(R.id.ltvRadioGroup);
        btnSave = view.findViewById(R.id.btn_save);
        recyclerView = view.findViewById(R.id.recyclerViewSchemes);
    }

    private void initDb() {
        schemeDb = SchemeDatabase.getInstance(getContext());
        ltvSettingsDao = LTVDB.getInstance(requireContext()).ltvSettingsDao();
    }

    private void setListeners() {
        btnUpdateBaseLTV.setOnClickListener(v -> updateBaseLTV());
        btnSave.setOnClickListener(v -> saveScheme());
        new ItemTouchHelper(getSwipeToDeleteCallback()).attachToRecyclerView(recyclerView);
    }

    private void updateBaseLTV() {
        String priceStr = et75LtvPrice.getText().toString().trim();
        if (TextUtils.isEmpty(priceStr)) {
            showToast("Enter 75LTV Price first");
            return;
        }

        try {
            current75LtvPrice = Float.parseFloat(priceStr);

            new Thread(() -> {
                // 1. Save new base to local DB
                ltvSettingsDao.insertOrUpdate(new LTVSettings(current75LtvPrice));

                // 2. Update base LTV in Firebase
                FirebaseDatabase.getInstance().getReference("BaseLTV")
                        .setValue(current75LtvPrice);

                // 3. Update all local scheme prices
                schemeDb.schemeDao().updatePricesForAllSchemes(current75LtvPrice);

                // 4. Get updated schemes from local DB
                List<Scheme> updatedSchemes = schemeDb.schemeDao().getAllSchemes();

                // 5. Delete all schemes from Firebase
                FirebaseDatabase.getInstance().getReference("Schemes").removeValue()
                        .addOnSuccessListener(aVoid -> {
                            // 6. Re-upload updated schemes to Firebase
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schemes");
                            for (Scheme scheme : updatedSchemes) {
                                ref.child(scheme.getName()).setValue(scheme);
                            }

                            // 7. Update UI
                            requireActivity().runOnUiThread(() -> {
                                schemeAdapter.setSchemeList(updatedSchemes);
                                showToast("✅ Base LTV updated. Schemes refreshed online & offline.");
                            });
                        });

            }).start();
        } catch (NumberFormatException e) {
            showToast("Invalid price");
        }
    }



    private void saveScheme() {
        String name = etSchemeName.getText().toString().trim().toUpperCase();
        String interestStr = etInterest.getText().toString().trim();
        String minLimitStr = etMinLimit.getText().toString().trim();
        String maxLimitStr = etMaxLimit.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(interestStr) ||
                TextUtils.isEmpty(minLimitStr) || TextUtils.isEmpty(maxLimitStr)) {
            showToast("Please fill all fields");
            return;
        }

        if (current75LtvPrice <= 0) {
            showToast("Update 75LTV price first");
            return;
        }

        int selectedLtvId = ltvRadioGroup.getCheckedRadioButtonId();
        if (selectedLtvId == -1) {
            showToast("Select LTV type");
            return;
        }

        RadioButton selectedRadio = ltvRadioGroup.findViewById(selectedLtvId);
        String ltvType = selectedRadio.getText().toString();

        try {
            float interest = Float.parseFloat(interestStr);
            float minLimit = Float.parseFloat(minLimitStr);
            float maxLimit = Float.parseFloat(maxLimitStr);

            new Thread(() -> {
                Scheme existing = schemeDb.schemeDao().getSchemeByName(name);
                if (existing == null) {
                    int price = calculatePriceByLtvType(ltvType, current75LtvPrice);
                    Scheme scheme = new Scheme(name, ltvType, price, minLimit, maxLimit, interest);
                    schemeDb.schemeDao().insert(scheme);
                    uploadSchemeToFirebase(scheme);

                    requireActivity().runOnUiThread(() -> {
                        showToast("Scheme saved");
                        clearInputs();
                        loadSchemes();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> showToast("Scheme already exists!"));
                }
            }).start();

        } catch (NumberFormatException e) {
            showToast("Invalid numeric values");
        }
    }

    private void uploadSchemeToFirebase(Scheme scheme) {
        FirebaseDatabase.getInstance().getReference("Schemes")
                .child(scheme.getName())
                .setValue(scheme)
                .addOnSuccessListener(aVoid ->
                        requireActivity().runOnUiThread(() ->
                                showToast("\u2705 Scheme also uploaded online")
                        )
                )
                .addOnFailureListener(e ->
                        requireActivity().runOnUiThread(() ->
                                showToast("\u274C Upload failed: " + e.getMessage())
                        )
                );
    }

    private int calculatePriceByLtvType(String ltvType, float base75) {
        switch (ltvType) {
            case "50LTV": return (int) ((base75 / 75f) * 50f);
            case "60LTV": return (int) ((base75 / 75f) * 60f);
            case "65LTV": return (int) ((base75 / 75f) * 65f);
            case "75LTV":
            default: return (int) base75;
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        schemeAdapter = new SchemeAdapter();
        recyclerView.setAdapter(schemeAdapter);

        schemeAdapter.setOnDeleteClickListener(scheme -> new Thread(() -> {
            schemeDb.schemeDao().deleteScheme(scheme);
            requireActivity().runOnUiThread(() -> {
                showToast("Scheme deleted");
                loadSchemes();
            });
        }).start());
    }

    private void loadSchemes() {
        new Thread(() -> {
            List<Scheme> schemes = schemeDb.schemeDao().getAllSchemes();
            requireActivity().runOnUiThread(() -> schemeAdapter.setSchemeList(schemes));
        }).start();
    }

    private void loadBaseLTV() {
        new Thread(() -> {
            LTVSettings settings = ltvSettingsDao.getBase75LTV();

            if (settings != null) {
                current75LtvPrice = settings.getBase75LTV();
                requireActivity().runOnUiThread(() ->
                        et75LtvPrice.setHint(String.valueOf((int) current75LtvPrice)));
            } else {
                // Fetch from Firebase if not found locally
                FirebaseDatabase.getInstance().getReference("BaseLTV")
                        .get()
                        .addOnSuccessListener(dataSnapshot -> {
                            if (dataSnapshot.exists()) {
                                float firebaseLTV = dataSnapshot.getValue(Float.class);
                                current75LtvPrice = firebaseLTV;

                                // Save locally too
                                new Thread(() -> {
                                    ltvSettingsDao.insertOrUpdate(new LTVSettings(firebaseLTV));
                                }).start();

                                et75LtvPrice.setHint(String.valueOf((int) firebaseLTV));
                                showToast("Base LTV loaded from cloud");
                            }
                        });
            }
        }).start();
    }


    private void clearInputs() {
        etSchemeName.setText("");
        etInterest.setText("");
        etMinLimit.setText("");
        etMaxLimit.setText("");
        ltvRadioGroup.clearCheck();
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private ItemTouchHelper.SimpleCallback getSwipeToDeleteCallback() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder tgt) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
                int pos = vh.getAdapterPosition();
                Scheme schemeToDelete = schemeAdapter.getSchemeAt(pos);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this scheme?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            new Thread(() -> {
                                schemeDb.schemeDao().deleteScheme(schemeToDelete);
                                FirebaseDatabase.getInstance().getReference("Schemes")
                                        .child(schemeToDelete.getName())
                                        .removeValue();

                                requireActivity().runOnUiThread(() -> {
                                    showToast("Scheme deleted");
                                    loadSchemes();
                                });
                            }).start();
                            schemeAdapter.removeSchemeAt(pos);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> schemeAdapter.notifyItemChanged(pos))
                        .setCancelable(false)
                        .show();
            }
        };
    }

    private void uploadAllLocalSchemesToFirebase() {
        new Thread(() -> {
            List<Scheme> schemes = schemeDb.schemeDao().getAllSchemes();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schemes");

            for (Scheme scheme : schemes) {
                ref.child(scheme.getName()).setValue(scheme);
            }

            requireActivity().runOnUiThread(() ->
                    showToast("All local schemes uploaded to Firebase"));
        }).start();
    }

    private void syncFromFirebaseToLocal() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Step 1: Fetch BaseLTV
        rootRef.child("BaseLTV").get().addOnSuccessListener(baseSnapshot -> {
            if (baseSnapshot.exists()) {
                Float baseLtvValue = baseSnapshot.getValue(Float.class);
                if (baseLtvValue != null) {
                    current75LtvPrice = baseLtvValue;

                    new Thread(() -> {
                        ltvSettingsDao.insertOrUpdate(new LTVSettings(current75LtvPrice));

                        // Ensure UI is still active before updating
                        if (isAdded() && getActivity() != null) {
                            requireActivity().runOnUiThread(() -> {
                                if (et75LtvPrice != null) {
                                    et75LtvPrice.setHint(String.valueOf((int) current75LtvPrice));
                                }
                            });
                        }
                    }).start();
                }
            }
        }).addOnFailureListener(e -> showToast("❌ Failed to load BaseLTV"));

        // Step 2: Fetch All Schemes
        rootRef.child("Schemes").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                List<Scheme> firebaseSchemes = new java.util.ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Scheme scheme = child.getValue(Scheme.class);
                    if (scheme != null) {
                        firebaseSchemes.add(scheme);
                    }
                }

                new Thread(() -> {
                    schemeDb.schemeDao().clearAll();
                    schemeDb.schemeDao().insertAll(firebaseSchemes);

                    // Ensure UI is active before updating adapter
                    if (isAdded() && getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (schemeAdapter != null) {
                                schemeAdapter.setSchemeList(firebaseSchemes);
                            }
                            showToast("✅ Synced from cloud.");
                        });
                    }
                }).start();
            }
        }).addOnFailureListener(e -> showToast("❌ Failed to sync schemes"));
    }


}