package com.manech.manechapp.view.order;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;


public class OrderStatusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final OrderStatusClickListener listener;
    private final TextView tvStatus;
    private final TextView tvDate;

    public OrderStatusViewHolder(@NonNull View itemView, OrderStatusClickListener listener) {
        super(itemView);
        tvStatus = itemView.findViewById(R.id.tvStatus);
        //tvState.setOnClickListener(this);
        tvDate =itemView.findViewById(R.id.tvDate);

        this.listener = listener;
    }


    public TextView getTvStatus() {
        return tvStatus;
    }



    public TextView getTvDate() {
        return tvDate;
    }

    @Override
    public void onClick(View v) {
      /*  switch (v.getId()) {
            case R.id.imgAdd2Cart:
             listener.add2CartClicked(this.getLayoutPosition());
                break;
            case R.id.tvName:
             listener.itemClicked(this.getLayoutPosition());
                break;
            default:
                break;
        }*/
    }
}
