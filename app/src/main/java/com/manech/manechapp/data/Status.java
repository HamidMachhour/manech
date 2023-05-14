package com.manech.manechapp.data;

/**
 *     PENDING: the order has been received and is awaiting processing
 *     PROCESSING: the order is being prepared for shipment or delivery
 *     SHIPPED: the order has been shipped or is en route to the buyer
 *     DELIVERED: the order has been delivered to the buyer
 *     RETURNED: the order has been returned by the buyer
 *     CANCELLED: the order has been cancelled by the buyer or seller
 */
public enum Status {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    RETURNED,
    CANCELLED
}

