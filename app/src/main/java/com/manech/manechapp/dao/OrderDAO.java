package com.manech.manechapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.manech.manechapp.data.Address;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.data.Order;
import com.manech.manechapp.data.OrderLine;
import com.manech.manechapp.data.OrderStatus;
import com.manech.manechapp.data.DeliveryMethod;

import java.util.List;
import java.util.UUID;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface OrderDAO {

    @Insert(onConflict = REPLACE)
    public void upsertOrder(Order order);


    @Query("select * from `Order` where id=:orderId")
    Order orderById(UUID orderId);


    @Query("select * from OrderLine where orderId=:orderId")
    List<OrderLine> orderLines(UUID orderId);

    @Query("select * from OrderStatus where orderId=:orderId order by statusDate desc ")
    List<OrderStatus> orderStatus(UUID orderId);

    @Insert(onConflict = REPLACE)
    void addDeliveryMethod(DeliveryMethod deliveryMethod);

    @Query("select * from deliverymethod ")
    List<DeliveryMethod> deliveryMethods();

    @Query("select * from DeliveryMethod " +
            "where id = (select deliveryMethodId from `Order` where id = :orderId) ")
    DeliveryMethod deliveryMethod(UUID orderId);

    @Query("select * from Address " +
            "where id = (select deliveryAddressId from `Order` where id = :orderId) ")
    Address deliveryAddress(UUID orderId);

    @Query("select * from Address " +
            "where id = (select billingAddressId from `Order` where id = :orderId) ")
    Address billingAddress(UUID orderId);

    @Query("select * from `Order` " +
            "where ownerId=:owner and deleteAt is null and trashAt is null " +
            "order by createAt desc limit 1")
    Order lastOrder(UUID owner);


    @Query("UPDATE Item SET quantity = quantity - (SELECT SUM(quantity) FROM OrderLine " +
            "  WHERE Item.id = OrderLine.itemId AND orderId=:orderId GROUP BY OrderLine.itemId) " +
            "WHERE EXISTS ( SELECT 1 FROM OrderLine WHERE Item.id = OrderLine.itemId )")
    void quantitiesDownByOrder(UUID orderId);

    @Query("UPDATE Item SET quantity = quantity + (SELECT SUM(quantity) FROM OrderLine " +
            "  WHERE Item.id = OrderLine.itemId AND orderId=:orderId GROUP BY OrderLine.itemId) " +
            "WHERE EXISTS ( SELECT 1 FROM OrderLine WHERE Item.id = OrderLine.itemId )")
    void quantitiesUpByOrder(UUID orderId);

    @Query("UPDATE Item SET quantity = quantity - (SELECT quantity FROM OrderLine " +
            "  WHERE OrderLine.id=:orderlineId) " +
            "WHERE EXISTS ( SELECT 1 FROM OrderLine WHERE Item.id = OrderLine.itemId )")
    void quantitiesDownByOrderline(UUID orderlineId);

    @Query("UPDATE Item SET quantity = quantity + (SELECT quantity FROM OrderLine " +
            "  WHERE OrderLine.id=:orderlineId) " +
            "WHERE EXISTS ( SELECT 1 FROM OrderLine WHERE Item.id = OrderLine.itemId )")
    void quantitiesUpByOrderline(UUID orderlineId);



    @Query("select * from `Order` " +
            "where ownerId=:owner and deleteAt is null and trashAt is null " +
            "order by createAt desc ")
    List<Order> ordersByOwner(UUID owner);

    @Query("select * from Item where id=:itemId")
    Item orderLineItem(UUID itemId);

    @Insert(onConflict = REPLACE)
    void upsertOrderLine(OrderLine orderLine);


    @Query("update Item set quantity = max(0, coalesce(quantity, 0) + :delta) where id=:itemId")
    void adjustItemStock(UUID itemId, double delta);

    @Insert(onConflict = REPLACE)
    void upsertOrderStatus(OrderStatus os);

    @Query("select * from `Order` " +
            " where deleteAt is null and trashAt is null " +
            " and id in(select distinct orderId from orderline where store=:storeId) " +
            " order by createAt desc ")
    List<Order> ordersByStore(UUID storeId);

    @Query("select * from `Order` " +
            " where deleteAt is null and trashAt is null " +
            " and id in(select distinct orderId from orderline where store=(select storeId from User where id=:userId)) " +
            " order by createAt desc ")
    List<Order> ordersByUserStore(UUID userId);
}
