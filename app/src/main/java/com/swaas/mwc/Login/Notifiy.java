package com.swaas.mwc.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.swaas.mwc.API.Model.AccountSettingsResponse;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by barath on 6/24/2018.
 */

public class Notifiy extends Activity {

    Button button5;
    TextView skip;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    boolean mIsFromFTL;
    AlertDialog mCustomAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enable_notification);
        skip = (TextView) findViewById(R.id.skip_button_1);
        button5 = (Button) findViewById(R.id.enable_touch_button);

        getIntentData();
        setButtonBackgroundColor();
        getAccountSettings();


        button5.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(Notifiy.this);
                LayoutInflater inflater = (LayoutInflater) Notifiy.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.custom_dialog, null);
                builder.setView(view);
                builder.setCancelable(false);

                final Button BtnAllow = (Button) view.findViewById(R.id.allow_button);
                final Button BtnCancel = (Button) view.findViewById(R.id.cancel_button);

                BtnAllow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomAlertDialog.dismiss();
                        updatePushNotificationAndLoggedInStatus();
                        if (mIsFromFTL) {
                            Intent intent = new Intent(Notifiy.this, LoginHelpUserGuideActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if(mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0){
                                if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("0")){
                                    Intent intent = new Intent(Notifiy.this, LoginAgreeTermsAcceptanceActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("1")){
                                    Intent intent = new Intent(Notifiy.this, LoginHelpUserGuideActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }
                });

                BtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomAlertDialog.dismiss();
                        updateLoggedInStatus();
                        if (mIsFromFTL) {
                            Intent intent = new Intent(Notifiy.this, LoginHelpUserGuideActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if(mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0){
                                if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("0")){
                                    Intent intent = new Intent(Notifiy.this, LoginAgreeTermsAcceptanceActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("1")){
                                    Intent intent = new Intent(Notifiy.this, LoginHelpUserGuideActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }
                });

                mCustomAlertDialog = builder.create();
                mCustomAlertDialog.show();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLoggedInStatus();
                if (mIsFromFTL) {
                    Intent intent = new Intent(Notifiy.this, LoginHelpUserGuideActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if(mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0){
                        if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("0")){
                            Intent intent = new Intent(Notifiy.this, LoginAgreeTermsAcceptanceActivity.class);
                            startActivity(intent);
                            finish();
                        } else if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("1")){
                            Intent intent = new Intent(Notifiy.this, LoginHelpUserGuideActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        });
    }

    private void getAccountSettings() {
        AccountSettings accountSettings = new AccountSettings(Notifiy.this);
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

    public void onBackPressed() { }
    private void getIntentData() {

        if (getIntent() != null) {
            mIsFromFTL = getIntent().getBooleanExtra(Constants.IS_FROM_FTL, false);
        }
    }

    private void setButtonBackgroundColor() {

        getWhiteLabelProperities();

        if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
            String mobileItemEnableColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            String mobileItemDisableColor = mWhiteLabelResponses.get(0).getItem_Unselected_Color();

            int itemEnableColor = Color.parseColor(mobileItemEnableColor);
            int itemDisableColor = Color.parseColor(mobileItemDisableColor);

            if (mobileItemEnableColor != null) {
                // Initialize a new GradientDrawable
                GradientDrawable shape = new GradientDrawable();

                // Specify the shape of drawable
                shape.setShape(GradientDrawable.RECTANGLE);

                // Make the border rounded
                shape.setCornerRadius(50f);

                // Set the fill color of drawable
                shape.setColor(itemEnableColor);

                button5.setBackgroundDrawable(shape);
            } else if (mobileItemDisableColor != null) {
                // Initialize a new GradientDrawable
                GradientDrawable shape = new GradientDrawable();

                // Specify the shape of drawable
                shape.setShape(GradientDrawable.RECTANGLE);

                // Make the border rounded
                shape.setCornerRadius(50f);

                // Set the fill color of drawable
                shape.setColor(itemDisableColor);

                button5.setBackgroundDrawable(shape);
            }
        } else {
            button5.setBackgroundResource(R.drawable.next);
        }

        /*String mobileItemEnableColor = PreferenceUtils.getMobileItemEnableColor(this);
        String mobileItemDisableColor = PreferenceUtils.getMobileItemDisableColor(this);

        int itemEnableColor = 0;
        int itemDisableColor = 0;

        if (mobileItemEnableColor != null) {
            itemEnableColor = Color.parseColor(mobileItemEnableColor);
        }
        if (mobileItemDisableColor != null) {
            itemDisableColor = Color.parseColor(mobileItemDisableColor);
        }

        if (mobileItemEnableColor != null) {
            // Initialize a new GradientDrawable
            GradientDrawable shape = new GradientDrawable();

            // Specify the shape of drawable
            shape.setShape(GradientDrawable.RECTANGLE);

            // Make the border rounded
            shape.setCornerRadius(50f);

            // Set the fill color of drawable
            shape.setColor(itemEnableColor);

            button5.setBackgroundDrawable(shape);

        } else {

        }*/
    }

    private void getWhiteLabelProperities() {

        AccountSettings accountSettings = new AccountSettings(Notifiy.this);
        accountSettings.SetWhiteLabelCB(new AccountSettings.GetWhiteLabelCB() {
            @Override
            public void getWhiteLabelSuccessCB(List<WhiteLabelResponse> whiteLabelResponses) {
                if (whiteLabelResponses != null && whiteLabelResponses.size() > 0) {
                    mWhiteLabelResponses = whiteLabelResponses;
                }
            }

            @Override
            public void getWhiteLabelFailureCB(String message) {

            }
        });

        accountSettings.getWhiteLabelProperties();
    }

    private void updateLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(this);
        accountSettings.updatePushNotificationEnableStatus(String.valueOf(Constants.Push_Notification_Completed));
    }

    private void updatePushNotificationAndLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(this);
        accountSettings.updatePushNotificationEnableAndLoggedInStatus(String.valueOf(Constants.Push_Notification_Completed), "1");
    }
}
