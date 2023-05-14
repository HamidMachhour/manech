package com.manech.manechapp.repository;

import com.manech.manechapp.dao.AddressDAO;
import com.manech.manechapp.dao.ManechStoreDAO;
import com.manech.manechapp.dao.ResourceDAO;
import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.data.images.StoreImage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManechStoreRepository {
    private static ManechStoreRepository instance;
    private final ManechStoreDAO manechStoreDAO;
    private final AddressDAO addressDAO;
    private final ResourceDAO resourceDAO;

    private ManechStoreRepository(ManechStoreDAO manechStoreDAO, AddressDAO addressDAO, ResourceDAO resourceDAO) {
        this.manechStoreDAO = manechStoreDAO;
        this.addressDAO=addressDAO;
        this.resourceDAO = resourceDAO;
    }

    public static ManechStoreRepository getInstance(ManechStoreDAO manechStoreDAO,AddressDAO addressDAO,ResourceDAO resourceDAO) {
        if (instance == null) {
            instance = new ManechStoreRepository(manechStoreDAO,addressDAO, resourceDAO);
        }
        return instance;
    }

    public void insert(ManechStore store) {
        manechStoreDAO.insert(store);
    }

    public void delete(UUID storeId) {
        manechStoreDAO.delete(storeId);
    }

    public int updateStore(ManechStore store) {
        return manechStoreDAO.updateStore(store);
    }

    public void deleteAll(UUID userId) {
        this.manechStoreDAO.deleteAll(userId);
    }

    public ManechStore fetchStore(UUID storeId) {
        return manechStoreDAO.storeById(storeId);
    }

    public ManechStore duplicate(UUID storeId) {
        ManechStore current = fetchStore(storeId);
        insert(current.clone());
        return current;
    }

    public void populate(UUID userId) {
        insert(new ManechStore("Store Name", "Store Description",userId, null));
    }

    public List<ManechStore> storesByUser(UUID userId) {
        return this.manechStoreDAO.storeByUser(userId);
    }

    public List<ManechStore> storesInTrash(UUID userId) {
        return this.manechStoreDAO.storesInTrash(userId);
    }

    public List<ManechStore> fetchUserStores(UUID userId) {
        return this.manechStoreDAO.fetchStores(userId);
    }

    public void moveToTrash(UUID storeId) {
        this.manechStoreDAO.moveToTrash(storeId);
    }

    public void restoring(UUID storeId) {
        this.manechStoreDAO.restoreFromTrash(storeId);
    }

    public void publishing(UUID storeId) {
        this.manechStoreDAO.publishing(storeId);
    }

    public void privating(UUID storeId) {
        this.manechStoreDAO.privating(storeId);

    }

    public Address fetchAddress(UUID addressId) {
        return addressDAO.addressById(addressId);
    }

    public StoreImage fetchImage(UUID imageId) {
        return manechStoreDAO.fetchStoreImage(imageId);
    }

    private List<StoreImage> fetchImages(UUID storeId) {
        final List<StoreImage> storeImages = resourceDAO.fetchStoreImages(storeId);
        if(storeImages==null) return new ArrayList<>();
        return storeImages;
    }

    public void insertWithResources(ManechStore manechStore) {
        manechStoreDAO.insert(manechStore);
        if(manechStore.getImages()!=null && ! manechStore.getImages().isEmpty()){
            for(StoreImage storeImage : manechStore.getImages()){
                resourceDAO.insertStoreImage(storeImage);
            }
        }
    }
}
