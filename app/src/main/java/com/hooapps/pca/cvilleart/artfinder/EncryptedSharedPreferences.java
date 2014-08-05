package com.hooapps.pca.cvilleart.artfinder;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import oak.ObscuredSharedPreferences;

public class EncryptedSharedPreferences extends ObscuredSharedPreferences {

    public EncryptedSharedPreferences(Context context, SharedPreferences delegate) {
        super(context, delegate);
    }

    @Override
    protected char[] getSpecialCode() {
        return "C4VR17LFL1!3ND3R".toCharArray();
    }

    @Override
    public Set<String> getStringSet(String s, Set<String> strings) {
        return null;
    }


}
