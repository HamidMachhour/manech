package com.manech.manechapp.data;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.manech.manechapp.dao.AddressDAO;
import com.manech.manechapp.dao.CartDAO;
import com.manech.manechapp.dao.OrderDAO;
import com.manech.manechapp.dao.ItemDAO;
import com.manech.manechapp.dao.ManechStoreDAO;
import com.manech.manechapp.dao.ResourceDAO;
import com.manech.manechapp.dao.UserDAO;
import com.manech.manechapp.data.images.ItemImage;
import com.manech.manechapp.data.images.StoreImage;
import com.manech.manechapp.data.images.UserImage;
import com.manech.manechapp.helper.Converters;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Database(entities = {User.class, ManechStore.class, Address.class, Item.class, Cart.class,
        CartItem.class, Order.class, OrderLine.class,DeliveryMethod.class,
        OrderStatus.class, UserImage.class, StoreImage.class, ItemImage.class}, version = 1)

@TypeConverters({Converters.class})

public abstract class ManechDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "manech.db";
    private static ManechDatabase instance;
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            populateDb();
        }
    };

    public static synchronized ManechDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ManechDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static void populateDb() {
        Executor executor = Executors.newSingleThreadExecutor();
       /* ManechStoreDAO storeDAO = instance.manechStoreDAO();
        UserDAO userDAO = instance.userDAO();
        AddressDAO addressDAO = instance.addressDAO();*/
        final OrderDAO orderDAO = instance.orderDAO();

        executor.execute(() -> {
           /* userDAO.insert(new User("hamid", "12345678", "hamid.machhour@gmail.com", ""));

            addressDAO.insert(new Address("No Street", "No City", "00000",
                    "No country", "0.0", "0.0", "","", 1L));

            storeDAO.insert(new ManechStore("No Name 1", "No Description 1", "", 1L, 1L));
          storeDAO.insert(new ManechStore("No Name 2", "No Description 2", "", 1L, 1L));
            storeDAO.insert(new ManechStore("No Name 3", "No Description 3", "", 1L, 1L));*/
        orderDAO.addDeliveryMethod(new DeliveryMethod(DeliveryMethod.POS));
        orderDAO.addDeliveryMethod(new DeliveryMethod(DeliveryMethod.HOME_DELIVERY));

        });
    }

    public abstract UserDAO userDAO();

    public abstract ManechStoreDAO manechStoreDAO();

    public abstract AddressDAO addressDAO();

    public abstract ItemDAO itemDAO();

    public abstract CartDAO cartDAO();

    public abstract OrderDAO orderDAO();

    public abstract ResourceDAO resourceDAO();
}