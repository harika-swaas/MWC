package com.mwc.docportal.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.FingerPrintRequestModel;
import com.mwc.docportal.API.Model.SharedDocumentResponseModel;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.ShareEndUserDocumentsService;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static android.app.Activity.RESULT_OK;


/**
 * Created by barath on 6/24/2018.
 */

public class Touchid extends Activity {

    Button button2;
    TextView skip1;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    boolean mIsFromFTL;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    String finger_print_settings;
    KeyguardManager keyguardManager;
    private static final int CREDENTIALS_RESULT = 4342;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enable_touch_id);
        skip1 = (TextView) findViewById(R.id.skip_button);
        button2 = (Button) findViewById(R.id.enable_touch_button);

        getIntentData();
        setButtonBackgroundColor();

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                checkCredentials();
            }
        });

        skip1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                updateLoggedInStatus();


                Intent intent = new Intent(Touchid.this, Notifiy.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendFingerPrintStatusToServer(String opt_value)
    {

        if (NetworkUtils.isNetworkAvailable(Touchid.this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final FingerPrintRequestModel externalShareResponseModel = new FingerPrintRequestModel("finger_print", "Android", opt_value);

            String request = new Gson().toJson(externalShareResponseModel);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final ShareEndUserDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);

            Call call = mGetCategoryDocumentsService.sendFingerPrintStatus(params, PreferenceUtils.getAccessToken(Touchid.this));

            call.enqueue(new Callback<SharedDocumentResponseModel>() {
                @Override
                public void onResponse(Response<SharedDocumentResponseModel> response, Retrofit retrofit) {

                    if (response != null) {

                        if (response.body().getStatus().getCode() instanceof Boolean) {
                            if (response.body().getStatus().getCode() == Boolean.FALSE) {


                                Intent intent = new Intent(Touchid.this, Notifiy.class);
                                intent.putExtra(Constants.IS_FROM_FTL,mIsFromFTL);
                                startActivity(intent);
                                finish();



                            }

                        }
                      /*  else if (response.body().getStatus().getCode() instanceof Double) {

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
                        }*/
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }

    }

    private void getAccountSettings()
    {
        final AccountSettings accountSettings = new AccountSettings(Touchid.this);
        accountSettings.SetLoggedInCB(new AccountSettings.GetLoggedInCB() {
            @Override
            public void getLoggedInSuccessCB(List<AccountSettingsResponse> accountSettingsResponses) {
                if (accountSettingsResponses != null && accountSettingsResponses.size() > 0) {
                    mAccountSettingsResponses = accountSettingsResponses;
                }
            }

            @Override
            public void getLoggedInFailureCB(String message) {

            }
        });
        accountSettings.getLoggedInStatusDetails();
    }

    private void getIntentData() {

        if(getIntent() != null) {
            mIsFromFTL = getIntent().getBooleanExtra(Constants.IS_FROM_FTL,false);
        }
    }

    private void setButtonBackgroundColor() {

        getWhiteLabelProperities();

        if(mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
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

                button2.setBackgroundDrawable(shape);
            } else if(mobileItemDisableColor != null){
                // Initialize a new GradientDrawable
                GradientDrawable shape = new GradientDrawable();

                // Specify the shape of drawable
                shape.setShape(GradientDrawable.RECTANGLE);

                // Make the border rounded
                shape.setCornerRadius(50f);

                // Set the fill color of drawable
                shape.setColor(itemDisableColor);

                button2.setBackgroundDrawable(shape);
            }
        } else {
            button2.setBackgroundResource(R.drawable.next);
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

            button2.setBackgroundDrawable(shape);

        } else {

        }*/
    }

    private void getWhiteLabelProperities() {

        AccountSettings accountSettings = new AccountSettings(Touchid.this);
        accountSettings.SetWhiteLabelCB(new AccountSettings.GetWhiteLabelCB() {
            @Override
            public void getWhiteLabelSuccessCB(List<WhiteLabelResponse> whiteLabelResponses) {
                if(whiteLabelResponses != null && whiteLabelResponses.size() > 0){
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
        accountSettings.updateLocalAuthEnableStatus(String.valueOf("1"));
    }

    private void updateLocalAuthAndLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(this);
        accountSettings.updateLocalAuthEnableAndLoggedInStatus(String.valueOf(Constants.Local_Auth_Completed),"1");
    }
    public void onBackPressed() { }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkCredentials() {
        keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        Intent credentialsIntent = keyguardManager.createConfirmDeviceCredentialIntent("Password required", "please enter your pattern to receive your token");

        if (credentialsIntent != null) {
            startActivityForResult(credentialsIntent, CREDENTIALS_RESULT);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREDENTIALS_RESULT) {

            if (resultCode == RESULT_OK) {
                updateLocalAuthAndLoggedInStatus();
                getAccountSettings();

                if(mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0)
                {
                    finger_print_settings =  mAccountSettingsResponses.get(0).getIs_Local_Auth_Enabled();
                }

                String opt_value =  null;
                if(finger_print_settings != null && !finger_print_settings.isEmpty())
                {
                    if(finger_print_settings.equals("1"))
                    {
                        opt_value = "opt-in";
                    }
                    else if(finger_print_settings.equals("0"))
                    {
                        opt_value = "opt-out";
                    }
                }


                if(opt_value != null && !opt_value.isEmpty())
                {
                    sendFingerPrintStatusToServer(opt_value);
                }

            }
            else{
                Toast.makeText(Touchid.this,"Authentication Failed",Toast.LENGTH_SHORT).show();

            }
        }
    }
}

