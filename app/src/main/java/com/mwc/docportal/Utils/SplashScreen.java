package com.mwc.docportal.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import android.support.v7.app.AppCompatActivity;
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
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.DMS.NavigationSettingsActivity;
import com.mwc.docportal.DMS.NavigationSharedActivity;
import com.mwc.docportal.DMS.UploadListActivity;
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
    boolean isFromForeground = false;
    String activityName = "";
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        isFromForeground = getIntent().getBooleanExtra("IsFromForeground", false);
        if(getIntent().getStringExtra("ActivityName") != null)
        {
            activityName = getIntent().getStringExtra("ActivityName");
        }

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
            if(!((Activity) context ).isFinishing()) {
                showConfirmPasswordAlert();
            }
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

                Intent intent = new Intent(context, NavigationMyFolderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            } else {
                Toast.makeText(context, "You have cancelled this action.", Toast.LENGTH_SHORT).show();
                finish();
                moveTaskToBack(true);
                //    showAuthenticationFailureMessage();

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
      //  content.setText("Please confirm your "+getResources().getString(R.string.app_name)+" password");
        content.setText("Please enter your password.");
        title.setText("Enter Password.");
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
                                mAlertDialog.dismiss();

                                String message = "";
                                if(response.body().getStatus().getMessage() != null)
                                {
                                    message = response.body().getStatus().getMessage().toString();
                                }

                                if(response.body().getStatus().getCode() instanceof Double)
                                {
                                    double status_value = new Double(response.body().getStatus().getCode().toString());
                                    if (status_value == 401.3)
                                    {
                                        showAlertDialogForAccessDenied(context, message);
                                    }
                                    else if(status_value ==  401 || status_value ==  401.0)
                                    {
                                        showAlertDialogForSessionExpiry(context, message);
                                    }
                                }
                                else if(response.body().getStatus().getCode() instanceof Integer)
                                {
                                    int integerValue = new Integer(response.body().getStatus().getCode().toString());
                                    if(integerValue ==  401)
                                    {
                                        showAlertDialogForSessionExpiry(context, message);
                                    }
                                }
                                else if(response.body().getStatus().getCode() instanceof Boolean)
                                {
                                    if (response.body().getStatus().getCode() == Boolean.TRUE)
                                    {
                                        showAlertDialogIncorrectPassword(context, message);
                                    }
                                    else {

                                        startActivity(new Intent(context, NavigationMyFolderActivity.class));
                                        finish();

                                    }
                                }


                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            transparentProgressDialog.dismiss();
                            mAlertDialog.dismiss();
                            CommonFunctions.showTimeoutAlert(context);
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
                Toast.makeText(context, "You have cancelled this action.", Toast.LENGTH_SHORT).show();
                finish();
                moveTaskToBack(true);


            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void showAlertDialogIncorrectPassword(Context context, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText(message);

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setVisibility(View.GONE);

        okButton.setText("OK");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

                if(!((Activity) context ).isFinishing()) {
                    showConfirmPasswordAlert();
                }

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void showAuthenticationFailureMessage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText("You have cancelled this action.");

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setVisibility(View.GONE);

        okButton.setText("OK");

        okButton.setOnClickListener(new View.OnClickListener() {
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


    private void showAlertDialogForSessionExpiry(Context context, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Session Expired");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText(message);

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setVisibility(View.GONE);

        okButton.setText("OK");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                AccountSettings accountSettings = new AccountSettings(context);
                accountSettings.LogouData();
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }


    private void showAlertDialogForAccessDenied(Context context, String message)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText(message);

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setVisibility(View.GONE);

        okButton.setText("OK");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }




 }
