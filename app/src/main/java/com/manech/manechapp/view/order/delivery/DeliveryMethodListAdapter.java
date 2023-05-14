package com.manech.manechapp.view.order.delivery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.DeliveryMethod;

import java.util.List;
import java.util.UUID;

public class DeliveryMethodListAdapter extends RecyclerView.Adapter<DeliveryMethodHolder> {
    private List<DeliveryMethod> list;
    private final DeliveryMethodFragment listListener;
    private final UUID deliveryMethodId;

    public DeliveryMethodListAdapter(List<DeliveryMethod> list, DeliveryMethodFragment listListener, UUID deliveryMethodId) {
        this.list = list;
        this.listListener = listListener;
        this.deliveryMethodId = deliveryMethodId;
    }

    @NonNull
    @Override
    public DeliveryMethodHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        return new DeliveryMethodHolder(view,new DeliveryMethodClickListener(){

            @Override
            public void deliveryMethodRadioClicked(int position) {
                listListener.deliveryMethodSelected(position)  ;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.deliverymethod_item_view;
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryMethodHolder holder, int position) {
        DeliveryMethod deliveryMethod=list.get(position);
        holder.getRadioDeliveryMethod().setText(deliveryMethod.getName());
        holder.getRadioDeliveryMethod().setChecked(deliveryMethod.getId()==deliveryMethodId);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public DeliveryMethod getItem(int position) {
        return list.get(position);
    }
}
