package com.manech.manechapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


import com.manech.manechapp.helper.MyUtils;

import java.util.Date;
import java.util.UUID;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = Order.class, parentColumns = "id", childColumns = "orderId", onDelete = CASCADE),
        },
        indices = { @Index("orderId"),
                @Index(name = "orderid_status", value = {"orderId", "status"}, unique = true),
        })
public class OrderStatus extends ManechEntity {
    private UUID orderId;
    private Status status;
    private Date statusDate;


    public OrderStatus(UUID orderId, Status status, Date statusDate) {
        this.orderId = orderId;
        this.status = status;
        this.statusDate = statusDate;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
