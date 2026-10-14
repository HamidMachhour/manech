package com.manech.manechapp.repository;

import com.manech.manechapp.dao.CartDAO;
import com.manech.manechapp.dao.OrderDAO;
import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.Cart;
import com.manech.manechapp.data.CartItem;
import com.manech.manechapp.data.Order;
import com.manech.manechapp.data.OrderLine;
import com.manech.manechapp.data.OrderStatus;
import com.manech.manechapp.data.Status;
import com.manech.manechapp.data.DeliveryMethod;

import java.util.List;
import java.util.UUID;


public class OrderRepository {
    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;

    private static OrderRepository instance;

    private OrderRepository(OrderDAO orderDAO, CartDAO cartDAO) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
    }

    public static OrderRepository getInstance(OrderDAO orderDAO, CartDAO cartDAO) {
        if (instance == null)
            instance = new OrderRepository(orderDAO, cartDAO);
        return instance;
    }


    public Order orderById(UUID orderId) {
        final Order order = orderDAO.orderById(orderId);

        order.setOrderStatusList(orderDAO.orderStatus(orderId));

        List<OrderLine> orderLines = orderDAO.orderLines(orderId);
        for (OrderLine ol : orderLines) {
            ol.setItem(orderDAO.orderLineItem(ol.getItemId()));
        }
        order.setOrderLineList(orderLines);

        order.setBillingAddress(orderDAO.billingAddress(orderId));
        order.setDeliveryAddress(orderDAO.deliveryAddress(orderId));
        order.setDeliveryMethod(orderDAO.deliveryMethod(orderId));
        return order;
    }

    public List<OrderLine> orderLines(UUID orderId) {
        return orderDAO.orderLines(orderId);
    }

    public List<OrderStatus> orderStatus(UUID orderId) {
        return orderDAO.orderStatus(orderId);
    }

    public List<DeliveryMethod> deliveryMethods() {
        return orderDAO.deliveryMethods();
    }

    public DeliveryMethod deliveryMethod(UUID orderId) {
        return orderDAO.deliveryMethod(orderId);
    }

    public Address deliveryAddress(UUID orderId) {
        return orderDAO.deliveryAddress(orderId);
    }

    public Address billingAddress(UUID orderId) {
        return orderDAO.billingAddress(orderId);
    }

    public Order lastOrder(UUID owner) {
        return orderDAO.lastOrder(owner);
    }


    public List<Order> orderByOwner(UUID owner) {
        final List<Order> orders = orderDAO.ordersByOwner(owner);
        for (Order order : orders) {
            order.setOrderLineList(orderDAO.orderLines(order.getId()));
            order.setOrderStatusList(orderDAO.orderStatus(order.getId()));
        }
        return orders;
    }

    public boolean orderLineToCart(OrderLine orderLine, UUID ownerId) {
        Cart cart = cartDAO.cartByOwner(ownerId);
        if (cart == null) {// create cart
            cart = new Cart(ownerId);
            cartDAO.insertCart(new Cart(ownerId));
        }
        if (cart != null) {
            CartItem cartItem = cartDAO.cartItem(cart.getId(), orderLine.getItemId());
            if (cartItem == null) {
                cartItem = new CartItem(cart.getId(), orderLine.getItemId(), orderLine.getQuantity());
            } else {
                cartItem.setQuantity(cartItem.getQuantity() + orderLine.getQuantity());
            }
            if (cartItem.getQuantity() <= orderLine.getItem().getQuantity()) {
                cartDAO.insertCartItem(cartItem);
            } else
                return false;
        } else
            return false;
        return true;
    }

    public List<Order> ordersByUserStore(UUID userId) {
        return withDetails(orderDAO.ordersByUserStore(userId));
    }

    private List<Order> withDetails(List<Order> orders) {
        for (Order order : orders) {
            order.setOrderLineList(orderDAO.orderLines(order.getId()));
            order.setOrderStatusList(orderDAO.orderStatus(order.getId()));
        }
        return orders;
    }

    public List<Order> ordersByStore(UUID storeId) {
        final List<Order> orders = orderDAO.ordersByStore(storeId);
        for (Order order : orders) {
            order.setOrderLineList(orderDAO.orderLines(order.getId()));
            order.setOrderStatusList(orderDAO.orderStatus(order.getId()));
        }
        return orders;
    }


    public void changeStatus(UUID orderId, Status status) {
        orderDAO.upsertOrderStatus(new OrderStatus(orderId, status, new java.util.Date()));
        // the goods go back on the shelf when the order is cancelled or returned
        if (status == Status.CANCELLED || status == Status.RETURNED) {
            for (OrderLine line : orderDAO.orderLines(orderId)) {
                orderDAO.adjustItemStock(line.getItemId(), line.getQuantity());
            }
        }
    }

    public void upsertOrder(Order order) {
        orderDAO.upsertOrder(order);
        for(OrderLine orderLine:order.getOrderLineList()){
            orderDAO.upsertOrderLine(orderLine);
        }
        for(OrderStatus orderStatus:order.getOrderStatusList()){
            orderDAO.upsertOrderStatus(orderStatus);
        }
    }
}
