package com.hooapps.pca.cvilleart.artfinder.api;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenue;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenueResponse;
import com.hooapps.pca.cvilleart.artfinder.constants.C;

import java.util.List;

import retrofit.RestAdapter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VenueDatabaseIntentService extends IntentService {

    private VenueService venueService;
    private Subscription artVenueFetchTask;
    private List<ArtVenue> artVenueList;

    public VenueDatabaseIntentService() {
        super("VenueDatabaseIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("SUCCESS", "onHandleIntent reached");

        String whereClause = intent.getStringExtra(C.WHERE_CLAUSE);

        venueService = initVenueService();
        artVenueFetchTask = venueService.getAllArtVenuesAfterDate(whereClause)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArtVenueResponse>() {
                    @Override
                    public void onNext(ArtVenueResponse response) {
                        Log.d("SUCCESS", "onNext");
                        if (response != null) {
                            artVenueList = response.results;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("SUCCESS", "onError");
                        Log.e(MainApp.TAG, e.getLocalizedMessage());
                    }

                    @Override
                    public void onCompleted() {
                        Log.d("SUCCESS", "" + artVenueList.size());
                        // TODO SAVE ITEMS TO VENUE TABLE IN DATABASE
                    }
                });

    }

    private VenueService initVenueService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(VenueService.VENUE_BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
        return restAdapter.create(VenueService.class);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(artVenueFetchTask != null) {
            artVenueFetchTask.unsubscribe();
        }
    }
}
