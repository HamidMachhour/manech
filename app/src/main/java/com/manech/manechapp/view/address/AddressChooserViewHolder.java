package com.manech.manechapp.view.address;

import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;

public class AddressChooserViewHolder extends RecyclerView.ViewHolder {

    private TextView tvAddress;
    private RadioButton radioDefaultAddress;
    public AddressChooserViewHolder(@NonNull View itemView) {
        super(itemView);
        tvAddress = itemView.findViewById(R.id.tvAddress);
        radioDefaultAddress =itemView.findViewById(R.id.radioDefaultAddress);
    }

    public TextView getTvAddress() {
        return tvAddress;
    }

    public RadioButton getRadioDefaultAddress() {
        return radioDefaultAddress;
    }
}
