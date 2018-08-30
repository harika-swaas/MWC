package com.mwc.docportal.DMS;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mwc.docportal.R;

public class NavigationSettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_navigation_settings;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_settings;
    }

}
