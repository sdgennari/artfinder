package com.hooapps.pca.cvilleart.artfinder.activity;

import android.app.Activity;

import com.flurry.android.FlurryAgent;
import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.constants.C;

public class BaseActivity extends Activity{

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(MainApp.getContext(), C.FLURRY_API_KEY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(MainApp.getContext());
    }
}
