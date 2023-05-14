package com.manech.manechapp.view.item.manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.appcompat.widget.SearchView;
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
import com.manech.manechapp.model.UserViewModel;

import java.util.ArrayList;
import java.util.Date;

public class ItemsManagerFragment extends Fragment {


    private ItemFilter filter = ItemFilter.ALL;

    private UserViewModel userViewModel;
    ManechStore store;

    private RecyclerView itemsListView;
    private ItemsManagerAdapter adapter;

    private boolean refreshInvoked = false;
    private ImageView imgArrow1;
    private LinearLayout filterHeader;
    private RadioGroup filterRadioGroup;
    private View.OnClickListener onClickListener;
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;

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

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.filter_header:
                        collapseCardView(imgArrow1, filterRadioGroup);
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
        if (store != null && store.getItems() != null) {
            adapter.getItemsList().clear();
            adapter.getItemsList().addAll(store.getItems());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        getActivity().setTitle(getString(R.string.items_fragment_title));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items_manage, container, false);

        imgArrow1 = view.findViewById(R.id.imgArrow1);
        filterHeader = view.findViewById(R.id.filter_header);
        filterHeader.setOnClickListener(onClickListener);

        filterRadioGroup = view.findViewById(R.id.filter_radio_group);
        filterRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        itemsListView = view.findViewById(R.id.items_list_view);
        itemsListView.setHasFixedSize(true);

        itemsListView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new ItemsManagerAdapter(new ArrayList<>(), ItemsManagerFragment.this);
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

    private void deleteAll() {
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete_all))
                .setMessage(R.string.delete_items_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        adapter.getItemsList().clear();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.items_menu_managemode, menu);


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
                .setTitle("Trash")
                .setMessage("Move this item to the Trash?")
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