package com.manech.manechapp.view.mstore;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.manech.manechapp.R;

/** Binds the collapsible filter card shared by the stores screens. */
final class StoreFilterCard {
    interface Listener {
        void onCriteriaChanged(String query, int sortMode, boolean optionOnly);
    }

    private StoreFilterCard() {
    }

    static void bind(View root, int optionLabel, Listener listener) {
        final View content = root.findViewById(R.id.store_filter_content);
        final ImageView arrow = root.findViewById(R.id.store_filter_arrow);
        root.findViewById(R.id.store_filter_header).setOnClickListener(v -> {
            final boolean show = content.getVisibility() == View.GONE;
            content.setVisibility(show ? View.VISIBLE : View.GONE);
            arrow.setImageResource(show ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
        });

        final EditText search = root.findViewById(R.id.edit_store_search);
        final Spinner sort = root.findViewById(R.id.spinner_store_sort);
        final CheckBox option = root.findViewById(R.id.check_store_option);
        sort.setAdapter(ArrayAdapter.createFromResource(root.getContext(),
                R.array.store_sort_options, android.R.layout.simple_spinner_dropdown_item));
        if (optionLabel == 0) {
            option.setVisibility(View.GONE);
        } else {
            option.setText(optionLabel);
        }

        final Runnable apply = () -> listener.onCriteriaChanged(search.getText().toString(),
                sort.getSelectedItemPosition(), option.isChecked());
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                apply.run();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        option.setOnCheckedChangeListener((b, checked) -> apply.run());
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                apply.run();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        root.findViewById(R.id.button_store_reset).setOnClickListener(v -> {
            search.setText("");
            sort.setSelection(0);
            option.setChecked(false);
        });
    }
}
