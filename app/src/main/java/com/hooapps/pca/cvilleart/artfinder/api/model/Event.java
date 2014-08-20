package com.hooapps.pca.cvilleart.artfinder.api.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.hooapps.pca.cvilleart.artfinder.MainApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // NOTE: This is not set via the JSON, it must be manually set
    public String category;

    @SerializedName("id")
    public String id;

    @SerializedName("created")
    public String created;

    @SerializedName("updated")
    public String updated;

    @SerializedName("summary")
    public String summary;

    @SerializedName("description")
    public String description;

    @SerializedName("location")
    public String location;

    @SerializedName("start")
    public Start start;

    @SerializedName("end")
    public End end;

    public class Start {
        public String dateTime;
        public String timeZone;
        public String date;
    }

    public class End {
        public String dateTime;
        public String timeZone;
        public String date;
    }

    public long getStartTime() {
        //2013-06-02T10:00:00-04:00
        try {
            if (start.dateTime != null) {
                Date date = dateTimeFormat.parse(start.dateTime.substring(0, 19).replaceAll("[a-zA-Z]", " ").trim());
                return date.getTime();
            } else if (start.date != null) {
                Date date = dateFormat.parse(start.date);
                return date.getTime();
            }
        } catch (ParseException e) {
            Log.e(MainApp.TAG, e.getLocalizedMessage());
        }
        return 0;
    }

    public long getEndTime() {
        try {
            if (end.dateTime != null) {
                Date date = dateTimeFormat.parse(end.dateTime.substring(0, 19).replaceAll("[a-zA-Z]", " ").trim());
                return date.getTime();
            } else if (end.date != null) {
                Date date = dateFormat.parse(end.date);
                return date.getTime();
            }
        } catch (ParseException e) {
            Log.e(MainApp.TAG, e.getLocalizedMessage());
        }
        return 0;
    }
}
