package com.hooapps.pca.cvilleart.artfinder.api;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.api.model.Event;
import com.hooapps.pca.cvilleart.artfinder.api.model.EventResponse;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.data.EventTable;
import com.hooapps.pca.cvilleart.artfinder.util.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.RestAdapter;

public class EventDatabaseIntentService extends IntentService {

    private EventService eventService;
    private SQLiteDatabase db;
    private List<Event> eventList = new ArrayList<Event>();

    public EventDatabaseIntentService() {
        super("EventDatabaseIntentService");
        db = MainApp.getDatabase();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        eventService = initEventService();

        String maxTime = intent.getStringExtra(C.EXT_MAX_TIME);
        String minTime = intent.getStringExtra(C.EXT_MIN_TIME);

        Log.d("TEST", "max: " + maxTime);

        // Dance
        EventResponse response = eventService.getDanceEvents(maxTime, minTime);
        for (Event event : response.items) {
            event.category = getResources().getString(R.string.category_dance);
        }
        eventList.addAll(response.items);

        // Family (Other)
        response = eventService.getFamilyEvents(maxTime, minTime);
        for (Event event : response.items) {
            event.category = getResources().getString(R.string.category_other);
        }
        eventList.addAll(response.items);

        // Film (Other)
        response = eventService.getFilmEvents(maxTime, minTime);
        for (Event event : response.items) {
            event.category = getResources().getString(R.string.category_other);
        }
        eventList.addAll(response.items);

        // Visual Arts (Gallery)
        response = eventService.getGalleryEvents(maxTime, minTime);
        for (Event event : response.items) {
            event.category = getResources().getString(R.string.category_gallery);
        }
        eventList.addAll(response.items);

        // Literary (Other)
        response = eventService.getLiteraryEvents(maxTime, minTime);
        for (Event event : response.items) {
            event.category = getResources().getString(R.string.category_other);
        }
        eventList.addAll(response.items);

        // Music
        response = eventService.getMusicEvents(maxTime, minTime);
        for (Event event : response.items) {
            event.category = getResources().getString(R.string.category_music);
        }
        eventList.addAll(response.items);

        // Theatre
        response = eventService.getTheaterEvents(maxTime, minTime);
        for (Event event : response.items) {
            event.category = getResources().getString(R.string.category_theatre);
        }
        eventList.addAll(response.items);

        insertEvents();
        deleteOldEvents();
    }

    private EventService initEventService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(EventService.EVENT_BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
        return restAdapter.create(EventService.class);
    }

    private void deleteOldEvents() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        // Set the calendar to the morning of the current day
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.MILLISECOND, 0);

        int del = db.delete(EventTable.TABLE_EVENTS,
                EventTable.COL_START_TIME + " < " + (c.getTimeInMillis()),
                null);

        Log.d("TEST", "Deleted " + del + " events");
    }

    private void insertEvents() {
        // Start the database transaction here
        db.beginTransaction();

        // Loop through every item and prep it for insert
        ContentValues values;

        Log.d("TEST", "Event Size: " + eventList.size());

        for (Event event : eventList) {
            values = makeContentValuesFromObject(event);
            // Insert or update the columns
            db.insertWithOnConflict(EventTable.TABLE_EVENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private ContentValues makeContentValuesFromObject(Event event) {
        ContentValues values = new ContentValues();
        values.put(EventTable.COL_EVENT_ID, event.id);
        values.put(EventTable.COL_CATEGORY, event.category);
        values.put(EventTable.COL_UPDATED, event.updated);
        values.put(EventTable.COL_SUMMARY, event.summary);
        values.put(EventTable.COL_DESCRIPTION, event.description);
        values.put(EventTable.COL_LOCATION, event.location);
        values.put(EventTable.COL_START_TIME, TimeUtils.parseUnixFromDate(event.start.dateTime));
        values.put(EventTable.COL_END_TIME, TimeUtils.parseUnixFromDate(event.end.dateTime));
        return values;
    }
}