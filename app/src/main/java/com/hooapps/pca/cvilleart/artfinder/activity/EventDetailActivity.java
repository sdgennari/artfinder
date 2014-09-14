package com.hooapps.pca.cvilleart.artfinder.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.api.model.Event;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.data.EventTable;
import com.hooapps.pca.cvilleart.artfinder.util.ColorUtils;
import com.hooapps.pca.cvilleart.artfinder.util.TimeUtils;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EventDetailActivity extends BaseActivity {

    private String eventId;
    private Event event;
    private SQLiteDatabase db;

    @InjectView(R.id.image_container_bg)
    View imageContainerBackground;
    @InjectView(R.id.event_image)
    ImageView imageView;
    @InjectView(R.id.event_name)
    TextView nameView;
    @InjectView(R.id.event_date)
    TextView dateView;
    @InjectView(R.id.event_time)
    TextView timeView;
    @InjectView(R.id.add_to_calendar_button)
    Button addToCalendarButton;
    @InjectView(R.id.address_container)
    LinearLayout addressContainer;
    @InjectView(R.id.event_address)
    TextView addressView;
    @InjectView(R.id.description_container)
    LinearLayout descriptionContainer;
    @InjectView(R.id.event_description)
    TextView descriptionView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.inject(this);

        db = MainApp.getDatabase();

        eventId = getIntent().getStringExtra(C.EXT_EVENT_ID);
        if (eventId == null || eventId.isEmpty()) {
            // TODO HANDLE ERROR HERE
        }
        event = fetchEventData();

        // Customize the ActionBar based on the event category
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setBackgroundDrawable(getResources().getDrawable(
                    ColorUtils.getColorDrawableForCategory(event.category)));
            getActionBar().setIcon(ColorUtils.getVenueDrawableForCategory(event.category));
        }

        populateCardViews();
    }

    private Event fetchEventData() {
        String[] projection = {
                EventTable.COL_CATEGORY,
                EventTable.COL_SUMMARY,
                EventTable.COL_DESCRIPTION,
                EventTable.COL_LOCATION,
                EventTable.COL_START_TIME,
                EventTable.COL_END_TIME
        };

        Cursor c = db.query(
                EventTable.TABLE_EVENTS,
                projection,
                EventTable.COL_EVENT_ID + "= ?",
                new String[] {eventId},
                null,
                null,
                null
        );

        c.moveToNext();

        Event event = new Event();
        event.category = c.getString(c.getColumnIndex(EventTable.COL_CATEGORY));
        event.summary = c.getString(c.getColumnIndex(EventTable.COL_SUMMARY));
        event.description = c.getString(c.getColumnIndex(EventTable.COL_DESCRIPTION));
        event.location = c.getString(c.getColumnIndex(EventTable.COL_LOCATION));
        event.unixStart = c.getLong(c.getColumnIndex(EventTable.COL_START_TIME));
        event.unixEnd = c.getLong(c.getColumnIndex(EventTable.COL_END_TIME));

        return event;
    }

    private void populateCardViews() {
        int colorResId = ColorUtils.getColorForCategory(event.category);
        imageContainerBackground.setBackgroundColor(getResources().getColor(colorResId));
        int drawableResId = ColorUtils.getVenueDrawableForCategory(event.category);
        Picasso.with(this).load(drawableResId).into(imageView);
        nameView.setText(event.summary);
        nameView.requestFocus();
        nameView.setSelected(true);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(event.unixStart);
        dateView.setText(TimeUtils.createDateString(c));
        String timeRange = TimeUtils.createTimeString(c);
        c.setTimeInMillis(event.unixEnd);
        timeRange += " - " + TimeUtils.createTimeString(c);
        timeView.setText(timeRange);
        if (event.location != null && !event.location.isEmpty()) {
            addressView.setText(event.location);
        } else {
            addressContainer.setVisibility(View.GONE);
        }
        if (event.description != null && !event.description.isEmpty()) {
            descriptionView.setText(event.description);
        } else {
            descriptionContainer.setVisibility(View.GONE);
        }

        addToCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEventToPhoneCalendar();
            }
        });
    }

    private void addEventToPhoneCalendar() {
        // Load the information into ContentValues
        String eventUriString = "content://com.android.calendar/events";

        ContentValues eventValues = new ContentValues();
        eventValues.put(Events.CALENDAR_ID, 1);
        eventValues.put(Events.TITLE, event.summary);
        eventValues.put(Events.DESCRIPTION, event.description);
        eventValues.put(Events.EVENT_LOCATION, event.location);
        eventValues.put(Events.DTSTART, event.unixStart);
        eventValues.put(Events.DTEND, event.unixEnd);
        eventValues.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        Uri eventUri = MainApp.getContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
        long eventId = Long.parseLong(eventUri.getLastPathSegment());

        String reminderUriString = "content://com.android.calendar/reminders";
        ContentValues reminderValues = new ContentValues();
        reminderValues.put(Reminders.EVENT_ID, eventId);
        reminderValues.put(Reminders.MINUTES, 20);
        reminderValues.put(Reminders.METHOD, 0);

        MainApp.getContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);

        Toast.makeText(this, "Added Event to Calendar", Toast.LENGTH_SHORT).show();
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
