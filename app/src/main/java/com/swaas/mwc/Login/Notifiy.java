package com.swaas.mwc.Login;

import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.swaas.mwc.R;

/**
 * Created by barath on 6/24/2018.
 */

public class Notifiy extends Activity {

    int Flag=0;
    Button button5;
    TextView skip;
    private static final int CREDENTIALS_RESULT = 4342;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enable_notification);
        skip = (TextView)findViewById(R.id.skip_button_1);
        button5 = (Button)findViewById(R.id.enable_touch_button);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(Notifiy.this);

                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle("Custom Alert Dialog");

                final TextView Text = (TextView) dialog.findViewById(R.id.Text);
                final TextView Text1 = (TextView) dialog.findViewById(R.id.Text1);
                Button btnallow       = (Button) dialog.findViewById(R.id.cancel);
                Button btnCancel        = (Button) dialog.findViewById(R.id.save);
                dialog.show();

                btnallow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Notifiy.this,Dashboard.class);
                        startActivity(intent);
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

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


    //just make sure it's unique within your activity.

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void checkCredentials() {
        KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        Intent credentialsIntent = keyguardManager.createConfirmDeviceCredentialIntent("Password required", "please enter your pattern to receive your token");
        if (credentialsIntent != null) {
            startActivityForResult(credentialsIntent, CREDENTIALS_RESULT);

        } else {
            //no password needed
            Intent intent = new Intent(this,Notifiy.class);
            startActivity(intent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Bundle data) {



        if (requestCode == CREDENTIALS_RESULT) {

            if(resultCode == RESULT_OK) {

                Flag=1;

            }

        }

    }
}
