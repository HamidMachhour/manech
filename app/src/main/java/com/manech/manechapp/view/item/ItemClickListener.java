package com.manech.manechapp.view.item;

public interface ItemClickListener {

    void duplicateClicked(int layoutPosition);

    void moveToTrashClicked(int layoutPosition);

    void deleteClicked(int layoutPosition);

    void restoreClicked(int layoutPosition);

    void publishClicked(int layoutPosition);

    void addFavorite(int layoutPosition);

    void addCart(int layoutPosition);

    void highlight(int layoutPosition);
    void clicked(int position);

    default void joinClicked(int layoutPosition) {
    }
}
