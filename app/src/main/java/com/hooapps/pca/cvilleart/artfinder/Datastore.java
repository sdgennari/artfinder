package com.hooapps.pca.cvilleart.artfinder;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.hooapps.pca.cvilleart.artfinder.constants.C;

public class Datastore {

    private static final String ART_VENUE_UPGRADE_DATE = "artVenueUpgradeDate";
    private static final String EVENT_LIST_FILTER = "eventListFilter";

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

    public void saveEventFilterItems(boolean[] checkedItems) {
        Gson gson = new Gson();
        String serializedArray = gson.toJson(checkedItems);
        getEditor().putString(EVENT_LIST_FILTER, serializedArray).commit();
    }

    public boolean[] getEventFilterItems() {
        String arrayString = getPrefs().getString(EVENT_LIST_FILTER, "");
        boolean[] checkedItems = new boolean[C.NUM_FILTER_ITEMS];
        if (!arrayString.isEmpty()) {
            Gson gson = new Gson();
            checkedItems = gson.fromJson(arrayString, boolean[].class);
        } else {
            for (int i = 0; i < checkedItems.length; i++) {
                checkedItems[i] = true;
            }
        }
        return checkedItems;
    }

}