package com.manech.manechapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.manech.manechapp.helper.MyUtils;

import java.util.UUID;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = Cart.class, parentColumns = "id", childColumns = "cartId", onDelete = CASCADE),
                @ForeignKey(entity = Item.class, parentColumns = "id", childColumns = "itemId", onDelete = CASCADE)

        },
        indices = { @Index("cartId"),@Index("itemId")})
public class CartItem extends ManechEntity{
    private UUID cartId;
    private UUID itemId;
    private double quantity;
    private double discount=0.0;
    @Ignore
    private Item item;

    public CartItem( UUID cartId, UUID itemId, double quantity) {
        this.cartId = cartId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public double getDiscount() {
        return MyUtils.ceil2f(discount);
    }

    public void setDiscount(double discount) {
        this.discount = MyUtils.ceil2f(discount);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public double getQuantity() {
        return MyUtils.ceil2f(quantity);
    }

    public void setQuantity(double quantity) {
        this.quantity = MyUtils.ceil2f(quantity);
    }

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }
}
