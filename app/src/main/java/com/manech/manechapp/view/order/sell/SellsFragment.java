package com.manech.manechapp.view.order.sell;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Order;
import com.manech.manechapp.helper.SecureSharedPref;
import com.manech.manechapp.model.Injection;
import com.manech.manechapp.model.OrderViewModel;
import com.manech.manechapp.view.order.LineClicked;
import com.manech.manechapp.view.order.OrderFragment;
import com.manech.manechapp.view.order.OrderListAdapter;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.manech.manechapp.view.order.OrderFilterCard;

import java.util.List;

public class SellsFragment extends Fragment implements LineClicked {



    private OrderViewModel orderViewModel;



    private RecyclerView sellListRecyclerView;
    private OrderListAdapter sellListAdapter;

    private ProgressDialog mDialog;
    private OrderFilterCard filterCard;
    private TextView tvNoSells;
    private List<Order> allOrders = new java.util.ArrayList<>();

    private SecureSharedPref sharedPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = SecureSharedPref.getInstance(getActivity().getApplicationContext(), getString(R.string.preference_file_key));

        orderViewModel = ((MainActivity) getActivity()).getViewModel(OrderViewModel.class);
        orderViewModel.getOrders().observe(this, new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orders) {
                if (orders != null) {
                    /*
                    double price=0.0;
                    for(Order order: orders){
                        price+=order.getPrice();
                    }*/
                    getActivity().setTitle(getString(R.string.my_sells));

                    allOrders = orders;
                    applyStatusFilter();
                }
                hideProgressDialog();
            }
        });

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sells, container, false);

        sellListRecyclerView = view.findViewById(R.id.order_list_view);
        sellListRecyclerView.setHasFixedSize(true);
        sellListRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        tvNoSells = view.findViewById(R.id.tv_no_sells);
        filterCard = OrderFilterCard.bind(view, this::applyStatusFilter);

        showProgressDialog();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // reload so status changes made in the details screen show up
        orderViewModel.fetchSells(sharedPref.getUUID(getString(R.string.current_user_id), null));
    }

    private void applyStatusFilter() {
        if (filterCard == null || sellListRecyclerView == null) return;
        final List<Order> shown = filterCard.apply(allOrders);
        sellListAdapter = new OrderListAdapter(shown, SellsFragment.this);
        sellListRecyclerView.setAdapter(sellListAdapter);
        tvNoSells.setVisibility(shown.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showProgressDialog() {
        mDialog = new ProgressDialog(getContext());
        mDialog.setTitle(getString(R.string.please_wait));
        mDialog.setMessage(getString(R.string.loading_data));
        mDialog.setCancelable(true);
        mDialog.show();
    }

    private void hideProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_back:
                ((MainActivity) getActivity()).actionBack();
                return true;
            case R.id.action_refresh:
                showProgressDialog();
                orderViewModel.fetchSells(sharedPref.getUUID(getString(R.string.current_user_id), null));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // menu.clear();
        inflater.inflate(R.menu.orders_menu, menu);
      /*  MenuItem searchMenuItem = menu.findItem(R.id.action_search); // todo
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        if(searchView!=null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //adapter.getFilter().filter(newText); todo
                    return true;
                }
            });
        }*/
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void itemClicked(int layoutPosition) {
        final Order order = sellListAdapter.getItem(layoutPosition);
        sharedPref.putUUID(getString(R.string.saved_order_id), order.getId());
        ((MainActivity) getActivity()).initFragment(SellFragment.class, getString(R.string.sell_details_view));
    }
}