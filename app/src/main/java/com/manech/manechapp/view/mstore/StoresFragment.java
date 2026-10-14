package com.manech.manechapp.view.mstore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.data.User;
import com.manech.manechapp.model.UserViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class StoresFragment extends Fragment {
    public static final String ARG_TRASH_MODE = "trash_mode";
    private boolean trashMode = false;
    // stores moved to trash locally, kept so that Save still persists them
    private final List<ManechStore> hiddenStores = new ArrayList<>();
    private UserViewModel userViewModel;

    private RecyclerView storesListView;
    private StoreListAdapter adapter;

    private boolean refreshInvoked = false;
    private User currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trashMode = getArguments() != null && getArguments().getBoolean(ARG_TRASH_MODE, false);
        userViewModel = ((MainActivity) getActivity()).getViewModel(UserViewModel.class);
        userViewModel.getUserMutableLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (refreshInvoked) {
                    refreshInvoked = false;
                    if (user != null) {
                        currentUser = user;
                        fillUI();
                    } else {
                        ((MainActivity) getActivity()).logout();
                    }

                    ((MainActivity)getActivity()).hideProgressBar();
                }
            }
        });
        userViewModel.getUserUpdateStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (isResumed() && isVisible() && aBoolean) {
                    refreshAction(true,true);
                }
            }

        });


    }

    private void fillUI() {
        if (currentUser != null && currentUser.getStores() != null) {
            adapter.getStoreList().clear();
            adapter.setDefaultStore(currentUser.getStoreId());
            hiddenStores.clear();
            for (ManechStore store : currentUser.getStores()) {
                if ((store.getTrashDate() != null) == trashMode) {
                    adapter.getStoreList().add(store);
                } else {
                    hiddenStores.add(store);
                }
            }
            adapter.refresh();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(trashMode ? R.string.manage_stores_in_trash : R.string.manage_stores);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stores, container, false);

        storesListView = view.findViewById(R.id.stores_list_view);
        storesListView.setHasFixedSize(true);
        storesListView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new StoreListAdapter(new ArrayList<>(), StoresFragment.this, null);
        storesListView.setAdapter(adapter);
        StoreFilterCard.bind(view, trashMode ? 0 : R.string.published_only, adapter::setCriteria);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInvoked = false;
        refreshAction(false,true);
    }

    private void refreshAction(boolean reload,boolean reloadStores) {
        refreshInvoked = true;
        if (userViewModel != null) {
            userViewModel.fetchCurrentUser(reload, reloadStores, false, false);
        }
    }


    public void storeClicked(int position) {
        userViewModel.setCurrentStore(adapter.getItem(position));
        ((MainActivity) getActivity()).initFragment(StoreFragment.class, getString(R.string.manage_store_edit_mode));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_back:
                ((MainActivity) getActivity()).actionBack();
                return true;
            case R.id.action_save:
                saveAction();
                return true;
            case R.id.action_add:
                addAction();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addAction() {
        adapter.getStoreList().add(new ManechStore("Store Name",
                "Store Description", currentUser.getId(), null));
        adapter.refresh();
    }

    private void saveAction() {

        ((MainActivity)getActivity()).showProgressBar();

        final List<ManechStore> stores = new ArrayList<>(adapter.getStoreList());
        stores.addAll(hiddenStores);
        userViewModel.updateCurrentUser(null, stores, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.stores_menu, menu);
        if (trashMode) {
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_add).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    public void duplicateClicked(int p) {
        adapter.getStoreList().add(adapter.getItem(p).clone());
        adapter.refresh();
    }

    public void deleteClicked(int p) {
        ManechStore store = adapter.getItem(p);
        if (trashMode) {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.delete_single_store))
                    .setMessage(R.string.delete_store_confirm)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        ((MainActivity) requireActivity()).showProgressBar();
                        refreshInvoked = true;
                        userViewModel.deleteStoreForever(store.getId());
                    })
                    .setNegativeButton(android.R.string.no, null).show();
            return;
        }
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.delete_single_store))
                .setMessage(R.string.delete_store_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (store.getId().equals(currentUser.getStoreId())) {
                            currentUser.setStoreId(null);
                            adapter.setDefaultStore(null);
                        }

                        adapter.getStoreList().remove(store);
                        adapter.refresh();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void setDefaultClicked(int layoutPosition) {
        final UUID storeId = adapter.getItem(layoutPosition).getId();
        currentUser.setStoreId(storeId);
        adapter.setDefaultStore(storeId);
        adapter.refresh();
    }

    public void moveToTrashClicked(int p) {

        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.move_to_trash_title)
                .setMessage(R.string.move_store_to_trash_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final ManechStore toTrash = adapter.getItem(p);
                        toTrash.setTrashDate(new Date());
                        adapter.getStoreList().remove(toTrash);
                        hiddenStores.add(toTrash);
                        if (toTrash.getId().equals(currentUser.getStoreId())) {
                            currentUser.setStoreId(null);
                            adapter.setDefaultStore(null);
                        }
                        adapter.refresh();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void restoreClicked(int position) {
        ManechStore fromTrash = adapter.getItem(position);
        fromTrash.setTrashDate(null);
        if (trashMode) {
            ((MainActivity) requireActivity()).showProgressBar();
            userViewModel.updateCurrentUser(null, Collections.singletonList(fromTrash), null);
            return;
        }
        adapter.refresh();
    }

    public void publishClicked(int position) {
        ManechStore store = adapter.getItem(position);
        if (store.getPublishDate() == null) {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.publish_store_title)
                    .setMessage(R.string.publish_store_confirm)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            store.setPublishDate(new Date());
                            adapter.refresh();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.unpublish_store_title)
                    .setMessage(R.string.unpublish_store_confirm)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            store.setPublishDate(null);
                            adapter.refresh();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }
}