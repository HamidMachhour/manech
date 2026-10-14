package com.manech.manechapp.data;

import android.content.Context;

import com.manech.manechapp.R;

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
    CANCELLED;

    /** Statuses a seller may move an order to from this one. */
    public Status[] nextStatuses() {
        switch (this) {
            case PENDING:
                return new Status[]{PROCESSING, CANCELLED};
            case PROCESSING:
                return new Status[]{SHIPPED, CANCELLED};
            case SHIPPED:
                return new Status[]{DELIVERED, RETURNED};
            case DELIVERED:
                return new Status[]{RETURNED};
            default:
                return new Status[0];
        }
    }

    public String getLocalizedName(Context context) {
        int resourceId;
        switch (this) {
            case PENDING:
                resourceId = R.string.status_pending;
                break;
            case PROCESSING:
                resourceId = R.string.status_processing;
                break;
            case SHIPPED:
                resourceId = R.string.status_shipped;
                break;
            case DELIVERED:
                resourceId = R.string.status_delivered;
                break;
            case RETURNED:
                resourceId = R.string.status_returned;
                break;
            case CANCELLED:
                resourceId = R.string.status_cancelled;
                break;
            default:
                throw new IllegalStateException("Unsupported order status: " + this);
        }
        return context.getString(resourceId);
    }
}
