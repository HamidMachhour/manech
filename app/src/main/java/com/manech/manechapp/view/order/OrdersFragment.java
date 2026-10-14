package com.manech.manechapp.view.order;

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

import java.util.List;

public class OrdersFragment extends Fragment implements LineClicked{

    private OrderViewModel orderViewModel;



    private RecyclerView orderListRecyclerView;
    private OrderListAdapter orderListAdapter;

    private ProgressDialog mDialog;
    private OrderFilterCard filterCard;
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
                   /* double price=0.0;
                    for(Order order: orders){
                        price+=order.getPrice();
                    }*/
                    getActivity().setTitle(getString(R.string.my_orders));

                    allOrders = orders;
                    applyFilter();
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
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        orderListRecyclerView = view.findViewById(R.id.order_list_view);
        orderListRecyclerView.setHasFixedSize(true);
        orderListRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        filterCard = OrderFilterCard.bind(view, this::applyFilter);

        showProgressDialog();
        orderViewModel.fetchOrders(sharedPref.getUUID(getString(R.string.current_user_id), null));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideProgressDialog();
    }


    private void applyFilter() {
        if (filterCard == null || orderListRecyclerView == null) return;
        orderListAdapter = new OrderListAdapter(filterCard.apply(allOrders), OrdersFragment.this);
        orderListRecyclerView.setAdapter(orderListAdapter);
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
                orderViewModel.fetchOrders(sharedPref.getUUID(getString(R.string.current_user_id), null));
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
        final Order order = orderListAdapter.getItem(layoutPosition);
        sharedPref.putUUID(getString(R.string.saved_order_id), order.getId());
        ((MainActivity) getActivity()).initFragment(OrderFragment.class, getString(R.string.order_details_view));
    }
}