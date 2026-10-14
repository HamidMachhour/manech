package com.manech.manechapp.view.order;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.manech.manechapp.R;
import com.manech.manechapp.data.Order;
import com.manech.manechapp.data.OrderLine;
import com.manech.manechapp.data.OrderStatus;
import com.manech.manechapp.data.Status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Collapsible filter card shared by MY ORDERS and MY SELLS. */
public final class OrderFilterCard {
    private final EditText search, minPrice, maxPrice;
    private final Spinner statusSpinner, sortSpinner;
    private final Runnable onChange;

    private OrderFilterCard(View root, Runnable onChange) {
        this.onChange = onChange;
        final View content = root.findViewById(R.id.order_filter_content);
        final ImageView arrow = root.findViewById(R.id.order_filter_arrow);
        root.findViewById(R.id.order_filter_header).setOnClickListener(v -> {
            final boolean show = content.getVisibility() == View.GONE;
            content.setVisibility(show ? View.VISIBLE : View.GONE);
            arrow.setImageResource(show ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
        });

        search = root.findViewById(R.id.edit_order_search);
        minPrice = root.findViewById(R.id.edit_order_min_price);
        maxPrice = root.findViewById(R.id.edit_order_max_price);
        statusSpinner = root.findViewById(R.id.spinner_order_status);
        sortSpinner = root.findViewById(R.id.spinner_order_sort);

        final List<String> labels = new ArrayList<>();
        labels.add(root.getContext().getString(R.string.all_statuses));
        for (Status status : Status.values()) {
            labels.add(status.getLocalizedName(root.getContext()));
        }
        statusSpinner.setAdapter(new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_spinner_dropdown_item, labels));
        sortSpinner.setAdapter(ArrayAdapter.createFromResource(root.getContext(),
                R.array.order_sort_options, android.R.layout.simple_spinner_dropdown_item));

        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onChange.run();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        search.addTextChangedListener(watcher);
        minPrice.addTextChangedListener(watcher);
        maxPrice.addTextChangedListener(watcher);
        final AdapterView.OnItemSelectedListener selected = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onChange.run();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        statusSpinner.setOnItemSelectedListener(selected);
        sortSpinner.setOnItemSelectedListener(selected);
        root.findViewById(R.id.button_order_reset).setOnClickListener(v -> {
            search.setText("");
            minPrice.setText("");
            maxPrice.setText("");
            statusSpinner.setSelection(0);
            sortSpinner.setSelection(0);
        });
    }

    public static OrderFilterCard bind(View root, Runnable onChange) {
        return new OrderFilterCard(root, onChange);
    }

    private static Double parse(EditText edit) {
        try {
            return Double.parseDouble(edit.getText().toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean isFinal(Status status) {
        return status == Status.CANCELLED || status == Status.RETURNED;
    }

    /** Returns the orders matching the current criteria, sorted as chosen. */
    public List<Order> apply(List<Order> all) {
        final String text = search.getText().toString().trim().toLowerCase();
        final Double min = parse(minPrice);
        final Double max = parse(maxPrice);
        final int statusPos = statusSpinner.getSelectedItemPosition();
        final List<Order> result = new ArrayList<>();
        for (Order order : all) {
            final List<OrderStatus> statuses = order.getOrderStatusList();
            if (statusPos > 0 && (statuses == null || statuses.isEmpty()
                    || statuses.get(0).getStatus() != Status.values()[statusPos - 1])) {
                continue;
            }
            final double price = order.getPrice();
            if ((min != null && price < min) || (max != null && price > max)) {
                continue;
            }
            if (!text.isEmpty() && !matches(order, text)) {
                continue;
            }
            result.add(order);
        }
        final int sort = sortSpinner.getSelectedItemPosition();
        Collections.sort(result, (a, b) -> {
            switch (sort) {
                case 1:
                    return compareDates(a, b);
                case 2:
                    return Double.compare(a.getPrice(), b.getPrice());
                case 3:
                    return Double.compare(b.getPrice(), a.getPrice());
                default:
                    return compareDates(b, a);
            }
        });
        return result;
    }

    private static int compareDates(Order a, Order b) {
        if (a.getCreateAt() == null || b.getCreateAt() == null) return 0;
        return a.getCreateAt().compareTo(b.getCreateAt());
    }

    private static boolean matches(Order order, String text) {
        if (order.getQrCode() != null && order.getQrCode().toLowerCase().contains(text)) {
            return true;
        }
        if (order.getOrderLineList() != null) {
            for (OrderLine line : order.getOrderLineList()) {
                if (line.getTitle() != null && line.getTitle().toLowerCase().contains(text)) {
                    return true;
                }
                if (line.getCode() != null && line.getCode().toLowerCase().contains(text)) {
                    return true;
                }
            }
        }
        return false;
    }
}
