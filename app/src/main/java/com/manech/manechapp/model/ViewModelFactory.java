package com.manech.manechapp.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.manech.manechapp.data.ManechDatabase;
import com.manech.manechapp.data.Order;
import com.manech.manechapp.repository.AddressRepository;
import com.manech.manechapp.repository.CartRepository;
import com.manech.manechapp.repository.OrderRepository;
import com.manech.manechapp.repository.ItemRepository;
import com.manech.manechapp.repository.ManechStoreRepository;
import com.manech.manechapp.repository.ResourceRepository;
import com.manech.manechapp.repository.UserRepository;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Executor executor;
    private final Context context;
    private ItemViewModel itemViewModel;
    private MStoreViewModel mStoreViewModel;
    private UserViewModel userViewModel;
    private CartViewModel cartViewModel;
    private OrderViewModel orderViewModel;
    private AddressViewModel addressViewModel;
    private ResourceViewModel resourceViewModel;

    public ViewModelFactory(Executor executor, Context context) {
        this.executor = executor;
        this.context = context;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        final ManechDatabase manechDatabase = ManechDatabase.getInstance(context);

        if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            if (itemViewModel == null) {
                itemViewModel = new ItemViewModel();
                itemViewModel.setExecutor(executor);
                itemViewModel.setRepository(ItemRepository.getInstance(manechDatabase.itemDAO(), manechDatabase.resourceDAO()));
            }
            return (T) itemViewModel;
        }

        if (modelClass.isAssignableFrom(MStoreViewModel.class)) {
            if (mStoreViewModel == null) {
                mStoreViewModel = new MStoreViewModel();
                mStoreViewModel.setExecutor(executor);
                mStoreViewModel.setRepository(ManechStoreRepository.getInstance(manechDatabase.manechStoreDAO(),
                        manechDatabase.addressDAO(), manechDatabase.resourceDAO()));
                mStoreViewModel.setItemRepository(ItemRepository.getInstance(manechDatabase.itemDAO(), manechDatabase.resourceDAO()));
            }
            return (T) mStoreViewModel;
        }

        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            if (userViewModel == null) {
                userViewModel = new UserViewModel();
                userViewModel.setExecutor(executor);
                userViewModel.setUserRepository(UserRepository.getInstance(manechDatabase.userDAO()));
                userViewModel.setStoreRepository(ManechStoreRepository.getInstance(manechDatabase.manechStoreDAO(),
                        manechDatabase.addressDAO(),manechDatabase.resourceDAO()));
                userViewModel.setResourceRepository(ResourceRepository.getInstance(manechDatabase.resourceDAO()));
                userViewModel.setAddressRepository(AddressRepository.getInstance(manechDatabase.addressDAO()));
                userViewModel.setCartRepository(CartRepository.getInstance(manechDatabase.cartDAO(),manechDatabase.orderDAO()));
                userViewModel.setOrderRepository(OrderRepository.getInstance(manechDatabase.orderDAO(),manechDatabase.cartDAO()));
                userViewModel.setItemRepository(ItemRepository.getInstance(manechDatabase.itemDAO(), manechDatabase.resourceDAO()));
            }
            return (T) userViewModel;
        }

        if (modelClass.isAssignableFrom(CartViewModel.class)) {
            if (cartViewModel == null) {
                cartViewModel = new CartViewModel();
                cartViewModel.setExecutor(executor);
                cartViewModel.setRepository(CartRepository.getInstance(manechDatabase.cartDAO(), manechDatabase.orderDAO()));
            }
            return (T) cartViewModel;
        }

        if (modelClass.isAssignableFrom(OrderViewModel.class)) {
            if (orderViewModel == null) {
                orderViewModel = new OrderViewModel();
                orderViewModel.setExecutor(executor);
                orderViewModel.setOrderRepository(OrderRepository.getInstance(manechDatabase.orderDAO(), manechDatabase.cartDAO()));
            }
            return (T) orderViewModel;
        }

        if (modelClass.isAssignableFrom(AddressViewModel.class)) {
            if (addressViewModel == null) {
                addressViewModel = new AddressViewModel();
                addressViewModel.setExecutor(executor);
                addressViewModel.setRepository(AddressRepository.getInstance(manechDatabase.addressDAO()));
            }
            return (T) addressViewModel;
        }

        if (modelClass.isAssignableFrom(ResourceViewModel.class)) {
            if (resourceViewModel == null) {
                resourceViewModel = new ResourceViewModel();
                resourceViewModel.setExecutor(executor);
                resourceViewModel.setRepository(ResourceRepository.getInstance(manechDatabase.resourceDAO()));
            }
            return (T) resourceViewModel;
        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
