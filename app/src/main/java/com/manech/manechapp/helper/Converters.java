package com.manech.manechapp.helper;

import android.location.Location;

import androidx.room.TypeConverter;

import com.manech.manechapp.data.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Converters {
    @TypeConverter
    public static UUID fromString(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    @TypeConverter
    public static String uuidToString(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

    @TypeConverter
    public static Location fromJsonString(String jsonString) {
        Location location = new Location("");
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            location.setLatitude(jsonObject.getDouble("latitude"));
            location.setLongitude(jsonObject.getDouble("longitude"));
            location.setAccuracy((float) jsonObject.getDouble("accuracy"));
        } catch (JSONException e) {
           // e.printStackTrace();
        }
        return location;
    }

    @TypeConverter
    public static String toJsonString(Location location) {
            JSONObject jsonObject = new JSONObject();
        if(location!=null) {
            try {
                jsonObject.put("latitude", location.getLatitude());
                jsonObject.put("longitude", location.getLongitude());
                jsonObject.put("accuracy", location.getAccuracy());
            } catch (JSONException e) {
             //   e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @TypeConverter
    public static Date toDate(String value) {
        if (value != null) {
            try {
                return df.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String fromDate(Date date) {
        return date == null ? null : df.format(date);
    }

    @TypeConverter
    public static Status statusFromString(String value) {
        return Status.valueOf(value);
    }

    @TypeConverter
    public static String statusToString(Status value) {
        return value.name();
    }
}
