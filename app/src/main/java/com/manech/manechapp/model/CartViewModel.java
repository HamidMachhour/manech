package com.manech.manechapp.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.manech.manechapp.data.Cart;
import com.manech.manechapp.data.CartItem;
import com.manech.manechapp.data.Order;
import com.manech.manechapp.repository.CartRepository;

import java.util.UUID;
import java.util.concurrent.Executor;

public class CartViewModel extends ViewModel {

    // REPOSITORIES
    private CartRepository repository;
    private Executor executor;

    // DATA
    private MutableLiveData<Cart> cartMutableLiveData = new MutableLiveData<Cart>();
    private MutableLiveData<Boolean> booleanMutableLiveData =new MutableLiveData<Boolean>();
    public void setRepository(CartRepository repository) {
        this.repository = repository;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public MutableLiveData<Cart> getCartMutableLiveData() {
        return cartMutableLiveData;
    }

    public MutableLiveData<Boolean> getBooleanMutableLiveData() {
        return booleanMutableLiveData;
    }
/*
    public void upsertCartItem(CartItem cartItem, double availableQty, boolean addQty) {
        executor.execute(() -> {
            CartItem cartItem1 = repository.byOwnerItem(cartItem.getOwnerId(), cartItem.getItemId());
            if (cartItem1 == null && availableQty >= cartItem.getQuantity())
                repository.insert(cartItem);
            else if(cartItem1 != null) {
                cartItem1.setQuantity((addQty) ? cartItem1.getQuantity() + cartItem.getQuantity() : cartItem.getQuantity());
                if (availableQty >= cartItem1.getQuantity())
                    repository.update(cartItem1);
            }
        });
    }

*/
    public void deleteCartItem(CartItem cartItem) {
        executor.execute(() -> {
            repository.deleteCartItem(cartItem);
            booleanMutableLiveData.postValue(true);
        });
    }


    public void upsertCartItem(CartItem cartItem, boolean mergeQuantities) {
        executor.execute(() -> {
            final boolean b = repository.upsertCartItem(cartItem, mergeQuantities);
            booleanMutableLiveData.postValue(b);
        });
    }

    public void clearCart(UUID cartId) {
        executor.execute(() -> {
            repository.clearCart(cartId);
           booleanMutableLiveData.postValue(true);
        });
    }

    public void cartToOrder(Cart cart, Order order) {
        executor.execute(() -> {
            repository.cartToOrder(cart, order);
           booleanMutableLiveData.postValue(true);
        });
    }

    public void fetchCart(UUID ownerId) {
        executor.execute(() -> {
           cartMutableLiveData.postValue(repository.fetchCart(ownerId));
        });
    }
}