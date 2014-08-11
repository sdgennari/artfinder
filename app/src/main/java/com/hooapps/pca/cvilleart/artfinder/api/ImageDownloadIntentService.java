package com.hooapps.pca.cvilleart.artfinder.api;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.util.ImageUtils;

import java.util.ArrayList;

public class ImageDownloadIntentService extends IntentService {

    public ImageDownloadIntentService() {
        super("ImageDownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ArrayList<String> imageUrlList = intent.getStringArrayListExtra(C.EXT_IMAGE_URL_LIST);
        ArrayList<String> parseObjectIdList = intent.getStringArrayListExtra(C.EXT_PARSE_OBJECT_ID_LIST);
        Log.d("TEST", "imageUrlList " + imageUrlList.size());
        for (int i = 0; i < imageUrlList.size(); i++) {
            ImageUtils.saveImage(this, imageUrlList.get(i), ImageUtils.getVenueThumbPath(parseObjectIdList.get(i)));
        }
    }
}
