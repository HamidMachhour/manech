package com.manech.manechapp.data.images;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.manech.manechapp.data.Item;
import com.manech.manechapp.data.Resource;

import java.util.UUID;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = Item.class, parentColumns = "id", childColumns = "itemId", onDelete = CASCADE)
        },
        indices = {@Index("itemId")})
public class ItemImage extends Resource {
    UUID itemId;

    public UUID getItemId() {
        return itemId;
    }

    public ItemImage(String uri, UUID itemId) {
        super(uri);
        this.itemId = itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }
}
