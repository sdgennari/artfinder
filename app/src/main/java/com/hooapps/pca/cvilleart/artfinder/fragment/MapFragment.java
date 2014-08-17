package com.hooapps.pca.cvilleart.artfinder.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
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

public class MapFragment extends BaseFragment implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

    private static final LatLng CVILLE_CENTER = new LatLng(38.030608, -78.481179);
    private static final int DEFAULT_ZOOM = 18;

    private LayoutInflater inflater;
    private GoogleMap map;
    private LocationClient locationClient;
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

        locationClient = new LocationClient(getActivity(), this, this);

        MapsInitializer.initialize(getActivity());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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
       if (servicesConnected() && !locationClient.isConnected()) {
           locationClient.connect();
       }
   }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationClient.isConnected()) {
            locationClient.disconnect();
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
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
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
    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    }

    @Override
    public void onDisconnected() {
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

        //-----

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
        map.setInfoWindowAdapter(new VenueInfoWIndowAdapter());

        // Launch the VenueDetailActivity when an info window is selected
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getActivity(), VenueDetailActivity.class);
                intent.putExtra(C.EXT_PARSE_OBJECT_ID, venueMarkerHashMap.get(marker).parseObjectId);
                startActivity(intent);
            }
        });

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CVILLE_CENTER, DEFAULT_ZOOM));

        // TODO LOAD VENUE ID FROM THE ARGUMENTS
        // String parseObjectId = getArguments().getString(VenueTable.COL_PARSE_OBJECT_ID);

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

        Log.d("VENUES", "" + cursor.getCount());

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

            int colorResId = ColorUtils.getColorForCategory(venue.primaryCategory);
            MarkerOptions options = new MarkerOptions().title(venue.organizationName)
                    .snippet(venue.streetAddress)
                    .position(new LatLng(venue.latitude, venue.longitude))
                    //.icon(BitmapDescriptorFactory.fromResource(colorResId))
                    .anchor(0.5f, 1.0f);
            Marker marker = map.addMarker(options);

            venueMarkerHashMap.put(marker, venue);

            // TODO RECENTER MAP ON SELECTED VENUE BASED ON ARGS
        }

    }

    class VenueInfoWIndowAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker marker) {
            View v = inflater.inflate(R.layout.info_window_map, null);

            TextView nameView = (TextView) v.findViewById(R.id.venue_name);
            nameView.setText(marker.getTitle());

            TextView addressView = (TextView) v.findViewById(R.id.venue_address);
            addressView.setText(marker.getSnippet());

            ImageView imageView = (ImageView) v.findViewById(R.id.venue_image);
            ArtVenue venue = venueMarkerHashMap.get(marker);
            int colorResId = ColorUtils.getColorForCategory(venue.primaryCategory);

            if (venue.imageUrl != null && !venue.imageUrl.isEmpty()) {
                Picasso.with(getActivity()).load(venue.imageUrl).resize(C.THUMB_SIZE, C.THUMB_SIZE).centerCrop().into(imageView);
            }

            return v;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
}
