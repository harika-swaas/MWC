package com.mwc.docportal.DMS;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList();
 //   MenuItem navigationFolder, navigationShared, navigationSettings;
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
