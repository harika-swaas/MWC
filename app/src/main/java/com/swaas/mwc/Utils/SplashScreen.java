package com.swaas.mwc.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.swaas.mwc.API.Model.AccountSettingsResponse;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.MainActivity;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by barath on 7/10/2018.
 */

public class SplashScreen extends RootActivity {

    AccountSettings accountSettings;
    Handler handler;
    String mCompanyName;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        ImageView logo = (ImageView)findViewById(R.id.LOGO);

        getAccountSettings();

        if(mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0){
            mCompanyName = mAccountSettingsResponses.get(0).getCompany_Name();
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("172.16.40.51")
                .appendPath("assets")
                .appendPath("images")
                .appendPath("whitelabels")
                .appendPath(mCompanyName)
                .appendPath("mwc-logo.png");
        String myUrl = builder.build().toString();
        AQuery aq=new AQuery(this); // intsialze aquery
        aq.id(logo).image(myUrl);
    }

    private void getAccountSettings() {

        final AccountSettings accountSettings = new AccountSettings(SplashScreen.this);
        accountSettings.SetLoggedInCB(new AccountSettings.GetLoggedInCB() {
            @Override
            public void getLoggedInSuccessCB(List<AccountSettingsResponse> accountSettingsResponses) {
                if(accountSettingsResponses != null && accountSettingsResponses.size() > 0){
                    mAccountSettingsResponses = accountSettingsResponses;
                }
            }

            @Override
            public void getLoggedInFailureCB(String message) {

            }
        });

        accountSettings.getLoggedInStatusDetails();
    }
}
