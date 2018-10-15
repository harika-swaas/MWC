package com.mwc.docportal.Login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.PushNotificationRequestModel;
import com.mwc.docportal.API.Model.SharedDocumentResponseModel;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.ShareEndUserDocumentsService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.DMS.MyFoldersDMSActivity;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.PushNotificatoinSettings_Respository;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.RootActivity;
import com.mwc.docportal.Utils.Constants;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by barath on 6/24/2018.
 */

public class Notifiy extends RootActivity {

    Button button5;
    TextView skip;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    boolean mIsFromFTL;
    AlertDialog mCustomAlertDialog;
    Notifiy mActivity;
    Context context = this;
    TextView notification_head;
    TextView notification_body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enable_notification);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        skip = (TextView) findViewById(R.id.skip_button_1);
        button5 = (Button) findViewById(R.id.enable_touch_button);
        notification_body = (TextView) findViewById(R.id.notification_body);
        notification_head = (TextView) findViewById(R.id.notification_head);
        notification_head.setText("Notifications for " + getResources().getString(R.string.app_name));

        boolean device_status = isNotificationChannelEnabled(Notifiy.this);
        String enabled_String = "";
        if(device_status)
        {
            enabled_String = "enabled";
        }
        else
        {
            enabled_String = "disabled";
        }

        notification_body.setText("Push notifications are "+ enabled_String +" for "+ getResources().getString(R.string.app_name) + " app. You can change the status from the settings page later.");
        skip.setVisibility(View.INVISIBLE);

        getIntentData();
        setButtonBackgroundColor();
        getAccountSettings();


        button5.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                updatePushNotificationAndLoggedInStatus();
                String register_type = "";
                boolean device_status = isNotificationChannelEnabled(Notifiy.this);

                if(device_status)
                {
                    register_type = "1";
                }
                else
                {
                    register_type = "0";
                }



                getPushNotificationDocumentService(register_type);

               /* final AlertDialog.Builder builder = new AlertDialog.Builder(Notifiy.this);
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
                        String register_type = "1";
                        getPushNotificationDocumentService(register_type);

                    }
                });

                BtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomAlertDialog.dismiss();
                        updateLoggedInStatus();

                       String register_type = "0";
                       getPushNotificationDocumentService(register_type);


                        *//*if (mIsFromFTL) {
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
                        }*//*
                    }
                });

                mCustomAlertDialog = builder.create();
                mCustomAlertDialog.show();*/
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLoggedInStatus();

                String register_type = "0";
                getPushNotificationDocumentService(register_type);

                /*if (mIsFromFTL) {
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
                }*/
            }
        });
    }

    private void getPushNotificationDocumentService(final String register_type)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(Notifiy.this);
            transparentProgressDialog.show();

            PushNotificatoinSettings_Respository pushNotificatoinSettings_respository = new PushNotificatoinSettings_Respository(context);
            String DeviceTokenId = pushNotificatoinSettings_respository.getDeviceTokenFromTableStatus();

            final PushNotificationRequestModel externalShareResponseModel = new PushNotificationRequestModel(DeviceTokenId, "Android", register_type);

            String request = new Gson().toJson(externalShareResponseModel);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final ShareEndUserDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);

            Call call = mGetCategoryDocumentsService.sendPushNotificatoinStatus(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<SharedDocumentResponseModel>() {
                @Override
                public void onResponse(Response<SharedDocumentResponseModel> response, Retrofit retrofit) {

                    if (response != null) {

                        transparentProgressDialog.dismiss();
                        String message = "";
                        if(response.body().getStatus().getMessage() != null)
                        {
                            message = response.body().getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, response.body().getStatus().getCode())) {

                            AccountSettings accountSettings = new AccountSettings(context);
                            accountSettings.UpdatePushNotificatoinSettings(register_type);

                            PushNotificatoinSettings_Respository pushNotificatoinSettings = new PushNotificatoinSettings_Respository(context);

                            String original_register_type = "";
                            if(register_type.equals("0"))
                            {
                                original_register_type = "2";
                            }
                            else
                            {
                                original_register_type  ="1";
                            }
                            pushNotificatoinSettings.updatePushNotificatoinStatus(original_register_type);


                            getAccountSettings();

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
                                    } else if(mAccountSettingsResponses.get(0).getIs_Help_Accepted().equals("1")){
                                        Intent intent = new Intent(Notifiy.this, LoginHelpUserGuideActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Intent intent = new Intent(Notifiy.this, NavigationMyFolderActivity.class);
                                        startActivity(intent);
                                        updateLoggedInStatus();
                                        finish();

                                    }
                                }
                            }

                        }

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("PinDevice error", t.getMessage());
                    CommonFunctions.showTimeoutAlert(mActivity);
                }
            });
        }

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
        accountSettings.updatePushNotificationEnableStatus(String.valueOf(Constants.All_Settings_Completed));
    }

    private void updatePushNotificationAndLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(this);
        accountSettings.updatePushNotificationEnableAndLoggedInStatus(String.valueOf(Constants.Push_Notification_Completed), "1");
    }

    public boolean isNotificationChannelEnabled(Activity context){
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
        return areNotificationsEnabled;

    }



}
