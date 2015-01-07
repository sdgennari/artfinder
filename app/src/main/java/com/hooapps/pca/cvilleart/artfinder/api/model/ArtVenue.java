package com.hooapps.pca.cvilleart.artfinder.api.model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArtVenue {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");

    @SerializedName("City")
    public String city;

    @SerializedName("Description")
    public String description;

    @SerializedName("Email_Address")
    public String emailAddress;

    @SerializedName("Home_Page_URL")
    public String homePageUrl;

    @SerializedName("Image_URL")
    public String imageUrl;

    @SerializedName("Latitude")
    public double latitude;

    @SerializedName("Longitude")
    public double longitude;

    @SerializedName("Organization_Name")
    public String organizationName;

    @SerializedName("Phone")
    public String phone;

    @SerializedName("Primary_Category")
    public String primaryCategory;

    @SerializedName("Secondary_Category")
    public String secondaryCategory;

    @SerializedName("State")
    public String state;

    @SerializedName("Street_Address")
    public String streetAddress;

    @SerializedName("Zip")
    public int zip;

    @SerializedName("createdAt")
    public String createdAtString;

    @SerializedName("updatedAt")
    public String updatedAtString;

    @SerializedName("isDeleted")
    public String isDeletedString;

    @SerializedName("objectId")
    public String parseObjectId;

    /**
     * Method to parse the createdAt string as UNIX time
     * @return UNIX time in milliseconds
     */
    public long getCreatedTime() {
        try {
            Date date = dateFormat.parse(createdAtString.replaceAll("[a-zA-Z]", " ").trim());
            return date.getTime();
        } catch (ParseException e) {
            //Log.e(MainApp.TAG, e.getLocalizedMessage());
        }
        return 0;
    }

    /**
     * Method to parse the updatedAt string as UNIX time
     * @return UNIX time in milliseconds
     */
    public long getUpdatedTime() {
        try {
            Date date = dateFormat.parse(updatedAtString.replaceAll("[a-zA-Z]", " ").trim());
            return date.getTime();
        } catch (ParseException e) {
            //Log.e(MainApp.TAG, e.getLocalizedMessage());
        }
        return 0;
    }
}
