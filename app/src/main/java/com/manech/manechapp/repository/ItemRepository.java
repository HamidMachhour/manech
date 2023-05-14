package com.manech.manechapp.repository;

import com.manech.manechapp.dao.ItemDAO;
import com.manech.manechapp.data.Item;
import com.manech.manechapp.helper.ItemFilter;

import java.util.List;
import java.util.UUID;

public class ItemRepository {
    private final ItemDAO itemDAO;
    private static ItemRepository instance;

    private ItemRepository(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }
    public static ItemRepository getInstance(ItemDAO itemDAO){
        if(instance==null)
            instance=new ItemRepository(itemDAO);
        return  instance;
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
        return itemDAO.itemById(itemId);
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
            case PUBLISHED:
                items=itemDAO.publicItems(storeId);
                break;
            default:
                items=itemDAO.itemsByStore(storeId);
        }
        return items;
    }

    public List<Item> itemsInTrash(UUID storeId) {
        return this.itemDAO.inTrashItems(storeId);
    }

    public List<Item> activeItems(UUID storeId) {
        return this.itemDAO.activeItems(storeId);
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
        return itemDAO.publicItems();
    }

    public List<Item> publicAndPrivateItems(UUID storeId) {
        return itemDAO.publicAndPrivateItems(storeId);
    }

    public void clearTrash(UUID storeId) {
        itemDAO.clearTrash(storeId);
    }

    public int updateItem(Item item) {
        return itemDAO.updateItem(item);
    }
}
