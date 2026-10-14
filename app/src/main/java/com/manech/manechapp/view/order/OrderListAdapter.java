package com.manech.manechapp.view.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.Order;
import com.manech.manechapp.helper.Converters;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderViewHolder> implements OrderClickListener{

    private final LineClicked listListener;
    private List<Order> orderList;

    public OrderListAdapter(List<Order> orderList, LineClicked listener) {
        this.orderList = orderList;
        this.listListener = listener;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.fragment_order_line;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        return new OrderViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.getTvOrderId().setText(order.getQrCode()+"");
        holder.getTvOrderDate().setText(Converters.fromDate(order.getCreateAt()));
        holder.getTvTotalPrice().setText(order.getPrice() + " DH");
        holder.getTvOrderStatus().setText(order.getOrderStatusList() == null || order.getOrderStatusList().isEmpty()
                ? holder.itemView.getContext().getString(R.string.status_unknown)
                : order.getOrderStatusList().get(0).getStatus()
                .getLocalizedName(holder.itemView.getContext()));
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public Order getItem(int itemPosition) {
        return orderList.get(itemPosition);
    }

    @Override
    public void itemClicked(int layoutPosition) {
        listListener.itemClicked(layoutPosition);
    }
}