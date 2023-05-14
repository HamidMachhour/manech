package com.manech.manechapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.manech.manechapp.data.Address;

import java.util.List;
import java.util.UUID;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AddressDAO {
    @Insert(onConflict = REPLACE)
    public void insert(Address address);

    @Query("select * from address where userId=:userId")
    List<Address> getAddresses(UUID userId);

    @Delete
    int deleteAddress(Address address);

    @Query("select * from address where id=:addressId")
    Address addressById(UUID addressId);

    @Update
    int updateAddress(Address address);
}
