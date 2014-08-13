package com.hooapps.pca.cvilleart.artfinder.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;

import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenue;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.data.VenueTable;

public class VenueDetailActivity extends BaseActivity {

    private String parseObjectId;
    private ArtVenue venue;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_detail);

        db = MainApp.getDatabase();

        // Make the ActionBar title clickable
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        parseObjectId = getIntent().getStringExtra(C.EXT_PARSE_OBJECT_ID);
        if (parseObjectId == null || parseObjectId.isEmpty()) {
            // TODO HANDLE ERROR HERE
            return;
        }
        venue = fetchVenueData();
    }

    private ArtVenue fetchVenueData() {

        String[] projection = {
                VenueTable.COL_ORGANIZATION_NAME,
                VenueTable.COL_STREET_ADDRESS,
                VenueTable.COL_CITY,
                VenueTable.COL_STATE,
                VenueTable.COL_PRIMARY_CATEGORY,
                VenueTable.COL_PHONE,
                VenueTable.COL_DESCRIPTION,
                VenueTable.COL_LATITUDE,
                VenueTable.COL_LONGITUDE
        };

        Cursor c = db.query(
                VenueTable.TABLE_VENUES,
                projection,
                VenueTable.COL_PARSE_OBJECT_ID + " = ?",
                new String[] {parseObjectId},
                null,
                null,
                null
        );

        c.moveToNext();

        ArtVenue artVenue = new ArtVenue();
        artVenue.organizationName = c.getString(c.getColumnIndex(VenueTable.COL_ORGANIZATION_NAME));
        artVenue.streetAddress = c.getString(c.getColumnIndex(VenueTable.COL_STREET_ADDRESS));
        artVenue.city = c.getString(c.getColumnIndex(VenueTable.COL_CITY));
        artVenue.state = c.getString(c.getColumnIndex(VenueTable.COL_STATE));
        artVenue.primaryCategory = c.getString(c.getColumnIndex(VenueTable.COL_PRIMARY_CATEGORY));
        artVenue.phone = c.getString(c.getColumnIndex(VenueTable.COL_PHONE));
        artVenue.description = c.getString(c.getColumnIndex(VenueTable.COL_DESCRIPTION));
        artVenue.latitude = c.getDouble(c.getColumnIndex(VenueTable.COL_LATITUDE));
        artVenue.longitude = c.getDouble(c.getColumnIndex(VenueTable.COL_LONGITUDE));

        return artVenue;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
