package com.manech.manechapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.manech.manechapp.helper.MyUtils;

import java.util.List;
import java.util.UUID;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.NO_ACTION;
import static androidx.room.ForeignKey.SET_NULL;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = Order.class, parentColumns = "id", childColumns = "orderId", onDelete = CASCADE),
                @ForeignKey(entity = Item.class, parentColumns = "id", childColumns = "itemId", onDelete = NO_ACTION)
        },
        indices = { @Index("orderId"),@Index("itemId")})
public class OrderLine extends ManechEntity{

    private UUID itemId;
    private UUID store;
    private UUID orderId;
    private double quantity;
    private String code;
    private String title;
    private double itemPrice;
    private double fee;
    private double discount;

    @Ignore
    private Item item;

    public OrderLine(UUID itemId, UUID store, UUID orderId, double quantity, String code, String title, double itemPrice, double fee, double discount) {
        this.itemId = itemId;
        this.store=store;
        this.orderId = orderId;
        this.quantity = quantity;
        this.code = code;
        this.title = title;
        this.itemPrice = itemPrice;
        this.fee = fee;
        this.discount=discount;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public UUID getStore() {
        return store;
    }

    public void setStore(UUID store) {
        this.store = store;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPrice() {
        return MyUtils.ceil2f(((getFee()+ getItemPrice())* getQuantity()) * (1.0-(getDiscount()/100.0)));
    }

    @NonNull
    @Override
    public String toString() {
        return   this.title +" \n"
                +this.quantity +" \n"
                +this.code +" \n"
                +this.itemPrice +" \n"
                +this.fee +" \n"
                +this.discount+" \n";
    }
}
