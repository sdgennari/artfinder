package com.hooapps.pca.cvilleart.artfinder.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.constants.C;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeFragment extends BaseFragment {

    public interface OnHomeFragmentButtonSelectedListener {
        public void onHomeFragmentButtonSelected(int n);
    }

    private OnHomeFragmentButtonSelectedListener mCallback;

    @InjectView(R.id.button_map)
    Button mapButton;
    @InjectView(R.id.button_venue)
    Button venueButton;
    @InjectView(R.id.button_event)
    Button eventButton;
    @InjectView(R.id.button_transportation)
    Button transButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.inject(this, rootView);
        bindButtonListeners();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnHomeFragmentButtonSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHomeFragmentButtonSelectedLister");
        }
    }

    private void bindButtonListeners() {
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onHomeFragmentButtonSelected(C.HOME_MAP);
            }
        });

        venueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onHomeFragmentButtonSelected(C.HOME_VENUE);
            }
        });

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onHomeFragmentButtonSelected(C.HOME_EVENT);
            }
        });

        transButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onHomeFragmentButtonSelected(C.HOME_TRANS);
            }
        });
    }
}
