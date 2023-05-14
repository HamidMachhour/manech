package com.manech.manechapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.manech.manechapp.helper.MyUtils;

import java.util.UUID;

@Entity
public class DeliveryMethod extends ManechEntity{

    private String name;

    @Ignore
    public static final String POS="Pick Up Store - POS";
    @Ignore
    public static final String HOME_DELIVERY="Home Delivery";
    public DeliveryMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
