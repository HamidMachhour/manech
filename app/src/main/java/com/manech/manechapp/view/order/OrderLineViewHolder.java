package com.manech.manechapp.view.order;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;


public class OrderLineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final OrderLineClickListener listener;
    private final TextView tvName;
    private final TextView tvPrice;
    private final TextView tvFee;
    private final  TextView tvSubTotal;
    private final ImageView imgAdd2Cart;


    public OrderLineViewHolder(@NonNull View itemView, OrderLineClickListener listener) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        tvName.setOnClickListener(this);

        tvPrice=itemView.findViewById(R.id.tvPrice);
        tvFee=itemView.findViewById(R.id.tvFee);
        tvSubTotal=itemView.findViewById(R.id.tvSubTotal);


        imgAdd2Cart = itemView.findViewById(R.id.imgAdd2Cart);
        imgAdd2Cart.setOnClickListener(this);

        this.listener = listener;
    }


    public TextView getTvName() {
        return tvName;
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
            case R.id.imgAdd2Cart:
             listener.add2CartClicked(this.getLayoutPosition());
                break;
            case R.id.tvName:
             listener.itemClicked(this.getLayoutPosition());
                break;
            default:
                break;
        }
    }
}
