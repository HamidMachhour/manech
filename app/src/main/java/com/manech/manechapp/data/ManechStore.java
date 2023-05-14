package com.manech.manechapp.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.manech.manechapp.data.images.StoreImage;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_NULL;

@Entity(
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE),
                @ForeignKey(entity = Address.class, parentColumns = "id", childColumns = "addressId", onDelete = SET_NULL)
        },
        indices = {@Index("userId"), @Index("addressId")})

public class ManechStore extends ManechEntity {

    private String name;
    private String description;

    private Date publishDate;
    private Date trashDate;

    private UUID userId;
    private UUID addressId;
    private UUID imageId;

    @Ignore
    private Address address;
    @Ignore
    private StoreImage defaultImage;
    @Ignore
    List<StoreImage> images;
    @Ignore
    List<Item> items;

    public ManechStore(String name, String description, UUID userId, UUID addressId) {
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.addressId = addressId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public UUID getAddressId() {
        return addressId;
    }

    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getTrashDate() {
        return trashDate;
    }

    public void setTrashDate(Date trashDate) {
        this.trashDate = trashDate;
    }

    public ManechStore clone() {
        final ManechStore manechStore = new ManechStore(this.name, this.description, this.userId, this.addressId);
        manechStore.setDefaultImage(this.defaultImage);
        manechStore.setImages(this.images);
        manechStore.setPublishDate(this.publishDate);
        manechStore.setTrashDate(this.trashDate);
        manechStore.setAddress(this.address);
        manechStore.setImageId(this.imageId);
        manechStore.setQrCode(this.getQrCode());
        return manechStore;
    }

    public void copyTo(ManechStore manechStore) {
        if (manechStore != null) {
            manechStore.setId(this.getId());
            manechStore.setName(this.name);
            manechStore.setDescription(this.description);
            manechStore.setUserId(this.userId);
            manechStore.setAddressId(this.addressId);
            manechStore.setDefaultImage(this.defaultImage);
            manechStore.setImages(this.images);
            manechStore.setPublishDate(this.publishDate);
            manechStore.setTrashDate(this.trashDate);
            manechStore.setAddress(this.address);
            manechStore.setImageId(this.imageId);
            manechStore.setQrCode(this.getQrCode());
        }
    }

    @NonNull
    @Override
    public String toString() {
        return name + "\n" + description;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public List<StoreImage> getImages() {
        return images;
    }

    public void setImages(List<StoreImage> images) {
        this.images = images;
    }

    public StoreImage getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(StoreImage defaultImage) {
        this.defaultImage = defaultImage;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManechStore that = (ManechStore) o;
        return userId.equals(that.userId) && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

}
