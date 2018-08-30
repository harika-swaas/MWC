package com.mwc.docportal.Login;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.DMS.MyFoldersDMSActivity;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.PushNotificatoinSettings_Respository;
import com.mwc.docportal.Fragments.LoginFragment;
import com.mwc.docportal.R;
import com.mwc.docportal.RootActivity;
import com.mwc.docportal.Utils.Constants;
import com.mwc.docportal.Utils.SplashScreen;

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
    Context context = this;

    PushNotificatoinSettings_Respository pushNotificatoinSettings_respository;
    AlertDialog mCustomAlertDialog;
    String pushNotificatonStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pushNotificatoinSettings_respository = new PushNotificatoinSettings_Respository(context);
        int rowCount = pushNotificatoinSettings_respository.getCountOfPushNotificationSettings();

        if (rowCount == 0) {

            showPushNotificationWarningAlert();
        }

        mLoginFragment = new LoginFragment();
        loadFTLFragment();
    }

    private void showPushNotificationWarningAlert()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);

        final Button BtnAllow = (Button) view.findViewById(R.id.allow_button);
        final Button BtnCancel = (Button) view.findViewById(R.id.cancel_button);

        BtnAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomAlertDialog.dismiss();
                pushNotificatoinSettings_respository.insertIntoPushNotificatonTable(1, 1);
            }
        });

        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomAlertDialog.dismiss();
                pushNotificatoinSettings_respository.insertIntoPushNotificatonTable(0, 1);
            }
        });

        mCustomAlertDialog = builder.create();
        mCustomAlertDialog.show();
    }



    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment, mLoginFragment).
                addToBackStack(null).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();

        pushNotificatonStatus = pushNotificatoinSettings_respository.getPushNotificatonSettingsStatus();


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

                    if(pushNotificatonStatus != null && pushNotificatonStatus.equals("1"))
                    {
                        startActivity(new Intent(LoginActivity.this, LoginHelpUserGuideActivity.class));
                        LoginActivity.this.finish();
                    }
                    else
                    {
                        startActivity(new Intent(LoginActivity.this, Notifiy.class));
                        LoginActivity.this.finish();
                    }

                }
            }, timeout);
        }

        else {
            checkAppStatusAfterPushNotification(mAccountSettingsResponses);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkAppStatusAfterPushNotification(final List<AccountSettingsResponse> mAccountSettingsResponses) {

        if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("0")){
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

                    startActivity(new Intent(LoginActivity.this, NavigationMyFolderActivity.class));
                    LoginActivity.this.finish();
                    if(mAccountSettingsResponses.get(0).getIs_Local_Auth_Enabled().equalsIgnoreCase("1")) {
                        checkCredentials();
                    }
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

            if(pushNotificatonStatus != null && pushNotificatonStatus.equals("1"))
            {
                Intent intent = new Intent(LoginActivity.this, LoginHelpUserGuideActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
            else
            {
                Intent intent = new Intent(LoginActivity.this, Notifiy.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }


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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREDENTIALS_RESULT) {

            if (resultCode == RESULT_OK) {

            }
            else{
                Toast.makeText(LoginActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
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
