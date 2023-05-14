package com.manech.manechapp.repository;

import com.manech.manechapp.dao.CartDAO;
import com.manech.manechapp.dao.OrderDAO;
import com.manech.manechapp.data.CartItem;
import com.manech.manechapp.data.Cart;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.data.Order;
import com.manech.manechapp.data.OrderLine;
import com.manech.manechapp.data.OrderStatus;
import com.manech.manechapp.data.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CartRepository {
    private final CartDAO cartDAO;
    private final OrderDAO orderDAO;
    private static CartRepository instance;

    private CartRepository(CartDAO cartDAO, OrderDAO orderDAO) {
        this.cartDAO = cartDAO;
        this.orderDAO = orderDAO;
    }

    public static CartRepository getInstance(CartDAO cartDAO, OrderDAO orderDAO) {
        if (instance == null)
            instance = new CartRepository(cartDAO, orderDAO);
        return instance;
    }

    public boolean upsertCartItem(CartItem cartItem, boolean mergeQuantities) {
        final CartItem cartItem1 = cartDAO.cartItem(cartItem.getCartId(), cartItem.getItemId());
        final Item item = cartDAO.itemById(cartItem.getItemId());
        if (item == null)
            return false;
        // final Item item = cartItem.getItem();

        if (cartItem1 == null && item.getQuantity() >= cartItem.getQuantity()) {
            cartDAO.insertCartItem(cartItem);
        } else if (cartItem1 != null) {
            double qte = (mergeQuantities) ? cartItem1.getQuantity() + cartItem.getQuantity() : cartItem.getQuantity();
            cartItem1.setQuantity(qte);
            if (item.getQuantity() >= qte)
                cartDAO.updateCartItem(cartItem1);
            else
                return false;
        } else {
            return false;
        }
        return true;
    }


    public void clearCart(UUID cartId) {
        cartDAO.clearCart(cartId);
    }

    public void cartToOrder(Cart cart, Order baseOrder) {
        //insert an order for each store !!!
        Map<UUID, Order> orderByStoreMap = new HashMap<>();

        for (CartItem cartItem : cart.getCartItems()) {
            Item item = cartItem.getItem();
            if (item != null) {
                UUID storeId = item.getStore();
                Order order = orderByStoreMap.get(storeId);
                if (order == null) {

                    order = baseOrder.clone();
                    order.setOrderLineList(new ArrayList<>());

                    orderByStoreMap.put(storeId, order);

                    orderDAO.upsertOrder(order);
                    orderDAO.upsertOrderStatus(new OrderStatus(order.getId(), Status.PENDING, order.getCreateAt()));
                }
                insertOrderLine(order,item, cartItem.getQuantity(),cartItem.getDiscount());
            }
        }
        cartDAO.clearCart(cart.getId());
    }

    private void insertOrderLine(Order order, Item item,double quantity,double discount) {
            OrderLine orderLine = new OrderLine(
                    item.getId(),
                    item.getStore(),
                    order.getId(),
                    quantity,
                    item.getCode(),
                    item.getTitle(),
                    item.getPrice(),
                    item.getShipping(),
                    discount
            );
            orderDAO.upsertOrderLine(orderLine);
            order.getOrderLineList().add(orderLine);
    }

    public Cart fetchCart(UUID ownerId) {
        Cart cart = cartDAO.cartByOwner(ownerId);
        if (cart == null) {
            Cart newCart = new Cart(ownerId);
            cartDAO.insertCart(newCart);
        } else {
            cart.setCartItems(cartDAO.cartItems(cart.getId()));
            for (CartItem ci : cart.getCartItems()) {
                ci.setItem(cartDAO.itemById(ci.getItemId()));
            }
        }
        return cart;
    }

    public long deleteCartItem(CartItem cartItem) {
        return cartDAO.deleteCartItem(cartItem);
    }
}
