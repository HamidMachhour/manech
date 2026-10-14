package com.manech.manechapp.view.item.buy;

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

public class ItemsBuyAdapter extends RecyclerView.Adapter<ItemBuyViewHolder> implements Filterable {

    private final ItemsBuyFragment listListener;
    private List<Item> itemsList;
    private boolean gridFormat = false;
    private List<Item> allItems;

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

    public ItemsBuyAdapter(List<Item> itemsList, ItemsBuyFragment listener) {
        this.itemsList = itemsList;
        this.allItems = new ArrayList<>(itemsList);
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
        return gridFormat ? R.layout.item_gridview_buy : R.layout.item_listview_buy ;
    }

    @NonNull
    @Override
    public ItemBuyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ItemBuyViewHolder(view, new ItemClickListener() {

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
    public void onBindViewHolder(@NonNull ItemBuyViewHolder holder, int position) {
        Item item = itemsList.get(position);
        Double qte;
        qte = item.getQuantity() == null ? 0. : item.getQuantity();

        holder.getTvTitle().setText(item.getTitle());

        holder.getTvItemCode().setText(item.getCode());
        holder.getTvPrice().setText("DH " + item.getPrice());
        holder.getTvState().setText(qte + " In stock");

        loadDefaultImage(item,holder);

        holder.getImgHighlight().setVisibility(View.VISIBLE);

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

    private void loadDefaultImage(Item item,  ItemBuyViewHolder holder) {
        final ImageView imageView = holder.getImgItem();
        final ItemImage itemImage = item.getImage();
        MyUtils.loadResourceImage(itemImage, imageView);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public List<Item> getItemsList() {
        return itemsList;
    }

    @Override
    public Filter getFilter() {
        return itemsFilter;
    }

    public Item getItem(int itemPosition) {
        return itemsList.get(itemPosition);
    }

}