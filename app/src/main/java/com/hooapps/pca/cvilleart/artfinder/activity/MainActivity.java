package com.hooapps.pca.cvilleart.artfinder.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hooapps.pca.cvilleart.artfinder.Datastore;
import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.adapter.NavDrawerAdapter;
import com.hooapps.pca.cvilleart.artfinder.api.VenueDatabaseIntentService;
import com.hooapps.pca.cvilleart.artfinder.api.VenueService;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenue;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenueResponse;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.fragment.EventListFragment;
import com.hooapps.pca.cvilleart.artfinder.fragment.HomeFragment;
import com.hooapps.pca.cvilleart.artfinder.fragment.MapFragment;
import com.hooapps.pca.cvilleart.artfinder.fragment.TransportationFragment;
import com.hooapps.pca.cvilleart.artfinder.fragment.VenueListFragment;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RestAdapter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @InjectView(R.id.nav_drawer)
    ListView navDrawer;

    private VenueService venueService;
    private Datastore datastore;

    private NavDrawerAdapter navDrawerAdapter;
    private ActionBarDrawerToggle drawerToggle;

    private Subscription artVenueFetchTask;
    private List<ArtVenue> artVenueList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // Initialize the datastore singleton
        datastore = Datastore.getInstance();

        // Fetch ArtVenue data
        fetchArtVenueData();

        // Configure the adapter to add items to the NavDrawer
        ArrayList<String> navDrawerItems = new ArrayList<String>(
                Arrays.asList(getResources().getStringArray(R.array.nav_drawer_items)));
        navDrawerAdapter = new NavDrawerAdapter(this, navDrawerItems);
        navDrawer.setAdapter(navDrawerAdapter);

        navDrawer.setOnItemClickListener(new NavDrawerListener());

        // Bind the DrawerToggle with the Drawer
        // TODO UPDATE THE ICON
        drawerToggle = new ActionBarDrawerToggle(
                this,                       // Activity
                drawerLayout,               // DrawerLayout
                R.drawable.ic_launcher,     // Image to replace arrow icon
                R.string.drawer_open,       // Open description
                R.string.drawer_closed      // Closed description
        ) {
            // TODO HANDLE DRAWER TOGGLE EVENTS HERE
        };
        drawerLayout.setDrawerListener(drawerToggle);

        // Make the ActionBar title clickable
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        // Add the home screen fragment to the activity
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.content_frame, new HomeFragment());
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class NavDrawerListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch(position) {
                case 0: swapFragment(new HomeFragment());
                    break;
                case 1: swapFragment(new MapFragment());
                    break;
                case 2: swapFragment(new VenueListFragment());
                    break;
                case 3: swapFragment(new EventListFragment());
                    break;
                case 4: swapFragment(new TransportationFragment());
                    break;
            }
            drawerLayout.closeDrawers();
        }
    }

    private void swapFragment(Fragment f) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.content_frame, f);
        ft.commit();
    }

    private void fetchArtVenueData() {
        String upgradeDateString = datastore.getArtVenueUpgradeDate();
        Log.d("SUCCESS", upgradeDateString);
        String whereClause = String.format(VenueService.WHERE_DATE_QUERY_BASE, upgradeDateString);
        Log.d("SUCCESS", whereClause);

        Intent intent = new Intent(this, VenueDatabaseIntentService.class);
        intent.putExtra(C.WHERE_CLAUSE, whereClause);
        startService(intent);
    }
}