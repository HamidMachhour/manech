package com.manech.manechapp.view.mstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.helper.FavoriteIds;
import com.manech.manechapp.helper.MyUtils;
import com.manech.manechapp.helper.SecureSharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StoreBrowserAdapter extends RecyclerView.Adapter<StoreBrowserAdapter.StoreHolder> implements Filterable {
    private final StoreBrowserFragment listener;
    private final SecureSharedPref preferences;
    private final UUID ownerId;
    private final boolean favoritesOnly;
    private final List<ManechStore> allStores = new ArrayList<>();
    private final List<ManechStore> visibleStores = new ArrayList<>();
    private String query = "";
    private int sortMode;
    private boolean favoritesOnlyOption;

    StoreBrowserAdapter(StoreBrowserFragment listener, SecureSharedPref preferences, UUID ownerId, boolean favoritesOnly) {
        this.listener = listener;
        this.preferences = preferences;
        this.ownerId = ownerId;
        this.favoritesOnly = favoritesOnly;
    }

    void setStores(List<ManechStore> stores) {
        allStores.clear();
        allStores.addAll(stores);
        filter(query);
    }

    void setCriteria(String query, int sortMode, boolean favoritesOnlyOption) {
        this.sortMode = sortMode;
        this.favoritesOnlyOption = favoritesOnlyOption;
        filter(query);
    }

    void refreshFavorites() {
        filter(query);
    }

    @NonNull
    @Override
    public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_browser_item, parent, false);
        return new StoreHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreHolder holder, int position) {
        ManechStore store = visibleStores.get(position);
        holder.name.setText(store.getName());
        holder.description.setText(store.getDescription());
        holder.address.setText(store.getAddress() == null ? "" : store.getAddress().toString());
        boolean favorite = FavoriteIds.isStoreFavorite(preferences, ownerId, store.getId());
        holder.phones.setText(store.getAddress() == null ? "" : store.getAddress().getPhones());
        holder.favorite.setImageResource(favorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        holder.favorite.setContentDescription(holder.itemView.getContext().getString(
                favorite ? R.string.remove_favorite : R.string.add_favorite));
        MyUtils.loadResourceImage(store.getDefaultImage(), holder.image);
        holder.favorite.setOnClickListener(view -> listener.toggleFavorite(store));
        holder.view.setOnClickListener(view -> listener.viewStoreItems(store));
        holder.copy.setVisibility(favoritesOnly ? View.GONE : View.VISIBLE);
        holder.copy.setOnClickListener(view -> listener.copyStore(store));
    }

    @Override
    public int getItemCount() {
        return visibleStores.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String normalized = constraint == null ? "" : constraint.toString().trim().toLowerCase();
                List<ManechStore> matches = new ArrayList<>();
                for (ManechStore store : allStores) {
                    if ((favoritesOnly || favoritesOnlyOption) && !FavoriteIds.isStoreFavorite(preferences, ownerId, store.getId())) {
                        continue;
                    }
                    String text = (store.getName() == null ? "" : store.getName()) + " "
                            + (store.getDescription() == null ? "" : store.getDescription())
                            + (store.getAddress() == null ? "" : " " + store.getAddress());
                    if (text.toLowerCase().contains(normalized)) {
                        matches.add(store);
                    }
                }
                if (sortMode == 1 || sortMode == 2) {
                    java.util.Collections.sort(matches, (a, b) -> {
                        final int c = (a.getName() == null ? "" : a.getName())
                                .compareToIgnoreCase(b.getName() == null ? "" : b.getName());
                        return sortMode == 1 ? c : -c;
                    });
                }
                FilterResults results = new FilterResults();
                results.values = matches;
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                query = constraint == null ? "" : constraint.toString();
                visibleStores.clear();
                visibleStores.addAll((List<ManechStore>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    private void filter(String constraint) {
        getFilter().filter(constraint);
    }

    static class StoreHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView description;
        final TextView address;
        final TextView phones;
        final ImageView image;
        final ImageView favorite;
        final ImageView view;
        final ImageView copy;

        StoreHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.store_browser_name);
            description = itemView.findViewById(R.id.store_browser_description);
            address = itemView.findViewById(R.id.store_browser_address);
            phones = itemView.findViewById(R.id.store_browser_phones);
            image = itemView.findViewById(R.id.store_browser_image);
            favorite = itemView.findViewById(R.id.store_browser_favorite);
            view = itemView.findViewById(R.id.store_browser_view);
            copy = itemView.findViewById(R.id.store_browser_copy);
        }
    }
}
