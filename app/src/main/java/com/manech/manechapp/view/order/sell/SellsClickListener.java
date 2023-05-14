package com.manech.manechapp.view.order.sell;

import android.text.TextWatcher;

import com.manech.manechapp.view.order.LineClicked;

public interface SellsClickListener extends LineClicked {

    void itemUpdateClicked(int layoutPosition, double fee, double discount);
}
