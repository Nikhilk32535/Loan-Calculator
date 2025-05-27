package com.example.loancalculator.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loancalculator.R;
import com.google.android.material.navigation.NavigationView;

public class user_manual extends Fragment {
    public user_manual() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_user_manual, container, false);

      Button  start=root.findViewById(R.id.startButton);

      start.setOnClickListener(v->{
          Fragment fragment=new home_fragment();
          getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment).commit();
      });


        return root;
    }
}