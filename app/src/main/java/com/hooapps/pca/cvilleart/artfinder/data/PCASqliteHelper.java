package com.hooapps.pca.cvilleart.artfinder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PCASqliteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "artfinder.db";
    private static final int DATABASE_VERSION = 1;

    public PCASqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        VenueTable.onCreate(db);
        EventTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        VenueTable.onUpgrade(db, oldVersion, newVersion);
        EventTable.onUpgrade(db, oldVersion, newVersion);
    }
}
