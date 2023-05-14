package com.manech.manechapp.view.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.data.OrderLine;

import java.util.ArrayList;
import java.util.List;

public class OrderLineListAdapter extends RecyclerView.Adapter<OrderLineViewHolder> implements Filterable, OrderLineClickListener {

    private final OrderLineClickListener listListener;
    private List<OrderLine> orderLineList;
    private List<OrderLine> allOrderLines;


    private Filter orderLineFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<OrderLine> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(allOrderLines);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (OrderLine orderLine : allOrderLines) {
                    String text = orderLine.toString();
                    if (text.toLowerCase().trim().contains(filterPattern)) {
                        filteredList.add(orderLine);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            orderLineList.clear();
            orderLineList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public OrderLineListAdapter(List<OrderLine> orderLineList, OrderLineClickListener listener) {
        this.orderLineList = orderLineList;
        this.listListener = listener;
        this.allOrderLines = new ArrayList<>(orderLineList);
    }


    @Override
    public int getItemViewType(final int position) {
        return R.layout.orderline_item_view;
    }

    @NonNull
    @Override
    public OrderLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new OrderLineViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderLineViewHolder holder, int position) {
        OrderLine orderLine = orderLineList.get(position);
        holder.getTvName().setText(orderLine.getQuantity() + " x " + orderLine.getTitle());
        holder.getTvPrice().setText(orderLine.getItemPrice() + "");
        holder.getTvFee().setText(orderLine.getFee() + "");
        holder.getTvSubTotal().setText("" + orderLine.getPrice());
    }


    @Override
    public Filter getFilter() {
        return orderLineFilter;
    }

    @Override
    public int getItemCount() {
        return orderLineList.size();
    }


    public OrderLine getItem(int itemPosition) {
        return orderLineList.get(itemPosition);
    }

    @Override
    public void add2CartClicked(int layoutPosition) {
        listListener.add2CartClicked(layoutPosition);
    }

    @Override
    public void itemClicked(int layoutPosition) {
        listListener.itemClicked(layoutPosition);
    }
}