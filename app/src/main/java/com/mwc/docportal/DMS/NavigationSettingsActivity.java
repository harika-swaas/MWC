package com.mwc.docportal.DMS;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.google.gson.Gson;
import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.FingerPrintRequestModel;
import com.mwc.docportal.API.Model.PushNotificationRequestModel;
import com.mwc.docportal.API.Model.SharedDocumentResponseModel;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.ShareEndUserDocumentsService;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.PushNotificatoinSettings_Respository;
import com.mwc.docportal.FTL.WebviewLoaderTermsActivity;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.OffLine_Files_List;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.UserProfileActivity;
import com.mwc.docportal.Utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class NavigationSettingsActivity extends BaseActivity {

    ImageView LOGO_image;
    LinearLayout username_layout, offline_layout, help_layout, terms_privacy_layout, logout_layout, logo_layout, finger_print_layout;
    TextView user_name_txt;
    String mCompanyName, userName;
    SwitchCompat push_notification_Switch, finger_print_Switch;
    Context context = this;
    String msplashscreen;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    KeyguardManager keyguardManager;
    private static final int CREDENTIALS_RESULT = 4342;
    private static final String MWC = "MWC";
    AlertDialog mAlertDialog;
    String finger_print_settings, push_notificaton_settings;

    boolean push_notificatoin;
    int backButtonCount = 0;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intiaizeViews();
        getAccountSettings();
        getWhiteLabelSettings();
        OnClickListeners();

        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure() == true) {
            finger_print_layout.setVisibility(View.VISIBLE);
        } else {
            finger_print_layout.setVisibility(View.GONE);
        }

        if (mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0) {
            mCompanyName = mAccountSettingsResponses.get(0).getCompany_Name();
            userName = mAccountSettingsResponses.get(0).getUser_Name();
        }

        if (userName != null && !userName.isEmpty()) {
            user_name_txt.setText(userName);
        }


        if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
            msplashscreen = mWhiteLabelResponses.get(0).getSplash_Screen_Color();
        }

        if (msplashscreen != null) {
            int itemEnableColor = Color.parseColor(msplashscreen);
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(itemEnableColor);
            logo_layout.setBackgroundDrawable(shape);
        }


      /*  Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("172.16.40.51")
                .appendPath("assets")
                .appendPath("images")
                .appendPath("whitelabels")
                .appendPath(mCompanyName)
                .appendPath("mwc-logo.png");
        String myUrl = builder.build().toString();
        AQuery aq = new AQuery(context); // intsialze aquery
        aq.id(LOGO_image).image(myUrl);*/

        if(PreferenceUtils.getLogoImagePath(context) != null)
        {
            File imgFile = new  File(PreferenceUtils.getLogoImagePath(context));

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                LOGO_image.setImageBitmap(myBitmap);

            }
        }
    }


    private void getAccountSettings() {
        final AccountSettings accountSettings = new AccountSettings(context);
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
        final AccountSettings accountSettings = new AccountSettings(context);
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


    private void intiaizeViews() {
        LOGO_image = (ImageView) findViewById(R.id.LOGO_image);
        username_layout = (LinearLayout) findViewById(R.id.username_layout);
        user_name_txt = (TextView) findViewById(R.id.user_name_txt);
        offline_layout = (LinearLayout) findViewById(R.id.offline_layout);
        help_layout = (LinearLayout) findViewById(R.id.help_layout);
        terms_privacy_layout = (LinearLayout) findViewById(R.id.terms_privacy_layout);
        logout_layout = (LinearLayout) findViewById(R.id.logout_layout);
        logo_layout = (LinearLayout) findViewById(R.id.logo_layout);
        push_notification_Switch = (SwitchCompat) findViewById(R.id.push_notification_Switch);
        finger_print_Switch = (SwitchCompat) findViewById(R.id.finger_print_Switch);
        finger_print_layout = (LinearLayout) findViewById(R.id.finger_print_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Settings");
        toolbarTextAppernce();

    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }


    @Override
    protected void onResume() {
        super.onResume();

        getAccountSettings();

        if (mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0) {
            push_notificaton_settings = mAccountSettingsResponses.get(0).getIs_Push_Notification_Enabled();
            finger_print_settings =  mAccountSettingsResponses.get(0).getIs_Local_Auth_Enabled();
        }



        if(finger_print_settings != null && !finger_print_settings.isEmpty())
        {
            if(finger_print_settings.equals("1"))
            {
                finger_print_Switch.setChecked(true);

            }
            else if(finger_print_settings.equals("0"))
            {
                finger_print_Switch.setChecked(false);
            }
        }



        if(push_notificaton_settings != null && !push_notificaton_settings.isEmpty())
        {

            if(push_notificaton_settings.equals("1"))
            {
                push_notificatoin = true;
                push_notification_Switch.setChecked(true);

            }
            else if(push_notificaton_settings.equals("0"))
            {
                push_notificatoin = false;
                push_notification_Switch.setChecked(false);
            }
        }


        String channalId = "my_channel_01";
        boolean device_status = isNotificationChannelEnabled(NavigationSettingsActivity.this, channalId);


        if(push_notificatoin != device_status)
        {

            String register_type = null;
            if(isNotificationChannelEnabled(NavigationSettingsActivity.this, channalId) == true)
            {
                register_type = "1";
            }
            else {
                register_type = "0";
            }

            getPushNotificationDocumentService(register_type);
        }


    }


    private void getPushNotificationDocumentService(final String register_type)
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

                        if (response.body().getStatus().getCode() instanceof Boolean) {
                            if (response.body().getStatus().getCode() == Boolean.FALSE) {


                                AccountSettings accountSettings = new AccountSettings(context);
                                accountSettings.UpdatePushNotificatoinSettings(register_type);

                                PushNotificatoinSettings_Respository pushNotificatoinSettings = new PushNotificatoinSettings_Respository(context);
                                pushNotificatoinSettings.updatePushNotificatoinStatus(register_type);

                                if(register_type.equals("1"))
                                {
                                    push_notification_Switch.setChecked(true);
                                }
                                else if(register_type.equals("0"))
                                {
                                    push_notification_Switch.setChecked(false);
                                }


                            }

                        }
 /*                       else if (response.body().getStatus().getCode() instanceof Double) {

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


    public boolean isNotificationChannelEnabled(Activity context, String channelId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!TextUtils.isEmpty(channelId)) {

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if(manager==null) {
                    NotificationChannel channel = manager.getNotificationChannel(channelId);
                    return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
                }
            }
            return false;
        } else {

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
            // return NotificationManagerCompat.from(context).areNotificationsEnabled();
            return areNotificationsEnabled;
        }
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_navigation_settings;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_settings;
    }


    private void OnClickListeners() {
        offline_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, OffLine_Files_List.class);
                startActivity(intent);

            }
        });

        username_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, UserProfileActivity.class);
                startActivity(intent);
            }
        });


        push_notification_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed() == true) {

                    if (push_notification_Switch.isChecked() == true) {
                        push_notification_Switch.setChecked(false);
                    } else {
                        push_notification_Switch.setChecked(true);
                    }


                    Intent intent = new Intent();
                    if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
                    } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("app_package", context.getPackageName());
                        intent.putExtra("app_uid", context.getApplicationInfo().uid);
                    } else {
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                    }

                    startActivity(intent);


                }
            }
        });


        finger_print_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed() == true) {

                    if (finger_print_Switch.isChecked() == true) {
                        finger_print_Switch.setChecked(false);
                    } else {
                        finger_print_Switch.setChecked(true);
                    }


                    checkCredentials();

                }

            }
        });


        help_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0) {
                    Intent mIntent = new Intent(context, WebviewLoaderTermsActivity.class);
                    mIntent.putExtra(Constants.SETASSISTANCEPOPUPCONTENTURL, mAccountSettingsResponses.get(0).getHelp_Guide_URL());
                    startActivity(mIntent);
                }

            }
        });


        terms_privacy_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0) {
                    Intent mIntent = new Intent(context, WebviewLoaderTermsActivity.class);
                    mIntent.putExtra(Constants.SETTERMSPAGECONTENTURL, mAccountSettingsResponses.get(0).getTerms_URL());
                    startActivity(mIntent);
                }

            }
        });


        logout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowWarningMessageForLogout();

            }
        });

    }

    private void ShowWarningMessageForLogout() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText("Do you want to logout?");

        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("CANCEL");


        sendPinButton.setText("OK");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        sendPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

                AccountSettings accountSettings = new AccountSettings(context);
                accountSettings.LogouData(NavigationSettingsActivity.this);

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();


    }


    private void clearApplicationData() {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s + " DELETED");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkCredentials() {
        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        Intent credentialsIntent = keyguardManager.createConfirmDeviceCredentialIntent("Password required", "please enter your pattern to receive your token");

        if (credentialsIntent != null) {
            startActivityForResult(credentialsIntent, CREDENTIALS_RESULT);
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREDENTIALS_RESULT) {

            if (resultCode == RESULT_OK) {

                getAccountSettings();

                if (mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0) {
                    finger_print_settings = mAccountSettingsResponses.get(0).getIs_Local_Auth_Enabled();
                }

                String opt_value = null;
                if (finger_print_settings != null && !finger_print_settings.isEmpty()) {
                    if (finger_print_settings.equals("1")) {
                        opt_value = "opt-in";
                    } else if (finger_print_settings.equals("0")) {
                        opt_value = "opt-out";
                    }
                }


                if (opt_value != null && !opt_value.isEmpty()) {
                    sendFingerPrintStatusToServer(opt_value);
                }

            } else {
                Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }


        }

    }


    private void sendFingerPrintStatusToServer(final String opt_value) {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final FingerPrintRequestModel externalShareResponseModel = new FingerPrintRequestModel("finger_print", "Android", opt_value);

            String request = new Gson().toJson(externalShareResponseModel);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final ShareEndUserDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);

            Call call = mGetCategoryDocumentsService.sendFingerPrintStatus(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<SharedDocumentResponseModel>() {
                @Override
                public void onResponse(Response<SharedDocumentResponseModel> response, Retrofit retrofit) {

                    if (response != null) {

                        if (response.body().getStatus().getCode() instanceof Boolean) {
                            if (response.body().getStatus().getCode() == Boolean.FALSE) {

                                String optValue;
                                if (opt_value.equalsIgnoreCase("opt-in")) {
                                    optValue = "opt-out";
                                    String message = "Secutity settings revoked";
                                    showWarningAlertForLocalAuthenticationStatus(message, optValue);


                                } else {
                                    optValue = "opt-in";
                                    String message = "Thank you for enabling protection. From now on every app launch, you will be required to provide your authentication";
                                    showWarningAlertForLocalAuthenticationStatus(message, optValue);


                                }

                                AccountSettings accountSettings = new AccountSettings(context);
                                accountSettings.UpdateFingerPrintSettings(optValue);


                            }

                        }
                        /*else if (response.body().getStatus().getCode() instanceof Double) {

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


    private void showWarningAlertForLocalAuthenticationStatus(String message, String optValue) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText(message);

        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("CANCEL");
        cancelButton.setVisibility(View.GONE);

        sendPinButton.setText("OK");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        sendPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();


                if (optValue.equalsIgnoreCase("opt-out")) {
                    finger_print_Switch.setChecked(false);
                } else {
                    finger_print_Switch.setChecked(true);
                }


            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
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