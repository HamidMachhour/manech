package com.manech.manechapp.helper;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

public class SecureSharedPref {
    private static SecureSharedPref instance = null;

    private SharedPreferences sharedPreferences;

    public static SecureSharedPref getInstance(Context context, String fileName) {
        if (instance == null) {
            try {
                instance = new SecureSharedPref(context, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public SecureSharedPref(Context context, String fileName) throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        sharedPreferences = EncryptedSharedPreferences.create(
                fileName,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public UUID getUUID(String key, UUID defValue) {

        return Converters.fromString(getString(key,Converters.uuidToString(defValue)));
    }

    public void putUUID(String key, UUID value) {
        putString(key,Converters.uuidToString(value));
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public void putInt(String key, int defValue) {
        sharedPreferences.edit().putInt(key,defValue).apply();
    }
}

