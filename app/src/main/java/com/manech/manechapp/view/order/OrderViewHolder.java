package com.manech.manechapp.view.order;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;


public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final OrderClickListener listener;
    private final TextView tvOrderId;
    private final TextView tvOrderDate;
    private final TextView tvTotalPrice;
    private final  TextView tvOrderStatus;
    private final TextView tvOrderDiscount;


    public OrderViewHolder(@NonNull View itemView, OrderClickListener listener) {
        super(itemView);
        tvOrderId = itemView.findViewById(R.id.tvOrderId);
        tvOrderDate=itemView.findViewById(R.id.tvOrderDate);
        tvTotalPrice=itemView.findViewById(R.id.tvTotalPrice);
        tvOrderStatus=itemView.findViewById(R.id.tvOrderStatus);
        tvOrderDiscount=itemView.findViewById(R.id.tvDiscount);
        this.listener = listener;

        tvOrderId.setOnClickListener(this);
    }

    public TextView getTvOrderDiscount() {
        return tvOrderDiscount;
    }

    public TextView getTvOrderId() {
        return tvOrderId;
    }

    public TextView getTvOrderDate() {
        return tvOrderDate;
    }

    public TextView getTvTotalPrice() {
        return tvTotalPrice;
    }

    public TextView getTvOrderStatus() {
        return tvOrderStatus;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvOrderId:
                listener.itemClicked(this.getLayoutPosition());
                break;
            default:
                break;
        }
    }
}
