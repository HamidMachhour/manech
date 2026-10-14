package com.manech.manechapp.helper;

import com.manech.manechapp.data.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public final class ItemQrTransfer {
    private static final String FORMAT = "manech-item";
    private static final int VERSION = 1;

    private ItemQrTransfer() {
    }

    public static String encode(Item item) throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("format", FORMAT);
        payload.put("version", VERSION);
        payload.put("code", jsonValue(item.getCode()));
        payload.put("title", jsonValue(item.getTitle()));
        payload.put("description", jsonValue(item.getDescription()));
        payload.put("basePrice", jsonValue(item.getBasePrice()));
        payload.put("price", jsonValue(item.getPrice()));
        payload.put("shipping", jsonValue(item.getShipping()));
        payload.put("quantity", jsonValue(item.getQuantity()));
        payload.put("minQty", jsonValue(item.getMinQty()));
        payload.put("maxQty", jsonValue(item.getMaxQty()));
        payload.put("location", jsonValue(item.getLocation()));
        return payload.toString();
    }

    public static Item decode(String value, UUID currentStoreId) throws JSONException {
        if (currentStoreId == null) {
            throw new JSONException("A current store is required.");
        }
        if (value == null) {
            throw new JSONException("Item QR data is missing.");
        }
        JSONObject payload = new JSONObject(value);
        if (!FORMAT.equals(payload.optString("format"))
                || payload.optInt("version", -1) != VERSION) {
            throw new JSONException("Unsupported item QR format.");
        }

        String title = optionalString(payload, "title");
        if (title == null || title.trim().isEmpty()) {
            throw new JSONException("Item title is missing.");
        }
        Item item = new Item(
                optionalString(payload, "code"),
                title,
                optionalString(payload, "description"),
                optionalDouble(payload, "basePrice"),
                optionalDouble(payload, "price"),
                optionalDouble(payload, "shipping"),
                optionalDouble(payload, "quantity"),
                optionalDouble(payload, "minQty"),
                optionalDouble(payload, "maxQty"),
                optionalInteger(payload, "location"),
                currentStoreId);
        return item;
    }

    private static Object jsonValue(Object value) {
        return value == null ? JSONObject.NULL : value;
    }

    private static String optionalString(JSONObject object, String key) throws JSONException {
        Object value = object.opt(key);
        if (value == null || value == JSONObject.NULL) {
            return null;
        }
        if (!(value instanceof String)) {
            throw new JSONException("Invalid item field: " + key);
        }
        return (String) value;
    }

    private static Double optionalDouble(JSONObject object, String key) throws JSONException {
        Object value = object.opt(key);
        if (value == null || value == JSONObject.NULL) {
            return null;
        }
        if (!(value instanceof Number)) {
            throw new JSONException("Invalid item field: " + key);
        }
        double number = ((Number) value).doubleValue();
        if (Double.isNaN(number) || Double.isInfinite(number)) {
            throw new JSONException("Invalid item field: " + key);
        }
        return number;
    }

    private static Integer optionalInteger(JSONObject object, String key) throws JSONException {
        Double value = optionalDouble(object, key);
        if (value == null) {
            return null;
        }
        if (value != Math.rint(value) || value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new JSONException("Invalid item field: " + key);
        }
        return value.intValue();
    }
}
