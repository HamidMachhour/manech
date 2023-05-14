package com.manech.manechapp.view.address;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddressChooserAdapter extends RecyclerView.Adapter<AddressChooserViewHolder> {
    private final List<Address> addressList;

    private final View.OnClickListener listener;
    private UUID defaultAddress;

    public AddressChooserAdapter(List<Address> addressList, View.OnClickListener listener, UUID defaultAddress) {

        this.addressList = addressList;
        this.listener = listener;
        this.defaultAddress = defaultAddress;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    @NonNull
    @Override
    public AddressChooserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        view.setOnClickListener(listener);
        return new AddressChooserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressChooserViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.getTvAddress().setText(address.toString());
        holder.getRadioDefaultAddress().setChecked( address.getId().equals(defaultAddress));
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.address_chooser_item_view;
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }


    public Address getItem(int position) {
        return addressList.get(position);
    }

    public void setDefaultAddress(UUID defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

}
