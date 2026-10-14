package com.manech.manechapp.repository;

import com.manech.manechapp.dao.ItemDAO;
import com.manech.manechapp.dao.ResourceDAO;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.data.images.ItemImage;
import com.manech.manechapp.helper.ItemFilter;

import java.util.List;
import java.util.UUID;

public class ItemRepository {
    private final ItemDAO itemDAO;
    private final ResourceDAO resourceDAO;
    private static ItemRepository instance;

    private ItemRepository(ItemDAO itemDAO, ResourceDAO resourceDAO) {
        this.itemDAO = itemDAO;
        this.resourceDAO = resourceDAO;
    }
    public static ItemRepository getInstance(ItemDAO itemDAO, ResourceDAO resourceDAO){
        if(instance==null)
            instance=new ItemRepository(itemDAO, resourceDAO);
        return  instance;
    }


    public void joinStore(UUID itemId, UUID storeId) {
        itemDAO.joinStore(itemId, storeId);
    }

    public void insert(Item item) {
        itemDAO.insert(item);
    }

    public void delete(Item item) {
        itemDAO.delete(item);
    }

    public void delete(UUID item) {
        itemDAO.delete(item);
    }
    public void update(Item item) {
        itemDAO.updateItem(item);
    }

    public Item fetchItem(UUID itemId) {
        final Item item = withImage(itemDAO.itemById(itemId));
        if (item != null) {
            item.setImages(resourceDAO.fetchItemImages(item.getId()));
        }
        return item;
    }

    public Item duplicate(UUID itemId, UUID storeId) {
        Item current = fetchItem(itemId);
        final Item clone = current.clone();
        clone.setStore(storeId);
        insert(clone);
        return current;
    }

    public void populate(UUID storeId) {
        if(storeId==null)
            return;

        insert(new Item("0x00000","Item Title","Item Description",0.0,0.0,0.0,
                1000.0,0.0,0.0,1,storeId));
    }

    public List<Item> itemsByStore(UUID storeId, ItemFilter filter) {
        List<Item> items;
        switch (filter){
            case ACTIVE:
                items=itemDAO.activeItems(storeId);
                break;
            case IN_TRASH:
                items=itemDAO.inTrashItems(storeId);
                break;
            case ORPHAN:
                items=itemDAO.orphanItems();
                break;
            case PUBLISHED:
                items=itemDAO.publicItems(storeId);
                break;
            default:
                items=itemDAO.itemsByStore(storeId);
        }
        return withImages(items);
    }

    private Item withImage(Item item) {
        if (item != null && item.getImageId() != null) {
            item.setImage(resourceDAO.fetchItemImage(item.getImageId()));
        }
        return item;
    }

    private List<Item> withImages(List<Item> items) {
        if (items != null) {
            for (Item item : items) {
                withImage(item);
            }
        }
        return items;
    }

    public List<Item> itemsInTrash(UUID storeId) {
        return withImages(this.itemDAO.inTrashItems(storeId));
    }

    public List<Item> activeItems(UUID storeId) {
        return withImages(this.itemDAO.activeItems(storeId));
    }

    public void moveToTrash(UUID itemId) {
        this.itemDAO.moveToTrash(itemId);
    }

    public void restoring(UUID itemId) {
        this.itemDAO.restoreFromTrash(itemId);
    }

    public void publishing(UUID itemId) {
        this.itemDAO.publishing(itemId);
    }

    public void privating(UUID itemId) {
        this.itemDAO.privating(itemId);
    }

    public List<Item> publicItems() {
        return withImages(itemDAO.publicItems());
    }

    public List<Item> publicItems(UUID storeId) {
        return withImages(itemDAO.publicItems(storeId));
    }

    public List<Item> publicAndPrivateItems(UUID storeId) {
        return withImages(itemDAO.publicAndPrivateItems(storeId));
    }

    public int duplicateItems(UUID sourceStoreId, UUID targetStoreId) {
        int copied = 0;
        for (Item source : itemDAO.itemsByStore(sourceStoreId)) {
            if (source.getTrashDate() != null) {
                continue;
            }
            Item copy = source.clone();
            copy.setStore(targetStoreId);
            insert(copy);
            UUID copiedDefaultImageId = null;
            for (ItemImage image : resourceDAO.fetchItemImages(source.getId())) {
                ItemImage imageCopy = new ItemImage(image.getUri(), copy.getId());
                resourceDAO.insertItemImage(imageCopy);
                if (image.getId().equals(source.getImageId())) {
                    copiedDefaultImageId = imageCopy.getId();
                }
            }
            copy.setImageId(copiedDefaultImageId);
            itemDAO.updateItem(copy);
            copied++;
        }
        return copied;
    }

    public void clearTrash(UUID storeId) {
        itemDAO.clearTrash(storeId);
    }

    public int updateItem(Item item) {
        return itemDAO.updateItem(item);
    }
}
