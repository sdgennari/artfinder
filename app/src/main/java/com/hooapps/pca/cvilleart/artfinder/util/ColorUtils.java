package com.hooapps.pca.cvilleart.artfinder.util;

import com.hooapps.pca.cvilleart.artfinder.R;

public class ColorUtils {

    public static int getColorForCategory(String category) {
        int colorResId;
        if (category.equalsIgnoreCase("dance")) {
            colorResId = R.color.green;
        } else if (category.equalsIgnoreCase("gallery")) {
            colorResId = R.color.blue;
        } else if (category.equalsIgnoreCase("music")) {
            colorResId = R.color.orange;
        } else if (category.equalsIgnoreCase("theatre")) {
            colorResId = R.color.purple;
        } else {
            colorResId = R.color.indigo;
        }
        return colorResId;
    }
}
