package com.manech.manechapp.view.cart;

public interface CartItemClickListener {


    void updateQtyClicked(int layoutPosition);

    void deleteClicked(int layoutPosition);

    void itemClicked(int layoutPosition);

}
