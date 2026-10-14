package com.manech.manechapp.helper;

import com.manech.manechapp.data.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class FavoriteIds {
    private static final String ITEMS_KEY = "favorite_items_";
    private static final String STORES_KEY = "favorite_stores_";

    private FavoriteIds() {
    }

    public static boolean isItemFavorite(SecureSharedPref preferences, UUID ownerId, UUID itemId) {
        return readIds(preferences, ITEMS_KEY, ownerId).contains(itemId.toString());
    }

    public static boolean isStoreFavorite(SecureSharedPref preferences, UUID ownerId, UUID storeId) {
        return readIds(preferences, STORES_KEY, ownerId).contains(storeId.toString());
    }

    public static boolean toggleItem(SecureSharedPref preferences, UUID ownerId, UUID itemId) {
        return toggle(preferences, ITEMS_KEY, ownerId, itemId);
    }

    public static boolean toggleStore(SecureSharedPref preferences, UUID ownerId, UUID storeId) {
        return toggle(preferences, STORES_KEY, ownerId, storeId);
    }

    public static List<Item> filterItems(SecureSharedPref preferences, UUID ownerId, List<Item> items) {
        Set<String> favorites = readIds(preferences, ITEMS_KEY, ownerId);
        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (favorites.contains(item.getId().toString())) {
                result.add(item);
            }
        }
        return result;
    }

    private static boolean toggle(SecureSharedPref preferences, String prefix, UUID ownerId, UUID entityId) {
        if (ownerId == null || entityId == null) {
            return false;
        }
        Set<String> ids = readIds(preferences, prefix, ownerId);
        boolean added = ids.add(entityId.toString());
        if (!added) {
            ids.remove(entityId.toString());
        }
        StringBuilder serializedIds = new StringBuilder();
        for (String id : ids) {
            if (serializedIds.length() > 0) {
                serializedIds.append(',');
            }
            serializedIds.append(id);
        }
        preferences.putString(key(prefix, ownerId), serializedIds.toString());
        return added;
    }

    private static Set<String> readIds(SecureSharedPref preferences, String prefix, UUID ownerId) {
        Set<String> ids = new HashSet<>();
        if (ownerId == null) {
            return ids;
        }
        String storedIds = preferences.getString(key(prefix, ownerId), "");
        if (!storedIds.isEmpty()) {
            for (String id : storedIds.split(",")) {
                if (!id.isEmpty()) {
                    ids.add(id);
                }
            }
        }
        return ids;
    }

    private static String key(String prefix, UUID ownerId) {
        return prefix + ownerId;
    }
}
