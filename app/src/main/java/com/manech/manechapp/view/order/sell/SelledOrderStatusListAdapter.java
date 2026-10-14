package com.manech.manechapp.view.order.sell;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.OrderStatus;
import com.manech.manechapp.helper.Converters;
import com.manech.manechapp.view.order.OrderLineClickListener;
import com.manech.manechapp.view.order.OrderStatusClickListener;
import com.manech.manechapp.view.order.OrderStatusViewHolder;

import java.util.List;

public class SelledOrderStatusListAdapter extends RecyclerView.Adapter<OrderStatusViewHolder> implements OrderStatusClickListener {

    private final SellsClickListener listListener;
    private List<OrderStatus> orderStatusList;
      public SelledOrderStatusListAdapter(List<OrderStatus> orderStatusList, SellsClickListener listener) {
        this.orderStatusList = orderStatusList;
        this.listListener = listener;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.orderstatus_item_view;
    }

    @NonNull
    @Override
    public OrderStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new OrderStatusViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderStatusViewHolder holder, int position) {
        OrderStatus orderStatus = orderStatusList.get(position);
        holder.getTvStatus().setText(orderStatus.getStatus()
                .getLocalizedName(holder.itemView.getContext()));
        holder.getTvDate().setText(Converters.fromDate(orderStatus.getStatusDate()) +"");
    }

    @Override
    public int getItemCount() {
        return orderStatusList.size();
    }


    public OrderStatus getItem(int itemPosition) {
        return orderStatusList.get(itemPosition);
    }

}