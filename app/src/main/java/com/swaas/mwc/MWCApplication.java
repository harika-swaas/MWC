package com.swaas.mwc;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;

import io.fabric.sdk.android.Fabric;

/**
 * Created by harika on 22-06-2018.
 */

public class MWCApplication extends MultiDexApplication {

    private static MWCApplication mMwcApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        final Context context = this;
        mMwcApplication = this;

        //  Preferences.INSTANCE.createPreferences(this);
        //  VolleyRequestQueue.INSTANCE.setContext(this);
        Fabric.with(this, new Crashlytics());
        Stetho.initializeWithDefaults(this);
    }

    public static MWCApplication getThis() {
        return mMwcApplication;
    }
}
