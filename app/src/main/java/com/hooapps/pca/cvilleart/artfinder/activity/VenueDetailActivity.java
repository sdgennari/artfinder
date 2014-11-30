package com.hooapps.pca.cvilleart.artfinder.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenue;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.data.VenueTable;
import com.hooapps.pca.cvilleart.artfinder.util.BlurTransform;
import com.hooapps.pca.cvilleart.artfinder.util.ColorUtils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class VenueDetailActivity extends BaseActivity {

    private String parseObjectId;
    private ArtVenue venue;
    private SQLiteDatabase db;
    private Context context;

    @InjectView(R.id.venue_image)
    ImageView bgImageView;
    @InjectView(R.id.venue_category_image)
    ImageView imageView;
    @InjectView(R.id.venue_name)
    TextView nameView;
    @InjectView(R.id.venue_address)
    TextView addressView;
    @InjectView(R.id.venue_category)
    TextView categoryView;
    @InjectView(R.id.venue_phone)
    TextView phoneNumberView;
    @InjectView(R.id.venue_description)
    TextView descriptionView;
    @InjectView(R.id.button_get_directions)
    Button directionsButton;
    @InjectView(R.id.button_view_on_map)
    Button viewOnMapButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_detail);
        ButterKnife.inject(this);

        db = MainApp.getDatabase();
        context = this;

        parseObjectId = getIntent().getStringExtra(C.EXT_PARSE_OBJECT_ID);
        if (parseObjectId == null || parseObjectId.isEmpty()) {
            // TODO HANDLE ERROR HERE
            return;
        }
        venue = fetchVenueData();

        // Customize the ActionBar based on the venue
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setBackgroundDrawable(getResources().getDrawable(
                    ColorUtils.getColorDrawableForCategory(venue.primaryCategory)));
            getActionBar().setIcon(ColorUtils.getVenueDrawableForCategory(venue.primaryCategory));
        }

        populateCardViews();
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
        artVenue.imageUrl = c.getString(c.getColumnIndex(VenueTable.COL_IMAGE_URL));

        return artVenue;
    }

    private void populateCardViews() {
        int colorResId = ColorUtils.getColorForCategory(venue.primaryCategory);
        int placeholderResId = ColorUtils.getVenueDrawableForCategory(venue.primaryCategory);
        bgImageView.setBackgroundColor(getResources().getColor(colorResId));
        if (venue.imageUrl != null && !venue.imageUrl.isEmpty()) {
            imageView.setVisibility(View.GONE);
            Picasso.with(this).load(venue.imageUrl)
                    .resize(400, 140)
                    .centerCrop()
                    .transform(new BlurTransform(this)).into(bgImageView);
        } else {
            imageView.setVisibility(View.VISIBLE);
            Picasso.with(this).load(placeholderResId).into(imageView);
        }
        nameView.setText(venue.organizationName);
        nameView.requestFocus();
        nameView.setSelected(true);
        addressView.setText(venue.streetAddress);
        categoryView.setText(venue.primaryCategory);
        categoryView.setTextColor(getResources().getColor(colorResId));
        phoneNumberView.setText(venue.phone);
        phoneNumberView.setTextColor(getResources().getColor(colorResId));
        descriptionView.setText(Html.fromHtml(venue.description));

        // Bind listeners for buttons/textivews
        phoneNumberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchPhoneIntent();
            }
        });

        directionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGoogleMapIntent();
            }
        });

        viewOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchVenueMapIntent();
            }
        });
    }

    private void launchPhoneIntent() {
        String uriString = "tel:" + venue.phone.trim();
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse(uriString));
        startActivity(dialIntent);
    }

    private void launchGoogleMapIntent() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Selected Venue", venue.organizationName);
        FlurryAgent.logEvent(getString(R.string.flurry_directions), params);

        String latLngString = venue.latitude + "," + venue.longitude;
        String uriString = String.format(C.GOOGLE_MAP_URL, "", latLngString);
        Intent directionsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        directionsIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(directionsIntent);
    }

    private void launchVenueMapIntent() {
        Intent intent = new Intent(context, VenueMapActivity.class);
        intent.putExtra(C.EXT_PARSE_OBJECT_ID, parseObjectId);
        startActivity(intent);
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
