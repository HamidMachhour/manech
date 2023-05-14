package com.manech.manechapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.data.User;

import java.util.List;
import java.util.UUID;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface UserDAO {
    @Insert(onConflict = REPLACE)
    public long insert(User user);

    @Query("SELECT * FROM User WHERE email=:email and password=:password and trashDate is null LIMIT 1 ")
    public User userAuth(String email, String password);

    @Query("SELECT * FROM User WHERE email=:email LIMIT 1 ")
    User userByEmail(String email);
/*
    @Query("update User set userName =:userName,pictureId =:pictureId, password =:password, " +
            "defaultAddressId=:defaultAddress, deliveryAddressId=:deliveryAddress, billingAddressId=:billingAddress " +
            "where id= :id and password =:oldPassword")
    public void updatePassword(UUID id, String userName, UUID pictureId, String oldPassword, String password, UUID defaultAddress, UUID deliveryAddress, UUID billingAddress);

    @Query("update User set userName =:userName,pictureId =:pictureId, " +
            "defaultAddressId=:defaultAddress, deliveryAddressId=:deliveryAddress, billingAddressId=:billingAddress where id= :id ")
    public void update(UUID id, String userName, UUID pictureId, UUID defaultAddress, UUID deliveryAddress, UUID billingAddress);
*/
    //todo
    @Query("delete from User where id=:userId")
    int delete(UUID userId);

    @Query("update user set trashDate=CURRENT_TIMESTAMP where id=:userId")
    void disableUser(UUID userId);

    @Query("update ManechStore set trashDate=CURRENT_TIMESTAMP where userId=:userId")
    void storesToTrash(UUID userId);


    @Query("SELECT * from User WHERE id=:userId and trashDate is null")
    User userById(UUID userId);

    @Update
    int updateUser(User user);

    @Query("Select * from ManechStore where userId=:userId")
    List<ManechStore> fetchUserStores(UUID userId);
}
