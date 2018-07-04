package com.swaas.mwc.Login;

import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.swaas.mwc.R;
import com.swaas.mwc.Utils.Constants;

/**
 * Created by barath on 6/24/2018.
 */

public class Notifiy extends Activity {

    Button button5;
    TextView skip;
    public static final int ACCESS_NOTIFICATION_POLICY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enable_notification);
        skip = (TextView)findViewById(R.id.skip_button_1);
        button5 = (Button)findViewById(R.id.enable_touch_button);

        button5.setOnClickListener(new View.OnClickListener() {
            public static final int REQUEST_PERMISSION_SETTING = 1432;
            public static final int MY_PERMISSIONS_REQUEST_ACCESS_NOTIFICATION_POLICY = 1234 ;

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(Notifiy.this,
                        Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Notifiy.this,
                            Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(Notifiy.this,
                                new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                                MY_PERMISSIONS_REQUEST_ACCESS_NOTIFICATION_POLICY);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    Intent intent = new Intent(Notifiy.this,Dashboard.class);
                    startActivity(intent);

                }


                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {

                        } else {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, RC_ACCESS_NOTIFICATION_POLICY);

                        }
                    }
                }*/
          /*      final Dialog dialog = new Dialog(Notifiy.this);
                dialog.setContentView(R.layout.custom_dialog);

                final TextView Text = (TextView) dialog.findViewById(R.id.title1);
                final TextView Text1 = (TextView) dialog.findViewById(R.id.txt_message1);
                final Button BtnAllow  = (Button) dialog.findViewById(R.id.cancel_button1);
                final Button BtnCancel = (Button) dialog.findViewById(R.id.send_pin_button1);
                dialog.show();

                BtnAllow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Notifiy.this,Dashboard.class);
                        startActivity(intent);

                    }
                });

                BtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(Notifiy.this,Dashboard.class);
                        startActivity(intent);
                    }
                });*/

            }

        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notifiy.this,Dashboard.class);
                startActivity(intent);
            }
        });
    }


}
