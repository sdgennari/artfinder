package com.hooapps.pca.cvilleart.artfinder;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Datastore {

    private static final String ART_VENUE_UPGRADE_DATE = "artVenueUpgradeDate";
    private static final String DEFAULT_UPGRADE_TIME = "2014-01-01T00:00:00.000Z";

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

    public void saveVenueUpgradeDate(String date) {
        getEditor().putString(ART_VENUE_UPGRADE_DATE, date).commit();
    }

    public String getArtVenueUpgradeDate() {
        return getPrefs().getString(ART_VENUE_UPGRADE_DATE, DEFAULT_UPGRADE_TIME);
    }

}