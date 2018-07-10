package com.swaas.mwc.Login;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.MenuItem;

import com.swaas.mwc.API.Model.AccountSettingsResponse;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Fragments.LoginFragment;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;
import com.swaas.mwc.Utils.Constants;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by harika on 13-06-2018.
 */

public class LoginActivity extends RootActivity {

    LoginFragment mLoginFragment;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
           // startActivity(new Intent(LoginActivity.this, LoginActivity.class));
        }
        else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Login_Completed))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkSecurity();
            }
        }
        else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Local_Auth_Completed))) {
            startActivity(new Intent(LoginActivity.this, Notifiy.class));
            LoginActivity.this.finish();
        }
        else {
            checkAppStatusAfterPushNotification(mAccountSettingsResponses);
        }
    }

    private void checkAppStatusAfterPushNotification(List<AccountSettingsResponse> mAccountSettingsResponses) {

        if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("0")){
            startActivity(new Intent(LoginActivity.this, LoginAgreeTermsAcceptanceActivity.class));
            LoginActivity.this.finish();
        }
        else if(mAccountSettingsResponses.get(0).getIs_Help_Accepted().equals("1")){
            startActivity(new Intent(LoginActivity.this, LoginHelpUserGuideActivity.class));
            LoginActivity.this.finish();
        }
        else {
            startActivity(new Intent(LoginActivity.this, Dashboard.class));
            LoginActivity.this.finish();
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
}
