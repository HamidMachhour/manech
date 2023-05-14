package com.manech.manechapp.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.PrimaryKey;

import com.manech.manechapp.MainActivity;
import com.manech.manechapp.helper.MyUtils;

import java.util.UUID;

public class ManechEntity {
    @PrimaryKey
    @NonNull
    private UUID id= MyUtils.generateUniqueId();

    private String qrCode=MyUtils.generateQRCodeValue(id);

    @NonNull
    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj) || ((ManechEntity)obj).getId().equals(this.id);
    }
}
