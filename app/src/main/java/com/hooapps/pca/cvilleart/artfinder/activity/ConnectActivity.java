package com.hooapps.pca.cvilleart.artfinder.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.fragment.ConnectFragment;

public class ConnectActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayShowTitleEnabled(true);

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            actionBar.addTab(actionBar.newTab()
                    .setText(R.string.connect_facebook)
                    .setTabListener(new TabListener<ConnectFragment>(
                            this,
                            getString(R.string.connect_facebook),
                            C.URL_FACEBOOK,
                            R.drawable.logo_fb_grey,
                            ConnectFragment.class
                    )));
            actionBar.addTab(actionBar.newTab()
                    .setText(R.string.connect_twitter)
                    .setTabListener(new TabListener<ConnectFragment>(
                            this,
                            getString(R.string.connect_twitter),
                            C.URL_TWITTER,
                            R.drawable.logo_twitter_grey,
                            ConnectFragment.class
                    )));
            actionBar.addTab(actionBar.newTab()
                    .setText(R.string.connect_pca)
                    .setTabListener(new TabListener<ConnectFragment>(
                            this,
                            getString(R.string.connect_pca),
                            C.URL_PCA_HOMEPAGE,
                            R.drawable.logo_pca_grey,
                            ConnectFragment.class
                    )));
        }

    }

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final String mUrl;
        private final int mImageRes;

        public TabListener(Activity activity, String tag, String url, int imageRes, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mUrl = url;
            mImageRes = imageRes;
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment == null) {
                mFragment = ConnectFragment.newInstance(mUrl, mImageRes);
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
