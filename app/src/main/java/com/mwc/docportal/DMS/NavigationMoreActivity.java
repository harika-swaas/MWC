package com.mwc.docportal.DMS;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.OffLine_Files_List;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.UserProfileActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NavigationMoreActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView LOGO_image;
    LinearLayout username_layout, offline_layout, request_callback_layout,logo_layout;
    TextView user_name_txt;
    List<AccountSettingsResponse> mAccountSettingsResponses;
    List<WhiteLabelResponse> mWhiteLabelResponses;
    String mCompanyName, userName;
    Context context = this;
    String msplashscreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   showBadgeCount(navigationView, R.id.navigation_shared, GlobalVariables.totalUnreadableCount, NavigationMoreActivity.this);
        initiaizeViews();
        getAccountSettings();
        getWhiteLabelSettings();
        OnClickListeners();

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


        if(PreferenceUtils.getSettingsLogoImagePath(context) != null)
        {
           /* String decryptedPath = CommonFunctions.decryption(PreferenceUtils.getLogoImagePath(context));
            File imgFile = new  File(decryptedPath);*/
            File imgFile = new  File(PreferenceUtils.getSettingsLogoImagePath(context));

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                LOGO_image.setImageBitmap(myBitmap);

                setOriginalImageSize(myBitmap);

            }
        }
        else if(PreferenceUtils.getLogoImagePath(context) != null) {
            File imgFile = new  File(PreferenceUtils.getLogoImagePath(context));

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                LOGO_image.setImageBitmap(myBitmap);
                setOriginalImageSize(myBitmap);
            }
        }

    }

    private void OnClickListeners()
    {
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
    }

    private void setOriginalImageSize(Bitmap myBitmap)
    {
        if(myBitmap != null){
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, myBitmap.getWidth(), this.getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, myBitmap.getHeight(), this.getResources().getDisplayMetrics());

            LOGO_image.setMinimumWidth(width);
            LOGO_image.setMinimumHeight(height);
        }
    }


    private void initiaizeViews()
    {
        mAccountSettingsResponses = new ArrayList<>();
        mWhiteLabelResponses = new ArrayList<>();

        LOGO_image = (ImageView) findViewById(R.id.LOGO_image);
        username_layout = (LinearLayout) findViewById(R.id.username_layout);
        user_name_txt = (TextView) findViewById(R.id.user_name_txt);
        offline_layout = (LinearLayout) findViewById(R.id.offline_layout);
        request_callback_layout = (LinearLayout) findViewById(R.id.request_callback_layout);
        logo_layout = (LinearLayout) findViewById(R.id.logo_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("More");
        toolbarTextAppernce();
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

   /* @Override
    int getContentViewId() {
        return R.layout.activity_navigation_more;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_more;
    }*/

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

}
