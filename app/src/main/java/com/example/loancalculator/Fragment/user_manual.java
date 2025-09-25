package com.example.loancalculator.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loancalculator.R;
import com.example.loancalculator.purity.set_purity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class user_manual extends Fragment {

    private String CURRENT_VERSION;
    private DatabaseReference versionRef;

    public user_manual() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_manual, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CURRENT_VERSION = "v1.2";
        versionRef = FirebaseDatabase.getInstance().getReference("AppVersion");

        TextView textViewPurityInfo = view.findViewById(R.id.textViewPurityInfo);
        Button btnCheckUpdate = view.findViewById(R.id.btnCheckUpdate);
        TextView tvCurrentVersion = view.findViewById(R.id.tvAppVersion);

        // 🔹 Show current app version on screen
        tvCurrentVersion.setText("Current Version: " + CURRENT_VERSION);

        // 🔹 Purity Info Dialog
        textViewPurityInfo.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("How Gold Purity Works")
                    .setMessage("Different lenders deduct impurities based on their own policies." +
                            " This app lets you customize purity deductions to match your jeweller or bank’s calculation." +
                            "\n\nE.g., 22K gold can be valued differently:" +
                            "\n- Bank A: 91.6%\n- Jeweller B: 91.2%" +
                            "\n\nUse the settings below to set your purity percentage or multiplier.")
                    .setPositiveButton("Go to Set Purity", (dialog, which) -> {
                        Fragment setPurityFragment = new set_purity();
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_container, setPurityFragment)
                                .addToBackStack(null)
                                .commit();
                    })
                    .setNegativeButton("Close", null)
                    .show();
        });

        // 🔹 Check Update Button Logic
        btnCheckUpdate.setOnClickListener(v -> checkForUpdate());
    }

    // 🔹 Function: Check if update available
    private void checkForUpdate() {
        versionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String latestVersion = snapshot.getValue(String.class);

                    if (latestVersion != null && !latestVersion.equals(CURRENT_VERSION)) {
                        // Fetch download link only when update is available
                        fetchDownloadLinkAndPrompt(latestVersion);
                    } else {
                        Toast.makeText(getContext(), "Your app is up to date (" + CURRENT_VERSION + ")", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 🔹 If no version info in Firebase → add current version (first time only)
                    versionRef.setValue(CURRENT_VERSION);
                    Toast.makeText(getContext(), "Version info initialized: " + CURRENT_VERSION, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔹 Fetch download link from Firebase
    private void fetchDownloadLinkAndPrompt(String latestVersion) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AppDownloadLink");
        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String apkUrl = snapshot.getValue(String.class);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Update Available")
                        .setMessage("A new version (" + latestVersion + ") is available.\nYour version: " + CURRENT_VERSION)
                        .setPositiveButton("Update", (dialog, which) -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl));
                            startActivity(intent);
                        })
                        .setNegativeButton("Later", null)
                        .show();

            } else {
                Toast.makeText(getContext(), "Download link not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to fetch link: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}
