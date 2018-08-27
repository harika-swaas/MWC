package com.swaas.mwc.DMS;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.swaas.mwc.R;

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
