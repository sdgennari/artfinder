package com.hooapps.pca.cvilleart.artfinder;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hooapps.pca.cvilleart.artfinder.constants.C;

public class Datastore {

    private static final String ART_VENUE_UPGRADE_DATE = "artVenueUpgradeDate";

    private static Datastore instance;

    private EncryptedSharedPreferences encryptedSharedPreferences;

    protected Datastore() {
        encryptedSharedPreferences = new EncryptedSharedPreferences(MainApp.getContext(),
                PreferenceManager.getDefaultSharedPreferences(MainApp.getContext()));
    }

    public static Datastore getInstance() {
        if (instance == null) {
            instance = new Datastore();
        }
        return instance;
    }

    private SharedPreferences.Editor getEditor() {
        return encryptedSharedPreferences.edit();
    }

    private SharedPreferences getPrefs() {
        return encryptedSharedPreferences;
    }

    public void saveVenueUpdateDate(String date) {
        getEditor().putString(ART_VENUE_UPGRADE_DATE, date).commit();
    }

    public String getArtVenueUpdateDate() {
        return getPrefs().getString(ART_VENUE_UPGRADE_DATE, C.DEFAULT_UPDATED_AT_TIME);
    }

}