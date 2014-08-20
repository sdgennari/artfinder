package com.hooapps.pca.cvilleart.artfinder.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventResponse {

    @SerializedName("summary")
    public String category;

    public List<Event> items;
}
