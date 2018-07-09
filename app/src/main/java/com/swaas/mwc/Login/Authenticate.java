package com.swaas.mwc.Login;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;

import com.swaas.mwc.R;

/**
 * Created by barath on 6/27/2018.
 */

public class Authenticate extends Activity {

    KeyguardManager keyguardManager;
    private static final int CREDENTIALS_RESULT = 4342;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkSecurity() {
        keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        if(keyguardManager.isKeyguardSecure()==true) {
            Intent intent = new Intent(this, Touchid.class);
            startActivity(intent);
        }
    else    {
            Intent intent = new Intent(this,Notifiy.class);
            startActivity(intent);
        }
    }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void checkCredentials(){
            keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
            Intent credentialsIntent = keyguardManager.createConfirmDeviceCredentialIntent("Password required", "please enter your pattern to receive your token");

                    if (credentialsIntent != null) {
                        startActivityForResult(credentialsIntent, CREDENTIALS_RESULT);
                    }

                    else {
                        //no password needed
                        Intent intent = new Intent(Authenticate.this, Notifiy.class);
                        startActivity(intent);
                    }
    }
    public void onActivityResult(int requestCode, int resultCode, Bundle data) {

        if (requestCode == CREDENTIALS_RESULT) {

            if (resultCode == RESULT_OK) {



            }
        }
    }
}
