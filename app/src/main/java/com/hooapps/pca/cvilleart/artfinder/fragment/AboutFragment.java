package com.hooapps.pca.cvilleart.artfinder.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.api.PCAAboutService;
import com.hooapps.pca.cvilleart.artfinder.api.model.PCAResponse;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RestAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class AboutFragment extends BaseFragment {

    private PCAAboutService pcaAboutService;
    //private PCA pcaDescription;
    @InjectView(R.id.PCA_description)
    TextView descriptionView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.inject(this, rootView);

        // Fetch the description from Parse
        pcaAboutService = initPCAAboutService();
        pcaAboutService.getPCAAboutInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PCAResponse>() {
                    @Override
                    public void call(PCAResponse pcaResponse) {
                        descriptionView.setText(pcaResponse.getDescription());
                        Log.d("PCA", "OBSERVABLE CALLED");
                    }
                });

        return rootView;
    }

    private PCAAboutService initPCAAboutService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(PCAAboutService.PCA_BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
        return restAdapter.create(PCAAboutService.class);
    }
}
