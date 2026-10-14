package com.manech.manechapp.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.data.User;
import com.manech.manechapp.data.images.ItemImage;
import com.manech.manechapp.data.images.StoreImage;
import com.manech.manechapp.data.images.UserImage;
import com.manech.manechapp.helper.ItemFilter;
import com.manech.manechapp.repository.AddressRepository;
import com.manech.manechapp.repository.CartRepository;
import com.manech.manechapp.repository.ItemRepository;
import com.manech.manechapp.repository.ManechStoreRepository;
import com.manech.manechapp.repository.OrderRepository;
import com.manech.manechapp.repository.ResourceRepository;
import com.manech.manechapp.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    private ManechStoreRepository storeRepository;
    private ResourceRepository resourceRepository;
    private AddressRepository addressRepository;
    private CartRepository cartRepository;
    private ItemRepository itemRepository;
    private OrderRepository orderRepository;

    private Executor executor;

    // Data
    private User currentUser;
    private ManechStore currentStore;
    private Address currentAddress;
    private Item currentItem;

    // Mutable DATA
    private final MutableLiveData<Boolean> itemUpdateStatus = new MutableLiveData<>();

    private final MutableLiveData<Boolean> storeUpdateStatus = new MutableLiveData<>();

    private final MutableLiveData<Boolean> userUpdateStatus = new MutableLiveData<>();

    private final MutableLiveData<Boolean> addressUpdateStatus = new MutableLiveData<>();


    private final MutableLiveData<User> userMutableLiveData = new MutableLiveData<User>();

    private final MutableLiveData<ManechStore> storeMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<Address> addressMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<Item> itemMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getAddressUpdateStatus() {
        return addressUpdateStatus;
    }

    public MutableLiveData<Address> getAddressMutableLiveData() {
        return addressMutableLiveData;
    }

    public MutableLiveData<ManechStore> getStoreMutableLiveData() {
        return storeMutableLiveData;
    }

    public MutableLiveData<Boolean> getUserUpdateStatus() {
        return userUpdateStatus;
    }

    public MutableLiveData<Boolean> getStoreUpdateStatus() {
        return storeUpdateStatus;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setStoreRepository(ManechStoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public void setResourceRepository(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public void setAddressRepository(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public void setCartRepository(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public void setItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void authenticate(String email, String password) {
        executor.execute(() -> {
            currentUser = userRepository.userAuth(email, password);
            userMutableLiveData.postValue(currentUser);
        });
    }

    public void insertUser(User user) {
        executor.execute(() -> {
            userUpdateStatus.postValue(userRepository.insertUser(user));
        });
    }


    public LiveData<User> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void userByEmail(String email) {
        executor.execute(() -> {
            userMutableLiveData.postValue(userRepository.getUserByEmail(email));
        });
    }

    public void fetchUser(UUID userId) {
        executor.execute(() -> {
                    currentUser = userRepository.userById(userId);
                    this.userMutableLiveData.postValue(currentUser);
                }
        );
    }

    public void disableCurrentUser() {

        executor.execute(() -> {
            if (currentUser != null) {
                userRepository.disableAccount(currentUser.getId());
                currentUser = null;
                userMutableLiveData.postValue(currentUser);
            }
        });
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentStore(ManechStore store) {
        this.currentStore = store;
    }

    public void setCurrentAddress(Address address) {
        this.currentAddress = address;
    }

    public void setCurrentItem(Item item) {
        currentItem=item;
    }
    public void fetchDefaultStoreItems(boolean reloadItems, ItemFilter filter) {

        executor.execute(() -> {
            if (currentUser != null) {
                if (currentStore == null || !currentStore.getId().equals(currentUser.getStoreId())) {
                    if (currentUser.getStore() != null)
                        currentStore = currentUser.getStore();
                    else
                        currentStore = reloadCurrentStore(currentUser.getStoreId());
                }

                fetchCurrentStoreRun(false, false, reloadItems,filter);
                storeMutableLiveData.postValue(currentStore);
            }
        });


    }
    public void fetchCurrentItem(boolean reload, boolean reloadImages) {
        executor.execute(() -> {
            fetchCurrentItemRun(reload, reloadImages);
            itemMutableLiveData.postValue(currentItem);
        });
    }

    private void fetchCurrentItemRun(boolean reload, boolean reloadImages) {
        if (currentItem != null) {
            if (reload) {
                currentItem = reloadCurrentItem(currentItem.getId());
            }
            if (reloadImages) {
                final List<ItemImage> images = resourceRepository.fetchItemImages(currentItem.getId());
                currentItem.setImages(images == null ? new ArrayList<>() : images);
            }
        }
    }

    private Item reloadCurrentItem(UUID uuid) {
        final Item item = itemRepository.fetchItem(uuid);
        if (item != null) {
            item.setImage(resourceRepository.fetchItemImage(item.getImageId()));
        }
        return item;
    }
    public void fetchCurrentStore(boolean reload, boolean reloadImages, boolean reloadItems) {
        executor.execute(() -> {
            fetchCurrentStoreRun(reload, reloadImages, reloadItems, ItemFilter.ALL);
            storeMutableLiveData.postValue(currentStore);
        });
    }

    private void fetchCurrentStoreRun(boolean reload, boolean reloadImages, boolean reloadItems, ItemFilter filter) {
        if (currentStore != null) {
            if (reload) {
                currentStore = reloadCurrentStore(currentStore.getId());
            }
            if (reloadImages) {
                final List<StoreImage> images = resourceRepository.fetchStoreImages(currentStore.getId());
                currentStore.setImages(images == null ? new ArrayList<>() : images);
            }
            if (reloadItems) {
                final List<Item> items = itemRepository.itemsByStore(currentStore.getId(),filter);
                if(items==null){
                    currentStore.setItems(new ArrayList<>());
                }else {
                    for(Item item:items){
                        item.setImage(resourceRepository.fetchItemImage(item.getImageId()));
                    }
                    currentStore.setItems(items);

                }
            }
        }
    }

    private ManechStore reloadCurrentStore(UUID uuid) {
        final ManechStore manechStore = storeRepository.fetchStore(uuid);
        if (manechStore != null) {
            manechStore.setDefaultImage(resourceRepository.fetchStoreImage(manechStore.getImageId()));
            manechStore.setAddress(addressRepository.fetchAddress(manechStore.getAddressId()));
        }
        return manechStore;
    }

    public void updateCurrentStore(List<StoreImage> updatedImages) {
        executor.execute(() -> {
            if (currentStore != null) {
                if (updatedImages != null) {
                    List<StoreImage> oldList = currentStore.getImages();
                    for (StoreImage storeImage : updatedImages) {
                        if (!oldList.contains(storeImage)) {
                            resourceRepository.insertStoreImage(storeImage);
                        }
                    }
                    for (StoreImage storeImage : oldList) {
                        if (!updatedImages.contains(storeImage)) {
                            resourceRepository.deleteStoreImage(storeImage);
                        }
                    }
                }
            }
            storeUpdateStatus.postValue(storeRepository.updateStore(currentStore) > 0);
        });
    }


    public void deleteStoreForever(UUID storeId) {
        executor.execute(() -> {
            storeRepository.delete(storeId);
            userUpdateStatus.postValue(true);
        });
    }

    public void fetchCurrentUser(boolean reload, boolean reloadStores, boolean reloadAddresses, boolean reloadImages) {
        executor.execute(() -> {
            if (currentUser != null) {
                if (reload) {
                    reloadCurrentUser();
                }
                if (reloadStores) {
                    final List<ManechStore> stores = storeRepository.fetchUserStores(currentUser.getId());
                    if (stores != null) {
                        for (ManechStore store : stores) {
                            store.setAddress(addressRepository.fetchAddress(store.getAddressId()));
                            store.setDefaultImage(resourceRepository.fetchStoreImage(store.getImageId()));
                        }
                        currentUser.setStores(stores);

                    } else {
                        currentUser.setStores(new ArrayList<>());
                    }
                }
                if (reloadAddresses) {
                    final List<Address> addresses = addressRepository.fetchUserAddresses(currentUser.getId());
                    currentUser.setUserAddresses(addresses == null ? new ArrayList<>() : addresses);
                }
                if (reloadImages) {
                    final List<UserImage> images = resourceRepository.fetchUserImages(currentUser.getId());
                    currentUser.setUserImages(images == null ? new ArrayList<>() : images);
                }
            }
            userMutableLiveData.postValue(currentUser);
        });
    }


    private void reloadCurrentUser() {
        currentUser = userRepository.userById(currentUser.getId());
        currentUser.setStore(storeRepository.fetchStore(currentUser.getStoreId()));
        currentUser.setImage(resourceRepository.fetchUserImage(currentUser.getImageId()));
        currentUser.setAddress(addressRepository.fetchAddress(currentUser.getAddressId()));
    }

    public void updateCurrentUser(List<UserImage> updatedImages,
                                  List<ManechStore> updatedStores,
                                  List<Address> updatedAddresses) {
        executor.execute(() -> {
            if (currentUser != null) {

                if (updatedImages != null) {
                    List<UserImage> oldImages = currentUser.getUserImages();
                    for (UserImage userImage : updatedImages) {
                        if (!oldImages.contains(userImage)) {
                            resourceRepository.insertUserImage(userImage);
                        }
                    }
                    for (UserImage userImage : oldImages) {
                        if (!updatedImages.contains(userImage)) {
                            resourceRepository.deleteUserImage(userImage);
                        }
                    }
                }

                if (updatedStores != null) {
                    List<ManechStore> oldStores = currentUser.getStores();
                    for (ManechStore manechStore : updatedStores) {
                        if (oldStores.contains(manechStore)) {
                            storeRepository.updateStore(manechStore);
                        } else if (manechStore.getTrashDate() == null) {
                            storeRepository.insert(manechStore);
                        }
                    }
                }

                if (updatedAddresses != null) {
                    List<Address> oldAddresses = currentUser.getUserAddresses();
                    for (Address address : updatedAddresses) {
                        if (!oldAddresses.contains(address)) {
                            addressRepository.insertAddress(address);
                        }
                    }
                    for (Address address : oldAddresses) {
                        if (!updatedAddresses.contains(address)) {
                            addressRepository.deleteAddress(address);
                        }
                    }
                }
            }
            //if(transact==Transact.LOCAL)
            userUpdateStatus.postValue(userRepository.updateUser(currentUser) > 0);
        });
    }

    public void fetchCurrentAddress(boolean reload) {
        executor.execute(() -> {
            if (currentAddress != null) {
                if (reload) {
                    reloadCurrentAddress();
                }
            }
            addressMutableLiveData.postValue(currentAddress);

        });
    }

    private void reloadCurrentAddress() {
        Address address = addressRepository.fetchAddress(currentAddress.getId());
        if (address != null) {
            currentAddress = address;
        }
    }

    public void updateCurrentAddress() {
        executor.execute(() -> {
            if (currentAddress != null) {
                addressUpdateStatus.postValue(addressRepository.updateAddress(currentAddress) > 0);
            }
        });
    }

    public void joinItemToCurrentStore(Item item) {
        executor.execute(() -> {
            if (currentStore != null && item != null) {
                itemRepository.joinStore(item.getId(), currentStore.getId());
            }
            storeUpdateStatus.postValue(true);
        });
    }

    public void updateCurrentStoreItems(List<Item> itemsList) {
        executor.execute(() -> {
            if (currentStore != null) {
                if (itemsList != null) {
                    List<Item> oldList = currentStore.getItems();
                    for (Item item : itemsList) {
                        if (!oldList.contains(item)) {
                            itemRepository.insert(item);
                        } else {
                            itemRepository.update(item);
                        }
                    }
                    for (Item item : oldList) {
                        if (!itemsList.contains(item)) {
                            if (item.getTrashDate() != null)
                                itemRepository.delete(item);
                            else {
                                item.setTrashDate(new Date());
                                itemRepository.update(item);
                            }
                        }
                    }
                }
            }
            storeUpdateStatus.postValue(true);
        });
    }

    public void addItemToCurrentStore(Item item) {
        executor.execute(() -> {
            if (currentStore == null || item == null
                    || !currentStore.getId().equals(item.getStore())) {
                storeUpdateStatus.postValue(false);
                return;
            }
            itemRepository.insert(item);
            if (currentStore.getItems() == null) {
                currentStore.setItems(new ArrayList<>());
            }
            currentStore.getItems().add(item);
            storeUpdateStatus.postValue(true);
        });
    }

    public MutableLiveData<Boolean> getItemUpdateStatus() {
        return itemUpdateStatus;
    }

    public MutableLiveData<Item> getItemMutableLiveData() {
        return itemMutableLiveData;
    }


    public void updateCurrentItem(List<ItemImage> updatedImages) {
        executor.execute(() -> {
            if (currentItem != null) {
                if (updatedImages != null) {
                    List<ItemImage> oldList = currentItem.getImages();
                    for (ItemImage itemImage : updatedImages) {
                        if (!oldList.contains(itemImage)) {
                            resourceRepository.insertItemImage(itemImage);
                        }
                    }
                    for (ItemImage itemImage : oldList) {
                        if (!updatedImages.contains(itemImage)) {
                            resourceRepository.deleteItemImage(itemImage);
                        }
                    }
                }
            }
            itemUpdateStatus.postValue(itemRepository.updateItem(currentItem) > 0);
        });
    }

}
