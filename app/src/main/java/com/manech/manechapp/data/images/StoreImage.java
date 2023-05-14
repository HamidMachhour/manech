package com.manech.manechapp.data.images;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.manech.manechapp.data.ManechStore;
import com.manech.manechapp.data.Resource;

import java.util.UUID;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = ManechStore.class, parentColumns = "id", childColumns = "storeId", onDelete = CASCADE)
        },
        indices = {@Index("storeId")})
public class StoreImage extends Resource {
    UUID storeId;

    public UUID getStoreId() {
        return storeId;
    }

    public StoreImage(String uri, UUID storeId) {
        super(uri);
        this.storeId = storeId;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }
}
