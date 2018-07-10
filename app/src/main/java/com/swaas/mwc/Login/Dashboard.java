package com.swaas.mwc.Login;

import android.app.Activity;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.annotation.Nullable;

import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;

/**
 * Created by barath on 6/26/2018.
 */

public class Dashboard extends RootActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
    }
}
