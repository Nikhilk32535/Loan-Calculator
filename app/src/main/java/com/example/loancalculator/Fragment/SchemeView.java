package com.example.loancalculator.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loancalculator.R;
import com.example.loancalculator.Scheme;
import com.example.loancalculator.SchemeDatabase;
import com.example.loancalculator.adapter.SchemeAdapter;
import java.util.List;

public class SchemeView extends Fragment {

  private RecyclerView recyclerView;
  private SchemeAdapter adapter;

  public SchemeView() {}

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_scheme_view, container, false);
    recyclerView = view.findViewById(R.id.schemeRecycler);
    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    adapter = new SchemeAdapter();
    recyclerView.setAdapter(adapter);

    loadSchemes();

    adapter.setOnDeleteClickListener(
        scheme -> {
          new Thread(
                  () -> {
                    SchemeDatabase.getInstance(requireContext()).schemeDao().deleteScheme(scheme);
                    requireActivity().runOnUiThread(this::loadSchemes);
                  })
              .start();
        });

    return view;
  }

  private void loadSchemes() {
    new Thread(
            () -> {
              List<Scheme> schemes =
                  SchemeDatabase.getInstance(requireContext()).schemeDao().getAllSchemes();

              requireActivity()
                  .runOnUiThread(
                      () -> {
                        adapter.setSchemeList(schemes);
                      });
            })
        .start();
  }
}
