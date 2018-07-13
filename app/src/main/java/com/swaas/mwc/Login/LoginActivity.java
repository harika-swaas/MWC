package com.swaas.mwc.Login;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.swaas.mwc.API.Model.AccountSettingsResponse;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Fragments.LoginFragment;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;
import com.swaas.mwc.Utils.Constants;
import com.swaas.mwc.Utils.SplashScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by harika on 13-06-2018.
 */

public class LoginActivity extends RootActivity {

    LoginFragment mLoginFragment;
    KeyguardManager keyguardManager;
    private static final int CREDENTIALS_RESULT = 4342;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    int backButtonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginFragment = new LoginFragment();
        loadFTLFragment();
    }

    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment, mLoginFragment).
                addToBackStack(null).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAppStatus();
    }

    private void checkAppStatus() {
        String loginStatus = "";
        getLoggedInStatus();

        if(mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0) {
            loginStatus = mAccountSettingsResponses.get(0).getLogin_Complete_Status();
        } else {
            loginStatus = "";
        }

        if (TextUtils.isEmpty(loginStatus)) {
        }
        else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Login_Completed))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkSecurity();
            }
        }
        else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Local_Auth_Completed))) {

            Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
            startActivity(intent);

            int timeout = 2000; // make the activity visible for 2 seconds

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    finish();
                    checkCredentials();
                    startActivity(new Intent(LoginActivity.this, Notifiy.class));
                    LoginActivity.this.finish();
                }
            }, timeout);
        }
        else {
            checkAppStatusAfterPushNotification(mAccountSettingsResponses);
        }
    }

    private void checkAppStatusAfterPushNotification(final List<AccountSettingsResponse> mAccountSettingsResponses) {

        if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("0")){
            Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
            startActivity(intent);

            int timeout = 2000; // make the activity visible for 2 seconds

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    finish();
                    if(mAccountSettingsResponses.get(0).getIs_Local_Auth_Enabled().equalsIgnoreCase(String.valueOf(Constants.Local_Auth_Completed))) {
                        checkCredentials();
                    }
                    startActivity(new Intent(LoginActivity.this, LoginAgreeTermsAcceptanceActivity.class));
                    LoginActivity.this.finish();
                }
            }, timeout);
        }
        else if(mAccountSettingsResponses.get(0).getIs_Help_Accepted().equals("1")){
            Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
            startActivity(intent);

            int timeout = 2000; // make the activity visible for 2 seconds

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    finish();
                    if(mAccountSettingsResponses.get(0).getIs_Local_Auth_Enabled().equalsIgnoreCase(String.valueOf(Constants.Local_Auth_Completed))) {
                        checkCredentials();
                    }
                    startActivity(new Intent(LoginActivity.this, LoginHelpUserGuideActivity.class));
                    LoginActivity.this.finish();
                }
            }, timeout);
        }
        else {
            Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
            startActivity(intent);

            int timeout = 2000; // make the activity visible for 2 seconds

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    finish();
                    if(mAccountSettingsResponses.get(0).getIs_Local_Auth_Enabled().equalsIgnoreCase(String.valueOf(Constants.Local_Auth_Completed))) {
                        checkCredentials();
                    }
                    startActivity(new Intent(LoginActivity.this, Dashboard.class));
                    LoginActivity.this.finish();
                }
            }, timeout);
        }
    }

    private void getLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(LoginActivity.this);
        accountSettings.SetLoggedInCB(new AccountSettings.GetLoggedInCB() {
            @Override
            public void getLoggedInSuccessCB(List<AccountSettingsResponse> accountSettingsResponse) {
                if (accountSettingsResponse != null && accountSettingsResponse.size() > 0) {
                    mAccountSettingsResponses = accountSettingsResponse;
                }
            }

            @Override
            public void getLoggedInFailureCB(String message) {

            }
        });

        accountSettings.getLoggedInStatusDetails();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkSecurity() {
        KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure() == true) {
            Intent intent = new Intent(LoginActivity.this, Touchid.class);
            startActivity(intent);
            LoginActivity.this.finish();
        } else {
            Intent intent = new Intent(LoginActivity.this, Notifiy.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkCredentials(){
        keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        Intent credentialsIntent = keyguardManager.createConfirmDeviceCredentialIntent("Password required", "please enter your pattern to receive your token");

        if (credentialsIntent != null) {
            startActivityForResult(credentialsIntent, CREDENTIALS_RESULT);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Bundle data) {
        if (requestCode == CREDENTIALS_RESULT) {
            if (resultCode == RESULT_OK) {

            }
        }
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
