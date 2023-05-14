package com.manech.manechapp.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.manech.manechapp.data.Address;
import com.manech.manechapp.repository.AddressRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

public class AddressViewModel  extends ViewModel {
    private Executor executor;
    private AddressRepository repository;
    private MutableLiveData<List<Address>> addresses=new MutableLiveData<>();
    private MutableLiveData<Address> defaultAddress=new MutableLiveData<>();

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setRepository(AddressRepository repository) {
        this.repository = repository;
    }

    public MutableLiveData<List<Address>> getAddresses() {
        return addresses;
    }

    public void fetchAddresses(UUID userId) {
        executor.execute(()->{
            addresses.postValue(repository.fetchUserAddresses(userId));
        });
    }

    public void duplicate(Address address) {
        executor.execute(() -> {
            repository.duplicateAddress(address);
        });
    }

    public void populateAddress(UUID userId) {
        executor.execute(() -> {
            repository.insertAddress(new Address("No Street", "No City", "00000",
                    "No country", null, "0.0", "",userId));
        });
    }

    public LiveData<Address> getAddress() {
        return defaultAddress;
    }

    public void fetchAddress(UUID addressId) {
        executor.execute(()->{
            defaultAddress.postValue(repository.fetchAddress(addressId));
        });
    }

    public void updateAddress(Address address) {
        executor.execute(()->{
            repository.updateAddress(address);
            defaultAddress.postValue(address);
        });
    }
}
