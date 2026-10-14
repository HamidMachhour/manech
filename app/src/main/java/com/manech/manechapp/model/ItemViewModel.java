package com.manech.manechapp.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.manech.manechapp.data.Item;
import com.manech.manechapp.repository.ItemRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

public class ItemViewModel extends ViewModel {

    // REPOSITORIES
    private ItemRepository repository;
    private Executor executor;

    // DATA
    private MutableLiveData<List<Item>> items = new MutableLiveData<List<Item>>();
    private MutableLiveData<Item> currentItem = new MutableLiveData<Item>();

    public void setRepository(ItemRepository repository) {
        this.repository = repository;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public MutableLiveData<List<Item>> getItems() {
        return items;
    }

    public void fetchActiveItems(UUID storeId) {
        executor.execute(() -> {
                    this.items.postValue(repository.activeItems(storeId));
                }
        );
    }


    public void fetchTrash(UUID storeId) {
        executor.execute(() -> {
                    this.items.postValue(repository.itemsInTrash(storeId));
                }
        );
    }

    public void insert(Item item) {
        executor.execute(() -> {
            repository.insert(item);
        });
    }

    public void deleteItem(UUID itemId) {
        executor.execute(() -> {
            repository.delete(itemId);
        });
        fetchItem(itemId);
    }

    public void fetchItem(UUID itemId) {
        executor.execute(() ->
                this.currentItem.postValue(repository.fetchItem(itemId))
        );
    }

    public void update(Item item) {
        executor.execute(() -> {
            repository.update(item);
        });
        fetchItem(item.getId());
    }

    public MutableLiveData<Item> getCurrentItem() {
        return currentItem;
    }

    public void populate(UUID storeId) {
        executor.execute(() -> {
            repository.populate(storeId);
        });
        fetchActiveItems(storeId);
    }

    public void duplicateItem(UUID itemId, UUID storeId) {
        executor.execute(() -> {
            this.currentItem.postValue(repository.duplicate(itemId,storeId));
        });
    }

    public void moveToTrash(UUID itemId) {
        executor.execute(() -> {
            repository.moveToTrash(itemId);
        });
        fetchItem(itemId);
    }

    public void restoreFromTrash(UUID itemId) {
        executor.execute(() -> {
            repository.restoring(itemId);
        });
        fetchItem(itemId);
    }

    public void publishingItem(UUID itemId) {
        executor.execute(() -> {
            repository.publishing(itemId);
        });
        fetchItem(itemId);
    }

    public void privatingItem(UUID itemId) {
        executor.execute(() -> {
            repository.privating(itemId);
        });
        fetchItem(itemId);
    }

    public void fetchPublicItems() {
        executor.execute(() -> {
                    this.items.postValue(repository.publicItems());
                }
        );
    }

    public void fetchPublicItems(UUID storeId) {
        executor.execute(() -> {
            this.items.postValue(repository.publicItems(storeId));
        });
    }

    public void fetchPublicAndPrivateItems(UUID storeId) {
        executor.execute(() -> {
                    this.items.postValue(repository.publicAndPrivateItems(storeId));
                }
        );
    }

    public void clearTrash(UUID storeId) {
        executor.execute(()->{
            repository.clearTrash(storeId);
        });
    }
}