package com.manech.manechapp.view.mstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.data.images.StoreImage;
import com.manech.manechapp.helper.MyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.manech.manechapp.helper.MyUtils.loadResourceImage;

public class StoreListAdapter extends RecyclerView.Adapter<StoreViewHolder> implements Filterable {

    private final StoresFragment listListener;
    private  UUID defaultStore;
    private final List<ManechStore> storeList;
    private final List<ManechStore> allStores;
    private String query = "";
    private int sortMode;
    private boolean publishedOnly;

    private final Filter storesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            query = constraint == null ? "" : constraint.toString();
            FilterResults results = new FilterResults();
            results.values = compute();
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            storeList.clear();
            storeList.addAll((List<ManechStore>) results.values);
            notifyDataSetChanged();
        }
    };

    private List<ManechStore> compute() {
        final String pattern = query.toLowerCase().trim();
        final List<ManechStore> result = new ArrayList<>();
        for (ManechStore store : allStores) {
            String text = (store.getName() == null ? "" : store.getName()) + " "
                    + (store.getDescription() == null ? "" : store.getDescription());
            if (store.getAddress() != null) {
                text += " " + store.getAddress().toString();
            }
            if ((!pattern.isEmpty() && !text.toLowerCase().contains(pattern))
                    || (publishedOnly && store.getPublishDate() == null)) {
                continue;
            }
            result.add(store);
        }
        if (sortMode == 1 || sortMode == 2) {
            java.util.Collections.sort(result, (a, b) -> {
                final int c = (a.getName() == null ? "" : a.getName())
                        .compareToIgnoreCase(b.getName() == null ? "" : b.getName());
                return sortMode == 1 ? c : -c;
            });
        }
        return result;
    }

    public void setCriteria(String query, int sortMode, boolean publishedOnly) {
        this.query = query == null ? "" : query;
        this.sortMode = sortMode;
        this.publishedOnly = publishedOnly;
        refresh();
    }

    // re-applies the criteria on the full list, then redraws
    public void refresh() {
        storeList.clear();
        storeList.addAll(compute());
        notifyDataSetChanged();
    }

    // full list, including the stores hidden by the current criteria
    public List<ManechStore> getStoreList() {
        return allStores;
    }

    public void setDefaultStore(UUID defaultStore) {
        this.defaultStore = defaultStore;
    }

    public StoreListAdapter(List<ManechStore> storeList, StoresFragment listener, UUID defaultStore) {
        this.allStores = storeList;
        this.storeList = new ArrayList<>(storeList);

        this.listListener = listener;
        this.defaultStore = defaultStore;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.store_view;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new StoreViewHolder(view, new StoreClickListener() {

            @Override
            public void duplicateClicked(int p) {
                listListener.duplicateClicked(p);
            }

            @Override
            public void moveToTrashClicked(int p) {
                listListener.moveToTrashClicked(p);
            }

            @Override
            public void setDefaultStoreClicked(int p) {
                listListener.setDefaultClicked(p);
            }

            @Override
            public void deleteClicked(int p) {
                listListener.deleteClicked(p);
            }

            @Override
            public void restoreClicked(int position) {
                listListener.restoreClicked(position);
            }

            @Override
            public void publishClicked(int position) {
                listListener.publishClicked(position);
            }

            @Override
            public void storeClicked(int layoutPosition) {
                listListener.storeClicked(layoutPosition);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        ManechStore store = storeList.get(position);
        Address address = store.getAddress();
        holder.getTvName().setText(store.getName());
        loadDefaultImage(store, holder);
        if (address != null) {
            holder.getTvAddress().setText(address.toString());
            holder.getTvPhones().setText(address.getPhones());
        }
        holder.getImgDelete().setVisibility(store.getTrashDate() != null ? View.VISIBLE : View.GONE);
        holder.getActiveActionsLayout().setVisibility(store.getTrashDate() == null ? View.VISIBLE : View.GONE);
        holder.getTrashActionsLayout().setVisibility(store.getTrashDate() != null ? View.VISIBLE : View.GONE);

        holder.getImgTrash().setColorFilter(ContextCompat.getColor(holder.getImgTrash().getContext(), R.color.colorAccent));

        if (store.getPublishDate() != null) {
            holder.getImgPuplish().setColorFilter(ContextCompat.getColor(holder.getImgPuplish().getContext(), R.color.colorPrimaryDark));
            holder.getTvState().setText(R.string.published_state);
        }
        holder.getImgTrash().setColorFilter(ContextCompat.getColor(holder.getImgTrash().getContext(), R.color.colorAccent));

        if (store.getTrashDate() != null) {
            holder.getImgTrash().setColorFilter(ContextCompat.getColor(holder.getImgTrash().getContext(), R.color.colorPrimaryDark));
            holder.getTvState().setText(R.string.in_trash_state);
        }
        holder.getRadioSetDefault().setChecked(store.getId().equals(defaultStore));
    }

    private void loadDefaultImage(ManechStore store, StoreViewHolder holder) {
        final StoreImage storeImage = store.getDefaultImage();
        final ImageView imgStore = holder.getImgStore();
        if (storeImage != null) {
            MyUtils.loadResourceImage(storeImage, imgStore);
        }
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    @Override
    public Filter getFilter() {
        return storesFilter;
    }

    public ManechStore getItem(int itemPosition) {
        return storeList.get(itemPosition);
    }

}