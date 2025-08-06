package com.example.loancalculator.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.loancalculator.R;
import com.example.loancalculator.purity.set_purity;

public class user_manual extends Fragment {

    public user_manual() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_manual, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // You can access views like this if needed:
        // TextView title = view.findViewById(R.id.manualTitle);
        TextView textViewPurityInfo=view.findViewById(R.id.textViewPurityInfo);

        textViewPurityInfo.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
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
                                .replace(R.id.main_container, setPurityFragment) // <- Replace with your container's ID
                                .addToBackStack(null) // Allows user to press back to return
                                .commit();
                    })
                    .setNegativeButton("Close", null)
                    .show();
        });

    }
}
