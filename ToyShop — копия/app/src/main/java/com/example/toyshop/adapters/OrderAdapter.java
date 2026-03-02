package com.example.toyshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyshop.R;
import com.example.toyshop.models.Order;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnOrderClickListener listener;
    private boolean isAdminMode;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onUpdateStatusClick(Order order);
    }

    public OrderAdapter(Context context, List<Order> orderList, boolean isAdminMode) {
        this.context = context;
        this.orderList = orderList;
        this.isAdminMode = isAdminMode;
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Заказ #" + order.getOrderId().substring(0, 8));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        holder.tvOrderDate.setText(sdf.format(new Date(order.getOrderDate())));

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        holder.tvOrderTotal.setText(format.format(order.getTotalAmount()));

        holder.tvOrderStatus.setText(getStatusText(order.getStatus()));
        holder.tvOrderStatus.setBackgroundColor(getStatusColor(order.getStatus()));

        holder.tvOrderItems.setText("Товаров: " + order.getItems().size());

        if (isAdminMode) {
            holder.btnUpdateStatus.setVisibility(View.VISIBLE);
        } else {
            holder.btnUpdateStatus.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onOrderClick(order);
                }
            }
        });

        holder.btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onUpdateStatusClick(order);
                }
            }
        });
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Ожидает обработки";
            case "processing": return "В обработке";
            case "shipped": return "Отправлен";
            case "delivered": return "Доставлен";
            case "cancelled": return "Отменен";
            default: return status;
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "pending": return context.getColor(R.color.status_pending);
            case "processing": return context.getColor(R.color.status_processing);
            case "shipped": return context.getColor(R.color.status_shipped);
            case "delivered": return context.getColor(R.color.status_delivered);
            case "cancelled": return context.getColor(R.color.status_cancelled);
            default: return context.getColor(R.color.status_default);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderTotal, tvOrderStatus, tvOrderItems;
        Button btnUpdateStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}