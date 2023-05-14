package com.manech.manechapp.view.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.CartItem;
import com.manech.manechapp.helper.MyUtils;

import java.util.ArrayList;
import java.util.List;

public class CartListAdapter extends RecyclerView.Adapter<CartItemViewHolder> implements Filterable {

    private final CartFragment listListener;
    private List<CartItem> cartItems;
    private List<CartItem> allCartItems;
    private Filter cartFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CartItem> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(allCartItems);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CartItem cartItem : allCartItems) {
                    String text = cartItem.getItem().toString();
                    if (text.toLowerCase().trim().contains(filterPattern)) {
                        filteredList.add(cartItem);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            cartItems.clear();
            cartItems.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public CartListAdapter(List<CartItem> cartItems, CartFragment listener) {
        this.cartItems = cartItems;
        this.allCartItems = new ArrayList<>(cartItems);

        this.listListener = listener;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.cart_item_view;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new CartItemViewHolder(view, new CartItemClickListener() {

            @Override
            public void updateQtyClicked(int p) {
                final TextView txtQty = view.findViewById(R.id.txtQuantity);
                listListener.updateClicked(p, MyUtils.parseDouble(txtQty.getText().toString()));
            }

            @Override
            public void deleteClicked(int p) {
                listListener.deleteClicked(p);
            }

            @Override
            public void itemClicked(int layoutPosition) {
                listListener.itemClicked(layoutPosition);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.getTvName().setText(cartItem.getItem().getTitle());
        holder.getTvPrice().setText(cartItem.getItem().getPrice()+"");
        holder.getTvFee().setText(cartItem.getItem().getShipping()+"");
        holder.getTxtQuantity().setText(cartItem.getQuantity()+"");
        holder.getTvSubTotal().setText(""+((cartItem.getItem().getShipping()+cartItem.getItem().getPrice())*cartItem.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    @Override
    public Filter getFilter() {
        return cartFilter;
    }

    public CartItem getItem(int position) {
        return cartItems.get(position);
    }

}