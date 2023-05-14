package com.manech.manechapp.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.data.images.StoreImage;
import com.manech.manechapp.repository.ResourceRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

public class ResourceViewModel extends ViewModel {
    private Executor executor;
    private ResourceRepository repository;
    private MutableLiveData<List<StoreImage>> storeImages =new MutableLiveData<>();
    private MutableLiveData<StoreImage> storeImage =new MutableLiveData<>();

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setRepository(ResourceRepository repository) {
        this.repository = repository;
    }

    public void fetchStoreImages(UUID storeId) {
        executor.execute(()->{
            storeImages.postValue(repository.fetchStoreImages(storeId));
        });
    }

    public MutableLiveData<List<StoreImage>> getStoreImages() {
        return storeImages;
    }

    public MutableLiveData<StoreImage> getStoreImage() {
        return storeImage;
    }

}
