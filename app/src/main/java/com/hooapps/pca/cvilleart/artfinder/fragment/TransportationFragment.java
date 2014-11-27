package com.hooapps.pca.cvilleart.artfinder.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.data.VenueTable;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TransportationFragment extends BaseFragment {

    private static final String CAT_PACKAGE_NAME = "com.cville.cattail";

    private SQLiteDatabase db;
    private LayoutInflater inflater;
    private HashMap<String, String> venueAddressHashMap;
    private GoogleMap map;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private LatLng waterStreetGarage = new LatLng(38.029357, -78.480873);
    private LatLng waterStreetLot = new LatLng(38.029534, -78.481688);
    private LatLng marketStreetGarage = new LatLng(38.030210, -78.477815);

    @InjectView(R.id.trans_direction_button)
    Button directionsButton;
    @InjectView(R.id.map_view)
    MapView mapView;
    @InjectView(R.id.trans_download_cat_button)
    Button catDownloadButton;
    @InjectView(R.id.trans_start_auto_comp)
    AutoCompleteTextView startAutoComplete;
    @InjectView(R.id.trans_end_auto_comp)
    AutoCompleteTextView endAutoComplete;
    @InjectView(R.id.cat_container)
    LinearLayout catContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transportation, container, false);
        this.inflater = inflater;
        db = MainApp.getDatabase();
        ButterKnife.inject(this, rootView);

        mapView.onCreate(savedInstanceState);
        initVenueAddressHashMap();
        configureViews();
        configureMap();

        return rootView;
    }

    private void configureViews() {
        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Selected Venue", endAutoComplete.getText().toString());
                FlurryAgent.logEvent(getString(R.string.flurry_directions), params);

                String startAddress = startAutoComplete.getText().toString();
                String endAddress = endAutoComplete.getText().toString();
                launchGoogleMapIntent(startAddress, endAddress);
            }
        });

        String[] venues = new String[venueAddressHashMap.keySet().size()];
        int i = 0;
        for (String venueName : venueAddressHashMap.keySet()) {
            venues[i] = venueName;
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.trans_autocomp_item, venues);
        startAutoComplete.setAdapter(adapter);
        endAutoComplete.setAdapter(adapter);

        if (isCATInstalled()) {
            catContainer.setVisibility(View.GONE);
        }

        catDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlurryAgent.logEvent(getString(R.string.flurry_get_cat_app));
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + CAT_PACKAGE_NAME));
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException anfe) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + CAT_PACKAGE_NAME));
                    startActivity(intent);
                }
            }
        });
    }

    private void configureMap() {
        map = mapView.getMap();

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = inflater.inflate(R.layout.trans_info_window, null);
                TextView nameView = (TextView) view.findViewById(R.id.garage_name);
                nameView.setText(marker.getTitle());

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Lot", marker.getTitle());
                FlurryAgent.logEvent(getString(R.string.flurry_parking_directions), params);
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                endAutoComplete.setText(marker.getTitle());
                marker.hideInfoWindow();
            }
        });

        map.getUiSettings().setAllGesturesEnabled(false);
        map.getUiSettings().setScrollGesturesEnabled(true);

        // Add the markers to the map
        MarkerOptions options = new MarkerOptions().title(getString(R.string.trans_water_street_garage))
                .position(waterStreetGarage)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_garage))
                .anchor(0.50F, 1.0F);
        venueAddressHashMap.put(getString(R.string.trans_water_street_garage), waterStreetGarage.latitude + ", " + waterStreetGarage.longitude);
        markers.add(map.addMarker(options));

        options = new MarkerOptions().title(getString(R.string.trans_water_street_lot))
                .position(waterStreetLot)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_garage))
                .anchor(0.50F, 1.0F);
        venueAddressHashMap.put(getString(R.string.trans_water_street_lot), waterStreetLot.latitude + ", " + waterStreetLot.longitude);
        markers.add(map.addMarker(options));

        options = new MarkerOptions().title(getString(R.string.trans_market_street_garage))
                .position(marketStreetGarage)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_garage))
                .anchor(0.50F, 1.0F);
        venueAddressHashMap.put(getString(R.string.trans_market_street_garage), marketStreetGarage.latitude + ", " + marketStreetGarage.longitude);
        markers.add(map.addMarker(options));

        mapView.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                moveToMapCenter();
            }
        });
    }

    private void moveToMapCenter() {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (Marker m : markers) {
            builder.include(m.getPosition());
        }
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void launchGoogleMapIntent(String startAddress, String endAddress) {
        if (endAddress == null || endAddress.isEmpty()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.trans_empty_destination), Toast.LENGTH_SHORT).show();
            return;
        }

        if (venueAddressHashMap.get(startAddress) != null) {
            startAddress = venueAddressHashMap.get(startAddress);
        }
        if (venueAddressHashMap.get(endAddress) != null) {
            endAddress = venueAddressHashMap.get(endAddress);
        }

        // Encode the url
        String uri = String.format(C.GOOGLE_MAP_URL, startAddress, endAddress);

        Intent directionsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        directionsIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(directionsIntent);
    }

    // Note: The parking garages are added in a later method
    private void initVenueAddressHashMap() {
        venueAddressHashMap = new HashMap<String, String>();

        String[] projection = {
                VenueTable.COL_ORGANIZATION_NAME,
                VenueTable.COL_STREET_ADDRESS
        };

        Cursor cursor = db.query(VenueTable.TABLE_VENUES,
                projection,
                VenueTable.COL_IS_DELETED + "= 'NO'",
                null,
                null,
                null,
                null);

        String name;
        String address;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex(VenueTable.COL_ORGANIZATION_NAME));
                address = cursor.getString(cursor.getColumnIndex(VenueTable.COL_STREET_ADDRESS));
                venueAddressHashMap.put(name, address);
            }
        }
    }

    private boolean isCATInstalled() {
        PackageManager pm = MainApp.getContext().getPackageManager();
        boolean isInstalled = false;
        try {
            pm.getPackageInfo(CAT_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            isInstalled = true;
        } catch (Exception e) {
            isInstalled = false;
        }
        return isInstalled;
    }
}
