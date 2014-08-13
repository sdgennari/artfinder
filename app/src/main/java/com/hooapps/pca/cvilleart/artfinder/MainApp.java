package com.hooapps.pca.cvilleart.artfinder;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.hooapps.pca.cvilleart.artfinder.data.PCASqliteHelper;

public class MainApp extends Application {

    public static String TAG = "cville-artfinder";
    private static Context sContext;

    // TODO ADD ANALYTICS TRACKING
    private static final String ANALYTICS_TRACKER_ID = "XXX";

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }

    public static SQLiteDatabase getDatabase() {
        PCASqliteHelper dbHelper = new PCASqliteHelper(MainApp.getContext());
        return dbHelper.getWritableDatabase();
    }
}