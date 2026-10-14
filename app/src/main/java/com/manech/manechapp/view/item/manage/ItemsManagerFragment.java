package com.manech.manechapp.view.item.manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.helper.ItemFilter;
import com.manech.manechapp.helper.ItemQrTransfer;
import com.manech.manechapp.model.UserViewModel;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ItemsManagerFragment extends Fragment {
    public static final String ARG_TRASH_MODE = "trash_mode";
    private boolean trashMode = false;


    private ItemFilter filter = ItemFilter.ACTIVE;

    private UserViewModel userViewModel;
    ManechStore store;

    private RecyclerView itemsListView;
    private ItemsManagerAdapter adapter;

    private boolean refreshInvoked = false;
    private ImageView imgArrow1;
    private LinearLayout filterHeader;
    private RadioGroup filterRadioGroup;
    private View filterContent;
    private EditText searchInput;
    private View.OnClickListener onClickListener;
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trashMode = getArguments() != null && getArguments().getBoolean(ARG_TRASH_MODE, false);
        filter = trashMode ? ItemFilter.IN_TRASH : ItemFilter.ACTIVE;
        userViewModel = ((MainActivity) getActivity()).getViewModel(UserViewModel.class);
        userViewModel.getStoreUpdateStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (isResumed() && isVisible() && aBoolean) {
                    refreshAction(true, filter);
                }
            }
        });
        userViewModel.getStoreMutableLiveData().observe(this, new Observer<ManechStore>() {
            @Override
            public void onChanged(ManechStore manechStore) {
                if (refreshInvoked) {
                    refreshInvoked = false;
                    if (manechStore != null) {
                        store = manechStore;
                        fillUI();
                    }
                    ((MainActivity)getActivity()).hideProgressBar();
                }
            }
        });

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.filter_header:
                        collapseCardView(imgArrow1, filterRadioGroup);
                        filterContent.setVisibility(filterRadioGroup.getVisibility());
                        break;
                }
            }
        };
        onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioAll:
                        filter = ItemFilter.ALL;
                        break;
                    case R.id.radionActive:
                        filter = ItemFilter.ACTIVE;
                        break;
                    case R.id.radioTrash:
                        filter = ItemFilter.IN_TRASH;
                        break;
                    case R.id.radioOrphan:
                        filter = ItemFilter.ORPHAN;
                        break;
                    case R.id.radioPublished:
                        filter = ItemFilter.PUBLISHED;
                        break;
                }
                refreshAction(true, filter);
            }
        };
    }

    private void setupFilterControls(View view) {
        searchInput = view.findViewById(R.id.edit_item_search);
        final EditText minInput = view.findViewById(R.id.edit_min_price);
        final EditText maxInput = view.findViewById(R.id.edit_max_price);
        final android.widget.CheckBox inStock = view.findViewById(R.id.check_in_stock);
        final android.widget.Spinner sort = view.findViewById(R.id.spinner_sort);
        sort.setAdapter(android.widget.ArrayAdapter.createFromResource(requireContext(),
                R.array.item_sort_options, android.R.layout.simple_spinner_dropdown_item));

        final Runnable apply = () -> adapter.setFilterCriteria(parsePrice(minInput.getText()),
                parsePrice(maxInput.getText()), inStock.isChecked(), sort.getSelectedItemPosition());
        final android.text.TextWatcher priceWatcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                apply.run();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        };
        minInput.addTextChangedListener(priceWatcher);
        maxInput.addTextChangedListener(priceWatcher);
        inStock.setOnCheckedChangeListener((b, checked) -> apply.run());
        sort.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View v, int position, long id) {
                apply.run();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
        view.findViewById(R.id.button_scan_code).setOnClickListener(v ->
                com.google.mlkit.vision.codescanner.GmsBarcodeScanning.getClient(requireActivity()).startScan()
                        .addOnSuccessListener(barcode -> {
                            if (barcode.getRawValue() != null) {
                                searchInput.setText(barcode.getRawValue());
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(requireContext(), R.string.barcode_scan_failed, Toast.LENGTH_LONG).show()));
        view.findViewById(R.id.button_reset_filters).setOnClickListener(v -> {
            minInput.setText("");
            maxInput.setText("");
            inStock.setChecked(false);
            sort.setSelection(0);
            searchInput.setText("");
        });
    }

    private static Double parsePrice(CharSequence text) {
        try {
            return text == null || text.length() == 0 ? null : Double.valueOf(text.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void collapseCardView(ImageView img, ViewGroup viewGroup) {
        viewGroup.setVisibility(viewGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        img.setImageResource(viewGroup.getVisibility() == View.GONE ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_up);
    }

    private void fillUI() {
        if (store != null && store.getItems() != null) {
            adapter.getItemsList().clear();
            adapter.getItemsList().addAll(store.getItems());
            adapter.refresh();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        getActivity().setTitle(getString(trashMode ? R.string.manage_trash_items_title : R.string.items_fragment_title));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items_manage, container, false);

        imgArrow1 = view.findViewById(R.id.imgArrow1);
        filterHeader = view.findViewById(R.id.filter_header);
        filterHeader.setOnClickListener(onClickListener);

        filterRadioGroup = view.findViewById(R.id.filter_radio_group);
        filterContent = view.findViewById(R.id.manage_filter_content);
        filterRadioGroup.check(R.id.radionActive);
        filterRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        // trashed items live in their own screen
        view.findViewById(R.id.radioAll).setVisibility(View.GONE);
        view.findViewById(R.id.radioTrash).setVisibility(View.GONE);
        if (trashMode) {
            view.findViewById(R.id.filter_card).setVisibility(View.GONE);
        }

        itemsListView = view.findViewById(R.id.items_list_view);
        itemsListView.setHasFixedSize(true);

        itemsListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new ItemsManagerAdapter(new ArrayList<>(), ItemsManagerFragment.this);
        itemsListView.setAdapter(adapter);
        setupFilterControls(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInvoked = false;
        refreshAction(true, filter);
    }

    private void refreshAction(boolean reloadItems, ItemFilter filter) {
        refreshInvoked = true;
        if (userViewModel != null) {
            userViewModel.fetchDefaultStoreItems(reloadItems, filter);
        }
    }

    public void itemClicked(int position) {
        Item item = adapter.getItem(position);
        userViewModel.setCurrentItem(item);
        ((MainActivity) getActivity()).initFragment(ItemManagerFragment.class, getString(R.string.view_item_edit_mode));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_back:
                ((MainActivity) getActivity()).actionBack();
                return true;
            case R.id.action_viewFormat:
                viewFormat(item);
                return true;
            case R.id.action_save:
                saveAction();
                return true;
            case R.id.action_add:
                addAction();
                return true;

            case R.id.action_trash:
                deleteAll();
                return true;
            case R.id.action_print_item_qr_codes:
                printItemQrCodes();
                return true;
            case R.id.action_import_item_qr:
                scanItemQr();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveAction() {
        ((MainActivity)getActivity()).showProgressBar();

        userViewModel.updateCurrentStoreItems(adapter.getItemsList());

    }

    private void addAction() {
        if (store != null) {
            adapter.getItemsList().add(new Item("0x00000", "Item Title", "Item Description", 0.0, 0.0, 0.0,
                    1000.0, 0.0, 0.0, 1, store.getId()));
            adapter.refresh();
        }
    }

    private void viewFormat(@NonNull MenuItem item) {
        item.setIcon(adapter.isGridFormat() ? R.drawable.ic_grid : R.drawable.ic_list);
        itemsListView.setLayoutManager(adapter.isGridFormat() ?
                new LinearLayoutManager(getContext()) :
                new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        adapter.setGridFormat(!adapter.isGridFormat());
        adapter.refresh();
    }

    private void deleteAll() {
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete_all))
                .setMessage(R.string.delete_items_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        adapter.getItemsList().clear();
                        adapter.refresh();
                        if (trashMode) {
                            saveAction();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void printItemQrCodes() {
        if (adapter == null) {
            return;
        }
        List<Item> activeItems = new ArrayList<>();
        for (Item item : adapter.getItemsList()) {
            if (item.getTrashDate() == null) {
                activeItems.add(item);
            }
        }
        if (activeItems.isEmpty()) {
            Toast.makeText(requireContext(), R.string.item_qr_print_empty, Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<String> payloads = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> codes = new ArrayList<>();
        try {
            for (Item item : activeItems) {
                payloads.add(ItemQrTransfer.encode(item));
                titles.add(item.getTitle() == null ? "" : item.getTitle());
                codes.add(item.getCode() == null ? "" : item.getCode());
            }
        } catch (JSONException exception) {
            Toast.makeText(requireContext(), R.string.item_qr_import_invalid, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(requireActivity(), ItemQrPrintActivity.class);
        intent.putStringArrayListExtra(ItemQrPrintActivity.EXTRA_PAYLOADS, payloads);
        intent.putStringArrayListExtra(ItemQrPrintActivity.EXTRA_TITLES, titles);
        intent.putStringArrayListExtra(ItemQrPrintActivity.EXTRA_CODES, codes);
        intent.putExtra(ItemQrPrintActivity.EXTRA_JOB_NAME, getString(R.string.item_qr_print_job));
        startActivity(intent);
    }

    private void scanItemQr() {
        if (store == null || store.getId() == null) {
            Toast.makeText(requireContext(), R.string.item_qr_import_invalid, Toast.LENGTH_LONG).show();
            return;
        }
        UUID currentStoreId = store.getId();
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        GmsBarcodeScanning.getClient(requireActivity(), options).startScan()
                .addOnSuccessListener(barcode -> {
                    if (!isAdded()) {
                        return;
                    }
                    String value = barcode == null ? null : barcode.getRawValue();
                    try {
                        Item importedItem = ItemQrTransfer.decode(value, currentStoreId);
                        userViewModel.addItemToCurrentStore(importedItem);
                        Toast.makeText(requireContext(), R.string.item_qr_import_added, Toast.LENGTH_LONG).show();
                    } catch (JSONException exception) {
                        Toast.makeText(requireContext(), R.string.item_qr_import_invalid, Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(error -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), R.string.barcode_scan_failed, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.items_menu_managemode, menu);
        if (trashMode) {
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_add).setVisible(false);
            menu.findItem(R.id.action_print_item_qr_codes).setVisible(false);
            menu.findItem(R.id.action_import_item_qr).setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }


    public void duplicateClicked(int p) {
        adapter.getItemsList().add(adapter.getItem(p).clone());
        adapter.refresh();
        Toast.makeText(getContext(), getString(R.string.add_to_default_store_confirm), Toast.LENGTH_SHORT).show();
    }

    public void deleteClicked(int p) {
        Item item = adapter.getItem(p);
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete_single_item))
                .setMessage(R.string.delete_item_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        adapter.getItemsList().remove(item);
                        adapter.refresh();
                        if (trashMode) {
                            saveAction();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void moveToTrashClicked(int p) {
        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.move_to_trash_title)
                .setMessage(R.string.move_item_to_trash_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // removed from the list: saving moves it to the trash
                        adapter.getItemsList().remove(adapter.getItem(p));
                        adapter.refresh();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void restoreClicked(int position) {
        Item item = adapter.getItem(position);
        item.setTrashDate(null);
        adapter.refresh();
        if (trashMode) {
            saveAction();
        }
    }

    public void joinClicked(int position) {
        final Item item = adapter.getItem(position);
        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.join_item_title)
                .setMessage(R.string.join_item_msg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    refreshInvoked = true;
                    userViewModel.joinItemToCurrentStore(item);
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void publishClicked(int position) {
        Item item = adapter.getItem(position);

        if (item.getPublishDate() == null) {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.publish_item_title_dialog)
                    .setMessage(R.string.publish_item_dialog_msg)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            item.setPublishDate(new Date());
                            adapter.refresh();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.unpublish_item_dialog_title)
                    .setMessage(R.string.unpublish_item_dialog_msg)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            item.setPublishDate(null);
                            adapter.refresh();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }


    public void highlight(int layoutPosition) {
        Item item = adapter.getItem(layoutPosition);
        ((MainActivity) getActivity()).actionHighlight(new Integer[]{item.getLocation()});
    }
}