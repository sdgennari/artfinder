package com.hooapps.pca.cvilleart.artfinder.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenue;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

public class ImageUtils {

    private static final String THUMB_DIR = "thumbnails";

    public static void saveImage(Context context, String url, String destPath) {
        File thumbnailFolder = context.getExternalFilesDir(THUMB_DIR);
        if (thumbnailFolder != null && !thumbnailFolder.exists()) {
            thumbnailFolder.mkdir();
        }

        final File thumb = new File(thumbnailFolder, destPath);

        Target target = new Target() {
            @Override
            public void onBitmapFailed(Drawable arg0) {
                Log.d("TEST", "Bitmap failed");
            }

            @Override
            public void onPrepareLoad(Drawable arg0) {}

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d("TEST", "Bitmap loaded");
                try {
                    FileOutputStream out = new FileOutputStream(thumb);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    Log.e(MainApp.TAG, e.getLocalizedMessage());
                }
            }
        };

        if (url != null && !url.isEmpty()) {
            Picasso.with(context).load(url).resize(C.THUMB_SIZE, C.THUMB_SIZE).centerCrop().into(target);
        }
    }

    public static String getVenueThumbPath(ArtVenue venue) {
        return getVenueThumbPath(venue.parseObjectId);
    }

    public static String getVenueThumbPath(String parseObjectId) {
        return parseObjectId + "_thumb.png";
    }
}
