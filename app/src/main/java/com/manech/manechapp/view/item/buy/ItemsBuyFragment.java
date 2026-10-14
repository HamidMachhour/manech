package com.manech.manechapp.view.item.buy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.Cart;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.helper.ItemFilter;
import com.manech.manechapp.model.UserViewModel;
import com.manech.manechapp.view.cart.CartFragment;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemsBuyFragment extends Fragment {
    private int currentPage = 0;
    private final int ITEMS_PER_PAGE = 20;
    
    private boolean refreshInvoked = false;


    private ItemFilter filter = ItemFilter.ALL;

    private UserViewModel userViewModel;
    ManechStore store;
    Cart currentCart;
    List<Item> buyItems;

    private RecyclerView itemsListView;
    private ItemsBuyAdapter adapter;

    private ImageView imgArrow1;
    private LinearLayout filterHeader, filterLayout;
    private RadioGroup filterRadioGroup;
    private Button btnGoToCart;
    private SeekBar seekBar;
    private TextView tvQty;

    private View.OnClickListener onClickListener;
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;
    private SeekBar.OnSeekBarChangeListener seekBarListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        seekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 0) {
                    seekBar.setProgress(1);
                    progress = 1;
                }
                tvQty.setText("x" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.filter_header:
                        collapseCardView(imgArrow1, filterLayout);
                        break;
                    case R.id.btnGoToCart:
                        ((MainActivity) getActivity()).initFragment(CartFragment.class, getString(R.string.cart_fragment));
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
                    case R.id.radioPublished:
                        filter = ItemFilter.PUBLISHED;
                        break;
                }
                refreshAction(true, filter);
            }
        };

    }

    private void collapseCardView(ImageView img, ViewGroup viewGroup) {
        viewGroup.setVisibility(viewGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        img.setImageResource(viewGroup.getVisibility() == View.GONE ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_up);
    }

    private void fillUI() {
        if (currentCart != null && buyItems != null) {
            adapter.getItemsList().clear();
            adapter.getItemsList().addAll(store.getItems());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        getActivity().setTitle(getString(R.string.buy_items_fragment_title));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items_buy, container, false);

        btnGoToCart = view.findViewById(R.id.btnGoToCart);
        btnGoToCart.setOnClickListener(onClickListener);

        seekBar = view.findViewById(R.id.seekBarQty);
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        tvQty = view.findViewById(R.id.tvQty);

        imgArrow1 = view.findViewById(R.id.imgArrow1);
        filterHeader = view.findViewById(R.id.filter_header);
        filterHeader.setOnClickListener(onClickListener);
        filterLayout=view.findViewById(R.id.filter_layout);


        filterRadioGroup = view.findViewById(R.id.filter_radio_group);
        filterRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        itemsListView = view.findViewById(R.id.items_list_view);
        itemsListView.setHasFixedSize(true);

        itemsListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new ItemsBuyAdapter(new ArrayList<>(), ItemsBuyFragment.this);
        itemsListView.setAdapter(adapter);

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
        ((MainActivity) getActivity()).initFragment(ItemBuyFragment.class, getString(R.string.view_item_buy_mode));
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
            case R.id.action_refresh:
                return true;
            case R.id.action_save:
                saveAction();
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
            adapter.notifyDataSetChanged();
        }
    }

    private void viewFormat(@NonNull MenuItem item) {
        item.setIcon(adapter.isGridFormat() ? R.drawable.ic_grid : R.drawable.ic_list);
        itemsListView.setLayoutManager(adapter.isGridFormat() ?
                new LinearLayoutManager(getContext()) :
                new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        adapter.setGridFormat(!adapter.isGridFormat());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.items_menu_buymode, menu);


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
                    adapter.getFilter().filter(newText);
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


    public void duplicateClicked(int p) {
        adapter.getItemsList().add(adapter.getItem(p).clone());
        adapter.notifyDataSetChanged();
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
                        adapter.notifyDataSetChanged();
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
                        Item item = adapter.getItem(p);
                        item.setTrashDate(new Date());
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void restoreClicked(int position) {
        Item item = adapter.getItem(position);
        item.setTrashDate(null);
        adapter.notifyDataSetChanged();
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
                            adapter.notifyDataSetChanged();
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
                            adapter.notifyDataSetChanged();
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