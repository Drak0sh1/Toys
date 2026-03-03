package com.example.toyshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toyshop.R;
import com.example.toyshop.models.Toy;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ToyAdapter extends RecyclerView.Adapter<ToyAdapter.ToyViewHolder> {

    private Context context;
    private List<Toy> toyList;
    private OnItemClickListener listener;
    private boolean isAdminMode;

    public interface OnItemClickListener {
        void onItemClick(Toy toy);
        void onEditClick(Toy toy);
        void onDeleteClick(Toy toy);
        void onAddToCartClick(Toy toy);
    }

    public ToyAdapter(Context context, List<Toy> toyList, boolean isAdminMode) {
        this.context = context;
        this.toyList = toyList;
        this.isAdminMode = isAdminMode;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ToyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_toy, parent, false);
        return new ToyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToyViewHolder holder, int position) {
        Toy toy = toyList.get(position);

        holder.tvToyName.setText(toy.getName());

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        holder.tvToyPrice.setText(format.format(toy.getPrice()));

        holder.tvToyCategory.setText(toy.getCategory());
        holder.tvToyAgeGroup.setText("Для детей " + toy.getAgeGroup() + "+");

        if (toy.isAvailable()) {
            holder.tvStockStatus.setText("В наличии: " + toy.getStockQuantity());
            holder.tvStockStatus.setTextColor(context.getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvStockStatus.setText("Нет в наличии");
            holder.tvStockStatus.setTextColor(context.getColor(android.R.color.holo_red_dark));
        }

        if (toy.getImageUrl() != null && !toy.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(toy.getImageUrl())
                    .placeholder(R.drawable.placeholder_toy)
                    .into(holder.ivToyImage);
        } else {
            holder.ivToyImage.setImageResource(R.drawable.placeholder_toy);
        }

        if (isAdminMode) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnAddToCart.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnAddToCart.setVisibility(View.VISIBLE);

            if (!toy.isAvailable()) {
                holder.btnAddToCart.setEnabled(false);
            } else {
                holder.btnAddToCart.setEnabled(true);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(toy);
                }
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditClick(toy);
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(toy);
                }
            }
        });

        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAddToCartClick(toy);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return toyList.size();
    }

    public static class ToyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivToyImage;
        TextView tvToyName, tvToyPrice, tvToyCategory, tvToyAgeGroup, tvStockStatus;
        Button btnEdit, btnDelete, btnAddToCart;

        public ToyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivToyImage = itemView.findViewById(R.id.ivToyImage);
            tvToyName = itemView.findViewById(R.id.tvToyName);
            tvToyPrice = itemView.findViewById(R.id.tvToyPrice);
            tvToyCategory = itemView.findViewById(R.id.tvToyCategory);
            tvToyAgeGroup = itemView.findViewById(R.id.tvToyAgeGroup);
            tvStockStatus = itemView.findViewById(R.id.tvStockStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}