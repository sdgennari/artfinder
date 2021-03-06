package com.hooapps.pca.cvilleart.artfinder.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
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
import com.hooapps.pca.cvilleart.artfinder.activity.VenueDetailActivity;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenue;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.data.VenueTable;
import com.hooapps.pca.cvilleart.artfinder.util.ColorUtils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MapFragment extends BaseFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final LatLng CVILLE_CENTER = new LatLng(38.030608, -78.481179);
    private static final int DEFAULT_ZOOM = 18;
    private static final String TAG = "MAPS";

    private LayoutInflater inflater;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;

    @InjectView(R.id.map_view)
    MapView mapView;
    @InjectView(R.id.map_center_button)
    Button centerButton;
    private HashMap<Marker, ArtVenue> venueMarkerHashMap;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = MainApp.getDatabase();
        venueMarkerHashMap = new HashMap<Marker, ArtVenue>();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        MapsInitializer.initialize(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.inject(this, rootView);
        mapView.onCreate(savedInstanceState);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpMap();
    }

   @Override
   public void onResume() {
       super.onResume();
       mapView.onResume();
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    private boolean servicesConnected() {
        int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity());
        if (statusCode != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(statusCode, getActivity(), 0).show();
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

        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(CVILLE_CENTER, DEFAULT_ZOOM));
            }
        });

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
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Selected Venue", venueMarkerHashMap.get(marker).organizationName);
                FlurryAgent.logEvent(getString(R.string.flurry_map_callout), params);

                Intent intent = new Intent(getActivity(), VenueDetailActivity.class);
                intent.putExtra(C.EXT_PARSE_OBJECT_ID, venueMarkerHashMap.get(marker).parseObjectId);
                startActivity(intent);
            }
        });

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CVILLE_CENTER, DEFAULT_ZOOM));

        String[] projection = {
                VenueTable.COL_PARSE_OBJECT_ID,
                VenueTable.COL_ORGANIZATION_NAME,
                VenueTable.COL_PRIMARY_CATEGORY,
                VenueTable.COL_STREET_ADDRESS,
                VenueTable.COL_LATITUDE,
                VenueTable.COL_LONGITUDE,
                VenueTable.COL_IMAGE_URL
        };

        Cursor cursor = db.query(VenueTable.TABLE_VENUES,
                projection,
                VenueTable.COL_IS_DELETED + "= 'NO'",
                null,
                null,
                null,
                null);

        ArtVenue venue;
        while (cursor != null && cursor.moveToNext()) {
            venue = new ArtVenue();
            venue.parseObjectId = cursor.getString(cursor.getColumnIndex(VenueTable.COL_PARSE_OBJECT_ID));
            venue.organizationName = cursor.getString(cursor.getColumnIndex(VenueTable.COL_ORGANIZATION_NAME));
            venue.primaryCategory = cursor.getString(cursor.getColumnIndex(VenueTable.COL_PRIMARY_CATEGORY));
            venue.streetAddress = cursor.getString(cursor.getColumnIndex(VenueTable.COL_STREET_ADDRESS));
            venue.latitude = cursor.getDouble(cursor.getColumnIndex(VenueTable.COL_LATITUDE));
            venue.longitude = cursor.getDouble(cursor.getColumnIndex(VenueTable.COL_LONGITUDE));
            venue.imageUrl = cursor.getString(cursor.getColumnIndex(VenueTable.COL_IMAGE_URL));

            int drawableResId = ColorUtils.getMarkerDrawableForCategory(venue.primaryCategory);
            MarkerOptions options = new MarkerOptions().title(venue.organizationName)
                    .snippet(venue.streetAddress)
                    .position(new LatLng(venue.latitude, venue.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(drawableResId))
                    .anchor(0.5f, 1.0f);
            Marker marker = map.addMarker(options);

            venueMarkerHashMap.put(marker, venue);
        }

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
            ArtVenue venue = venueMarkerHashMap.get(marker);

            int drawableResId = ColorUtils.getVenueDrawableForCategory(venue.primaryCategory);
            if (venue.imageUrl != null && !venue.imageUrl.isEmpty()) {
                Picasso.with(getActivity()).load(venue.imageUrl)
                        .placeholder(drawableResId)
                        .resize(C.THUMB_SIZE, C.THUMB_SIZE)
                        .centerCrop().into(imageView);
            } else {
                Picasso.with(getActivity()).load(drawableResId)
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
}
