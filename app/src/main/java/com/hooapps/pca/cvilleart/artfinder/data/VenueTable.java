package com.hooapps.pca.cvilleart.artfinder.data;

import android.database.sqlite.SQLiteDatabase;

public class VenueTable {

    public static final String TABLE_VENUES = "venues";
    public static final String COL_ID = "_id";
    public static final String COL_PARSE_OBJECT_ID = "parse_object_id";
    public static final String COL_CITY = "city";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_EMAIL = "email_address";
    public static final String COL_HOME_PAGE = "home_page_url";
    public static final String COL_IMAGE_URL = "image_url";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGITUDE = "longitude";
    public static final String COL_ORGANIZATION_NAME = "organization_name";
    public static final String COL_PHONE = "phone";
    public static final String COL_PRIMARY_CATEGORY = "primary_category";
    public static final String COL_SECONDARY_CATEGORY = "secondary_category";
    public static final String COL_STATE = "state";
    public static final String COL_STREET_ADDRESS = "street_address";
    public static final String COL_ZIP = "zip";
    public static final String COL_IS_DELETED = "is_deleted";
    public static final String COL_CREATED_AT = "created_at";
    public static final String COL_UPDATED_AT = "updated_at";

    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_VENUES
            + " ( "
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_PARSE_OBJECT_ID + " VARCHAR(10) NOT NULL UNIQUE, "
            + COL_CITY + " VARCHAR(255), "
            + COL_DESCRIPTION + " TEXT, "
            + COL_EMAIL + " VARCHAR(255), "
            + COL_HOME_PAGE + " VARCHAR(255), "
            + COL_IMAGE_URL + " VARCHAR(255), "
            + COL_LATITUDE + " VARCHAR(20), "
            + COL_LONGITUDE + " VARCHAR(20), "
            + COL_ORGANIZATION_NAME + " VARCHAR(255) NOT NULL, "
            + COL_PHONE + " VARCHAR(20), "
            + COL_PRIMARY_CATEGORY + " VARCHAR(20), "
            + COL_SECONDARY_CATEGORY + " VARCHAR(20), "
            + COL_STATE + " VARCHAR(2), "
            + COL_STREET_ADDRESS + " VARCHAR(255), "
            + COL_ZIP + " INTEGER, "
            + COL_IS_DELETED + " BOOL NOT NULL DEFAULT '1', "
            + COL_CREATED_AT + " INTEGER, "
            + COL_UPDATED_AT + " INTEGER "
            + ");";

    private static final String DROP_TABLE = "DROP TABLIE IF EXISTS " + TABLE_VENUES;

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

}