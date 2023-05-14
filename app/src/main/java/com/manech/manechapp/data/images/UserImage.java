package com.manech.manechapp.data.images;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.manech.manechapp.data.Resource;
import com.manech.manechapp.data.User;

import java.util.UUID;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE)
        },
        indices = {@Index("userId")})
public class UserImage extends Resource {
    UUID userId;

    public UUID getUserId() {
        return userId;
    }

    public UserImage(String uri, UUID userId) {
        super(uri);
        this.userId = userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
