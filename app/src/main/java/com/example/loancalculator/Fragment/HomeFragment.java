package com.example.loancalculator.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.loancalculator.R;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        LinearLayout btnGoldToLoan = view.findViewById(R.id.tab_gold_to_loan);
        LinearLayout btnComingSoon = view.findViewById(R.id.tab_coming_soon);

        btnGoldToLoan.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.main_container, new goldtoloancalculation());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnComingSoon.setOnClickListener(v ->
                Toast.makeText(getContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
        );

        return view;
    }
}
