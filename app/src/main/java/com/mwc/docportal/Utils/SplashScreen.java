package com.mwc.docportal.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.ConfirmPasswordRequestModel;
import com.mwc.docportal.API.Model.ConfirmPasswordResponseModel;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.UploadNewFolderService;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Login.Notifiy;
import com.mwc.docportal.MainActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.RootActivity;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by barath on 7/10/2018.
 */

public class SplashScreen extends RootActivity {

    String mCompanyName;
    String msplashscreen;

    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    KeyguardManager keyguardManager;
    public final int CREDENTIALS_RESULT = 12345;
    Context context = this;
    AlertDialog mAlertDialog;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        ImageView logo = (ImageView) findViewById(R.id.LOGO);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.BG);
        getAccountSettings();
        getWhiteLabelSettings();

        if (mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0) {
            mCompanyName = mAccountSettingsResponses.get(0).getCompany_Name();
        }

        if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
            msplashscreen = mWhiteLabelResponses.get(0).getSplash_Screen_Color();
        }

        if (msplashscreen != null) {
            int itemEnableColor = Color.parseColor(msplashscreen);
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(itemEnableColor);
            relativeLayout.setBackgroundDrawable(shape);
        }

        /*Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("http172.16.40.51")
                .appendPath("assets")
                .appendPath("images")
                .appendPath("whitelabels")
                .appendPath(mCompanyName)
                .appendPath("mwc-logo.png");
        String myUrl = builder.build().toString();
        AQuery aq = new AQuery(this); // intsialze aquery
        aq.id(logo).image(myUrl);*/

        if(PreferenceUtils.getLogoImagePath(context) != null)
        {
            File imgFile = new  File(PreferenceUtils.getLogoImagePath(context));

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                logo.setImageBitmap(myBitmap);

            }
        }


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                handler.removeCallbacksAndMessages(null);
                checkTouchIdEnabledOrNot();
            }
        }, 2000);   // 2 seconds



    }

    private void checkTouchIdEnabledOrNot()
    {
        if(mAccountSettingsResponses.get(0).getIs_Local_Auth_Enabled().equalsIgnoreCase("1") && checkTouchIdEnabled()) {
            checkTouchIdCredentials();
        }
        else
        {

            showConfirmPasswordAlert();
        }
    }

    private void getAccountSettings() {

        final AccountSettings accountSettings = new AccountSettings(SplashScreen.this);
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

    private void getWhiteLabelSettings() {

        final AccountSettings accountSettings = new AccountSettings(SplashScreen.this);
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

                Intent intent = new Intent(SplashScreen.this, NavigationMyFolderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            } else {
                Toast.makeText(SplashScreen.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                finish();
                moveTaskToBack(true);
            }

        }

    }

    private void showConfirmPasswordAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.confirm_password_layout, null);
        builder.setView(view1);
        builder.setCancelable(false);

        Button cancel = (Button) view1.findViewById(R.id.cancel_b);
        Button allow = (Button) view1.findViewById(R.id.allow);
        final EditText password_edttxt = (EditText) view1.findViewById(R.id.edit_username1);
        TextView title = (TextView) view1.findViewById(R.id.title);
        TextView content = (TextView) view1.findViewById(R.id.content_data);
        content.setText("Please confirm your "+getResources().getString(R.string.app_name)+" password");
        title.setText("Confirm password");
        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password_txt = password_edttxt.getText().toString().trim();

                if(password_txt == null || password_txt.isEmpty())
                {
                    Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (NetworkUtils.isNetworkAvailable(context)) {

                    Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                    final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
                    transparentProgressDialog.show();

                    ConfirmPasswordRequestModel uploadNewFolderRequest = new ConfirmPasswordRequestModel(password_txt);

                    String request = new Gson().toJson(uploadNewFolderRequest);

                    //Here the json data is add to a hash map with key data
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("data", request);

                    final UploadNewFolderService uploadNewFolderService = retrofitAPI.create(UploadNewFolderService.class);

                    Call call = uploadNewFolderService.getConfirmPassword(params, PreferenceUtils.getAccessToken(context));

                    call.enqueue(new Callback<ConfirmPasswordResponseModel>() {
                        @Override
                        public void onResponse(Response<ConfirmPasswordResponseModel> response, Retrofit retrofit) {
                            ConfirmPasswordResponseModel apiResponse = response.body();
                            if (apiResponse != null) {

                                transparentProgressDialog.dismiss();

                                if (apiResponse.getStatus().getCode() instanceof Boolean) {
                                    if (apiResponse.getStatus().getCode() == Boolean.FALSE) {
                                        mAlertDialog.dismiss();
                                        startActivity(new Intent(context, NavigationMyFolderActivity.class));
                                        finish();

                                    }
                                    else  if (apiResponse.getStatus().getCode() == Boolean.TRUE) {
                                        String mMessage = apiResponse.getStatus().getMessage().toString();

                                        password_edttxt.getText().clear();
                                        Toast.makeText(context, mMessage, Toast.LENGTH_LONG).show();


                                    }

                                } else if (apiResponse.getStatus().getCode() instanceof Integer) {
                                    mAlertDialog.dismiss();
                                    String mMessage = apiResponse.getStatus().getMessage().toString();

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View view1 = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                    builder.setView(view1);
                                    builder.setCancelable(false);

                                    TextView txtMessage = (TextView) view1.findViewById(R.id.txt_message);

                                    txtMessage.setText(mMessage);

                                    Button sendPinButton = (Button) view1.findViewById(R.id.send_pin_button);
                                    Button cancelButton = (Button) view1.findViewById(R.id.cancel_button);

                                    cancelButton.setVisibility(View.GONE);

                                    sendPinButton.setText("OK");

                                    sendPinButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mAlertDialog.dismiss();
                                            context.startActivity(new Intent(context, LoginActivity.class));
                                        }
                                    });

                                    mAlertDialog = builder.create();
                                    mAlertDialog.show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            transparentProgressDialog.dismiss();
                            Log.d("PinDevice error", t.getMessage());
                        }
                    });
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                finish();
                moveTaskToBack(true);

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

}