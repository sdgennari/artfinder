package com.hooapps.pca.cvilleart.artfinder.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hooapps.pca.cvilleart.artfinder.Datastore;
import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.adapter.NavDrawerAdapter;
import com.hooapps.pca.cvilleart.artfinder.api.EventDatabaseIntentService;
import com.hooapps.pca.cvilleart.artfinder.api.VenueDatabaseIntentService;
import com.hooapps.pca.cvilleart.artfinder.api.VenueService;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenue;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.fragment.AboutFragment;
import com.hooapps.pca.cvilleart.artfinder.fragment.EventListFragment;
import com.hooapps.pca.cvilleart.artfinder.fragment.HomeFragment;
import com.hooapps.pca.cvilleart.artfinder.fragment.MapFragment;
import com.hooapps.pca.cvilleart.artfinder.fragment.TransportationFragment;
import com.hooapps.pca.cvilleart.artfinder.fragment.VenueListFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

public class MainActivity extends BaseActivity implements
        HomeFragment.OnHomeFragmentButtonSelectedListener {

    private static final long MS_PER_60_DAYS = 1000L * 60 * 60 * 24 * 60;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @InjectView(R.id.nav_drawer)
    ListView navDrawer;

    private Datastore datastore;

    private NavDrawerAdapter navDrawerAdapter;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // Initialize the datastore singleton
        datastore = Datastore.getInstance();

        // Fetch ArtVenue data
        fetchArtVenueData();

        // Fetch Event data
        fetchEventData();

        // Configure the adapter to add items to the NavDrawer
        ArrayList<String> navDrawerItems = new ArrayList<String>(
                Arrays.asList(getResources().getStringArray(R.array.nav_drawer_items)));
        navDrawerAdapter = new NavDrawerAdapter(this, navDrawerItems);
        navDrawer.setAdapter(navDrawerAdapter);

        navDrawer.setOnItemClickListener(new NavDrawerListener());

        // Make the ActionBar title clickable
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        // Bind the DrawerToggle with the Drawer
        drawerToggle = new ActionBarDrawerToggle(
                this,                       // Activity
                drawerLayout,               // DrawerLayout
                R.drawable.ic_nav_drawer,   // Image to replace arrow icon
                R.string.drawer_open,       // Open description
                R.string.drawer_closed      // Closed description
        ) {
            // Handle drawer toggle events here
        };
        drawerLayout.setDrawerListener(drawerToggle);

        // Add the home screen fragment to the activity
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.content_frame, new HomeFragment());
        ft.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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
                case 5: Intent connectIntent = new Intent(getApplicationContext(), ConnectActivity.class);
                    startActivity(connectIntent);
                    break;
                case 6: swapFragment(new AboutFragment());
                    break;
            }
            drawerLayout.closeDrawers();
        }
    }

    @Override
    public void onHomeFragmentButtonSelected(int n) {
        switch(n) {
            case C.HOME_MAP: swapFragment(new MapFragment());
                break;
            case C.HOME_VENUE: swapFragment(new VenueListFragment());
                break;
            case C.HOME_EVENT: swapFragment(new EventListFragment());
                break;
            case C.HOME_TRANS: swapFragment(new TransportationFragment());
                break;
            case C.HOME_CONNECT: Intent connectIntent = new Intent(getApplicationContext(), ConnectActivity.class);
                startActivity(connectIntent);
        }
    }

    private void swapFragment(Fragment f) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.content_frame, f);
        ft.commit();
    }

    private void fetchArtVenueData() {
        String upgradeDateString = datastore.getArtVenueUpdateDate();
        Log.d("SUCCESS", upgradeDateString);
        String whereClause = String.format(VenueService.WHERE_DATE_QUERY_BASE, upgradeDateString);
        Log.d("SUCCESS", whereClause);

        Intent intent = new Intent(this, VenueDatabaseIntentService.class);
        intent.putExtra(C.EXT_WHERE_CLAUSE, whereClause);
        startService(intent);
    }

    private void fetchEventData() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(System.currentTimeMillis());
        String minTime = sdf.format(new Date(c.getTimeInMillis())) + C.EVENT_URL_TIME_ZONE;
        String maxTime = sdf.format(new Date(c.getTimeInMillis() + MS_PER_60_DAYS)) + C.EVENT_URL_TIME_ZONE;

        Intent intent = new Intent(this, EventDatabaseIntentService.class);
        intent.putExtra(C.EXT_MAX_TIME, maxTime);
        intent.putExtra(C.EXT_MIN_TIME, minTime);
        startService(intent);

    }
}