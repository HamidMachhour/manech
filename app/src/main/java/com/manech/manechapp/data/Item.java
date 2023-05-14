package com.manech.manechapp.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.manech.manechapp.data.images.ItemImage;
import com.manech.manechapp.helper.MyUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = {@ForeignKey(entity = ManechStore.class, parentColumns = "id", childColumns = "store", onDelete = CASCADE)},
        indices = { @Index("store"),@Index("code")}
)
public class Item extends ManechEntity {

    private String code;
    private String title;
    private String description;
    private Double basePrice;
    private Double price;
    private Double shipping;
    private Double quantity;
    private Double minQty;
    private Double maxQty;
    private Integer location;

    private Date publishDate;
    private Date trashDate;

    private UUID imageId;
    // foreign key
    private UUID store;

    @Ignore
    private ItemImage image;
    @Ignore
    private List<ItemImage> images;

    public Item(String code, String title, String description, Double basePrice, Double price, Double shipping, Double quantity, Double minQty, Double maxQty, Integer location, UUID store) {
        setCode(code);
        setTitle(title);
        setDescription(description);
        setBasePrice(basePrice);
        setPrice(price);
        setShipping(shipping);
        setQuantity(quantity);
        setMinQty(minQty);
        setMaxQty(maxQty);
        setLocation(location);
        setStore(store);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBasePrice() {
        return MyUtils.ceil2f(basePrice);
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = MyUtils.ceil2f(basePrice);
    }

    public Double getPrice() {
        return MyUtils.ceil2f(price);
    }

    public void setPrice(Double price) {
        this.price = MyUtils.ceil2f(price);
    }

    public Double getShipping() {
        return MyUtils.ceil2f(shipping);
    }

    public void setShipping(Double shipping) {
        this.shipping = MyUtils.ceil2f(shipping);
    }

    public Double getQuantity() {
        return MyUtils.ceil2f(quantity);
    }

    public void setQuantity(Double quantity) {
        this.quantity = MyUtils.ceil2f(quantity);
    }

    public Double getMinQty() {
        return MyUtils.ceil2f(minQty);
    }

    public void setMinQty(Double minQty) {
        this.minQty = MyUtils.ceil2f(minQty);
    }

    public Double getMaxQty() {
        return MyUtils.ceil2f(maxQty);
    }

    public void setMaxQty(Double maxQty) {
        this.maxQty = MyUtils.ceil2f(maxQty);
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
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

    public UUID getStore() {
        return store;
    }

    public void setStore(UUID store) {
        this.store = store;
    }

    public Item clone() {
        return new Item(getCode(), getTitle(), getDescription(), getBasePrice(),
                getPrice(), getShipping(), getQuantity(), getMinQty(), getMaxQty(),
                getLocation(), getStore());
    }

    @Override
    public String toString() {
        return title +
                "\nCode: " + code +
                "\nPrice: " + price + " DH" +
                "\nQuantity: " + quantity + " In stock" +
                "\nLocation: " + location;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public ItemImage getImage() {
        return image;
    }

    public void setImage(ItemImage image) {
        this.image = image;
    }

    public List<ItemImage> getImages() {
        return images;
    }

    public void setImages(List<ItemImage> images) {
        this.images = images;
    }
}
