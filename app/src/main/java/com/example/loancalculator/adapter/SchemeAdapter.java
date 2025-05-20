package com.example.loancalculator.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loancalculator.R;
import com.example.loancalculator.Scheme;

import java.util.ArrayList;
import java.util.List;

public class SchemeAdapter extends RecyclerView.Adapter<SchemeAdapter.SchemeViewHolder> {

    private List<Scheme> schemeList = new ArrayList<>();

    public void setSchemeList(List<Scheme> schemes) {
        this.schemeList = schemes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SchemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scheme, parent, false);
        return new SchemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchemeViewHolder holder, int position) {
        Scheme scheme = schemeList.get(position);

        holder.tvSchemeName.setText("SCHEME "+scheme.getName());
        holder.tvLtvType.setText("LTV Type: " + scheme.getLtvType());
        holder.tvLtvPrice.setText("Price: ₹" + scheme.getPrice());

        holder.tvmin.setText("Minimum : "+scheme.getMinLimit());
        holder.tvmax.setText("Maximum : "+scheme.getMaxLimit());

        holder.tvInterestYearly.setText("Yearly Interest: " + scheme.getInterest() + "%");

        // Calculate monthly interest (yearly / 12), formatted to 2 decimals
        float monthlyInterest = scheme.getInterest() / 12f;
        holder.tvInterestMonthly.setText(String.format("Monthly Interest: %.2f%%", monthlyInterest));
    }

    @Override
    public int getItemCount() {
        return schemeList.size();
    }

    static class SchemeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSchemeName, tvLtvType, tvLtvPrice, tvmin , tvmax , tvInterestYearly, tvInterestMonthly;

        public SchemeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSchemeName = itemView.findViewById(R.id.tvSchemeName);
            tvLtvType = itemView.findViewById(R.id.tvLtvType);
            tvLtvPrice = itemView.findViewById(R.id.tvLtvPrice);
            tvmin = itemView.findViewById(R.id.tvmin);
            tvmax = itemView.findViewById(R.id.tvmax);
            tvInterestYearly = itemView.findViewById(R.id.tvInterestYearly);
            tvInterestMonthly = itemView.findViewById(R.id.tvInterestMonthly);
        }
    }
}
