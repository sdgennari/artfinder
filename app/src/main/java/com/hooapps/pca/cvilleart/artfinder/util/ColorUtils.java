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

    public static int getColorDrawableForCategory(String category) {
        int drawableResId;
        if (category.equalsIgnoreCase("dance")) {
            drawableResId = R.drawable.bg_green;
        } else if (category.equalsIgnoreCase("gallery")) {
            drawableResId = R.drawable.bg_blue;
        } else if (category.equalsIgnoreCase("music")) {
            drawableResId = R.drawable.bg_orange;
        } else if (category.equalsIgnoreCase("theatre")) {
            drawableResId = R.drawable.bg_purple;
        } else {
            drawableResId = R.drawable.bg_indigo;
        }
        return drawableResId;
    }

    public static int getVenueDrawableForCategory(String category) {
        int drawableResId;
        if (category.equalsIgnoreCase("dance")) {
            drawableResId = R.drawable.dance;
        } else if (category.equalsIgnoreCase("gallery")) {
            drawableResId = R.drawable.gallery;
        } else if (category.equalsIgnoreCase("music")) {
            drawableResId = R.drawable.music;
        } else if (category.equalsIgnoreCase("theatre")) {
            drawableResId = R.drawable.theatre;
        } else {
            drawableResId = R.drawable.other;
        }
        return drawableResId;
    }

    public static int getMarkerDrawableForCategory(String category) {
        int drawableResId;
        if (category.equalsIgnoreCase("dance")) {
            drawableResId = R.drawable.marker_dance;
        } else if (category.equalsIgnoreCase("gallery")) {
            drawableResId = R.drawable.marker_gallery;
        } else if (category.equalsIgnoreCase("music")) {
            drawableResId = R.drawable.marker_music;
        } else if (category.equalsIgnoreCase("theatre")) {
            drawableResId = R.drawable.marker_theatre;
        } else {
            drawableResId = R.drawable.marker_other;
        }
        return drawableResId;
    }
}
