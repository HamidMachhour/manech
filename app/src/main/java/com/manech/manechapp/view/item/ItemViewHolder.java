package com.manech.manechapp.view.item;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;


public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ItemClickListener listener;
    private final TextView tvTitle;
    private final TextView tvState;
    private final TextView tvItemCode;
    private final TextView tvPrice;
    private final TextView tvDescription;

    private final ImageView imgTrash;
    private final ImageView imgDuplicate;
    private final ImageView imgPuplish;

    private final ImageView imgRestore;
    private final ImageView imgDelete;

    private final ImageView imgFavorite;
    private final ImageView imgAdd2Cart;

    private final ImageView imgHighlight;

    private LinearLayout trashActionsLayout;
    private LinearLayout activeActionsLayout;
    private LinearLayout actionsLayout;
    private LinearLayout buyerActionsLayout;

    private final ImageView imgItem;

    public ItemViewHolder(@NonNull View itemView, ItemClickListener listener) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvState = itemView.findViewById(R.id.tvState);
        tvItemCode = itemView.findViewById(R.id.tvItemCode);
        tvPrice = itemView.findViewById(R.id.tvPrice);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        imgItem = itemView.findViewById(R.id.imgItem);
        //actions
        imgTrash = itemView.findViewById(R.id.imgTrash);
        imgTrash.setOnClickListener(this);
        imgDuplicate = itemView.findViewById(R.id.imgDuplicate);
        imgDuplicate.setOnClickListener(this);
        imgPuplish = itemView.findViewById(R.id.imgPublish);
        imgPuplish.setOnClickListener(this);
        imgHighlight = itemView.findViewById(R.id.imgHighlight);
        imgHighlight.setOnClickListener(this);
        // layouts
        actionsLayout = itemView.findViewById(R.id.actions_layout);
        trashActionsLayout = itemView.findViewById(R.id.trash_actions_layout);
        activeActionsLayout = itemView.findViewById(R.id.active_actions_layout);
        buyerActionsLayout = itemView.findViewById(R.id.buyer_actions_layout);
        // actions in trash
        imgRestore = itemView.findViewById(R.id.imgRestore);
        imgRestore.setOnClickListener(this);
        imgDelete = itemView.findViewById(R.id.imgDeleteForever);
        imgDelete.setOnClickListener(this);
        // costumer actions
        imgFavorite = itemView.findViewById(R.id.imgFavorite);
        imgFavorite.setOnClickListener(this);
        imgAdd2Cart = itemView.findViewById(R.id.imgAdd2Cart);
        imgAdd2Cart.setOnClickListener(this);

        this.listener = listener;
    }

    public LinearLayout getBuyActionsLayout() {
        return buyerActionsLayout;
    }

    public LinearLayout getActionsLayout() {
        return actionsLayout;
    }

    public LinearLayout getTrashActionsLayout() {
        return trashActionsLayout;
    }

    public LinearLayout getActiveActionsLayout() {
        return activeActionsLayout;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public TextView getTvState() {
        return tvState;
    }

    public TextView getTvItemCode() {
        return tvItemCode;
    }

    public TextView getTvPrice() {
        return tvPrice;
    }

    public TextView getTvDescription() {
        return tvDescription;
    }

    public ImageView getImgTrash() {
        return imgTrash;
    }

    public ImageView getImgDuplicate() {
        return imgDuplicate;
    }

    public ImageView getImgPuplish() {
        return imgPuplish;
    }

    public ImageView getImgRestore() {
        return imgRestore;
    }

    public ImageView getImgDelete() {
        return imgDelete;
    }

    public ImageView getImgItem() {
        return imgItem;
    }

    public ImageView getImgHighlight() {
        return imgHighlight;
    }

    public ImageView getImgAdd2Cart() {
        return imgAdd2Cart;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgTrash:
                listener.moveToTrashClicked(this.getLayoutPosition());
                break;
            case R.id.imgDuplicate:
                listener.duplicateClicked(this.getLayoutPosition());
                break;
            case R.id.imgDeleteForever:
                listener.deleteClicked(this.getLayoutPosition());
                break;
            case R.id.imgRestore:
                listener.restoreClicked(this.getLayoutPosition());
                break;
            case R.id.imgPublish:
                listener.publishClicked(this.getLayoutPosition());
                break;
            case R.id.imgFavorite:
                listener.addFavorite(this.getLayoutPosition());
                break;
            case R.id.imgAdd2Cart:
                listener.addCart(this.getLayoutPosition());
                break;
            case R.id.imgHighlight:
                listener.highlight(this.getLayoutPosition());
                break;
            default:
                break;
        }
    }
}
