package com.manech.manechapp.view.order;

import android.app.ProgressDialog;
import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintManager;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.manech.manechapp.data.Order;
import com.manech.manechapp.data.OrderLine;
import com.manech.manechapp.data.DeliveryMethod;
import com.manech.manechapp.data.OrderStatus;
import com.manech.manechapp.helper.Converters;
import com.manech.manechapp.helper.MyUtils;
import com.manech.manechapp.helper.SecureSharedPref;
import com.manech.manechapp.model.ItemViewModel;
import com.manech.manechapp.model.OrderViewModel;
import com.manech.manechapp.model.Injection;
import com.manech.manechapp.view.item.ItemFragment;
import com.manech.manechapp.view.item.ItemsFragment;

import net.glxn.qrgen.android.QRCode;

import java.util.List;

public class OrderFragment extends Fragment implements  OrderLineClickListener{

    private View.OnClickListener onClickListener;


    private OrderViewModel orderViewModel;

    //private TextView tvOrderId;
    private TextView tvOrderDate;
    private TextView tvFee;
    private TextView tvDiscount;
    private TextView tvTotalPrice;
    private TextView tvCurrentState;

    private TextView tvDeliveryMethod;
    private TextView tvDeliveryAddress;
    private TextView tvBillingAddress;

    private ViewGroup deliveryMethodLayout;
    private ViewGroup deliveryAddressLayout;
    private ViewGroup billingAddressLayout;

    private ImageView imgArrow1, imgArrow2, imgArrow3;
    private ViewGroup orderGeneralView, orderLinesView, orderStatusView;


    private RecyclerView orderLinesRecyclerView;
    private OrderLineListAdapter orderLineListAdapter;

    private RecyclerView orderStatusRecyclerView;
    private OrderStatusListAdapter orderStatusListAdapter;

    private SecureSharedPref sharedPref;

    private ProgressDialog mDialog;

    private Order currentOrder;
    private ImageView imgCancelOrder;


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

                    getActivity().setTitle(getString(R.string.order_details));

                   // tvOrderId.setText(order.getId() + "");
                    tvOrderDate.setText(Converters.fromDate(order.getCreateAt()));
                    tvFee.setText(order.getFee() + "");
                    tvDiscount.setText(order.getDiscount() + "");

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

                    List<OrderStatus> statuses = order.getOrderStatusList();
                    tvCurrentState.setText(statuses == null || statuses.isEmpty()
                            ? getString(R.string.status_unknown)
                            : statuses.get(0).getStatus().getLocalizedName(requireContext()));

                    imgCancelOrder.setVisibility(statuses != null && !statuses.isEmpty()
                            && statuses.get(0).getStatus() == com.manech.manechapp.data.Status.PENDING
                            ? View.VISIBLE : View.GONE);

                    orderLineListAdapter = new OrderLineListAdapter(order.getOrderLineList(), OrderFragment.this);
                    orderLinesRecyclerView.setAdapter(orderLineListAdapter);


                    orderStatusListAdapter = new OrderStatusListAdapter(order.getOrderStatusList(), OrderFragment.this);
                    orderStatusRecyclerView.setAdapter(orderStatusListAdapter);
                    // tvStore.setText(order.getStore() + ""); // TODO view store name instead

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
/*
        orderViewModel.getOrderStatusId().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long insertedId) {
                if(insertedId!=null){
                    orderViewModel.fetchOrder(insertedId);
                    Toast.makeText(getContext(), getString(R.string.changes_updated), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), getString(R.string.changes_not_updated), Toast.LENGTH_SHORT).show();
                }
            }
        });

 */
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    /*case R.id.imgCommentOrder:
                        //TODO
                        break;
                    //TODO in store order viewer maybe !
                    /*case R.id.imgHighlight:
                        //highlightLocations();
                        break;*/
                    case R.id.imgCancelOrder:
                        confirmCancelOrder();
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


    private void confirmCancelOrder() {
        if (currentOrder == null) return;
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setMessage(R.string.confirm_cancel_order)
                .setPositiveButton(android.R.string.ok, (d, w) ->
                        orderViewModel.changeOrderStatus(currentOrder.getId(), com.manech.manechapp.data.Status.CANCELLED))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
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
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        orderLinesRecyclerView = view.findViewById(R.id.orderline_list_view);
        orderLinesRecyclerView.setHasFixedSize(true);
        orderLinesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        orderStatusRecyclerView = view.findViewById(R.id.orderState_list_view);
        orderStatusRecyclerView.setHasFixedSize(true);
        orderStatusRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

       // tvOrderId = view.findViewById(R.id.tvOrderId);
        tvOrderDate = view.findViewById(R.id.tvOrderDate);
        tvFee = view.findViewById(R.id.tvFee);
        tvDiscount = view.findViewById(R.id.tvDiscount);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        tvCurrentState = view.findViewById(R.id.tvOrderStatus);

        tvDeliveryMethod = view.findViewById(R.id.tvDeliveryMethod);
        tvDeliveryAddress = view.findViewById(R.id.tvDeliveryAddress);
        tvBillingAddress = view.findViewById(R.id.tvBillingAddress);
        deliveryMethodLayout = view.findViewById(R.id.orderDeliveryMethodView);
        deliveryAddressLayout = view.findViewById(R.id.orderDeliveryAddressView);
        billingAddressLayout = view.findViewById(R.id.orderBillingAddressView);

        imgArrow1 = view.findViewById(R.id.imgArrow1);
        imgArrow2 = view.findViewById(R.id.imgArrow2);
        imgArrow3 = view.findViewById(R.id.imgArrow3);

        imgCancelOrder = view.findViewById(R.id.imgCancelOrder);
        imgCancelOrder.setOnClickListener(onClickListener);
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
            case R.id.action_showQRCode:
                showQRCode();
                return true;
            case R.id.action_print_receipt:
                printReceipt();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showQRCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(R.string.print_receipt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                printReceipt();
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
                Bitmap myBitmap = QRCode.from("order_id:"+currentOrder.getQrCode()).bitmap();
                image.setImageBitmap(myBitmap);
            }
        });

        dialog.show();
    }

    private void printReceipt() {
        if (currentOrder == null) {
            Toast.makeText(requireContext(), R.string.receipt_unavailable, Toast.LENGTH_LONG).show();
            return;
        }
        StringBuilder html = new StringBuilder("<html><head><meta charset=\"utf-8\"><style>")
                .append("body{font-family:sans-serif;padding:20px}table{width:100%;border-collapse:collapse}")
                .append("th,td{border-bottom:1px solid #bbb;padding:8px;text-align:left}")
                .append(".total{text-align:right;font-size:18px;font-weight:bold}</style></head><body>")
                .append("<h1>").append(getString(R.string.order_receipt)).append("</h1>")
                .append("<p>").append(getString(R.string.order_id)).append(": ")
                .append(android.text.Html.escapeHtml(currentOrder.getQrCode())).append("<br>")
                .append(getString(R.string.date)).append(": ")
                .append(android.text.Html.escapeHtml(Converters.fromDate(currentOrder.getCreateAt()))).append("</p>");
        if (currentOrder.getDeliveryMethod() != null) {
            html.append("<p>").append(getString(R.string.delivery_method)).append(": ")
                    .append(android.text.Html.escapeHtml(currentOrder.getDeliveryMethod().getName())).append("</p>");
        }
        if (currentOrder.getDeliveryAddress() != null) {
            html.append("<p>").append(getString(R.string.select_delivery_address)).append(": ")
                    .append(android.text.Html.escapeHtml(currentOrder.getDeliveryAddress().toString())).append("</p>");
        }
        if (currentOrder.getBillingAddress() != null) {
            html.append("<p>").append(getString(R.string.select_billing_address)).append(": ")
                    .append(android.text.Html.escapeHtml(currentOrder.getBillingAddress().toString())).append("</p>");
        }
        html.append("<p>").append(getString(R.string.column_fee)).append(": ")
                .append(currentOrder.getFee()).append(" DH<br>")
                .append(getString(R.string.column_discount)).append(": ")
                .append(currentOrder.getDiscount()).append("%</p>")
                .append("<table><tr><th>").append(getString(R.string.column_item)).append("</th><th>")
                .append(getString(R.string.column_quantity)).append("</th><th>")
                .append(getString(R.string.column_total)).append("</th></tr>");
        if (currentOrder.getOrderLineList() != null) {
            for (OrderLine line : currentOrder.getOrderLineList()) {
                html.append("<tr><td>").append(android.text.Html.escapeHtml(line.getTitle()))
                        .append("</td><td>").append(line.getQuantity()).append("</td><td>")
                        .append(line.getPrice()).append(" DH</td></tr>");
            }
        }
        html.append("</table><p class=\"total\">")
                .append(getString(R.string.total)).append(": ")
                .append(currentOrder.getPrice()).append(" DH</p></body></html>");

        PrintManager printManager = (PrintManager) requireContext().getSystemService(Context.PRINT_SERVICE);
        WebView printWebView = new WebView(requireContext());
        printWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String jobName = getString(R.string.order_receipt) + " " + currentOrder.getQrCode();
                printManager.print(jobName, view.createPrintDocumentAdapter(jobName),
                        new PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build());
            }
        });
        printWebView.loadDataWithBaseURL(null, html.toString(), "text/html", "UTF-8", null);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.clear();
        inflater.inflate(R.menu.order_menu, menu);
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
                    orderLineListAdapter.getFilter().filter(newText);
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
    public void add2CartClicked(int layoutPosition) {
        OrderLine orderLine = orderLineListAdapter.getItem(layoutPosition);
        if (currentOrder != null) {
            orderViewModel.orderLineToCart(orderLine, currentOrder.getOwnerId());
        }
    }

    @Override
    public void itemClicked(int layoutPosition) {
        final OrderLine orderLine = orderLineListAdapter.getItem(layoutPosition);
        sharedPref.putUUID(getString(R.string.item_id), orderLine.getItemId());
        sharedPref.putInt(getString(R.string.items_view_mode), ItemsFragment.BUY_MODE);

        ((MainActivity) getActivity()).initFragment(ItemFragment.class, getString(R.string.view_item_buy_mode));
    }

}