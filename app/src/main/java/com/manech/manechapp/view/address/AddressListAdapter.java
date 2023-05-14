package com.manech.manechapp.view.address;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressListAdapter extends RecyclerView.Adapter<AddressViewHolder>  implements Filterable {
    private final List<Address> addressList;
    private final List<Address> allAddresses;

    private final AddressesFragment parentListener;

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Address> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(allAddresses);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Address address : allAddresses) {
                    if (address.toString().toLowerCase().trim().contains(filterPattern)) {
                        filteredList.add(address);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            addressList.clear();
            addressList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public List<Address> getAddressList() {
        return addressList;
    }

    public AddressListAdapter(List<Address> addressList, AddressesFragment parentListener) {

        this.addressList = addressList;
        this.allAddresses=new ArrayList<>(addressList);
        this.parentListener = parentListener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new AddressViewHolder(new AddressClickListener() {
            @Override
            public void duplicateClicked(int layoutPosition) {
                parentListener.duplicateItem(layoutPosition);
            }

            @Override
            public void deleteClicked(int layoutPosition) {
                parentListener.deleteItem(layoutPosition);
            }

            @Override
            public void addressClicked(int layoutPosition) {
                parentListener.addressClicked(layoutPosition);
            }
        }, view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.getTvAddress().setText(address.toString());
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.address_item_view;
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }


    public Address getItem(int position) {
        return addressList.get(position);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
}
