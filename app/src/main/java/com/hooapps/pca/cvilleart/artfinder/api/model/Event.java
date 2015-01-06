package com.hooapps.pca.cvilleart.artfinder.api.model;

import com.google.gson.annotations.SerializedName;

public class Event {

    // NOTE: This is not set via the JSON, it must be manually set
    public String category;

    // NOTE: This is not set via the JSON, it must be manually set
    public boolean isAllDay = false;        // Default the value to false

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

    public long unixStart;
    public long unixEnd;

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
}
