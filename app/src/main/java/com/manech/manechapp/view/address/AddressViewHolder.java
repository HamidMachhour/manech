package com.manech.manechapp.view.address;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;

public class AddressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private final AddressClickListener addressClickListener;
    private final TextView tvAddress;
    private final ImageView imgDelete;
    private final ImageView imgDuplicate;

    public AddressViewHolder(AddressClickListener addressClickListener, @NonNull View itemView) {
        super(itemView);
        this.addressClickListener = addressClickListener;
        tvAddress = itemView.findViewById(R.id.tvAddress);
        tvAddress.setOnClickListener(this);
        imgDuplicate = itemView.findViewById(R.id.imgDuplicate);
        imgDuplicate.setOnClickListener(this);
        imgDelete = itemView.findViewById(R.id.imgDeleteForever);
        imgDelete.setOnClickListener(this);
    }

    public TextView getTvAddress() {
        return tvAddress;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAddress:
                addressClickListener.addressClicked(this.getLayoutPosition());
                break;
            case R.id.imgDuplicate:
                addressClickListener.duplicateClicked(this.getLayoutPosition());
                break;
            case R.id.imgDeleteForever:
                addressClickListener.deleteClicked(this.getLayoutPosition());
                break;
            default:
                break;
        }
    }
}
