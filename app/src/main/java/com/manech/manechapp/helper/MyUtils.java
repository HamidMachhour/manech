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
        if (uri == null) {
            return Long.MAX_VALUE;
        }
        try (Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                    return cursor.getLong(sizeIndex);
                }
            }
        }
        return Long.MAX_VALUE;
    }

    public static void loadResourceImage(Resource resource, ImageView imageView) {
        Picasso.get().cancelRequest(imageView);
        if (resource == null || resource.getUri() == null) {
            imageView.setImageResource(R.drawable.ic_manech_logo);
            return;
        }
        final String source = resource.getUri();
        imageView.setTag(R.id.imgItem, source);
        final Uri imageUri = source.startsWith("http") ? Uri.parse(source) : Uri.fromFile(new File(source));

        final Runnable load = () -> {
            // the view may have been recycled for another item meanwhile
            if (!source.equals(imageView.getTag(R.id.imgItem))) {
                return;
            }
            int width = imageView.getWidth();
            if (width > 0) {
                Picasso.get().load(imageUri)
                        .resize(width, width * 3 / 4)
                        .centerCrop()
                        .error(R.drawable.ic_manech_logo)
                        .into(imageView);
            } else {
                Picasso.get().load(imageUri)
                        .error(R.drawable.ic_manech_logo)
                        .into(imageView);
            }
        };

        if (imageView.getWidth() > 0) {
            load.run();
            return;
        }
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (imageView.getViewTreeObserver().isAlive()) {
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                load.run();
            }
        });
    }


    // Copies the picked image into app storage and returns its absolute path (null on failure)
    public static String getRealPathFromUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        File dir = new File(context.getFilesDir(), "images");
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }
        File target = new File(dir, UUID.randomUUID().toString() + ".img");
        try (java.io.InputStream in = context.getContentResolver().openInputStream(uri);
             java.io.OutputStream out = new java.io.FileOutputStream(target)) {
            if (in == null) {
                return null;
            }
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return target.getAbsolutePath();
        } catch (java.io.IOException e) {
            target.delete();
            return null;
        }
    }
}
