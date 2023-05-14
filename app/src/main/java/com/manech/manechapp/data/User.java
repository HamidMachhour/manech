package com.manech.manechapp.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;


import com.manech.manechapp.data.images.UserImage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity(indices = {@Index(value = {"email"}, unique = true)})
public class User extends ManechEntity {

    private String userName;
    private String password;
    private String email;
    private Date trashDate;

    private UUID addressId;
    private UUID imageId;
    private UUID storeId;

    @Ignore
    private Address address;

    @Ignore
    private List<UserImage> userImages;
    @Ignore
    private UserImage image;

    @Ignore
    private ManechStore store;

    @Ignore
    private List<ManechStore> stores;

    @Ignore
    private  List<Address> userAddresses ;

    public ManechStore getStore() {
        return store;
    }

    public void setStore(ManechStore store) {
        this.store = store;
    }

    public List<Address> getUserAddresses() {
        return userAddresses;
    }

    public void setUserAddresses(List<Address> userAddresses) {
        this.userAddresses = userAddresses;
    }

    public List<ManechStore> getStores() {
        return stores;
    }

    public void setStores(List<ManechStore> stores) {
        this.stores = stores;
    }

    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    public UUID getStoreId() {
        return storeId;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public UUID getAddressId() {
        return addressId;
    }

    public Date getTrashDate() {
        return trashDate;
    }

    public void setTrashDate(Date trashDate) {
        this.trashDate = trashDate;
    }

    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public List<UserImage> getUserImages() {
        return userImages;
    }

    public void setUserImages(List<UserImage> userImages) {
        this.userImages = userImages;
    }

    public UserImage getImage() {
        return image;
    }

    public void setImage(UserImage image) {
        this.image = image;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}
