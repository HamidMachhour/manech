package com.manech.manechapp.view.cart;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;


public class CartItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final CartItemClickListener listener;
    private final TextView tvName;
    private final TextView tvPrice;
    private final TextView tvFee;
    private final  TextView tvSubTotal;
    private final TextView txtQuantity;
    private final ImageView imgSave;
    private final ImageView imgDelete;


    public CartItemViewHolder(@NonNull View itemView, CartItemClickListener listener) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        tvName.setOnClickListener(this);

        tvPrice=itemView.findViewById(R.id.tvPrice);
        tvFee=itemView.findViewById(R.id.tvFee);
        tvSubTotal=itemView.findViewById(R.id.tvSubTotal);

        txtQuantity = itemView.findViewById(R.id.txtQuantity);

        imgSave = itemView.findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgDelete = itemView.findViewById(R.id.imgDeleteForever);
        imgDelete.setOnClickListener(this);

        this.listener = listener;
    }


    public TextView getTvName() {
        return tvName;
    }


    public TextView getTxtQuantity() {
        return txtQuantity;
    }

    public ImageView getImgSave() {
        return imgSave;
    }

    public ImageView getImgDelete() {
        return imgDelete;
    }

    public TextView getTvPrice() {
        return tvPrice;
    }

    public TextView getTvFee() {
        return tvFee;
    }

    public TextView getTvSubTotal() {
        return tvSubTotal;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgSave:
                listener.updateQtyClicked(this.getLayoutPosition());
                break;
            case R.id.imgDeleteForever:
                listener.deleteClicked(this.getLayoutPosition());
                break;
            case R.id.tvName:
                listener.itemClicked(this.getLayoutPosition());
                break;
            default:
                break;
        }
    }
}
