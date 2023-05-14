package com.manech.manechapp.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.manech.manechapp.data.Order;
import com.manech.manechapp.data.OrderLine;
import com.manech.manechapp.data.DeliveryMethod;
import com.manech.manechapp.repository.OrderRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

public class OrderViewModel extends ViewModel {

    // REPOSITORIES
    private OrderRepository orderRepository;

    private Executor executor;

    // DATA
    private MutableLiveData<Order> currentOrder = new MutableLiveData<Order>();
    private MutableLiveData<List<DeliveryMethod>> deliveryMethods = new MutableLiveData<List<DeliveryMethod>>();
    private MutableLiveData<List<Order>> orders = new MutableLiveData<List<Order>>();
    private MutableLiveData<Boolean> booleanMutableLiveData = new MutableLiveData<Boolean>();

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public MutableLiveData<Order> getCurrentOrder() {
        return currentOrder;
    }

    public MutableLiveData<Boolean> getBooleanMutableLiveData() {
        return booleanMutableLiveData;
    }

    public void fetchOrder(UUID orderId) {
        executor.execute(() -> {
            currentOrder.postValue(orderRepository.orderById(orderId));
        });
    }

    public void fetchDeliveryMethods() {
        executor.execute(() -> {
            deliveryMethods.postValue(orderRepository.deliveryMethods());
        });
    }

    public MutableLiveData<List<DeliveryMethod>> getDeliveryMethods() {
        return deliveryMethods;
    }


    public MutableLiveData<List<Order>> getOrders() {
        return orders;
    }

    public void fetchOrders(UUID owner) {
        executor.execute(() -> {
            orders.postValue(orderRepository.orderByOwner(owner));
        });
    }

    public void orderLineToCart(OrderLine orderLine, UUID ownerId) {
        executor.execute(() -> {
            booleanMutableLiveData.postValue(orderRepository.orderLineToCart(orderLine, ownerId));
        });

    }

    public void fetchOrdersByStore(UUID storeId) {
        executor.execute(() -> {
            orders.postValue(orderRepository.ordersByStore(storeId));
        });
    }

    public void upsertOrder(Order order) {
        executor.execute(() -> {
            orderRepository.upsertOrder(order);
            currentOrder.postValue(orderRepository.orderById(order.getId()));
        });
    }
}