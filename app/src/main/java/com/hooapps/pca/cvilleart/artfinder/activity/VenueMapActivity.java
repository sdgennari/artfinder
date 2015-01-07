package com.hooapps.pca.cvilleart.artfinder.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenue;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.data.VenueTable;
import com.hooapps.pca.cvilleart.artfinder.util.ColorUtils;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class VenueMapActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int DEFAULT_ZOOM = 18;
    private static final String TAG = "MAPS";

    private LayoutInflater inflater;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Context context;
    @InjectView(R.id.map_view)
    MapView mapView;
    private SQLiteDatabase db;
    private String parseObjectId;
    private ArtVenue artVenue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = MainApp.getDatabase();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        inflater = this.getLayoutInflater();
        context = this;

        MapsInitializer.initialize(this);

        this.setContentView(R.layout.activity_venue_map);
        ButterKnife.inject(this);

        mapView.onCreate(savedInstanceState);

        parseObjectId = getIntent().getStringExtra(C.EXT_PARSE_OBJECT_ID);
        if (parseObjectId == null || parseObjectId.isEmpty()) {
            // TODO HANDLE ERROR HERE
        }

        artVenue = fetchVenueData();

        // Customize the ActionBar based on the venue
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setBackgroundDrawable(getResources().getDrawable(
                    ColorUtils.getColorDrawableForCategory(artVenue.primaryCategory)));
            getActionBar().setIcon(ColorUtils.getVenueDrawableForCategory(artVenue.primaryCategory));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        if (servicesConnected() && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    private boolean servicesConnected() {
        int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (statusCode != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(statusCode, this, 0).show();
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Log.i(TAG, "GoogleApiClient connection failed");
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Log.i(TAG, "GoogleApiClient connection started");
    }

    private void setUpMap() {
        if (map == null) {
            map = mapView.getMap();
        }
        if (map != null) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.setMyLocationEnabled(true);
            map.getUiSettings().setRotateGesturesEnabled(false);
        }

        if (map == null) {
            return;
        }
        map.clear();

        // Configure the info window popups for the map
        map.setInfoWindowAdapter(new VenueInfoWindowAdapter());

        // Launch the VenueDetailActivity when an info window is selected
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Close the current activity and return to the VenueDetailActivity when clicked
                finish();
            }
        });

        // Configure the venue marker on the map
        int drawableResId = ColorUtils.getMarkerDrawableForCategory(artVenue.primaryCategory);
        MarkerOptions options = new MarkerOptions().title(artVenue.organizationName)
                .snippet(artVenue.streetAddress)
                .position(new LatLng(artVenue.latitude, artVenue.longitude))
                .icon(BitmapDescriptorFactory.fromResource(drawableResId))
                .anchor(0.5f, 1.0f);
        map.addMarker(options);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(options.getPosition(), DEFAULT_ZOOM));
    }

    private ArtVenue fetchVenueData() {
        String[] projection = {
                VenueTable.COL_PARSE_OBJECT_ID,
                VenueTable.COL_ORGANIZATION_NAME,
                VenueTable.COL_PRIMARY_CATEGORY,
                VenueTable.COL_STREET_ADDRESS,
                VenueTable.COL_LATITUDE,
                VenueTable.COL_LONGITUDE,
                VenueTable.COL_IMAGE_URL
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

        ArtVenue venue = new ArtVenue();
        venue.parseObjectId = c.getString(c.getColumnIndex(VenueTable.COL_PARSE_OBJECT_ID));
        venue.organizationName = c.getString(c.getColumnIndex(VenueTable.COL_ORGANIZATION_NAME));
        venue.primaryCategory = c.getString(c.getColumnIndex(VenueTable.COL_PRIMARY_CATEGORY));
        venue.streetAddress = c.getString(c.getColumnIndex(VenueTable.COL_STREET_ADDRESS));
        venue.latitude = c.getDouble(c.getColumnIndex(VenueTable.COL_LATITUDE));
        venue.longitude = c.getDouble(c.getColumnIndex(VenueTable.COL_LONGITUDE));
        venue.imageUrl = c.getString(c.getColumnIndex(VenueTable.COL_IMAGE_URL));
        return venue;
    }

    class VenueInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker marker) {
            View v = inflater.inflate(R.layout.info_window_map, null);

            TextView nameView = (TextView) v.findViewById(R.id.venue_name);
            nameView.setText(marker.getTitle());

            TextView addressView = (TextView) v.findViewById(R.id.venue_address);
            addressView.setText(marker.getSnippet());

            ImageView imageView = (ImageView) v.findViewById(R.id.venue_image);

            int drawableResId = ColorUtils.getVenueDrawableForCategory(artVenue.primaryCategory);
            if (artVenue.imageUrl != null && !artVenue.imageUrl.isEmpty()) {
                Picasso.with(context).load(artVenue.imageUrl)
                        .placeholder(drawableResId)
                        .resize(C.THUMB_SIZE, C.THUMB_SIZE)
                        .centerCrop().into(imageView);
            } else {
                Picasso.with(context).load(drawableResId)
                        .placeholder(drawableResId)
                        .resize(C.THUMB_SIZE, C.THUMB_SIZE)
                        .into(imageView);
            }

            return v;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
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
