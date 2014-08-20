package com.hooapps.pca.cvilleart.artfinder.api;

import com.hooapps.pca.cvilleart.artfinder.api.model.EventResponse;
import com.hooapps.pca.cvilleart.artfinder.constants.C;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface EventService {
    // TODO UPDATE DATE FORMAT
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String EVENT_BASE_URL = "https://www.googleapis.com/calendar/v3/calendars";
    public static final String EVENT_QUERY = "/events?singleEvents=true&key=" + C.GOOGLE_API_KEY;

    // Calendar specific
    public static final String FAMILY_URL   = C.CALENDAR_ID_FAMILY + EVENT_QUERY;
    public static final String MUSIC_URL    = C.CALENDAR_ID_MUSIC + EVENT_QUERY;
    public static final String THEATER_URL  = C.CALENDAR_ID_THEATER + EVENT_QUERY;
    public static final String FILM_URL     = C.CALENDAR_ID_FILM + EVENT_QUERY;
    public static final String DANCE_URL    = C.CALENDAR_ID_DANCE + EVENT_QUERY;
    public static final String GALLERY_URL  = C.CALENDAR_ID_GALLERY + EVENT_QUERY;
    public static final String LITERARY_URL = C.CALENDAR_ID_LITERARY + EVENT_QUERY;

    @GET(FAMILY_URL)
    public EventResponse getFamilyEvents(@Query("timeMax") String maxTime, @Query("timeMin") String minTime);

    @GET(MUSIC_URL)
    public EventResponse getMusicEvents(@Query("timeMax") String maxTime, @Query("timeMin") String minTime);

    @GET(THEATER_URL)
    public EventResponse getTheaterEvents(@Query("timeMax") String maxTime, @Query("timeMin") String minTime);

    @GET(FILM_URL)
    public EventResponse getFilmEvents(@Query("timeMax") String maxTime, @Query("timeMin") String minTime);

    @GET(DANCE_URL)
    public EventResponse getDanceEvents(@Query("timeMax") String maxTime, @Query("timeMin") String minTime);;

    @GET(GALLERY_URL)
    public EventResponse getGalleryEvents(@Query("timeMax") String maxTime, @Query("timeMin") String minTime);

    @GET(LITERARY_URL)
    public EventResponse getLiteraryEvents(@Query("timeMax") String maxTime, @Query("timeMin") String minTime);
}