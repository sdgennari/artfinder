package com.hooapps.pca.cvilleart.artfinder.api;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenue;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenueResponse;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.data.PCASqliteHelper;
import com.hooapps.pca.cvilleart.artfinder.data.VenueTable;

import java.util.List;

import retrofit.RestAdapter;

public class VenueDatabaseIntentService extends IntentService {

    private VenueService venueService;
    private List<ArtVenue> artVenueList;
    private SQLiteDatabase db;

    public VenueDatabaseIntentService() {
        super("VenueDatabaseIntentService");
        PCASqliteHelper dbHelper = new PCASqliteHelper(MainApp.getContext());
        db = dbHelper.getWritableDatabase();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String whereClause = intent.getStringExtra(C.WHERE_CLAUSE);
        venueService = initVenueService();
        ArtVenueResponse response = venueService.getAllArtVenuesAfterDate(whereClause);
        artVenueList = response.results;
        insertArtVenues();
    }

    private VenueService initVenueService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(VenueService.VENUE_BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
        return restAdapter.create(VenueService.class);
    }

    private void insertArtVenues() {
        // Start the database transaction here
        db.beginTransaction();

        // Loop through every item and prep it for insert
        ContentValues values;
        for (ArtVenue venue : artVenueList) {
            values = makeContentValuesFromObject(venue);
            // Insert or update the columns
            db.insertWithOnConflict(VenueTable.TABLE_VENUES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }

        // Commit the transaction all at once to save time
        db.setTransactionSuccessful();
        db.endTransaction();

        // TODO REMOVE AFTER VERIFYING SUCCESS
        testDataSave(db);
    }

    private void testDataSave(SQLiteDatabase db) {
        String[] projection = {
                VenueTable.COL_ID,
                VenueTable.COL_CREATED_AT,
                VenueTable.COL_UPDATED_AT,
                VenueTable.COL_ORGANIZATION_NAME
        };

        Cursor c = db.query(
                VenueTable.TABLE_VENUES,
                projection,
                null,
                null,
                null,
                null,
                null);

        Log.d("SUCCESS", "Items: " + c.getCount());
    }

    private ContentValues makeContentValuesFromObject(ArtVenue venue) {
        ContentValues values = new ContentValues();
        values.put(VenueTable.COL_PARSE_OBJECT_ID, venue.objectId);
        values.put(VenueTable.COL_CITY, venue.city);
        values.put(VenueTable.COL_DESCRIPTION, venue.description);
        values.put(VenueTable.COL_EMAIL, venue.emailAddress);
        values.put(VenueTable.COL_HOME_PAGE, venue.homePageUrl);
        values.put(VenueTable.COL_IMAGE_URL, venue.imageUrl);
        values.put(VenueTable.COL_LATITUDE, venue.latitude);
        values.put(VenueTable.COL_LONGITUDE, venue.longitude);
        values.put(VenueTable.COL_ORGANIZATION_NAME, venue.organizationName);
        values.put(VenueTable.COL_PHONE, venue.phone);
        values.put(VenueTable.COL_PRIMARY_CATEGORY, venue.primaryCategory);
        values.put(VenueTable.COL_SECONDARY_CATEGORY, venue.secondaryCategory);
        values.put(VenueTable.COL_STATE, venue.state);
        values.put(VenueTable.COL_STREET_ADDRESS, venue.streetAddress);
        values.put(VenueTable.COL_ZIP, venue.zip);
        values.put(VenueTable.COL_IS_DELETED, venue.isDeletedString);
        values.put(VenueTable.COL_CREATED_AT, venue.getCreatedTime());
        values.put(VenueTable.COL_UPDATED_AT, venue.getUpdatedTime());
        return values;
    }

}
