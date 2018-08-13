package com.swaas.mwc.Fragments;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.iid.FirebaseInstanceId;
import com.swaas.mwc.API.Model.AccountSettingsResponse;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.DMS.MyFoldersDMSActivity;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.OffLine_Files_List;
import com.swaas.mwc.R;
import com.swaas.mwc.UserProfileActivity;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by harika on 11-07-2018.
 */

public class ItemNavigationSettingsFragment extends Fragment{

    ImageView LOGO_image;
    MyFoldersDMSActivity mActivity;
    View mView;
    LinearLayout username_layout, offline_layout, help_layout, terms_privacy_layout, logout_layout, logo_layout;
    TextView user_name_txt;
    static Boolean isTouched = false;
    String mCompanyName, userName;
    String msplashscreen;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    SwitchCompat push_notification_Switch, finger_print_Switch;
    String push_notificaton_settings;
    boolean push_notificatoin;
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


        if (mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0) {
            push_notificaton_settings = mAccountSettingsResponses.get(0).getIs_Push_Notification_Enabled();
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


      /* String tokenid =  FirebaseInstanceId.getInstance().getToken();

       Toast.makeText(mActivity, tokenid, Toast.LENGTH_LONG).show();*/

        String channalId = "my_channel_01";
        if(push_notificatoin != isNotificationChannelEnabled(mActivity, channalId))
        {


        }


    }


    public boolean isNotificationChannelEnabled(Context context,String channelId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!TextUtils.isEmpty(channelId)) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = manager.getNotificationChannel(channelId);
                return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
            }
            return false;
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
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



        push_notification_Switch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isTouched = true;
                return false;
            }
        });

        push_notification_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isTouched) {
                    isTouched = false;
                    if (isChecked) {


                    }
                    else {

                        Intent intent = new Intent();
                        if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1){
                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                            intent.putExtra("android.provider.extra.APP_PACKAGE", mActivity.getPackageName());
                        }else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                            intent.putExtra("app_package", mActivity.getPackageName());
                            intent.putExtra("app_uid", mActivity.getApplicationInfo().uid);
                        }else {
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                        }

                        mActivity.startActivity(intent);

                    }
                }
            }
        });




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


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyFoldersDMSActivity.title_layout= (LinearLayout) getActivity().findViewById(R.id.l1);
        MyFoldersDMSActivity.title_layout.setVisibility(View.GONE);

        MyFoldersDMSActivity.floatingActionMenu = (FloatingActionMenu) getActivity().findViewById(R.id.floating_action_menu);
        MyFoldersDMSActivity.floatingActionMenu.setVisibility(View.GONE);

    }
}
