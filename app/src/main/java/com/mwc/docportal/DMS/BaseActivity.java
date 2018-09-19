package com.mwc.docportal.DMS;

import android.content.Intent;
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

                startActivity(new Intent(this, NavigationMyFolderActivity.class));
            } else if (itemId == R.id.navigation_shared) {
             //   changeIconColor(itemId);
                Intent intent = new Intent(this, NavigationSharedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (itemId == R.id.navigation_settings) {
              //  changeIconColor(itemId);
                startActivity(new Intent(this, NavigationSettingsActivity.class));
            }
            finish();
        return true;
    }

    private void changeIconColor(int itemid)
    {
        if(itemid == R.id.navigation_folder)
        {
           /* String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            int selectedColor = Color.parseColor(itemSelectedColor);
            MenuItem navigationFolder = navigationView.getMenu().findItem(itemid);
            menuIconColor(navigationFolder,selectedColor);
            String itemUnSelectedColor = mWhiteLabelResponses.get(0).getItem_Unselected_Color();
            int UnselectedColor = Color.parseColor(itemUnSelectedColor);
            MenuItem navigationShared = navigationView.getMenu().findItem(R.id.navigation_shared);
            menuIconColor(navigationShared,UnselectedColor);
            MenuItem navigationSettings = navigationView.getMenu().findItem(R.id.navigation_settings);
            menuIconColor(navigationSettings,UnselectedColor);*/
        }
        else if(itemid == R.id.navigation_shared)
        {
            String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            int selectedColor = Color.parseColor(itemSelectedColor);
            MenuItem navigationFolder = navigationView.getMenu().findItem(itemid);
            menuIconColor(navigationFolder,selectedColor);
            String itemUnSelectedColor = mWhiteLabelResponses.get(0).getItem_Unselected_Color();
            int UnselectedColor = Color.parseColor(itemUnSelectedColor);
            MenuItem navigationShared = navigationView.getMenu().findItem(R.id.navigation_folder);
            menuIconColor(navigationShared,UnselectedColor);
            MenuItem navigationSettings = navigationView.getMenu().findItem(R.id.navigation_settings);
            menuIconColor(navigationSettings,UnselectedColor);
        }
        else if(itemid == R.id.navigation_settings)
        {
            String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            int selectedColor = Color.parseColor(itemSelectedColor);
            MenuItem navigationFolder = navigationView.getMenu().findItem(itemid);
            menuIconColor(navigationFolder,selectedColor);
            String itemUnSelectedColor = mWhiteLabelResponses.get(0).getItem_Unselected_Color();
            int UnselectedColor = Color.parseColor(itemUnSelectedColor);
            MenuItem navigationShared = navigationView.getMenu().findItem(R.id.navigation_folder);
            menuIconColor(navigationShared,UnselectedColor);
            MenuItem navigationSettings = navigationView.getMenu().findItem(R.id.navigation_shared);
            menuIconColor(navigationSettings,UnselectedColor);
        }
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

    public static void menuIconColor(MenuItem menuItem, int color) {
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }


}
