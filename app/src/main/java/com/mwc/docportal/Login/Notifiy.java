package com.mwc.docportal.Login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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
import com.mwc.docportal.DMS.MyFoldersDMSActivity;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.PushNotificatoinSettings_Respository;
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

                mCustomAlertDialog = builder.create();
                mCustomAlertDialog.show();
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

    private void getPushNotificationDocumentService(String register_type)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

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

                            if (response.body().getStatus().getCode() == Boolean.FALSE) {


                                AccountSettings accountSettings = new AccountSettings(context);
                                accountSettings.UpdatePushNotificatoinSettings(register_type);

                                PushNotificatoinSettings_Respository pushNotificatoinSettings = new PushNotificatoinSettings_Respository(context);
                                pushNotificatoinSettings.updatePushNotificatoinStatus(register_type);

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
                                            finish();
                                        }
                                    }
                                }


                            }


                        else if (response.body().getStatus().getCode() instanceof Double) {

                            String mMessage = response.body().getStatus().getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                builder.setView(view);
                                builder.setCancelable(false);

                                TextView title = (TextView) view.findViewById(R.id.title);
                                title.setText("Alert");

                                TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                                txtMessage.setText(mMessage);

                                Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                cancelButton.setVisibility(View.GONE);

                                sendPinButton.setText("OK");

                                sendPinButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        AccountSettings accountSettings = new AccountSettings(mActivity);
                                        accountSettings.deleteAll();
                                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                                    }
                                });


                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("PinDevice error", t.getMessage());
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
        accountSettings.updatePushNotificationEnableStatus(String.valueOf(Constants.Push_Notification_Completed));
    }

    private void updatePushNotificationAndLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(this);
        accountSettings.updatePushNotificationEnableAndLoggedInStatus(String.valueOf(Constants.Push_Notification_Completed), "1");
    }



}
