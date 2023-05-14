package com.manech.manechapp.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.data.User;
import com.manech.manechapp.repository.ManechStoreRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

public class MStoreViewModel extends ViewModel {

    // REPOSITORIES
    private ManechStoreRepository repository;
    private Executor executor;

    // DATA
    private MutableLiveData<List<ManechStore>> storesMutableLiveData = new MutableLiveData<List<ManechStore>>();
    private MutableLiveData<ManechStore> storeMutableLiveData = new MutableLiveData<ManechStore>();
    ManechStore currentStore;

    public void setCurrentStore(ManechStore currentStore) {
        this.currentStore = currentStore;
    }

    public void setRepository(ManechStoreRepository repository) {
        this.repository = repository;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public MutableLiveData<List<ManechStore>> getStoresMutableLiveData() {
        return storesMutableLiveData;
    }

    public void fetchStores(UUID userId) {
        executor.execute(() -> {
                    final List<ManechStore> manechStores = repository.fetchUserStores(userId);
                    for (ManechStore store : manechStores) {
                        store.setAddress(repository.fetchAddress(store.getAddressId()));
                        store.setDefaultImage(repository.fetchImage(store.getImageId()));
                        //  store.setImages(repository.fetchImages(store.getId()));

                    }
                    this.storesMutableLiveData.postValue(manechStores);
                }
        );
    }


    public void fetchTrash(UUID userId) {
        executor.execute(() -> {
                    this.storesMutableLiveData.postValue(repository.storesInTrash(userId));
                }
        );
    }

    public void insert(ManechStore store) {
        executor.execute(() -> {
            repository.insert(store);
        });
    }

    public void deleteStore(UUID storeId) {
        executor.execute(() -> {
            repository.delete(storeId);
        });
        fetchStore(storeId);
    }

    public void fetchStore(UUID storeId) {
        executor.execute(() ->
                this.storeMutableLiveData.postValue(repository.fetchStore(storeId))
        );
    }

    public void updateStore(ManechStore store) {
        executor.execute(() -> {
            repository.updateStore(store);
            this.storeMutableLiveData.postValue(repository.fetchStore(store.getId()));
        });
    }

    /*
        public void deleteAllStores(long userId) {
            executor.execute(() -> {
                repository.deleteAll(userId);
            });
            fetchStores(userId, false);
        }
    */
    public MutableLiveData<ManechStore> getStoreMutableLiveData() {
        return storeMutableLiveData;
    }

    public void populate(UUID userId) {
        executor.execute(() -> {
            repository.populate(userId);
        });
        fetchStores(userId);
    }

    public void duplicateStore(UUID storeId) {
        // TODO review (what exactly to be duplicated?)
        executor.execute(() -> {
            this.storeMutableLiveData.postValue(repository.duplicate(storeId));
        });
    }

    public void moveToTrash(UUID storeId) {
        executor.execute(() -> {
            repository.moveToTrash(storeId);
        });
        fetchStore(storeId);
    }

    public void restoreFromTrash(UUID storeId) {
        executor.execute(() -> {
            repository.restoring(storeId);
        });
        fetchStore(storeId);
    }

    public void publishingStore(UUID storeId) {
        executor.execute(() -> {
            repository.publishing(storeId);
        });
        fetchStore(storeId);
    }

    public void privatingStore(UUID storeId) {
        executor.execute(() -> {
            repository.privating(storeId);
        });
        fetchStore(storeId);
    }

    public void updateStores(User user, List<ManechStore> updatedList) {
        executor.execute(() -> {
            List<ManechStore> oldList=user.getStores();
            for (ManechStore manechStore : updatedList) {
                if (!oldList.contains(manechStore) && manechStore.getTrashDate() != null)// excluded item
                    continue;
                else {
                    repository.insert(manechStore);
                }
            }
            for (ManechStore manechStore : oldList) {
                if (updatedList.contains(manechStore))// excluded item
                    continue;
                else {
                    repository.delete(manechStore.getId());
                }
            }
            storesMutableLiveData.postValue(repository.fetchUserStores(user.getId()));

        });
    }

    public void fetchCurrentStore() {

        executor.execute(() -> {
            storeMutableLiveData.postValue(currentStore);
        });
    }
}