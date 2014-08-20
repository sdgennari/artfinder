package com.hooapps.pca.cvilleart.artfinder.api;

import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenueResponse;
import com.hooapps.pca.cvilleart.artfinder.constants.C;

import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface VenueService {

    public static final String VENUE_BASE_URL = "https://api.parse.com";
    public static final String WHERE_DATE_QUERY_BASE = "{\"updatedAt\":{\"$gt\":{\"__type\":\"Date\", \"iso\":\"%s\"}}}";

    @Headers({
            "X-Parse-Application-Id: " + C.PARSE_APPLICATION_ID,
            "X-Parse-REST-API-Key: " + C.PARSE_REST_API_KEY
    })
    @GET("/1/classes/ArtVenue/&order=updatedAt")        // Sort by updatedAt to make last item in list most recent
    public ArtVenueResponse getAllArtVenues();

    // NOTE: date_string is of format: 2012-04-30T09:34:08.256Z
    @Headers({
            "X-Parse-Application-Id: " + C.PARSE_APPLICATION_ID,
            "X-Parse-REST-API-Key: " + C.PARSE_REST_API_KEY
    })
    @GET("/1/classes/ArtVenue/?order=updatedAt")        // Sort by updatedAt to make last item in list most recent
//    public Observable<ArtVenueResponse> getAllArtVenuesAfterDate(@Query("where") String whereClause);
    public ArtVenueResponse getAllArtVenuesAfterDate(@Query("where") String whereClause);


    // TODO CHECK PARSE SYNTAX FOR QUERYING A SINGLE OBJECT
    // This currently throws a 401 error
//    @Headers({
//            "X-Parse-Application-Id: " + C.PARSE_APPLICATION_ID,
//            "X-Parse-REST-API-Key: " + C.PARSE_REST_API_KEY
//    })
//    @GET("/1/classes/ArtVenue/{parse_object_id")
//    public Observable<ArtVenue> getArtVenue(@Path("parse_object_id") String parseObjectId);
}
