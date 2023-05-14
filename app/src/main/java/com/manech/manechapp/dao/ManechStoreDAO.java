package com.manech.manechapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.data.images.StoreImage;

import java.util.List;
import java.util.UUID;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ManechStoreDAO {
    @Insert(onConflict = REPLACE)
    public void insert(ManechStore store);

    @Update
    public int updateStore(ManechStore store);

    //todo
    @Query("DELETE FROM ManechStore where userId = :userId")
    public int deleteAll(UUID userId);

    @Query("SELECT * from ManechStore WHERE id= :storeId")
    public ManechStore storeById(UUID storeId);

    @Query("SELECT * from ManechStore WHERE userId= :userId")
    public List<ManechStore> storeByUser(UUID userId);

    //todo
    @Query("delete from ManechStore where id=:storeId")
    int delete(UUID storeId);


    @Query("Select * from ManechStore where userId=:userId and trashDate is not null ")
    List<ManechStore> storesInTrash(UUID userId);

    @Query("Select * from ManechStore where userId=:userId")
    List<ManechStore> fetchStores(UUID userId);

    @Query("update ManechStore set trashDate=CURRENT_TIMESTAMP where id=:storeId")
    void moveToTrash(UUID storeId);

    @Query("update ManechStore set trashDate=null where id=:storeId")
    void restoreFromTrash(UUID storeId);

    @Query("update ManechStore set publishDate=CURRENT_TIMESTAMP where id=:storeId")
    void publishing(UUID storeId);

    @Query("update ManechStore set publishDate=null where id=:storeId")
    void privating(UUID storeId);

    @Insert(onConflict = REPLACE)
    void insertStorePicture(StoreImage storeImage);

    @Query("delete from StoreImage where storeId=:storeId ")
    void deleteStorePictures(UUID storeId);

    @Query("select * from storeimage where id =:imageId ")
    StoreImage fetchStoreImage(UUID imageId);
}
