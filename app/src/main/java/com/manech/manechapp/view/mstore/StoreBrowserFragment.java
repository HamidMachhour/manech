package com.manech.manechapp.view.mstore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.R;
import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.helper.FavoriteIds;
import com.manech.manechapp.helper.SecureSharedPref;
import com.manech.manechapp.model.MStoreViewModel;
import com.manech.manechapp.view.item.ItemsFragment;

import java.util.UUID;

public class StoreBrowserFragment extends Fragment {
    private MStoreViewModel storeViewModel;
    private SecureSharedPref preferences;
    private UUID ownerId;
    private RecyclerView storesView;
    private StoreBrowserAdapter adapter;
    private boolean copyPending;

    protected boolean isFavoritesOnly() {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        preferences = SecureSharedPref.getInstance(requireContext(), getString(R.string.preference_file_key));
        ownerId = preferences.getUUID(getString(R.string.current_user_id), null);
        storeViewModel = ((MainActivity) requireActivity()).getViewModel(MStoreViewModel.class);
        storeViewModel.getStoresMutableLiveData().observe(this, stores -> {
            if (stores != null && adapter != null) {
                adapter.setStores(stores);
            }
            ((MainActivity) requireActivity()).hideProgressBar();
        });
        storeViewModel.getCopiedStoreMutableLiveData().observe(this, store -> {
            if (copyPending) {
                copyPending = false;
                if (store == null) {
                    Toast.makeText(requireContext(), R.string.store_copy_failed, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireContext(), R.string.store_copy_success, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_browser, container, false);
        requireActivity().setTitle(isFavoritesOnly() ? R.string.favorite_stores : R.string.public_stores);
        storesView = view.findViewById(R.id.public_stores_list);
        storesView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new StoreBrowserAdapter(this, preferences, ownerId, isFavoritesOnly());
        storesView.setAdapter(adapter);
        StoreFilterCard.bind(view, isFavoritesOnly() ? 0 : R.string.favorites_only, adapter::setCriteria);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        storeViewModel.fetchPublicStores();
    }

    void toggleFavorite(ManechStore store) {
        if (ownerId == null) {
            Toast.makeText(requireContext(), R.string.login_required, Toast.LENGTH_LONG).show();
            return;
        }
        FavoriteIds.toggleStore(preferences, ownerId, store.getId());
        adapter.refreshFavorites();
    }

    void copyStore(ManechStore store) {
        if (ownerId == null) {
            Toast.makeText(requireContext(), R.string.login_required, Toast.LENGTH_LONG).show();
            return;
        }
        copyPending = true;
        storeViewModel.copyPublicStore(store, ownerId);
    }

    void viewStoreItems(ManechStore store) {
        Bundle arguments = new Bundle();
        arguments.putString("store_id", store.getId().toString());
        arguments.putInt("view_mode", ItemsFragment.PUBLIC_STORE_ITEMS_MODE);
        String tag = getString(R.string.public_store_items_view) + store.getId();
        ((MainActivity) requireActivity()).initFragment(ItemsFragment.class, tag, arguments);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_back) {
            ((MainActivity) requireActivity()).actionBack();
            return true;
        }
        if (item.getItemId() == R.id.action_refresh) {
            storeViewModel.fetchPublicStores();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.store_browser_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}
