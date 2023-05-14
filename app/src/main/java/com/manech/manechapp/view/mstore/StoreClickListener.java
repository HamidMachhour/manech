package com.manech.manechapp.view.mstore;

public interface StoreClickListener {

    void duplicateClicked(int layoutPosition);

    void moveToTrashClicked(int layoutPosition);

    void setDefaultStoreClicked(int layoutPosition);

    void deleteClicked(int layoutPosition);

    void restoreClicked(int layoutPosition);

    void publishClicked(int layoutPosition);

    void storeClicked(int layoutPosition);
}
