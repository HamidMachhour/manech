package com.manech.manechapp.view.order.sell;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;
import com.manech.manechapp.helper.MyUtils;


public class SellLineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final SellsClickListener listener;
    private final TextView tvName;
    private final TextView tvPrice;
    private final EditText txtFee;
    private final TextView tvSubTotal;
    private final EditText txtDiscount;
    private final ImageView imgUpdate;


    public SellLineViewHolder(@NonNull View itemView, SellsClickListener listener) {
        super(itemView);

        tvName = itemView.findViewById(R.id.tvName);
        tvName.setOnClickListener(this);

        tvPrice = itemView.findViewById(R.id.tvPrice);

        txtFee = itemView.findViewById(R.id.txtFee);
        tvSubTotal = itemView.findViewById(R.id.tvSubTotal);

        txtDiscount = itemView.findViewById(R.id.txtDiscount);

        imgUpdate = itemView.findViewById(R.id.imgUpdate);
        imgUpdate.setOnClickListener(this);
        this.listener = listener;

    }

    public ImageView getImgUpdate() {
        return imgUpdate;
    }

    public EditText getTxtDiscount() {
        return txtDiscount;
    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvPrice() {
        return tvPrice;
    }

    public EditText getTxtFee() {
        return txtFee;
    }

    public TextView getTvSubTotal() {
        return tvSubTotal;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvName:
                listener.itemClicked(this.getLayoutPosition());
                break;
            case R.id.imgUpdate:
                listener.itemUpdateClicked(this.getLayoutPosition(),
                        MyUtils.parseDouble(txtFee.getText().toString()),
                        MyUtils.parseDouble(txtDiscount.getText().toString()));
                imgUpdate.setVisibility(View.GONE);
                break;
            default:


        }
    }
}
