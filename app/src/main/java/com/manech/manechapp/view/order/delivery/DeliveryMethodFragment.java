package com.manech.manechapp.view.order.delivery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.DeliveryMethod;
import com.manech.manechapp.helper.SecureSharedPref;
import com.manech.manechapp.model.OrderViewModel;
import com.manech.manechapp.model.Injection;

import java.util.List;
import java.util.UUID;

public class DeliveryMethodFragment extends DialogFragment {
    private RecyclerView recyclerView;
    private DeliveryMethodListAdapter adapter;
    private OrderViewModel orderViewModel;
    private  DeliveryMethodSelectListener listener;
    private static DeliveryMethodFragment instance;
    private SecureSharedPref sharedPref;
    public static DeliveryMethodFragment getInstance(){
        if(instance==null) {
            instance = new DeliveryMethodFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = SecureSharedPref.getInstance(getActivity().getApplicationContext(), getString(R.string.preference_file_key));
        final UUID deliveryMethodId=sharedPref.getUUID(getString(R.string.deliveryMethodId),null);
        orderViewModel = ((MainActivity) getActivity()).getViewModel(OrderViewModel.class);
        orderViewModel.getDeliveryMethods().observe(this, new Observer<List<DeliveryMethod>>() {
            @Override
            public void onChanged(List<DeliveryMethod> deliveryMethods) {

                adapter =new DeliveryMethodListAdapter(deliveryMethods,DeliveryMethodFragment.this, deliveryMethodId);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delivery_methods, container, false);

        recyclerView=view.findViewById(R.id.method_list_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        orderViewModel.fetchDeliveryMethods();
        return view;
    }

    public void deliveryMethodSelected(int position) {
        DeliveryMethod deliveryMethod= adapter.getItem(position);
        listener.onSelect(deliveryMethod);
    }

    public void setListener(DeliveryMethodSelectListener deliveryMethodSelectListener) {
        listener=deliveryMethodSelectListener;
    }
}
