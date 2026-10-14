package com.manech.manechapp.view.item;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemListAdapter extends RecyclerView.Adapter<ItemViewHolder> implements Filterable {

    private final ItemsFragment listListener;
    private List<Item> itemsList;
    private boolean gridFormat = false;
    private List<Item> allItems;
    private int viewMode = ItemsFragment.BUY_MODE;
    private final UUID defaultStore;
    private String query = "";
    private Double minPrice;
    private Double maxPrice;
    private int sortMode;
    private boolean favoritesFirst;
    private boolean inStockOnly;


    private Filter itemsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> filteredList = new ArrayList<>();
            String filterPattern = constraint == null ? "" : constraint.toString().toLowerCase().trim();
            for (Item item : allItems) {
                String text = (item.getTitle() == null ? "" : item.getTitle()) + " "
                        + (item.getCode() == null ? "" : item.getCode()) + " "
                        + (item.getDescription() == null ? "" : item.getDescription());
                double price = item.getPrice() == null ? 0.0 : item.getPrice();
                double quantity = item.getQuantity() == null ? 0.0 : item.getQuantity();
                if ((!filterPattern.isEmpty() && !text.toLowerCase().contains(filterPattern))
                        || (minPrice != null && price < minPrice)
                        || (maxPrice != null && price > maxPrice)
                        || (inStockOnly && quantity <= 0.0)) {
                    continue;
                }
                filteredList.add(item);
            }
            sortItems(filteredList);
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemsList.clear();
            itemsList.addAll((List<Item>) results.values);
            query = constraint == null ? "" : constraint.toString();
            notifyDataSetChanged();
        }
    };

    public List<Item> getItemsList() {
        return itemsList;
    }

    public ItemListAdapter(List<Item> itemsList, ItemsFragment listener, UUID defaultStore) {
        this.itemsList = itemsList;
        this.allItems = new ArrayList<>(itemsList);
        this.listListener = listener;
        this.defaultStore = defaultStore;
    }

    public void setViewMode(int viewMode) {
        this.viewMode = viewMode;
    }

    public void replaceItems(List<Item> items) {
        allItems = new ArrayList<>(items);
        itemsFilter.filter(query);
    }

    private void sortItems(List<Item> list) {
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
            java.util.Collections.sort(list, comparator);
        }
        if (favoritesFirst) {
            // stable sort keeps the chosen order inside each group
            java.util.Collections.sort(list, (a, b) -> Boolean.compare(listListener.isFavorite(b), listListener.isFavorite(a)));
        }
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

    public void setFilterCriteria(Double minPrice, Double maxPrice, boolean inStockOnly, int sortMode, boolean favoritesFirst) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.inStockOnly = inStockOnly;
        this.sortMode = sortMode;
        this.favoritesFirst = favoritesFirst;
        itemsFilter.filter(query);
    }

    public void setGridFormat(boolean gridFormat) {
        this.gridFormat = gridFormat;
    }

    public boolean isGridFormat() {
        return gridFormat;
    }

    @Override
    public int getItemViewType(final int position) {
        return gridFormat ? R.layout.item_gridview : R.layout.item_listview;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        view.setOnClickListener(this.listListener);
        return new ItemViewHolder(view, new ItemClickListener() {

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
            public void addFavorite(int layoutPosition) {
                listListener.addFavorite(layoutPosition);
            }

            @Override
            public void addCart(int layoutPosition) {
                listListener.addCart(layoutPosition);
            }

            @Override
            public void highlight(int layoutPosition) {
                listListener.highlight(layoutPosition);
            }

            @Override
            public void clicked(int position) {

            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemsList.get(position);
        Double qte;
        qte = item.getQuantity() == null ? 0.0 : item.getQuantity();

        holder.getTvTitle().setText(item.getTitle());
        holder.getTvDescription().setText(item.getDescription());
        holder.getTvItemCode().setText(item.getCode());
        holder.getTvPrice().setText("DH " + item.getPrice());
        holder.getTvState().setText(qte + " In stock");
        holder.getImgFavorite().setImageResource(
                listListener.isFavorite(item) ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        holder.getImgFavorite().setContentDescription(holder.itemView.getContext().getString(
                listListener.isFavorite(item) ? R.string.remove_favorite : R.string.add_favorite));

        int actionsMode = this.viewMode == ItemsFragment.FAVORITES_MODE
                || this.viewMode == ItemsFragment.PUBLIC_STORE_ITEMS_MODE
                ? ItemsFragment.BUY_MODE : this.viewMode;
        holder.getBuyActionsLayout().setVisibility(actionsMode == ItemsFragment.BUY_MODE ? View.VISIBLE : View.GONE);

        holder.getTrashActionsLayout().setVisibility(viewMode == ItemsFragment.TRASH_MODE ? View.VISIBLE : View.GONE);

        holder.getActiveActionsLayout().setVisibility(viewMode == ItemsFragment.MANAGE_MODE ? View.VISIBLE : View.GONE);

        holder.getImgAdd2Cart().setVisibility((actionsMode == ItemsFragment.BUY_MODE && qte > 0) ? View.VISIBLE : View.GONE);

       // new DownloadImageTask(holder.getImgItem()).execute(item.getImgURL());
        loadDefaultImage(item,holder);
        holder.getImgDuplicate().setVisibility((defaultStore == null || item.getTrashDate() != null) ? View.GONE : View.VISIBLE);

        if (item.getPublishDate() != null) {
            holder.getImgPuplish().setColorFilter(ContextCompat.getColor(holder.getImgPuplish().getContext(), R.color.colorPrimaryDark));
        }

        final boolean b = defaultStore != null && defaultStore == item.getStore();
        holder.getImgHighlight().setVisibility(b ? View.VISIBLE : View.GONE);
        /*if(b){
            holder.getTvState().setText(item.getQuantity() + " In stock \n(Local)");
        }*/
    }

    private void loadDefaultImage(Item item, ItemViewHolder holder) {
        final ImageView imageView = holder.getImgItem();
        final ItemImage itemImage = item.getImage();
        MyUtils.loadResourceImage(itemImage, imageView);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public Filter getFilter() {
        return itemsFilter;
    }

    public Item getItem(int itemPosition) {
        return itemsList.get(itemPosition);
    }

}