package com.mwc.docportal.Login;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.ConfirmPasswordRequestModel;
import com.mwc.docportal.API.Model.ConfirmPasswordResponseModel;
import com.mwc.docportal.API.Service.UploadNewFolderService;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Fragments.LoginFragment;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.RootActivity;
import com.mwc.docportal.Utils.Constants;
import com.mwc.docportal.Utils.SplashScreen;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by harika on 13-06-2018.
 */

public class LoginActivity extends RootActivity {
    LoginFragment mLoginFragment;
    KeyguardManager keyguardManager;
    public final int CREDENTIALS_RESULT = 12345;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    int backButtonCount;

    String documentVersionId = "";
    String notificationType = "";
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        if(getIntent().getStringExtra("document_version_id") != null)
        {
            documentVersionId = getIntent().getStringExtra("document_version_id");
            notificationType = getIntent().getStringExtra("notification_type");
            PreferenceUtils.setPushNotificationDocumentVersionId(LoginActivity.this, documentVersionId);
            PreferenceUtils.setPushNotificationDocumentShare(LoginActivity.this, notificationType);
        }


        mLoginFragment = new LoginFragment();
        loadFTLFragment();
    }


    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment, mLoginFragment).
                addToBackStack(null).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        checkAppStatus();
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        else
        {
            if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.All_Settings_Completed)))
            {
                gotoSplashScreenPage();
            }
            else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Login_Completed)))
            {
                boolean isTouchIdEnabled = checkTouchIdEnabled();
                if(isTouchIdEnabled)
                {

                    checkTouchIdCredentials();

                }
                else
                {
                    Intent intent = new Intent(LoginActivity.this, Notifiy.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }
            }
            else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Local_Auth_Completed)))
            {
                Intent intent = new Intent(LoginActivity.this, Notifiy.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
            else if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("0"))
            {
                startActivity(new Intent(LoginActivity.this, LoginAgreeTermsAcceptanceActivity.class));
                LoginActivity.this.finish();
            }
            else if(mAccountSettingsResponses.get(0).getIs_Help_Accepted().equals("1"))
            {
                startActivity(new Intent(LoginActivity.this, LoginHelpUserGuideActivity.class));
                LoginActivity.this.finish();

            }
            else
            {
                gotoSplashScreenPage();
            }

        }

       /* if (TextUtils.isEmpty(loginStatus)) {
        }
        else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Login_Completed))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkSecurity();
            }
        }
        else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Local_Auth_Completed))) {

            Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
            startActivity(intent);
            finish();

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
        }*/
    }




   /* @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkAppStatusAfterPushNotification(final List<AccountSettingsResponse> mAccountSettingsResponses) {

        if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("0")){
            if(mAccountSettingsResponses.get(0).getIs_Local_Auth_Enabled().equalsIgnoreCase("1")) {
            //    checkCredentials();
            }
            Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
            startActivity(intent);
            finish();

            int timeout = 2000; // make the activity visible for 2 seconds

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    finish();

                    startActivity(new Intent(LoginActivity.this, LoginAgreeTermsAcceptanceActivity.class));
                    LoginActivity.this.finish();
                }
            }, timeout);
        }
        else if(mAccountSettingsResponses.get(0).getIs_Help_Accepted().equals("1")){
            if(mAccountSettingsResponses.get(0).getIs_Local_Auth_Enabled().equalsIgnoreCase("1")) {
                checkCredentials();
            }
            Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
            startActivity(intent);
            finish();

            int timeout = 2000; // make the activity visible for 2 seconds

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    finish();
                    startActivity(new Intent(LoginActivity.this, LoginHelpUserGuideActivity.class));
                    LoginActivity.this.finish();
                }
            }, timeout);
        }
        else {

            Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
            startActivity(intent);
            finish();

            int timeout = 2000; // make the activity visible for 2 seconds

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    finish();

                    if(mAccountSettingsResponses.get(0).getIs_Local_Auth_Enabled().equalsIgnoreCase("1")) {
                        checkCredentials();
                    }

                    startActivity(new Intent(LoginActivity.this, NavigationMyFolderActivity.class));
                    LoginActivity.this.finish();

                }
            }, timeout);
        }
    }*/

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

    private void gotoSplashScreenPage()
    {
        Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
        startActivity(intent);
        finish();
    }



    private boolean checkTouchIdEnabled()
    {
        KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.isKeyguardSecure();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkTouchIdCredentials()
    {

        keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        Intent credentialsIntent = keyguardManager.createConfirmDeviceCredentialIntent("Password required", "please enter your pattern to receive your token");

        if (credentialsIntent != null) {
            startActivityForResult(credentialsIntent, CREDENTIALS_RESULT);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREDENTIALS_RESULT) {

            if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(LoginActivity.this, Notifiy.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    LoginActivity.this.finish();

            } else {
                Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show();
                finish();
                moveTaskToBack(true);

            }

        }

    }




}
