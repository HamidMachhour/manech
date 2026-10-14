package com.manech.manechapp.view.order.sell;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.DeliveryMethod;
import com.manech.manechapp.data.Order;
import com.manech.manechapp.data.OrderLine;
import com.manech.manechapp.helper.Converters;
import com.manech.manechapp.helper.MyUtils;
import com.manech.manechapp.helper.SecureSharedPref;
import com.manech.manechapp.model.Injection;
import com.manech.manechapp.model.ItemViewModel;
import com.manech.manechapp.model.OrderViewModel;
import com.manech.manechapp.view.item.ItemFragment;
import com.manech.manechapp.view.item.ItemsFragment;

import net.glxn.qrgen.android.QRCode;

import android.widget.Button;
import android.widget.LinearLayout;
import com.manech.manechapp.data.Status;

import java.util.ArrayList;
import java.util.List;

public class SellFragment extends Fragment implements SellsClickListener {

    private View.OnClickListener onClickListener;


    private OrderViewModel orderViewModel;

    //private TextView tvOrderId;
    private TextView tvOrderDate;
    private EditText txtOrderFee;
    private EditText txtOrderDiscount;
    private TextView tvTotalPrice;
    private TextView tvCurrentState;
    private TextView tvDeliveryMethod;
    private TextView tvDeliveryAddress;
    private TextView tvBillingAddress;

    private ImageView imgHighlight;
    // For highlight action
    private List<Integer> locations;

    private ViewGroup deliveryMethodLayout;
    private ViewGroup deliveryAddressLayout;
    private ViewGroup billingAddressLayout;

    private ImageView imgArrow1, imgArrow2, imgArrow3;
    private ViewGroup orderGeneralView, orderLinesView, orderStatusView;


    private RecyclerView sellLinesRecyclerView;
    private SellLineListAdapter sellLineListAdapter;

    private RecyclerView selledOrderStatusRecyclerView;
    private SelledOrderStatusListAdapter selledOrderStatusListAdapter;

    private SecureSharedPref sharedPref;

    private ProgressDialog mDialog;

    private Order currentOrder;
    private ViewGroup statusActionsLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = SecureSharedPref.getInstance(getActivity().getApplicationContext(), getString(R.string.preference_file_key));

        orderViewModel = ((MainActivity) getActivity()).getViewModel(OrderViewModel.class);
        orderViewModel.getCurrentOrder().observe(this, new Observer<Order>() {
            @Override
            public void onChanged(Order order) {
                if (order != null) {

                    currentOrder = order;

                    getActivity().setTitle(getString(R.string.sell_details));

                    //tvOrderId.setText(order.getId() + "");
                    tvOrderDate.setText(Converters.fromDate(order.getCreateAt()));
                    txtOrderFee.setText(order.getFee() + "");
                    txtOrderDiscount.setText(order.getDiscount()+"");

                    locations = listLocations(order.getOrderLineList());
                    imgHighlight.setVisibility(locations == null || locations.isEmpty() ? View.GONE : View.VISIBLE);

                    final DeliveryMethod deliveryMethod = order.getDeliveryMethod();

                    if (deliveryMethod != null) {
                        tvDeliveryMethod.setText(deliveryMethod.getName());
                        deliveryMethodLayout.setVisibility(View.VISIBLE);
                    } else {
                        tvDeliveryMethod.setText(getString(R.string.nothing_is_set_yet));
                        deliveryMethodLayout.setVisibility(View.GONE);
                    }

                    final Address deliveryAddress = order.getDeliveryAddress();
                    if (deliveryAddress != null) {
                        tvDeliveryAddress.setText(deliveryAddress.toString());
                        deliveryAddressLayout.setVisibility(View.VISIBLE);

                    } else {
                        tvDeliveryAddress.setText(getString(R.string.nothing_is_set_yet));
                        deliveryAddressLayout.setVisibility(View.GONE);
                    }

                    final Address billingAddress = order.getBillingAddress();
                    if (billingAddress != null) {
                        tvBillingAddress.setText(billingAddress.toString());
                        billingAddressLayout.setVisibility(View.VISIBLE);

                    } else {
                        tvBillingAddress.setText(getString(R.string.nothing_is_set_yet));
                        billingAddressLayout.setVisibility(View.GONE);
                    }

                    tvTotalPrice.setText(order.getPrice() + "");

                    tvCurrentState.setText(order.getOrderStatusList().get(0).getStatus()
                            .getLocalizedName(requireContext()));
                    buildStatusActions(order.getOrderStatusList().get(0).getStatus());

                    sellLineListAdapter = new SellLineListAdapter(order.getOrderLineList(), SellFragment.this);
                    sellLinesRecyclerView.setAdapter(sellLineListAdapter);


                    selledOrderStatusListAdapter = new SelledOrderStatusListAdapter(order.getOrderStatusList(), SellFragment.this);
                    selledOrderStatusRecyclerView.setAdapter(selledOrderStatusListAdapter);
                }
                hideProgressDialog();
            }
        });

        orderViewModel.getBooleanMutableLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(getContext(), getString(R.string.changes_updated), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
                }
            }
        });

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    /*case R.id.imgCommentOrder:
                        //TODO
                        break;*/
                    case R.id.imgHighlight:
                        highlightLocations();
                        break;
                    case R.id.imgArrow1:
                        collapseCardView(imgArrow1, orderGeneralView);
                        break;
                    case R.id.imgArrow2:
                        collapseCardView(imgArrow2, orderLinesView);
                        break;
                    case R.id.imgArrow3:
                        collapseCardView(imgArrow3, orderStatusView);
                        break;
                }
            }
        };
    }

    private void buildStatusActions(Status current) {
        statusActionsLayout.removeAllViews();
        final Status[] next = current.nextStatuses();
        statusActionsLayout.setVisibility(next.length == 0 ? View.GONE : View.VISIBLE);
        for (Status status : next) {
            final Button button = new Button(getContext());
            button.setText(getString(R.string.mark_as, status.getLocalizedName(requireContext())));
            button.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            button.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                    .setMessage(getString(R.string.confirm_status_change,
                            status.getLocalizedName(requireContext())))
                    .setPositiveButton(android.R.string.ok, (d, w) -> {
                        orderViewModel.changeOrderStatus(currentOrder.getId(), status);
                        Toast.makeText(getContext(), R.string.status_updated, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show());
            statusActionsLayout.addView(button);
        }
    }

    private void updateOrder() {
        if(currentOrder!=null) {
            currentOrder.setFee(Double.parseDouble(txtOrderFee.getText().toString()));
            currentOrder.setDiscount(Double.parseDouble(txtOrderDiscount.getText().toString()));
            orderViewModel.upsertOrder(currentOrder);
        }
    }



    private void collapseCardView(ImageView img, ViewGroup viewGroup) {
        viewGroup.setVisibility(viewGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        img.setImageResource(viewGroup.getVisibility() == View.GONE ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_up);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sell, container, false);

        sellLinesRecyclerView = view.findViewById(R.id.orderline_list_view);
        sellLinesRecyclerView.setHasFixedSize(true);
        sellLinesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        selledOrderStatusRecyclerView = view.findViewById(R.id.orderState_list_view);
        selledOrderStatusRecyclerView.setHasFixedSize(true);
        selledOrderStatusRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        //tvOrderId = view.findViewById(R.id.tvOrderId);
        tvOrderDate = view.findViewById(R.id.tvOrderDate);
        txtOrderFee = view.findViewById(R.id.txtOrderFee);
        txtOrderDiscount = view.findViewById(R.id.txtOrderDiscount);

        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        tvCurrentState = view.findViewById(R.id.tvOrderStatus);
        statusActionsLayout = view.findViewById(R.id.sellStatusActions);

        imgHighlight = view.findViewById(R.id.imgHighlight);
        imgHighlight.setOnClickListener(onClickListener);

        tvDeliveryMethod = view.findViewById(R.id.tvDeliveryMethod);
        tvDeliveryAddress = view.findViewById(R.id.tvDeliveryAddress);
        tvBillingAddress = view.findViewById(R.id.tvBillingAddress);
        deliveryMethodLayout = view.findViewById(R.id.orderDeliveryMethodView);
        deliveryAddressLayout = view.findViewById(R.id.orderDeliveryAddressView);
        billingAddressLayout = view.findViewById(R.id.orderBillingAddressView);

        imgArrow1 = view.findViewById(R.id.imgArrow1);
        imgArrow2 = view.findViewById(R.id.imgArrow2);
        imgArrow3 = view.findViewById(R.id.imgArrow3);

        imgArrow1.setOnClickListener(onClickListener);
        imgArrow2.setOnClickListener(onClickListener);
        imgArrow3.setOnClickListener(onClickListener);

        orderGeneralView = view.findViewById(R.id.orderGeneralView);
        orderLinesView = view.findViewById(R.id.orderLinesView);
        orderStatusView = view.findViewById(R.id.orderStatesView);

        orderGeneralView.setVisibility(View.GONE);
        orderLinesView.setVisibility(View.VISIBLE);
        orderStatusView.setVisibility(View.GONE);

        showProgressDialog();

        orderViewModel.fetchOrder(sharedPref.getUUID(getString(R.string.saved_order_id), null));
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
                orderViewModel.fetchOrder(currentOrder.getId());
                return true;
            case R.id.action_save:
                updateOrder();
                return true;
            case R.id.action_showQRCode:
                showQRCode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showQRCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(R.string.print, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.alert_dialog_image, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                ImageView image = (ImageView) dialog.findViewById(R.id.imgDialog);
                Bitmap myBitmap = QRCode.from("Bonjour hamid").bitmap();
                image.setImageBitmap(myBitmap);
            }
        });

        dialog.show();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.clear();
        inflater.inflate(R.menu.sell_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    sellLineListAdapter.getFilter().filter(newText);
                    return true;
                }
            });

            // Set the OnSearchClickListener for the search view
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the EditText view from the search view
                    EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

                    // Change the text color of the EditText
                    searchEditText.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
                }
            });
        }

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void itemUpdateClicked(int layoutPosition, double fee, double discount) {
        final OrderLine orderLine = sellLineListAdapter.getItem(layoutPosition);
        orderLine.setFee(fee);
        orderLine.setDiscount(discount);
        //orderViewModel.upsertOrderline(orderLine);
    }

    @Override
    public void itemClicked(int layoutPosition) {
        final OrderLine orderLine = sellLineListAdapter.getItem(layoutPosition);
        sharedPref.putUUID(getString(R.string.item_id), orderLine.getItemId());
        sharedPref.putInt(getString(R.string.items_view_mode), ItemsFragment.BUY_MODE);

        ((MainActivity) getActivity()).initFragment(ItemFragment.class, getString(R.string.view_item_buy_mode));
    }

    private void highlightLocations() {
        if (locations == null || locations.isEmpty()) return;
        Integer indexes[]  =new Integer[locations.size()];
        locations.toArray(indexes);
        ((MainActivity) getActivity()).actionHighlight(indexes);
    }


    private List<Integer> listLocations(List<OrderLine> orderLines) {
        if (orderLines == null || orderLines.isEmpty())
            return null;
        final ArrayList<Integer> l = new ArrayList<>();
        for (OrderLine orderLine : orderLines) {
            if (orderLine.getItem().getLocation() != null)
                l.add(orderLine.getItem().getLocation());
        }
        return l;
    }
}