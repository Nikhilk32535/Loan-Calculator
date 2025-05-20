package com.example.loancalculator.adapter;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loancalculator.R;
import com.example.loancalculator.Scheme;

import java.util.ArrayList;
import java.util.List;

public class SchemeAdapter extends RecyclerView.Adapter<SchemeAdapter.SchemeViewHolder> {

    private List<Scheme> schemeList = new ArrayList<>();

    // Define the interface
    public interface OnDeleteClickListener {
        void onDeleteClick(Scheme scheme);
    }

    // Declare the listener variable
    private OnDeleteClickListener deleteClickListener;

    // Correct setter
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

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

        holder.tvSchemeName.setText("SCHEME " + scheme.getName());
        holder.tvLtvType.setText("LTV Type: " + scheme.getLtvType());
        holder.tvLtvPrice.setText("Price: ₹" + scheme.getPrice());

        holder.tvmin.setText("Minimum ₹" + scheme.getMinLimit());
        holder.tvmax.setText("Maximum ₹" + scheme.getMaxLimit());

        holder.tvInterestYearly.setText("Yearly Interest: " + scheme.getInterest() + "%");

        // Calculate monthly interest (yearly / 12), formatted to 2 decimals
        float monthlyInterest = scheme.getInterest() / 12f;
        holder.tvInterestMonthly.setText(String.format("Monthly Interest: %.2f%%", monthlyInterest));

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                            if (deleteClickListener != null) {
                                deleteClickListener.onDeleteClick(schemeList.get(holder.getAdapterPosition()));
                            }
            }
        });
    }

    @Override
    public int getItemCount() {
        return schemeList.size();
    }

    public Scheme getItem(int position) {
        return schemeList.get(position);
    }

    static class SchemeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSchemeName, tvLtvType, tvLtvPrice, tvmin, tvmax, tvInterestYearly, tvInterestMonthly;
        ImageButton btnDelete;
        View foregroundLayout;

        public SchemeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSchemeName = itemView.findViewById(R.id.tvSchemeName);
            tvLtvType = itemView.findViewById(R.id.tvLtvType);
            tvLtvPrice = itemView.findViewById(R.id.tvLtvPrice);
            tvmin = itemView.findViewById(R.id.tvmin);
            tvmax = itemView.findViewById(R.id.tvmax);
            tvInterestYearly = itemView.findViewById(R.id.tvInterestYearly);
            tvInterestMonthly = itemView.findViewById(R.id.tvInterestMonthly);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            foregroundLayout = itemView.findViewById(R.id.foregroundLayout);
        }
    }
    public Scheme getSchemeAt(int position) {
        return schemeList.get(position);
    }

    public void removeSchemeAt(int position) {
        notifyItemRemoved(position); // notify first
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            schemeList.remove(position);
            notifyDataSetChanged();
        }, 300); // Delay to let animation play
    }



}
