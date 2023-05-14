package com.manech.manechapp.repository;

import com.manech.manechapp.dao.AddressDAO;
import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.User;

import java.util.List;
import java.util.UUID;

public class AddressRepository {
    private final AddressDAO addressDAO;
    private static  AddressRepository instance;
    private AddressRepository(AddressDAO addressDAO) {
        this.addressDAO = addressDAO;
    }
    public static AddressRepository getInstance(AddressDAO addressDAO){
        if(instance==null)
            instance =new AddressRepository(addressDAO);
        return  instance;
    }

    public List<Address> fetchUserAddresses(UUID userId) {
        return addressDAO.getAddresses(userId);
    }

    public void insertAddress(Address address) {
        addressDAO.insert(address);
    }

    public void duplicateAddress(Address address) {
        insertAddress(address.clone());
    }


    public void deleteAddress(Address address) {
            addressDAO.deleteAddress(address);
    }

    public Address fetchAddress(UUID addressId) {
        return addressDAO.addressById(addressId);
    }

    public int updateAddress(Address address) {
        return addressDAO.updateAddress(address);
    }
}
