package com.manech.manechapp.view.item;

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
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.manech.manechapp.data.CartItem;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.helper.SecureSharedPref;
import com.manech.manechapp.model.CartViewModel;
import com.manech.manechapp.model.ItemViewModel;
import com.manech.manechapp.model.MStoreViewModel;
import com.manech.manechapp.view.cart.CartFragment;

import java.util.List;
import java.util.UUID;

public class ItemsFragment extends Fragment implements View.OnClickListener {

    private ItemViewModel itemViewModel;
    private MStoreViewModel storeViewModel;
    private CartViewModel cartViewModel;
    private UUID cartId;
    private RecyclerView itemsListView;
    private Button btnGoToCart;
    private SeekBar seekBar;
    private TextView tvQty;

    private ItemListAdapter adapter;
    private UUID storeId;
    private UUID owner;

    private ProgressDialog mDialog;
    private int viewMode = BUY_MODE;
    private SecureSharedPref sharedPref;

    private SeekBar.OnSeekBarChangeListener seekBarListener;
    private View.OnClickListener goToCartListener;
    public static final int BUY_MODE = 0;
    public static final int MANAGE_MODE = 1;
    public static final int TRASH_MODE = 2;
    private double currentPrice=0.0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPref = SecureSharedPref.getInstance(getActivity().getApplicationContext(), getString(R.string.preference_file_key));

        owner = sharedPref.getUUID(getString(R.string.current_user_id), null);

        viewMode = sharedPref.getInt(getString(R.string.items_view_mode), -1);

        itemViewModel = ((MainActivity) getActivity()).getViewModel(ItemViewModel.class);
        storeViewModel = ((MainActivity) getActivity()).getViewModel(MStoreViewModel.class);
        cartViewModel = ((MainActivity) getActivity()).getViewModel(CartViewModel.class);

        itemViewModel.getItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {

                //update recycleview
                final boolean gridFormat = (adapter != null) ? adapter.isGridFormat() : false;

                adapter = new ItemListAdapter(items, ItemsFragment.this, storeId);
                adapter.setGridFormat(gridFormat);
                adapter.setViewMode(viewMode);
                itemsListView.setAdapter(adapter);
                hideProgressDialog();
            }
        });

        storeViewModel.getStoreMutableLiveData().observe(this, new Observer<ManechStore>() {
            @Override
            public void onChanged(ManechStore manechStore) {

                if (manechStore == null) {
                    if (viewMode == BUY_MODE)
                        itemViewModel.fetchPublicItems();
                    return;
                }
                switch (viewMode) {
                    case MANAGE_MODE:
                        itemViewModel.fetchActiveItems(manechStore.getId());
                        break;
                    case TRASH_MODE:
                        itemViewModel.fetchTrash(manechStore.getId());
                        break;
                    default:
                        itemViewModel.fetchPublicAndPrivateItems(manechStore.getId());
                }


            }
        });
        if (viewMode == BUY_MODE) {

            cartViewModel.getCartMutableLiveData().observe(this, new Observer<Cart>() {
                @Override
                public void onChanged(Cart cart) {
                    if (cart != null) {
                        cartId=cart.getId();
                        btnGoToCart.setText(cart.toString());
                        final double price = cart.getPrice();
                        if (price >0 && price !=currentPrice) {
                            Toast.makeText(getContext(), getString(R.string.add_to_cart_confirm), Toast.LENGTH_SHORT).show();
                            currentPrice= price;
                        }

                    } else {
                        btnGoToCart.setText("0 Items ~ 0.00 DH");
                        cartId=null;
                    }
                }
            });
            cartViewModel.getBooleanMutableLiveData().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if(aBoolean){
                        Toast.makeText(getContext(), getString(R.string.add_to_cart_confirm), Toast.LENGTH_SHORT).show();
                        cartViewModel.fetchCart(owner);
                    }else{
                        Toast.makeText(getContext(), getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            goToCartListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).initFragment(CartFragment.class, getString(R.string.cart_fragment));
                }
            };
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        getActivity().setTitle((viewMode == MANAGE_MODE) ? getString(R.string.items_fragment_title) : (viewMode == TRASH_MODE) ?
                getString(R.string.manage_trash_items_title) :
                getString(R.string.buy_items_fragment_title));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items, container, false);


        ((LinearLayout)view.findViewById(R.id.buy_header_layout)).setVisibility((viewMode == BUY_MODE) ? View.VISIBLE : View.GONE);

        btnGoToCart = view.findViewById(R.id.btnGoToCart);

        btnGoToCart.setOnClickListener(goToCartListener);
        seekBar = view.findViewById(R.id.seekBarQty);
        seekBar.setOnSeekBarChangeListener(seekBarListener);

        tvQty = view.findViewById(R.id.tvQty);

        itemsListView = view.findViewById(R.id.items_list_view);
        itemsListView.setHasFixedSize(true);

        if (adapter != null && adapter.isGridFormat())
            itemsListView.setLayoutManager(new GridLayoutManager(view.getContext(), 2, GridLayoutManager.VERTICAL, false));
        else
            itemsListView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        showProgressDialog();

        storeId = sharedPref.getUUID(getString(R.string.defaultStore), null);
        storeViewModel.fetchStore(storeId);

        if (owner !=null && viewMode == BUY_MODE)
            cartViewModel.fetchCart(owner);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    @Override
    public void onClick(View v) {
        Item item = adapter.getItem(itemsListView.getChildLayoutPosition(v));
        sharedPref.putUUID(getString(R.string.item_id), item.getId());
        sharedPref.putInt(getString(R.string.items_view_mode), viewMode);

        if (viewMode == MANAGE_MODE) {
            ((MainActivity) getActivity()).initFragment(ItemFragment.class, getString(R.string.view_item_edit_mode));
        } else {
            ((MainActivity) getActivity()).initFragment(ItemFragment.class, getString(R.string.view_item_buy_mode));
        }

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
            case R.id.action_viewFormat:
                if (adapter.isGridFormat()) {
                    item.setIcon(R.drawable.ic_grid);
                    itemsListView.setLayoutManager(new LinearLayoutManager(getContext()));
                } else {
                    item.setIcon(R.drawable.ic_list);
                    itemsListView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
                }
                adapter.setGridFormat(!adapter.isGridFormat());
                adapter.notifyDataSetChanged();
                return true;
            case R.id.action_refresh:
                showProgressDialog();
                storeId = sharedPref.getUUID(getString(R.string.defaultStore), null);
                storeViewModel.fetchStore(storeId);
                return true;
            case R.id.action_add:
                showProgressDialog();
                itemViewModel.populate(storeId);
                return true;

            case R.id.action_trash:
                if (viewMode == MANAGE_MODE) {
                    sharedPref.putInt(getString(R.string.items_view_mode), ItemsFragment.TRASH_MODE);
                    ((MainActivity) getActivity()).initFragment(ItemsFragment.class, getString(R.string.items_trash_mode));
                    return true;
                }
                if (viewMode == TRASH_MODE) {
                    deleteAll();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAll() {
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete_all))
                .setMessage(R.string.delete_items_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        showProgressDialog();
                        itemViewModel.clearTrash(storeId);
                        itemViewModel.fetchTrash(storeId);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.clear();
        switch (viewMode) {
            case MANAGE_MODE:
                inflater.inflate(R.menu.items_menu_managemode, menu);
                break;
            case TRASH_MODE:
                inflater.inflate(R.menu.items_menu_trashmode, menu);
                break;
            default:
                inflater.inflate(R.menu.items_menu_buymode, menu);
        }

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
        Item item = adapter.getItem(p);
        itemViewModel.duplicateItem(item.getId(), storeId);
        if (viewMode != BUY_MODE)
            itemViewModel.fetchActiveItems(storeId);

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
                        itemViewModel.deleteItem(item.getId());
                        showProgressDialog();
                        itemViewModel.fetchTrash(storeId);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void moveToTrashClicked(int p) {
        Item item = adapter.getItem(p);
        new AlertDialog.Builder(requireActivity())
                .setTitle("Move to Trash")
                .setMessage("Move this item to the Trash?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        showProgressDialog();
                        itemViewModel.moveToTrash(item.getId());
                        itemViewModel.fetchActiveItems(storeId);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void restoreClicked(int position) {
        Item item = adapter.getItem(position);
        itemViewModel.restoreFromTrash(item.getId());
        showProgressDialog();
        itemViewModel.fetchActiveItems(storeId);
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
                            showProgressDialog();
                            itemViewModel.publishingItem(item.getId());
                            itemViewModel.fetchActiveItems(storeId);
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
                            showProgressDialog();
                            itemViewModel.privatingItem(item.getId());
                            itemViewModel.fetchActiveItems(storeId);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    public void addFavorite(int layoutPosition) {
        //TODO

    }

    public void addCart(int layoutPosition) {
        if (owner !=null) {
            Item item = adapter.getItem(layoutPosition);
            final CartItem cartItem = new CartItem(cartId, item.getId(), seekBar.getProgress());
            cartItem.setItem(item);
            cartViewModel.upsertCartItem(cartItem,true);
        }
    }

    public void highlight(int layoutPosition) {
        Item item = adapter.getItem(layoutPosition);
        ((MainActivity) getActivity()).actionHighlight(new Integer[]{item.getLocation()});
    }
}