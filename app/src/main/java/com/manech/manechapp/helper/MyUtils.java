package com.manech.manechapp.helper;

import java.io.File;
import java.security.SecureRandom;
import java.util.UUID;
import java.nio.ByteBuffer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.Resource;
import com.squareup.picasso.Picasso;

public class MyUtils {
    public static UUID generateUniqueId() {
        SecureRandom random = new SecureRandom();
        long mostSigBits = random.nextLong();
        long leastSigBits = random.nextLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    public static String generateQRCodeValue(UUID uuid) {
        // Convert UUID to byte array
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        byte[] bytes = buffer.array();

        // Encode byte array as Base64 string
        return Base64.encodeToString(bytes, Base64.URL_SAFE);
    }

    public static double ceil2f(double d) {
        return Math.ceil(d * 100) / 100;
    }

    public static double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // method to get image size in bytes
    public static long getImageSize(Context mContext, Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        long imageSize = cursor.getLong(sizeIndex);
        cursor.close();
        return imageSize;
    }

    public static void loadResourceImage(Resource resource, ImageView imageView) {
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Uri imageUri = Uri.parse(resource.getUri());
                if (!resource.getUri().startsWith("http")) {
                    imageUri = Uri.fromFile(new File(resource.getUri()));
                }
                int width = imageView.getWidth();
                int height = width * (3 / 4);
                if (width > 0) {
                    Picasso.get().load(imageUri)
                            .resize(width, height)
                            .centerCrop()
                            //.placeholder(R.drawable.ic_manech_logo)
                            .error(R.drawable.ic_manech_logo)
                            .into(imageView);
                } else {
                    Picasso.get().load(imageUri)
                            //.placeholder(R.drawable.ic_manech_logo)
                            .error(R.drawable.ic_manech_logo)
                            .into(imageView);
                }
                // Remove the OnGlobalLayoutListener to prevent it from being called multiple times
                imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    public static String getRealPathFromUri(Context context,Uri uri) {
        String filePath = "";
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(column_index);
            }
            cursor.close();
        }
        return filePath;
    }
}