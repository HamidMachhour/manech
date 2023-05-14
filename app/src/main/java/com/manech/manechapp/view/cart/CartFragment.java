package com.manech.manechapp.view.cart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.CartItem;
import com.manech.manechapp.data.Cart;
import com.manech.manechapp.data.DeliveryMethod;
import com.manech.manechapp.data.Order;
import com.manech.manechapp.helper.SecureSharedPref;
import com.manech.manechapp.model.CartViewModel;
import com.manech.manechapp.model.Injection;
import com.manech.manechapp.model.ItemViewModel;
import com.manech.manechapp.model.OrderViewModel;
import com.manech.manechapp.model.UserViewModel;
import com.manech.manechapp.view.address.AddressChooserFragment;
import com.manech.manechapp.view.address.AddressChooserListener;
import com.manech.manechapp.view.item.ItemsFragment;
import com.manech.manechapp.view.order.OrdersFragment;
import com.manech.manechapp.view.order.delivery.DeliveryMethodFragment;
import com.manech.manechapp.view.order.delivery.DeliveryMethodSelectListener;
import com.manech.manechapp.view.item.ItemFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CartFragment extends Fragment {

    private View.OnClickListener onClickListener;

    private Cart currentCart;
    // For highlight action
    private List<Integer> locations;

    private CartViewModel cartViewModel;


    private TextView tvTotalPrice;
    private Button btnOrder;
    private ImageView imgHighlight;

    private RecyclerView cartRecyclerView;
    private CartListAdapter adapter;

    private UUID defaultStoreId; // my default store
    private UUID owner;

    private SecureSharedPref sharedPref;

    private ProgressDialog mDialog;

    private String taost_message ="";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = SecureSharedPref.getInstance(getActivity().getApplicationContext(), getString(R.string.preference_file_key));
        cartViewModel=((MainActivity)getActivity()).getViewModel(CartViewModel.class);

        cartViewModel.getCartMutableLiveData().observe(this, new Observer<Cart>() {

            @Override
            public void onChanged(Cart cart) {
                if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {

                    hideProgressDialog();
                    ((MainActivity) getActivity()).initFragment(ItemsFragment.class, getString(R.string.items_buy_mode));

                } else {
                    currentCart=cart;
                    tvTotalPrice.setText(cart.toString());
                    //update recycleview
                    adapter = new CartListAdapter(cart.getCartItems(), CartFragment.this);
                    cartRecyclerView.setAdapter(adapter);
                    //update locations list
                    locations = listLocations(cart.getCartItems());
                    imgHighlight.setVisibility(locations == null || locations.isEmpty() ? View.GONE : View.VISIBLE);
                    btnOrder.setVisibility((cart.getCartItems() == null || cart.getCartItems().isEmpty()) ? View.GONE : View.VISIBLE);

                }
                hideProgressDialog();
            }

        });
        cartViewModel.getBooleanMutableLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b) {

                    Toast.makeText(getContext(), taost_message, Toast.LENGTH_SHORT).show();
                    cartViewModel.fetchCart(owner);
                }else{
                    Toast.makeText(getContext(), getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
                }

                hideProgressDialog();
            }
        });

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnOrder:
                        selectDeliveryMethod();
                        break;
                    case R.id.imgHighlight:
                        highlightLocations();
                        break;
                }
            }
        };

    }


    private void selectDeliveryMethod() {
        DeliveryMethodFragment dialog = DeliveryMethodFragment.getInstance();
        dialog.setListener(new DeliveryMethodSelectListener() {
            @Override
            public void onSelect(DeliveryMethod dm) {
                sharedPref.putUUID(getString(R.string.deliveryMethodId), dm.getId());
                dialog.dismiss();
                if (dm.getName().equals(DeliveryMethod.POS)) {
                    sharedPref.remove(getString(R.string.deliveryAddressId));
                    sharedPref.remove(getString(R.string.billingAddressId));
                    confirmOrder();
                } else {
                    selectDeliveryAddress();
                }
            }
        });
        dialog.show(getActivity().getSupportFragmentManager(), DeliveryMethodFragment.class.getName());
    }

    private void selectDeliveryAddress() {
        UUID delivery_address_id = sharedPref.getUUID(getString(R.string.deliveryAddressId), null);
        AddressChooserFragment fragment = AddressChooserFragment
                .newInstance(delivery_address_id, getString(R.string.select_delivery_address));
        fragment.setListener(new AddressChooserListener() {
            @Override
            public void onSelect(Address selectedAddress) {
                sharedPref.putUUID(getString(R.string.deliveryAddressId), selectedAddress.getId());
                fragment.dismiss();
                selectBillingAddress();
            }
        });
        fragment.show(getActivity().getSupportFragmentManager(), getString(R.string.address_select_dialog));

    }

    private void selectBillingAddress() {
        UUID billing_address_id = sharedPref.getUUID(getString(R.string.billingAddressId), null);
        AddressChooserFragment fragment = AddressChooserFragment
                .newInstance(billing_address_id, getString(R.string.select_billing_address));
        fragment.setListener(new AddressChooserListener() {
            @Override
            public void onSelect(Address selectedAddress) {
                sharedPref.putUUID(getString(R.string.billingAddressId), selectedAddress.getId());
                fragment.dismiss();
                confirmOrder();
            }
        });
        fragment.show(getActivity().getSupportFragmentManager(), getString(R.string.address_select_dialog));
    }

    private void confirmOrder() {
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.make_new_order))
                .setMessage(R.string.make_new_order_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        showProgressDialog();
                        UUID method_id = sharedPref.getUUID(getString(R.string.deliveryMethodId), null);
                        UUID delivery_id = sharedPref.getUUID(getString(R.string.deliveryAddressId), null);
                        UUID billing_id = sharedPref.getUUID(getString(R.string.billingAddressId), null);
                        Order order = new Order(owner, method_id == null ? null : method_id,
                                delivery_id == null ? null : delivery_id,
                                billing_id == null ? null : billing_id,
                                0.0, 0.0, new Date(), null, null);
                        taost_message=getString(R.string.order_success);
                        cartViewModel.cartToOrder(currentCart, order);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void highlightLocations() {
        if (locations == null || locations.isEmpty()) return;
        Integer indexes[]  =new Integer[locations.size()];
        locations.toArray(indexes);
        ((MainActivity) getActivity()).actionHighlight(indexes);
    }

    private List<Integer> listLocations(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty())
            return null;
        final ArrayList<Integer> l = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem().getStore().equals(defaultStoreId) && cartItem.getItem().getLocation() != null)
                l.add(cartItem.getItem().getLocation());
        }
        return l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.manage_cart);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecyclerView = view.findViewById(R.id.cart_list_view);
        //cartRecyclerView.setHasFixedSize(true);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);

        btnOrder = view.findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(onClickListener);

        imgHighlight = view.findViewById(R.id.imgHighlight);
        imgHighlight.setOnClickListener(onClickListener);

        showProgressDialog();

        defaultStoreId = sharedPref.getUUID(getString(R.string.defaultStore), null);
        owner = sharedPref.getUUID(getString(R.string.current_user_id), null);

        cartViewModel.fetchCart(owner);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideProgressDialog();
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
                cartViewModel.fetchCart(owner);
                return true;
            case R.id.action_delete:
                clearCart();
                return true;
            case R.id.action_manage_orders:
                ((MainActivity) getActivity()).initFragment(OrdersFragment.class, getString(R.string.orderlist_view));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearCart() {
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete_cart))
                .setMessage(R.string.delete_cart_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        showProgressDialog();
                        taost_message=getString(R.string.cart_clear_success);
                        cartViewModel.clearCart(currentCart.getId());
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // menu.clear();
        inflater.inflate(R.menu.cart_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void deleteClicked(int p) {
        CartItem cartItem = adapter.getItem(p);
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete_single_cartItem))
                .setMessage(R.string.delete_cartitem_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        showProgressDialog();
                        taost_message=getString(R.string.removed_cartitem_success);
                        cartViewModel.deleteCartItem(cartItem);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void updateClicked(int layoutPosition, double quantity) {
        if(quantity<=0){
            deleteClicked(layoutPosition);
            return;
        }
        showProgressDialog();
        final CartItem cartItem = adapter.getItem(layoutPosition);
        cartItem.setQuantity(quantity);
        taost_message=getString(R.string.changes_updated);
        cartViewModel.upsertCartItem(cartItem, false);
    }

    public void itemClicked(int layoutPosition) {
        final CartItem cartItem = adapter.getItem(layoutPosition);
        sharedPref.putUUID(getString(R.string.item_id), cartItem.getItemId());
        ((MainActivity) getActivity()).initFragment(ItemFragment.class, getString(R.string.view_item_buy_mode));
    }
}