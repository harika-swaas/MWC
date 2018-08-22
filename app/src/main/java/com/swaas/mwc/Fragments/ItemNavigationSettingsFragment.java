package com.swaas.mwc.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.github.clans.fab.FloatingActionMenu;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.AccountSettingsResponse;
import com.swaas.mwc.API.Model.ExternalShareResponseModel;
import com.swaas.mwc.API.Model.FingerPrintRequestModel;
import com.swaas.mwc.API.Model.PushNotificationRequestModel;
import com.swaas.mwc.API.Model.SharedDocumentResponseModel;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.API.Service.ShareEndUserDocumentsService;
import com.swaas.mwc.DMS.MyFoldersDMSActivity;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.Authenticate;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.OffLine_Files_List;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.UserProfileActivity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by harika on 11-07-2018.
 */

public class ItemNavigationSettingsFragment extends Fragment{

    ImageView LOGO_image;
    MyFoldersDMSActivity mActivity;
    View mView;
    LinearLayout username_layout, offline_layout, help_layout, terms_privacy_layout, logout_layout, logo_layout, finger_print_layout;
    TextView user_name_txt;
 //   static Boolean isTouched = false;
    static Boolean fingerPrintTouch = false;
    String mCompanyName, userName;
    String msplashscreen;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    SwitchCompat push_notification_Switch, finger_print_Switch;
    String push_notificaton_settings, finger_print_settings;
    boolean push_notificatoin;
    public  FloatingActionMenu floatingActionMenu;

    KeyguardManager keyguardManager;
    private static final int CREDENTIALS_RESULT = 4342;
    String register_type = "0";
    Boolean finger_print_enabled = false;
    public static ItemNavigationSettingsFragment newInstance() {
        ItemNavigationSettingsFragment fragment = new ItemNavigationSettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MyFoldersDMSActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView =  inflater.inflate(R.layout.fragment_item_navigation_settings, container, false);
        setHasOptionsMenu(true);
        intiaizeViews();
        getAccountSettings();
        getWhiteLabelSettings();
        OnClickListeners();


        keyguardManager = (KeyguardManager) mActivity.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure() == true) {
            finger_print_layout.setVisibility(View.VISIBLE);
        }
        else {
            finger_print_layout.setVisibility(View.GONE);
        }

        if (mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0) {
            mCompanyName = mAccountSettingsResponses.get(0).getCompany_Name();
            userName = mAccountSettingsResponses.get(0).getUser_Name();
        }

        if(userName != null && !userName.isEmpty())
        {
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


        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("172.16.40.51")
                .appendPath("assets")
                .appendPath("images")
                .appendPath("whitelabels")
                .appendPath(mCompanyName)
                .appendPath("mwc-logo.png");
        String myUrl = builder.build().toString();
        AQuery aq = new AQuery(getActivity()); // intsialze aquery
        aq.id(LOGO_image).image(myUrl);


        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();


        getAccountSettings();

        if (mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0) {
            push_notificaton_settings = mAccountSettingsResponses.get(0).getIs_Push_Notification_Enabled();
            finger_print_settings =  mAccountSettingsResponses.get(0).getIs_Local_Auth_Enabled();
        }


        String opt_value =  null;
        if(finger_print_settings != null && !finger_print_settings.isEmpty())
        {
            if(finger_print_settings.equals("1"))
            {

                finger_print_Switch.setChecked(true);
                opt_value = "opt-in";

            }
            else if(finger_print_settings.equals("0"))
            {

                finger_print_Switch.setChecked(false);
                opt_value = "opt-out";
            }
        }

        if(finger_print_enabled == true)
        {
            if(opt_value != null && !opt_value.isEmpty())
            {
                sendFingerPrintStatusToServer(opt_value);
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
        boolean device_status = isNotificationChannelEnabled(mActivity, channalId);


        if(push_notificatoin != device_status)
        {

            if(isNotificationChannelEnabled(mActivity, channalId) == true)
            {
                register_type = "1";
            }
            else {
                register_type = "0";
            }

          getPushNotificationDocumentService(register_type);
        }


    }

    private void sendFingerPrintStatusToServer(final String opt_value)
    {
        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final FingerPrintRequestModel externalShareResponseModel = new FingerPrintRequestModel("finger_print", "Android", opt_value);

            String request = new Gson().toJson(externalShareResponseModel);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final ShareEndUserDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);

            Call call = mGetCategoryDocumentsService.sendFingerPrintStatus(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<SharedDocumentResponseModel>() {
                @Override
                public void onResponse(Response<SharedDocumentResponseModel> response, Retrofit retrofit) {

                    if (response != null) {

                        if (response.body().getStatus().getCode() instanceof Boolean) {
                            if (response.body().getStatus().getCode() == Boolean.FALSE) {

                                String optValue;
                                if(opt_value.equalsIgnoreCase("opt-in"))
                                {
                                    optValue = "opt-out";
                                }
                                else {
                                    optValue = "opt-in";
                                }

                                AccountSettings accountSettings = new AccountSettings(mActivity);
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

    private void getPushNotificationDocumentService(final String register_type)
    {
        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final PushNotificationRequestModel externalShareResponseModel = new PushNotificationRequestModel("", "Android", register_type);

            String request = new Gson().toJson(externalShareResponseModel);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final ShareEndUserDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);

            Call call = mGetCategoryDocumentsService.sendPushNotificatoinStatus(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<SharedDocumentResponseModel>() {
                @Override
                public void onResponse(Response<SharedDocumentResponseModel> response, Retrofit retrofit) {

                    if (response != null) {

                        if (response.body().getStatus().getCode() instanceof Boolean) {
                            if (response.body().getStatus().getCode() == Boolean.FALSE) {


                                AccountSettings accountSettings = new AccountSettings(mActivity);
                                accountSettings.UpdatePushNotificatoinSettings(register_type);


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

    private void OnClickListeners()
    {
        offline_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent intent =  new Intent(getActivity(), OffLine_Files_List.class);
            getActivity().startActivity(intent);

            }
        });

        username_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =  new Intent(getActivity(), UserProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });


        /*push_notification_Switch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isTouched = true;
                return false;
            }
        });*/

        push_notification_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(buttonView.isPressed() == true) {

                    if(push_notification_Switch.isChecked() ==  true)
                    {
                        push_notification_Switch.setChecked(false);
                    }
                    else {
                        push_notification_Switch.setChecked(true);
                    }


                        Intent intent = new Intent();
                        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                            intent.putExtra("android.provider.extra.APP_PACKAGE", mActivity.getPackageName());
                        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                            intent.putExtra("app_package", mActivity.getPackageName());
                            intent.putExtra("app_uid", mActivity.getApplicationInfo().uid);
                        } else {
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                        }

                        mActivity.startActivity(intent);

                        /*AccountSettings accountSettings = new AccountSettings(mActivity);
                        accountSettings.UpdatePushNotificatoinSettings(register_type);*/


                }
            }
        });



       /* finger_print_Switch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                fingerPrintTouch = true;
                return false;
            }
        });
*/
        finger_print_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(buttonView.isPressed() == true) {

                    if (finger_print_Switch.isChecked() == true) {
                        finger_print_Switch.setChecked(false);
                    } else {
                        finger_print_Switch.setChecked(true);
                    }


                    checkCredentials();

                }

            }
        });




    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkCredentials() {
        keyguardManager = (KeyguardManager) mActivity.getSystemService(Context.KEYGUARD_SERVICE);
        Intent credentialsIntent = keyguardManager.createConfirmDeviceCredentialIntent("Password required", "please enter your pattern to receive your token");

        if (credentialsIntent != null) {
            mActivity.startActivityForResult(credentialsIntent, CREDENTIALS_RESULT);
            finger_print_enabled = true;
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREDENTIALS_RESULT) {

            if (resultCode == RESULT_OK) {
             //   updateLocalAuthAndLoggedInStatus();
            }
            else{
                Toast.makeText(mActivity,"Authentication Failed",Toast.LENGTH_SHORT).show();

            }
        }
    }


    private void getWhiteLabelSettings()
    {
        final AccountSettings accountSettings = new AccountSettings(getActivity());
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

    private void getAccountSettings()
    {
        final AccountSettings accountSettings = new AccountSettings(getActivity());
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

    private void intiaizeViews()
    {
        LOGO_image = (ImageView)mView.findViewById(R.id.LOGO_image);
        username_layout = (LinearLayout) mView.findViewById(R.id.username_layout);
        user_name_txt = (TextView) mView.findViewById(R.id.user_name_txt);
        offline_layout = (LinearLayout) mView.findViewById(R.id.offline_layout);
        help_layout = (LinearLayout) mView.findViewById(R.id.help_layout);
        terms_privacy_layout = (LinearLayout) mView.findViewById(R.id.terms_privacy_layout);
        logout_layout = (LinearLayout) mView.findViewById(R.id.logout_layout);
        logo_layout = (LinearLayout)mView.findViewById(R.id.logo_layout);
        push_notification_Switch = (SwitchCompat)mView.findViewById(R.id.push_notification_Switch);
        finger_print_Switch =  (SwitchCompat)mView.findViewById(R.id.finger_print_Switch);
        finger_print_layout = (LinearLayout)mView.findViewById(R.id.finger_print_layout);
        floatingActionMenu = (FloatingActionMenu) getActivity().findViewById(R.id.floating_action_menu);
        floatingActionMenu.setVisibility(View.INVISIBLE);


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyFoldersDMSActivity.title_layout= (LinearLayout) getActivity().findViewById(R.id.linearlayout1);
        MyFoldersDMSActivity.title_layout.setVisibility(View.GONE);

       /* MyFoldersDMSActivity.floatingActionMenu = (FloatingActionMenu) getActivity().findViewById(R.id.floating_action_menu);
        MyFoldersDMSActivity.floatingActionMenu.setVisibility(View.GONE);
*/
    }
}
