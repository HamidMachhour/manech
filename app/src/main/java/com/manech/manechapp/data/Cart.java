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

@Entity(
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "ownerId", onDelete = CASCADE),
        },
        indices = {@Index(value = "ownerId", unique = true)}
)

public class Cart extends ManechEntity {

    private UUID ownerId;

    @Ignore
    List<CartItem> cartItems = null;

    public Cart(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public long nbItems() {
        return (cartItems == null) ? 0L : cartItems.size();
    }

    public double getPrice() {
        double price = 0.0;
        if (cartItems == null)
            return price;
        for (CartItem ci : cartItems) {
            price += (ci.getItem().getPrice() + ci.getItem().getShipping())*ci.getQuantity();
        }

        return MyUtils.ceil2f(price);
    }

    @NonNull
    @Override
    public String toString() {
        return nbItems() + " ITEMS ~ " + getPrice() + " DH";
    }
}
