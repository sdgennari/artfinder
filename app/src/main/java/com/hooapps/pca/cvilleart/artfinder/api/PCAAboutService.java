package com.hooapps.pca.cvilleart.artfinder.api;

import com.hooapps.pca.cvilleart.artfinder.api.model.PCAResponse;
import com.hooapps.pca.cvilleart.artfinder.constants.C;

import retrofit.http.GET;
import retrofit.http.Headers;
import rx.Observable;

public interface PCAAboutService {

    public static final String PCA_BASE_URL = "https://api.parse.com";

    @Headers({
            "X-Parse-Application-Id: " + C.PARSE_APPLICATION_ID,
            "X-Parse-REST-API-Key: " + C.PARSE_REST_API_KEY
    })
    @GET("/1/classes/PCA/")
    public Observable<PCAResponse> getPCAAboutInfo();
}
