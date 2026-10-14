package com.manech.manechapp.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.manech.manechapp.data.Item;

import java.util.List;
import java.util.UUID;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ItemDAO {
    @Insert(onConflict = REPLACE)
    public void insert(Item item);

    @Update
    public int updateItem(Item item);

    @Query("SELECT * from Item WHERE id= :itemId")
    public Item itemById(UUID itemId);

    @Query("SELECT * from Item WHERE store= :storeId")
    public List<Item> itemsByStore(UUID storeId);

    @Query("delete from Item where id=:itemId")
    int delete(UUID itemId);

    @Delete
    int delete(Item item);

    @Query("Select * from Item where store=:storeId and trashDate is not null ")
    List<Item> inTrashItems(UUID storeId);

    @Query("Select * from Item where store=:storeId and trashDate is null")
    List<Item> activeItems(UUID storeId);

    @Query("Select * from Item where store is null and trashDate is null")
    List<Item> orphanItems();

    @Query("update Item set store=:storeId where id=:itemId")
    void joinStore(UUID itemId, UUID storeId);

    @Query("update Item set trashDate=CURRENT_TIMESTAMP where id=:itemId")
    void moveToTrash(UUID itemId);

    @Query("update Item set trashDate=null where id=:itemId")
    void restoreFromTrash(UUID itemId);

    @Query("update Item set publishDate=CURRENT_TIMESTAMP where id=:itemId")
    void publishing(UUID itemId);

    @Query("update Item set publishDate=null where id=:itemId")
    void privating(UUID itemId);

    @Query("Select * from Item where publishDate is not null and trashDate is null")
    List<Item> publicItems();

    @Query("Select * from Item where (publishDate is not null or store =:storeId ) and trashDate is null")
    List<Item> publicAndPrivateItems(UUID storeId);

    //todo review how to clean items
    @Query("delete from item WHERE store= :storeId and trashDate is not null ")
    int clearTrash(UUID storeId);

    @Query("Select * from Item where publishDate is not null and trashDate is null and store =:storeId ")
    List<Item> publicItems(UUID storeId);

}
