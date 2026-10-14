package com.manech.manechapp.view.item.manage;

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
import com.manech.manechapp.data.Item;
import com.manech.manechapp.data.images.ItemImage;
import com.manech.manechapp.helper.MyUtils;
import com.manech.manechapp.view.item.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ItemsManagerAdapter extends RecyclerView.Adapter<ItemManagerViewHolder> implements Filterable {

    private final ItemsManagerFragment listListener;
    private List<Item> itemsList;
    private boolean gridFormat = false;
    private List<Item> allItems;

    private String query = "";
    private Double minPrice;
    private Double maxPrice;
    private boolean inStockOnly;
    private int sortMode;

    private final Filter itemsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            query = constraint == null ? "" : constraint.toString();
            FilterResults results = new FilterResults();
            results.values = compute();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemsList.clear();
            itemsList.addAll((List<Item>) results.values);
            notifyDataSetChanged();
        }
    };

    private List<Item> compute() {
        List<Item> result = new ArrayList<>();
        String pattern = query.toLowerCase().trim();
        for (Item item : allItems) {
            String text = (item.getTitle() == null ? "" : item.getTitle()) + " "
                    + (item.getCode() == null ? "" : item.getCode()) + " "
                    + (item.getDescription() == null ? "" : item.getDescription());
            double price = item.getPrice() == null ? 0.0 : item.getPrice();
            double quantity = item.getQuantity() == null ? 0.0 : item.getQuantity();
            if ((!pattern.isEmpty() && !text.toLowerCase().contains(pattern))
                    || (minPrice != null && price < minPrice)
                    || (maxPrice != null && price > maxPrice)
                    || (inStockOnly && quantity <= 0.0)) {
                continue;
            }
            result.add(item);
        }
        java.util.Comparator<Item> comparator = null;
        switch (sortMode) {
            case 1:
                comparator = (a, b) -> Double.compare(priceOf(a), priceOf(b));
                break;
            case 2:
                comparator = (a, b) -> Double.compare(priceOf(b), priceOf(a));
                break;
            case 3:
                comparator = (a, b) -> titleOf(a).compareToIgnoreCase(titleOf(b));
                break;
            case 4:
                comparator = (a, b) -> titleOf(b).compareToIgnoreCase(titleOf(a));
                break;
            case 5:
                comparator = (a, b) -> Double.compare(stockOf(b), stockOf(a));
                break;
            default:
                break;
        }
        if (comparator != null) {
            java.util.Collections.sort(result, comparator);
        }
        return result;
    }

    private static double priceOf(Item item) {
        return item.getPrice() == null ? 0.0 : item.getPrice();
    }

    private static double stockOf(Item item) {
        return item.getQuantity() == null ? 0.0 : item.getQuantity();
    }

    private static String titleOf(Item item) {
        return item.getTitle() == null ? "" : item.getTitle();
    }

    public void setFilterCriteria(Double minPrice, Double maxPrice, boolean inStockOnly, int sortMode) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.inStockOnly = inStockOnly;
        this.sortMode = sortMode;
        refresh();
    }

    // re-applies the criteria on the full list, then redraws
    public void refresh() {
        itemsList.clear();
        itemsList.addAll(compute());
        notifyDataSetChanged();
    }

    public ItemsManagerAdapter(List<Item> items, ItemsManagerFragment listener) {
        this.allItems = items;
        this.itemsList = new ArrayList<>(items);
        this.listListener = listener;
    }

    public void setGridFormat(boolean gridFormat) {
        this.gridFormat = gridFormat;
    }

    public boolean isGridFormat() {
        return gridFormat;
    }

    @Override
    public int getItemViewType(final int position) {
        return gridFormat ? R.layout.item_gridview_manage : R.layout.item_listview_manage ;
    }

    @NonNull
    @Override
    public ItemManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ItemManagerViewHolder(view, new ItemClickListener() {

            @Override
            public void duplicateClicked(int p) {
                listListener.duplicateClicked(p);
            }

            @Override
            public void moveToTrashClicked(int p) {
                listListener.moveToTrashClicked(p);
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
            public void joinClicked(int position) {
                listListener.joinClicked(position);
            }

            @Override
            public void addFavorite(int layoutPosition) {

            }

            @Override
            public void addCart(int layoutPosition) {
            }

            @Override
            public void highlight(int layoutPosition) {
                listListener.highlight(layoutPosition);
            }

            @Override
            public void clicked(int position) {
                listListener.itemClicked(position);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ItemManagerViewHolder holder, int position) {
        Item item = itemsList.get(position);
        Double qte;
        qte = item.getQuantity() == null ? 0. : item.getQuantity();

        holder.getTvTitle().setText(item.getTitle());

        holder.getTvItemCode().setText(item.getCode());
        holder.getTvPrice().setText("DH " + item.getPrice());
        holder.getTvState().setText(qte + " In stock");

        loadDefaultImage(item,holder);

        holder.getImgHighlight().setVisibility(View.VISIBLE);
        holder.getImgJoin().setVisibility(item.getStore() == null ? View.VISIBLE : View.GONE);

        holder.getTrashActionsLayout().setVisibility((item.getTrashDate() != null) ? View.VISIBLE : View.GONE);
        holder.getActiveActionsLayout().setVisibility((item.getTrashDate() != null) ? View.GONE : View.VISIBLE);

        if (item.getPublishDate() != null) {
            holder.getImgPublish().setColorFilter(ContextCompat.getColor(holder.getImgPublish().getContext(), R.color.colorPrimaryDark));
            holder.getTvState().setText(R.string.published_state);
        }else{
            holder.getImgPublish().setColorFilter(ContextCompat.getColor(holder.getImgPublish().getContext(), R.color.colorAccent));
            holder.getTvState().setText("");
        }

        if (item.getTrashDate() != null) {
            holder.getImgTrash().setColorFilter(ContextCompat.getColor(holder.getImgTrash().getContext(), R.color.colorPrimaryDark));
            holder.getTvState().setText(R.string.in_trash_state);
        }else {
            holder.getImgTrash().setColorFilter(ContextCompat.getColor(holder.getImgTrash().getContext(), R.color.colorAccent));
            holder.getTvState().setText("");
        }
    }

    private void loadDefaultImage(Item item,  ItemManagerViewHolder holder) {
        final ImageView imageView = holder.getImgItem();
        final ItemImage itemImage = item.getImage();
        MyUtils.loadResourceImage(itemImage, imageView);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    // full list, including the items hidden by the current criteria
    public List<Item> getItemsList() {
        return allItems;
    }

    @Override
    public Filter getFilter() {
        return itemsFilter;
    }

    public Item getItem(int itemPosition) {
        return itemsList.get(itemPosition);
    }

}