package com.manech.manechapp.view.user;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.data.Order;
import com.manech.manechapp.data.OrderLine;
import com.manech.manechapp.data.OrderStatus;
import com.manech.manechapp.data.Status;
import com.manech.manechapp.data.User;
import com.manech.manechapp.view.user.DashboardSalesChart.Entry;
import com.manech.manechapp.model.OrderViewModel;
import com.manech.manechapp.model.UserViewModel;
import com.manech.manechapp.helper.ItemFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.UUID;

public class DashboardFragment extends Fragment {

    private UserViewModel userViewModel;
    private OrderViewModel orderViewModel;
    private boolean refreshInvoked=false;
    private View dashboardContent;
    private TextView storeName, itemCount, stockCount, lowStockCount;
    private TextView orderCount, pendingOrderCount, sellCount, sellValue, pendingSellCount;
    private TextView emptyStoreMessage;
    private Button startDateButton, endDateButton;
    private DashboardSalesChart salesChart;
    private Spinner salesAggregation;
    private View salesContent;
    private LinearLayout trendingItemsContainer;
    private TextView trendingEmpty, trendingRange;
    private List<Item> storeItems;
    private List<Order> customerOrders;
    private List<Order> storeOrders;
    private Date startDate;
    private Date endDate;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel=((MainActivity)getActivity()).getViewModel(UserViewModel.class);
        orderViewModel=((MainActivity)getActivity()).getViewModel(OrderViewModel.class);
        userViewModel.getUserMutableLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {

                if(refreshInvoked){
                    if(user==null){
                        ((MainActivity)getActivity()).logout();
                    } else if (user.getStoreId() != null) {
                        dashboardContent.setVisibility(View.VISIBLE);
                        emptyStoreMessage.setVisibility(View.GONE);
                        storeName.setText(user.getStore() == null ? getString(R.string.dashboard_current_store)
                                : user.getStore().getName());
                        userViewModel.fetchDefaultStoreItems(true, ItemFilter.ACTIVE);
                        orderViewModel.fetchDashboardOrders(user.getId());
                        orderViewModel.fetchDashboardSells(user.getId());
                    } else {
                        dashboardContent.setVisibility(View.GONE);
                        emptyStoreMessage.setVisibility(View.VISIBLE);
                    }

                    if (getActivity() != null) {
                        ((MainActivity)getActivity()).hideProgressBar();
                    }
                    refreshInvoked = false;
                }
            }
        });
        userViewModel.getStoreMutableLiveData().observe(this, store -> {
            if (store != null) {
                if (storeName == null) return;
                storeName.setText(store.getName());
                storeItems = store.getItems();
                renderStatistics();
            }
        });
        orderViewModel.getDashboardOrders().observe(this, orders -> {
            customerOrders = orders;
            renderStatistics();
        });
        orderViewModel.getDashboardSells().observe(this, orders -> {
            storeOrders = orders;
            renderStatistics();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInvoked=false;
        refreshAction();
    }
    private void refreshAction() {
        refreshInvoked = true;
        if (userViewModel != null) {
            userViewModel.fetchCurrentUser(true,false,false,false);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ((MainActivity)getActivity()).setTitle(getString(R.string.home));
        dashboardContent = view.findViewById(R.id.dashboard_store_content);
        emptyStoreMessage = view.findViewById(R.id.dashboard_no_store);
        storeName = view.findViewById(R.id.dashboard_store_name);
        itemCount = view.findViewById(R.id.dashboard_item_count);
        stockCount = view.findViewById(R.id.dashboard_stock_count);
        lowStockCount = view.findViewById(R.id.dashboard_low_stock_count);
        orderCount = view.findViewById(R.id.dashboard_order_count);
        pendingOrderCount = view.findViewById(R.id.dashboard_pending_orders);
        sellCount = view.findViewById(R.id.dashboard_sell_count);
        sellValue = view.findViewById(R.id.dashboard_sell_value);
        pendingSellCount = view.findViewById(R.id.dashboard_pending_sells);
        salesChart = view.findViewById(R.id.dashboard_sales_chart);
        salesAggregation = view.findViewById(R.id.dashboard_sales_aggregation);
        salesAggregation.setAdapter(android.widget.ArrayAdapter.createFromResource(
                requireContext(), R.array.dashboard_sales_periods,
                android.R.layout.simple_spinner_dropdown_item));
        salesAggregation.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View v, int position, long id) {
                renderSalesChart();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        salesContent = view.findViewById(R.id.dashboard_sells_content);
        View salesHeader = view.findViewById(R.id.dashboard_sells_header);
        ImageView salesArrow = view.findViewById(R.id.dashboard_sells_arrow);
        salesHeader.setOnClickListener(v -> {
            boolean show = salesContent.getVisibility() != View.VISIBLE;
            salesContent.setVisibility(show ? View.VISIBLE : View.GONE);
            salesArrow.setImageResource(show ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
        });
        View trendingContent = view.findViewById(R.id.dashboard_trending_content);
        View trendingHeader = view.findViewById(R.id.dashboard_trending_header);
        ImageView trendingArrow = view.findViewById(R.id.dashboard_trending_arrow);
        trendingHeader.setOnClickListener(v -> {
            boolean show = trendingContent.getVisibility() != View.VISIBLE;
            trendingContent.setVisibility(show ? View.VISIBLE : View.GONE);
            trendingArrow.setImageResource(show ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
        });
        trendingItemsContainer = view.findViewById(R.id.dashboard_trending_items);
        trendingEmpty = view.findViewById(R.id.dashboard_trending_empty);
        trendingRange = view.findViewById(R.id.dashboard_trending_range);
        startDateButton = view.findViewById(R.id.dashboard_start_date);
        endDateButton = view.findViewById(R.id.dashboard_end_date);
        startDateButton.setOnClickListener(v -> showDatePicker(true));
        endDateButton.setOnClickListener(v -> showDatePicker(false));
        ImageView reset = view.findViewById(R.id.dashboard_reset_dates);
        reset.setOnClickListener(v -> {
            startDate = null;
            endDate = null;
            updateDateLabels();
            renderStatistics();
        });
        View filterHeader = view.findViewById(R.id.dashboard_filter_header);
        View filterContent = view.findViewById(R.id.dashboard_filter_content);
        ImageView arrow = view.findViewById(R.id.dashboard_filter_arrow);
        filterHeader.setOnClickListener(v -> {
            boolean show = filterContent.getVisibility() != View.VISIBLE;
            filterContent.setVisibility(show ? View.VISIBLE : View.GONE);
            arrow.setImageResource(show ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
        });
        updateDateLabels();
        return view;

    }

    private void showDatePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        Date selected = isStart ? startDate : endDate;
        if (selected != null) {
            calendar.setTime(selected);
        }
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (picker, year, month, day) -> {
                    Calendar chosen = Calendar.getInstance();
                    chosen.clear();
                    chosen.set(year, month, day);
                    if (isStart) {
                        chosen.set(Calendar.HOUR_OF_DAY, 0);
                        chosen.set(Calendar.MINUTE, 0);
                        chosen.set(Calendar.SECOND, 0);
                        chosen.set(Calendar.MILLISECOND, 0);
                        startDate = chosen.getTime();
                    } else {
                        chosen.set(Calendar.HOUR_OF_DAY, 23);
                        chosen.set(Calendar.MINUTE, 59);
                        chosen.set(Calendar.SECOND, 59);
                        chosen.set(Calendar.MILLISECOND, 999);
                        endDate = chosen.getTime();
                    }
                    updateDateLabels();
                    renderStatistics();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        if (isStart && endDate != null) {
            dialog.getDatePicker().setMaxDate(endDate.getTime());
        } else if (!isStart && startDate != null) {
            dialog.getDatePicker().setMinDate(startDate.getTime());
        }
        dialog.show();
    }

    private void updateDateLabels() {
        startDateButton.setText(startDate == null ? getString(R.string.dashboard_from_date)
                : dateFormat.format(startDate));
        endDateButton.setText(endDate == null ? getString(R.string.dashboard_to_date)
                : dateFormat.format(endDate));
    }

    private boolean inSelectedRange(Date date) {
        if (date == null) return false;
        return (startDate == null || !date.before(startDate))
                && (endDate == null || !date.after(endDate));
    }

    private Status latestStatus(Order order) {
        List<OrderStatus> statuses = order.getOrderStatusList();
        return statuses == null || statuses.isEmpty() ? null : statuses.get(0).getStatus();
    }

    private void renderStatistics() {
        if (itemCount == null) return;

        int activeItems = 0;
        int lowStockItems = 0;
        double stockUnits = 0;
        if (storeItems != null) {
            for (Item item : storeItems) {
                if (item.getTrashDate() != null) continue;
                activeItems++;
                double quantity = item.getQuantity() == null ? 0 : item.getQuantity();
                stockUnits += quantity;
                Double minimum = item.getMinQty();
                if (minimum != null && minimum > 0 && quantity <= minimum) lowStockItems++;
            }
        }
        itemCount.setText(getString(R.string.dashboard_items_value, activeItems));
        stockCount.setText(getString(R.string.dashboard_stock_value, stockUnits));
        lowStockCount.setText(getString(R.string.dashboard_low_stock_value, lowStockItems));

        int matchingOrders = 0;
        int pendingOrders = 0;
        if (customerOrders != null) {
            for (Order order : customerOrders) {
                if (!inSelectedRange(order.getCreateAt())) continue;
                matchingOrders++;
                if (latestStatus(order) == Status.PENDING) pendingOrders++;
            }
        }
        orderCount.setText(getString(R.string.dashboard_orders_value, matchingOrders));
        pendingOrderCount.setText(getString(R.string.dashboard_pending_value, pendingOrders));

        int matchingSales = 0;
        int pendingSales = 0;
        double salesValue = 0;
        if (storeOrders != null) {
            for (Order order : storeOrders) {
                if (!inSelectedRange(order.getCreateAt())) continue;
                matchingSales++;
                salesValue += order.getPrice();
                if (latestStatus(order) == Status.PENDING) pendingSales++;
            }
        }
        sellCount.setText(getString(R.string.dashboard_sells_value, matchingSales));
        sellValue.setText(getString(R.string.dashboard_sales_value, salesValue));
        pendingSellCount.setText(getString(R.string.dashboard_pending_value, pendingSales));
        renderSalesChart();
        renderTrendingItems();
    }

    private static final class SoldItem {
        final UUID id;
        String title;
        double quantity;
        double revenue;

        SoldItem(UUID id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    private void renderTrendingItems() {
        if (trendingItemsContainer == null) return;
        trendingItemsContainer.removeAllViews();
        String from = startDate == null ? getString(R.string.dashboard_trending_all_dates)
                : dateFormat.format(startDate);
        String to = endDate == null ? getString(R.string.dashboard_trending_all_dates)
                : dateFormat.format(endDate);
        trendingRange.setText(getString(R.string.dashboard_trending_range, from, to));

        Map<UUID, SoldItem> totals = new HashMap<>();
        if (storeOrders != null) {
            for (Order order : storeOrders) {
                if (!inSelectedRange(order.getCreateAt())) continue;
                Status status = latestStatus(order);
                if (status == Status.CANCELLED || status == Status.RETURNED) continue;
                List<OrderLine> lines = order.getOrderLineList();
                if (lines == null) continue;
                for (OrderLine line : lines) {
                    UUID itemId = line.getItemId();
                    if (itemId == null) continue;
                    String title = line.getTitle();
                    if (title == null || title.trim().isEmpty()) {
                        title = line.getCode() == null ? getString(R.string.dashboard_unknown_item) : line.getCode();
                    }
                    SoldItem soldItem = totals.get(itemId);
                    if (soldItem == null) {
                        soldItem = new SoldItem(itemId, title);
                        totals.put(itemId, soldItem);
                    }
                    soldItem.quantity += line.getQuantity();
                    soldItem.revenue += line.getPrice();
                }
            }
        }

        List<SoldItem> ranked = new ArrayList<>(totals.values());
        ranked.sort(Comparator.comparingDouble((SoldItem item) -> item.quantity).reversed()
                .thenComparing(item -> item.title, String.CASE_INSENSITIVE_ORDER));
        int count = Math.min(ranked.size(), 5);
        trendingEmpty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        for (int i = 0; i < count; i++) {
            SoldItem item = ranked.get(i);
            TextView row = new TextView(requireContext());
            row.setText(getString(R.string.dashboard_trending_item, i + 1, item.title,
                    item.quantity, item.revenue));
            row.setTextColor(requireContext().getColor(R.color.colorAccent));
            row.setTextSize(15);
            row.setPadding(0, 8, 0, 8);
            trendingItemsContainer.addView(row);
        }
    }

    private void renderSalesChart() {
        if (salesChart == null || salesAggregation == null) return;
        final int period = salesAggregation.getSelectedItemPosition();
        if (storeOrders == null || storeOrders.isEmpty()) {
            salesChart.setEntries(new ArrayList<>(), getString(R.string.dashboard_no_sales_chart_data));
            return;
        }

        Calendar first = Calendar.getInstance();
        Calendar last = Calendar.getInstance();
        boolean hasFirstDate = false;
        if (startDate != null) {
            first.setTime(startDate);
            hasFirstDate = true;
        } else {
            for (Order order : storeOrders) {
                Date created = order.getCreateAt();
                if (created != null && inSelectedRange(created)
                        && (!hasFirstDate || created.before(first.getTime()))) {
                    first.setTime(created);
                    hasFirstDate = true;
                }
            }
        }
        if (!hasFirstDate) {
            salesChart.setEntries(new ArrayList<>(), getString(R.string.dashboard_no_sales_chart_data));
            return;
        }
        if (endDate != null) last.setTime(endDate);

        normalizePeriod(first, period);
        normalizePeriod(last, period);
        if (first.after(last)) {
            salesChart.setEntries(new ArrayList<>(), getString(R.string.dashboard_no_sales_chart_data));
            return;
        }

        final Map<Long, Double> values = new TreeMap<>();
        Calendar cursor = (Calendar) first.clone();
        while (!cursor.after(last)) {
            values.put(cursor.getTimeInMillis(), 0d);
            cursor.add(calendarField(period), 1);
        }

        for (Order order : storeOrders) {
            Date created = order.getCreateAt();
            if (!inSelectedRange(created)) continue;
            Calendar bucket = Calendar.getInstance();
            bucket.setTime(created);
            normalizePeriod(bucket, period);
            Long key = bucket.getTimeInMillis();
            if (values.containsKey(key)) {
                values.put(key, values.get(key) + order.getPrice());
            }
        }

        final List<Entry> entries = new ArrayList<>();
        for (Map.Entry<Long, Double> value : values.entrySet()) {
            Calendar labelDate = Calendar.getInstance();
            labelDate.setTimeInMillis(value.getKey());
            String label = period == 0
                    ? new SimpleDateFormat("d MMM", Locale.getDefault()).format(labelDate.getTime())
                    : period == 1
                    ? new SimpleDateFormat("MMM yy", Locale.getDefault()).format(labelDate.getTime())
                    : new SimpleDateFormat("yyyy", Locale.getDefault()).format(labelDate.getTime());
            entries.add(new Entry(label, value.getValue()));
        }
        salesChart.setEntries(entries, getString(R.string.dashboard_no_sales_chart_data));
    }

    private static int calendarField(int period) {
        if (period == 1) return Calendar.MONTH;
        if (period == 2) return Calendar.YEAR;
        return Calendar.DAY_OF_YEAR;
    }

    private static void normalizePeriod(Calendar calendar, int period) {
        if (period == 0) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        } else if (period == 1) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        } else {
            calendar.set(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
    }
}
