package com.manech.manechapp.view.order.sell;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.OrderLine;
import com.manech.manechapp.helper.MyTextChangeWatcher;

import java.util.ArrayList;
import java.util.List;

public class SellLineListAdapter extends RecyclerView.Adapter<SellLineViewHolder> implements Filterable, SellsClickListener {

    private final SellsClickListener listListener;
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

    public SellLineListAdapter(List<OrderLine> orderLineList, SellsClickListener listener) {
        this.orderLineList = orderLineList;
        this.listListener = listener;
        this.allOrderLines = new ArrayList<>(orderLineList);
    }


    @Override
    public int getItemViewType(final int position) {
        return R.layout.selled_item_view;
    }

    @NonNull
    @Override
    public SellLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new SellLineViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SellLineViewHolder holder, int position) {
        OrderLine orderLine = orderLineList.get(position);
        holder.getTvName().setText(orderLine.getQuantity() + " x " + orderLine.getTitle());
        holder.getTvPrice().setText(orderLine.getItemPrice() + "");
        holder.getTxtFee().setText(orderLine.getFee() + "");
        holder.getTxtDiscount().setText(orderLine.getDiscount() + "");
        holder.getTvSubTotal().setText("" + orderLine.getPrice());

        holder.getTxtDiscount().addTextChangedListener(new MyTextChangeWatcher(holder.getTxtDiscount()) {
            @Override
            public void whenChangesOccure() {
                holder.getImgUpdate().setVisibility(View.VISIBLE);
            }
        });

        holder.getTxtFee().addTextChangedListener(new MyTextChangeWatcher(holder.getTxtFee()) {
            @Override
            public void whenChangesOccure() {
                holder.getImgUpdate().setVisibility(View.VISIBLE);
            }
        });

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
    public void itemUpdateClicked(int layoutPosition, double fee, double discount) {
        listListener.itemUpdateClicked(layoutPosition, fee, discount);
    }

    @Override
    public void itemClicked(int layoutPosition) {
        listListener.itemClicked(layoutPosition);
    }

}