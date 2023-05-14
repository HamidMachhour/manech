package com.manech.manechapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.data.images.ItemImage;
import com.manech.manechapp.data.images.StoreImage;
import com.manech.manechapp.data.images.UserImage;

import java.util.List;
import java.util.UUID;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ResourceDAO {
    @Insert(onConflict = REPLACE)
    public void insertStoreImage(StoreImage storeImage);

    @Query("select * from StoreImage where storeId=:storeId")
    List<StoreImage> fetchStoreImages(UUID storeId);

    @Delete
    int deleteStoreImage(StoreImage storeImage);

    @Query("select * from StoreImage where id=:imageId")
    StoreImage fetchStoreImage(UUID imageId);


    @Query("delete from StoreImage where storeId=:storeId ")
    void deleteStoreImages(UUID storeId);

    @Update
    void updateStore(ManechStore store);

    @Query("select * from UserImage where id=:imageId")
    UserImage fetchUserImage(UUID imageId);

    @Query("select * from UserImage where userId=:userId")
    List<UserImage> fetchUserImages(UUID userId);

    @Insert(onConflict = REPLACE)
    void insertUserImage(UserImage userImage);

    @Delete
    void deleteUserImage(UserImage userImage);

    @Query("select * from ItemImage where itemId=:itemId")
    List<ItemImage> fetchItemImages(UUID itemId);

    @Query("select * from ItemImage where id=:imageId")
    ItemImage fetchItemImage(UUID imageId);

    @Insert(onConflict = REPLACE)
    void insertItemImage(ItemImage itemImage);

    @Delete
    void deleteItemImage(ItemImage itemImage);
}
