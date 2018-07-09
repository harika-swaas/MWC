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

import com.google.firebase.messaging.FirebaseMessaging;
import com.swaas.mwc.R;
import com.swaas.mwc.Utils.Constants;

/**
 * Created by barath on 6/24/2018.
 */

public class Notifiy extends Activity {

    Button button5;
    TextView skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enable_notification);
        skip = (TextView)findViewById(R.id.skip_button_1);
        button5 = (Button)findViewById(R.id.enable_touch_button);

        button5.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

           final Dialog dialog = new Dialog(Notifiy.this);
                dialog.setContentView(R.layout.custom_dialog);

                final TextView Text = (TextView) dialog.findViewById(R.id.title1);
                final TextView Text1 = (TextView) dialog.findViewById(R.id.txt_message1);
                final Button BtnAllow  = (Button) dialog.findViewById(R.id.cancel_button1);
                final Button BtnCancel = (Button) dialog.findViewById(R.id.send_pin_button1);
                dialog.show();

                BtnAllow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*FirebaseMessaging.getInstance().subscribeToTopic("news");*/

                        Intent intent = new Intent(Notifiy.this,LoginAgreeTermsAcceptanceActivity.class);
                        startActivity(intent);

                    }
                });

                BtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /*FirebaseMessaging.getInstance().unsubscribeFromTopic("news");*/
                        Intent intent = new Intent(Notifiy.this,LoginAgreeTermsAcceptanceActivity.class);
                        startActivity(intent);
                    }
                });

            }

        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FirebaseMessaging.getInstance().unsubscribeFromTopic("news");*/
                Intent intent = new Intent(Notifiy.this,LoginAgreeTermsAcceptanceActivity.class);
                startActivity(intent);
            }
        });
    }


}
