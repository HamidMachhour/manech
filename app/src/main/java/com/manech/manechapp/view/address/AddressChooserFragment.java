package com.manech.manechapp.view.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InlineSuggestionsRequest;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.User;
import com.manech.manechapp.helper.Converters;
import com.manech.manechapp.model.AddressViewModel;
import com.manech.manechapp.model.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddressChooserFragment extends DialogFragment implements View.OnClickListener {
    private static AddressChooserFragment instance;

    private UserViewModel userViewModel;

    private RecyclerView addressListView;
    private AddressChooserAdapter adapter;

    private Address selectedAddress = null;

    private UUID selectedAddressId = null;
    private AddressChooserListener listener;

    public AddressChooserFragment() {
        // Required empty public constructor
        super();
    }

    public static AddressChooserFragment newInstance(UUID selectedAddressId, String dialogTitle) {
        if (instance == null)
            instance = new AddressChooserFragment();
        Bundle args = new Bundle();
        args.putString("selectedAddressId", Converters.uuidToString(selectedAddressId));
        args.putString("dialogTitle",dialogTitle);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = ((MainActivity) getActivity()).getViewModel(UserViewModel.class);
        userViewModel.getUserMutableLiveData().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (isResumed() && isVisible() && user != null && user.getUserAddresses()!=null) {
                    adapter.getAddressList().clear();
                    adapter.getAddressList().addAll(user.getUserAddresses());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        selectedAddress = adapter.getItem(addressListView.getChildLayoutPosition(v));
        adapter.setDefaultAddress(selectedAddress.getId());
        adapter.notifyDataSetChanged();
        listener.onSelect(selectedAddress);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_select, container, false);
        addressListView = view.findViewById(R.id.address_list_view);

        Bundle args=getArguments();
        if (args!= null) {
            selectedAddressId = Converters.fromString(args.getString("selectedAddressId"));
            ((TextView)view.findViewById(R.id.tvTitle))
                    .setText(args.getString("dialogTitle",getString(R.string.select_address)));
        }

        addressListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AddressChooserAdapter(new ArrayList<>(), this, selectedAddressId);
        addressListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        userViewModel.fetchCurrentUser(false,false,true,false);
    }

    public AddressChooserListener getListener() {
        return listener;
    }

    public void setListener(AddressChooserListener listener) {
        this.listener = listener;
    }


}