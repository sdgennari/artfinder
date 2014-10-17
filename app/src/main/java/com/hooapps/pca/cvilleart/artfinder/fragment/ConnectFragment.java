package com.hooapps.pca.cvilleart.artfinder.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.constants.C;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ConnectFragment extends BaseFragment {

    @InjectView(R.id.web_content)
    WebView webView;
    @InjectView(R.id.bg_image)
    ImageView bgImageView;

    public static ConnectFragment newInstance(String url, int imageRes) {
        ConnectFragment fragment = new ConnectFragment();

        final Bundle args = new Bundle(1);
        args.putString(C.EXT_CONNECT_URL, url);
        args.putInt(C.EXT_CONNECT_IMG, imageRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);
        ButterKnife.inject(this, rootView);
        if (this.getArguments() != null) {
            //bgImageView.setImageResource(R.drawable.logo_pca_grey);
            bgImageView.setImageResource(this.getArguments().getInt(C.EXT_CONNECT_IMG, R.drawable.logo_pca_grey));
        }
        // Configure the webview
        webView.setVisibility(View.GONE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.setVisibility(View.VISIBLE);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        // Load the url into the WebView; default is PCA homepage
        if (this.getArguments() != null) {
            webView.loadUrl(this.getArguments().getString(C.EXT_CONNECT_URL, C.URL_PCA_HOMEPAGE));
        } else {
            webView.loadUrl(C.URL_PCA_HOMEPAGE);
        }
        return rootView;
    }

}
