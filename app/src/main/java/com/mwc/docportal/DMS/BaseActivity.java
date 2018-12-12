package com.mwc.docportal.DMS;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.APIResponseModel;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.GetSharedCategoryDocumentsRequest;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.GetCategoryDocumentsService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import q.rorbin.badgeview.QBadgeView;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList();
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        getWhiteLabelProperities();
        if(mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0)
        {
            String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            int selectedColor = Color.parseColor(itemSelectedColor);
            setNavMenuItemThemeColors(selectedColor);
        }

   //     getSharedDocumentsTotalUnreadCount();


    }

    public void getSharedDocumentsTotalUnreadCount()
    {
        if (NetworkUtils.checkIfNetworkAvailable(this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final GetSharedCategoryDocumentsRequest mGetSharedDocumentsRequest;

            mGetSharedDocumentsRequest = new GetSharedCategoryDocumentsRequest("0");

            String request = new Gson().toJson(mGetSharedDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = mGetCategoryDocumentsService.getSharedCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<APIResponseModel>() {
                @Override
                public void onResponse(Response<APIResponseModel> response, Retrofit retrofit) {

                    if (response != null) {

                        String message = "";
                        if(response.body().getStatus().getMessage() != null)
                        {
                            message = response.body().getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(BaseActivity.this, message, response.body().getStatus().getCode()))
                        {
                            int totalUnreadCount = 0;
                            List<APIResponseModel.Category> categoryList = response.body().getData().getCategories();
                            if (categoryList != null && categoryList.size() > 0) {
                                for (APIResponseModel.Category category : categoryList) {
                                    totalUnreadCount = totalUnreadCount + category.getUnread_doc_count();
                                }
                            }

                            GlobalVariables.totalUnreadableCount = totalUnreadCount;

                           /* if(totalUnreadCount > 0)
                            {
                                showBadgeCount(navigationView, R.id.navigation_shared, totalUnreadCount);
                            }
                            else
                            {
                                removeTextLabel(navigationView, R.id.navigation_shared);
                            }*/

                            showBadgeCount(navigationView, R.id.navigation_shared, totalUnreadCount);

                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                    CommonFunctions.showTimeOutError(context, t);

                }
            });
        }

    }

   /* public void removeBadge(BottomNavigationView navigationView, int itemId)
    {
        BottomNavigationItemView itemView = navigationView.findViewById(itemId);
        View badge = LayoutInflater.from(context).inflate(R.layout.badge_count_item, navigationView, false);
        TextView text = badge.findViewById(R.id.unread_count);
     //   View badge = itemView.findViewById(R.id.notifications_badge);
        ((ViewGroup)text.getParent()).removeView(text);
        *//*if (itemView.getChildCount() == 3) {
            itemView.removeViewAt(1);
        }*//*
    }*/

    public void removeBadge(BottomNavigationView navigationView, int itemId)
    {
       /* BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(index);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        itemView.removeViewAt(itemView.getChildCount()-1);*/

       /* BottomNavigationItemView itemView = navigationView.findViewById(itemId);
        View badge = LayoutInflater.from(context).inflate(R.layout.badge_count_item, navigationView, false);

        TextView text = badge.findViewById(R.id.unread_count);

    //    badge.setVisibility(View.GONE);

        ((ViewGroup)text.getParent()).removeView(text);*/

        BottomNavigationItemView itemView = navigationView.findViewById(itemId);
        View badge = LayoutInflater.from(context).inflate(R.layout.badge_count_item, navigationView, false);

        TextView text = badge.findViewById(R.id.unread_count);
        RelativeLayout relativeLayout = badge.findViewById(R.id.badge_icon_linearlayout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)text.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        text.setLayoutParams(params);

        relativeLayout.setVisibility(View.GONE);

        itemView.addView(badge);




    }

    public void showBadgeCount(BottomNavigationView navigationView, int itemId, int badgeCountValue)
    {

        BottomNavigationItemView itemView = navigationView.findViewById(itemId);
        View badge = LayoutInflater.from(context).inflate(R.layout.badge_count_item, navigationView, false);

        TextView text = badge.findViewById(R.id.unread_count);
        RelativeLayout relativeLayout = badge.findViewById(R.id.badge_icon_linearlayout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)text.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        text.setLayoutParams(params);


        navigationView.removeView(navigationView.getChildAt(2));

        if(badgeCountValue > 0)
        {
            if(badgeCountValue > 99)
            {
                relativeLayout.setVisibility(View.VISIBLE);
                text.setText("99+");
                navigationView.addView(badge);
            }
            else
            {
                relativeLayout.setVisibility(View.VISIBLE);
                text.setText(""+badgeCountValue);
                navigationView.addView(badge);
            }
        }
        else
        {
            relativeLayout.setVisibility(View.VISIBLE);
            text.setText(""+badgeCountValue);
            navigationView.removeView(badge);

                View view2 = navigationView.getChildAt(1);

                if(view2 != null)
                {
                    view2.setVisibility(View.GONE);
                }
        }

    }

    public void removeTextLabel(@NonNull BottomNavigationView bottomNavigationView, @IdRes int menuItemId) {
        View view = bottomNavigationView.findViewById(menuItemId);
        if (view == null) return;
        if (view instanceof MenuView.ItemView) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View v = viewGroup.getChildAt(i);
                if (v instanceof ViewGroup) {
                    viewGroup.removeViewAt(i);
                }
            }
          //  viewGroup.setPadding(view.getPaddingLeft(), (viewGroup.getPaddingTop() + padding) / 2, view.getPaddingRight(), view.getPaddingBottom());
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();

    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_folder) {
                resetMoveVariables();
                Intent intent=new Intent(this,NavigationMyFolderActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.navigation_shared) {
                GlobalVariables.isMultiSelect = false;
                GlobalVariables.selectedCountValue = 0;
                GlobalVariables.sharedRootDocumentList.clear();
                Intent intent = new Intent(this, NavigationSharedActivity.class);
                startActivity(intent);
            } else if (itemId == R.id.navigation_settings) {
                startActivity(new Intent(this, NavigationSettingsActivity.class));
            }

            finish();
        return true;
    }

    private void resetMoveVariables()
    {
        GlobalVariables.isMultiSelect = false;
        GlobalVariables.selectedCountValue = 0;
        GlobalVariables.activityCount = 0;
        GlobalVariables.activityFinishCount = 0;
        GlobalVariables.moveOriginIndex = 0;
    }

    private void setNavMenuItemThemeColors(int selectedColor)
    {
        //Setting default colors for menu item Text and Icon

        int navDefaultIconColor = 0;
        if(mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0)
        {
            String itemUnSelectedColor = mWhiteLabelResponses.get(0).getItem_Unselected_Color();
            int unSelectedColor = Color.parseColor(itemUnSelectedColor);
        //    int navDefaultTextColor = Color.parseColor("#000000");
            navDefaultIconColor = Color.parseColor(itemUnSelectedColor);

        }


        //Defining ColorStateList for menu item Text
    /*    ColorStateList navMenuTextList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[] {
                        selectedColor,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor
                }
        );*/

        //Defining ColorStateList for menu item Icon
        ColorStateList navMenuIconList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[] {
                        selectedColor,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor
                }
        );

     //   navigationView.setItemTextColor(navMenuTextList);
        navigationView.setItemIconTintList(navMenuIconList);
    }


    private void getWhiteLabelProperities()
    {
        AccountSettings accountSettings = new AccountSettings(BaseActivity.this);
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

    private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = navigationView.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    abstract int getContentViewId();

    abstract int getNavigationMenuItemId();


}
