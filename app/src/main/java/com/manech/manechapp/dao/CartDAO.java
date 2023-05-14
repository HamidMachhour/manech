package com.manech.manechapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.manech.manechapp.data.CartItem;
import com.manech.manechapp.data.Cart;
import com.manech.manechapp.data.Item;

import java.util.List;
import java.util.UUID;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CartDAO {
    @Insert(onConflict = REPLACE)
    void insertCart(Cart cart);

    @Delete
    int delete(Cart cart);

    @Query("select * from cart where  ownerId=:ownerId limit 1 ")
    Cart cartByOwner(UUID ownerId);

    @Query("delete from cartitem where cartId=:cartId")
    void clearCart(UUID cartId);

    @Query("select * from cartitem where cartId=:cartId ")
    List<CartItem> cartItems(UUID cartId);

    @Query("select * from Item where id=:itemId ")
    Item itemById(UUID itemId);

    @Delete
    int deleteCartItem(CartItem cartItem);

    @Update
    int updateCartItem(CartItem cartItem);

    @Query("select * from cartitem where cartId=:cartId and itemId=:itemId Limit 1")
    CartItem cartItem(UUID cartId, UUID itemId);

    @Insert(onConflict = REPLACE)
    void insertCartItem(CartItem cartItem);
}
