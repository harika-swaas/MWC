package com.mwc.docportal.Login;

import android.app.Activity;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.mwc.docportal.R;
import com.mwc.docportal.RootActivity;

/**
 * Created by barath on 6/26/2018.
 */

public class Dashboard extends RootActivity{
    int backButtonCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
    }

    @Override
    public void onBackPressed() {
        if (backButtonCount >= 1) {
            backButtonCount = 0;
            moveTaskToBack(true);
        } else {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }
}
