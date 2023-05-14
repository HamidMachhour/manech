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


    private Filter itemsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(allItems);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Item item : allItems) {
                    String text = item.toString();
                    if (text.toLowerCase().trim().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemsList.clear();
            itemsList.addAll((List) results.values);
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

        holder.getBuyActionsLayout().setVisibility(viewMode == ItemsFragment.BUY_MODE ? View.VISIBLE : View.GONE);

        holder.getTrashActionsLayout().setVisibility(viewMode == ItemsFragment.TRASH_MODE ? View.VISIBLE : View.GONE);

        holder.getActiveActionsLayout().setVisibility(viewMode == ItemsFragment.MANAGE_MODE ? View.VISIBLE : View.GONE);

        holder.getImgAdd2Cart().setVisibility((viewMode == ItemsFragment.BUY_MODE && qte > 0) ? View.VISIBLE : View.GONE);

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
        if(itemImage!=null){
          MyUtils.loadResourceImage(itemImage,imageView);
        }
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