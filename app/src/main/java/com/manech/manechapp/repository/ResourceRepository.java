package com.manech.manechapp.repository;

import com.manech.manechapp.dao.ResourceDAO;
import com.manech.manechapp.data.images.ItemImage;
import com.manech.manechapp.data.images.StoreImage;
import com.manech.manechapp.data.images.UserImage;

import java.util.List;
import java.util.UUID;

public class ResourceRepository {
    private final ResourceDAO resourceDAO;
    private static ResourceRepository instance;
    private ResourceRepository(ResourceDAO resourceDAO) {
        this.resourceDAO = resourceDAO;
    }
    public static ResourceRepository getInstance(ResourceDAO resourceDAO){
        if(instance==null)
            instance =new ResourceRepository(resourceDAO);
        return  instance;
    }

    public List<StoreImage> fetchStoreImages(UUID storeId) {
        return resourceDAO.fetchStoreImages(storeId);
    }

    public void insertStoreImage(StoreImage storeImage) {
        resourceDAO.insertStoreImage(storeImage);
    }

    public void deleteStoreImage(StoreImage storeImage) {
        resourceDAO.deleteStoreImage(storeImage);
    }

    public StoreImage fetchStoreImage(UUID imageId) {
        return resourceDAO.fetchStoreImage(imageId);
    }

    public UserImage fetchUserImage(UUID imageId) {
        return resourceDAO.fetchUserImage(imageId);
    }

    public List<UserImage> fetchUserImages(UUID userId) {
        return resourceDAO.fetchUserImages(userId);
    }

    public void insertUserImage(UserImage userImage) {
        resourceDAO.insertUserImage(userImage);
    }

    public void deleteUserImage(UserImage userImage) {
        resourceDAO.deleteUserImage(userImage);
    }

    public List<ItemImage> fetchItemImages(UUID itemId) {
        return resourceDAO.fetchItemImages(itemId);
    }

    public ItemImage fetchItemImage(UUID imageId) {
        return resourceDAO.fetchItemImage(imageId);
    }

    public void insertItemImage(ItemImage itemImage) {
        resourceDAO.insertItemImage(itemImage);
    }

    public void deleteItemImage(ItemImage itemImage) {
        resourceDAO.deleteItemImage(itemImage);
    }
}
