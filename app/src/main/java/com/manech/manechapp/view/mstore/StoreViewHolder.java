package com.manech.manechapp.view.mstore;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;


public class StoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final StoreClickListener listener;
    private final TextView tvName;
    private final  TextView tvState;
    private final TextView tvAddress;
    private  final TextView tvPhones;
    private final RadioButton radioSetDefault;
    private final ImageView imgTrash;
    private final ImageView imgDuplicate;
    private final ImageView imgPuplish;

    private final ImageView imgRestore;
    private final ImageView imgDelete;

    private final ImageView imgStore;

    private LinearLayout trashActionsLayout;
    private LinearLayout activeActionsLayout;

    public StoreViewHolder(@NonNull View itemView, StoreClickListener listener) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        tvState = itemView.findViewById(R.id.tvState);
        tvAddress = itemView.findViewById(R.id.tvAddress);
        tvPhones = itemView.findViewById(R.id.tvPhones);
        imgStore = itemView.findViewById(R.id.imageView);
        imgStore.setOnClickListener(this);
        // layouts
        trashActionsLayout=itemView.findViewById(R.id.trash_actions_layout);
        activeActionsLayout=itemView.findViewById(R.id.active_actions_layout);
        //actions
        radioSetDefault = itemView.findViewById(R.id.radioSetDefault);
        radioSetDefault.setOnClickListener(this);
        imgTrash = itemView.findViewById(R.id.imgTrash);
        imgTrash.setOnClickListener(this);
        imgDuplicate = itemView.findViewById(R.id.imgDuplicate);
        imgDuplicate.setOnClickListener(this);
        imgPuplish = itemView.findViewById(R.id.imgPublish);
        imgPuplish.setOnClickListener(this);

        // actions in trash
        imgRestore = itemView.findViewById(R.id.imgRestore);
        imgRestore.setOnClickListener(this);
        imgDelete = itemView.findViewById(R.id.imgDeleteForever);
        imgDelete.setOnClickListener(this);

        this.listener = listener;
    }

    public LinearLayout getTrashActionsLayout() {
        return trashActionsLayout;
    }

    public LinearLayout getActiveActionsLayout() {
        return activeActionsLayout;
    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvState() {
        return tvState;
    }

    public TextView getTvAddress() {
        return tvAddress;
    }

    public TextView getTvPhones() {
        return tvPhones;
    }

    public RadioButton getRadioSetDefault() {
        return radioSetDefault;
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

    public ImageView getImgStore() {
        return imgStore;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radioSetDefault:
                listener.setDefaultStoreClicked(this.getLayoutPosition());
                break;
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
            case R.id.imageView:
                listener.storeClicked(this.getLayoutPosition());
                break;
            default:
                break;
        }
    }
}
