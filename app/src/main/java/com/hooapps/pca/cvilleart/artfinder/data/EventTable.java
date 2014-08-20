package com.hooapps.pca.cvilleart.artfinder.data;

import android.database.sqlite.SQLiteDatabase;

public class EventTable {

    public static final String TABLE_EVENTS = "events";
    public static final String COL_ID = "_id";
    public static final String COL_EVENT_ID = "event_id";
    public static final String COL_CATEGORY = "category";
    public static final String COL_UPDATED = "updated";
    public static final String COL_SUMMARY = "summary";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_LOCATION = "location";
    public static final String COL_START_TIME = "start_time";
    public static final String COL_END_TIME = "end_time";

    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_EVENTS
            + " ( "
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_EVENT_ID + " VARCHAR(43) NOT NULL UNIQUE, "
            + COL_CATEGORY + " VARCHAR(20), "
            + COL_UPDATED + " INTEGER, "
            + COL_SUMMARY + " VARCHAR(255) NOT NULL, "
            + COL_DESCRIPTION + " TEXT, "
            + COL_LOCATION + " VARCHAR(255), "
            + COL_START_TIME + " INTEGER, "
            + COL_END_TIME + " INTEGER "
            + ");";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_EVENTS;

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
