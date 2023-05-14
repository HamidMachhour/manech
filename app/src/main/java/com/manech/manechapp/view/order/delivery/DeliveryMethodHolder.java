package com.manech.manechapp.view.order.delivery;

import android.view.View;
import android.widget.RadioButton;

import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;

public class DeliveryMethodHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
    private final DeliveryMethodClickListener listener;
    private  final RadioButton radioDeliveryMethod;
    public DeliveryMethodHolder(View view, DeliveryMethodClickListener deliveryMethodClickListener) {
        super(view);
        listener=deliveryMethodClickListener;
        radioDeliveryMethod =view.findViewById(R.id.radioDeliveryMethod);
        radioDeliveryMethod.setOnClickListener(this);

    }

    public RadioButton getRadioDeliveryMethod() {
        return radioDeliveryMethod;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.radioDeliveryMethod:
                listener.deliveryMethodRadioClicked(this.getLayoutPosition());
                break;
        }

    }
}
