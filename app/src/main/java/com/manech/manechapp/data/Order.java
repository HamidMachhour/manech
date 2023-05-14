package com.manech.manechapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;


import com.manech.manechapp.helper.MyUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static androidx.room.ForeignKey.NO_ACTION;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "ownerId", onDelete = NO_ACTION),
                @ForeignKey(entity = Address.class, parentColumns = "id", childColumns = "deliveryAddressId", onDelete = NO_ACTION),
                @ForeignKey(entity = Address.class, parentColumns = "id", childColumns = "billingAddressId", onDelete = NO_ACTION),
                @ForeignKey(entity = DeliveryMethod.class, parentColumns = "id", childColumns = "deliveryMethodId", onDelete = NO_ACTION)
        },
        indices = { @Index("ownerId"),@Index("deliveryAddressId"),@Index("billingAddressId"),@Index("deliveryMethodId")})
public class Order extends ManechEntity {

    private UUID ownerId;
    private UUID deliveryMethodId;
    private UUID deliveryAddressId;
    private UUID billingAddressId;
    private double fee;
    private double discount;

    private Date createAt;
    private Date trashAt;
    private Date deleteAt;
    @Ignore
    private DeliveryMethod deliveryMethod=null;
    @Ignore
    private Address deliveryAddress=null;
    @Ignore
    private Address billingAddress=null;
    @Ignore
    private List<OrderLine> orderLineList;
    @Ignore
    private List<OrderStatus> orderStatusList;


    public Order(UUID ownerId, UUID deliveryMethodId, UUID deliveryAddressId, UUID billingAddressId, double fee, double discount, Date createAt, Date trashAt, Date deleteAt) {
        this.ownerId = ownerId;
        this.deliveryMethodId = deliveryMethodId;
        this.deliveryAddressId = deliveryAddressId;
        this.billingAddressId = billingAddressId;
        this.fee = fee;
        this.discount = discount;
        this.createAt = createAt;
        this.trashAt = trashAt;
        this.deleteAt = deleteAt;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public UUID getBillingAddressId() {
        return billingAddressId;
    }

    public void setBillingAddressId(UUID billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    public Date getTrashAt() {
        return trashAt;
    }

    public void setTrashAt(Date trashAt) {
        this.trashAt = trashAt;
    }

    public Date getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(Date deleteAt) {
        this.deleteAt = deleteAt;
    }

    public UUID getDeliveryMethodId() {
        return deliveryMethodId;
    }

    public void setDeliveryMethodId(UUID deliveryMethodId) {
        this.deliveryMethodId = deliveryMethodId;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public UUID getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(UUID deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public double getFee() {
        return MyUtils.ceil2f(fee);
    }

    public void setFee(double fee) {
        this.fee = MyUtils.ceil2f(fee);
    }

    public double getDiscount() {
        return MyUtils.ceil2f(discount);
    }

    public void setDiscount(double discount) {
        this.discount = MyUtils.ceil2f(discount);
    }


    public List<OrderLine> getOrderLineList() {
        return orderLineList;
    }

    public void setOrderLineList(List<OrderLine> orderLineList) {
        this.orderLineList = orderLineList;
    }

    public List<OrderStatus> getOrderStatusList() {
        return orderStatusList;
    }

    public void setOrderStatusList(List<OrderStatus> orderStatusList) {
        this.orderStatusList = orderStatusList;
    }

    public double getPrice() {
        double price = 0.0;
        for (OrderLine ol : orderLineList)
            price +=ol.getPrice();
        price +=  getFee();
        price = price * (1 - getDiscount() / 100.0);
        return MyUtils.ceil2f(price);
    }

    @NonNull
    @Override
    public Order clone()  {
        Order clone=new Order(ownerId,deliveryMethodId,deliveryAddressId,billingAddressId,fee,discount,createAt,trashAt,deleteAt);
        return clone;
    }
}
