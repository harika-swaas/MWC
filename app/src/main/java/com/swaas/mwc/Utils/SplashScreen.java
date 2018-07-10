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
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.MainActivity;
import com.swaas.mwc.R;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by barath on 7/10/2018.
 */

public class SplashScreen extends Activity {
    AccountSettings accountSettings;
    Handler handler;
    String companyname;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        ImageView logo = (ImageView)findViewById(R.id.LOGO);
        companyname = accountSettings.COMPANY_NAME;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("172.16.40.51")
                .appendPath("assets")
                .appendPath("images")
                .appendPath("whitelabels")
                .appendPath(companyname)
                .appendPath("mwc-logo.png");
        String myUrl = builder.build().toString();
        AQuery aq=new AQuery(this); // intsialze aquery
        aq.id(logo).image(myUrl);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },4000);






    }


}
