package com.swaas.mwc.DMS;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.swaas.mwc.Fragments.ItemNavigationFolderFragment;
import com.swaas.mwc.Fragments.ItemNavigationSettingsFragment;
import com.swaas.mwc.Fragments.ItemNavigationSharedFragment;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;
import com.swaas.mwc.Utils.Constants;

/**
 * Created by harika on 11-07-2018.
 */

public class MyFoldersDMSActivity extends RootActivity {

    public static final int FOLDER_FRAGMENT = 1;
    public static final int SHARED_FRAGMENT = 2;
    public static final int SETTINGS_FRAGMENT = 3;
    BottomNavigationView mBottomNavigationView;
    LinearLayout sortingView;
    int mSelectedItem = FOLDER_FRAGMENT;
    Menu mBottomNavigationMenu;
    MenuItem mFolderMenuItem, mSharedMenuItem, mSettingsMenuItem;
    ItemNavigationFolderFragment mFolderFragment;
    ItemNavigationSharedFragment mSharedFragment;
    ItemNavigationSettingsFragment mSettingsFragment;
    ImageView select;
    ImageView toggle;
    boolean check = false;
    int backButtonCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_folders_dms_fragment);

        intializeViews();

        loadBottomNavigation();
        switchFragment(FOLDER_FRAGMENT);

        addListenersToViews();
    }

    private void intializeViews() {

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        toggle = (ImageView) findViewById(R.id.toggle);
        sortingView = (LinearLayout) findViewById(R.id.sort);
        CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.white));
        select= (ImageView) findViewByIdInContent(R.id.checklist);
    }

    private void addListenersToViews() {
      /*  select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/


        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check == true)
                {
                    toggle.setImageResource(R.mipmap.ic_list);
                    if(mSelectedItem != 0) {
                            if (mSelectedItem == FOLDER_FRAGMENT) {

                            Bundle mBundle = new Bundle();
                            ItemNavigationFolderFragment itemNavigationFolderFragment = ItemNavigationFolderFragment.newInstance();
                            mBundle.putBoolean(Constants.TOOGLEGRID, check);
                            itemNavigationFolderFragment.setArguments(mBundle);

                            getSupportFragmentManager().beginTransaction().replace(R.id.container, itemNavigationFolderFragment).addToBackStack(null).commit();

                        } else if (mSelectedItem == SHARED_FRAGMENT) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();

                        } else if (mSelectedItem == SETTINGS_FRAGMENT) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
                        }
                    }

                    check = false;
                }
                else
                {
                    toggle.setImageResource(R.mipmap.ic_grid);
                    if(mSelectedItem != 0) {
                        if (mSelectedItem == FOLDER_FRAGMENT) {
                            Bundle mBundle = new Bundle();
                            ItemNavigationFolderFragment itemNavigationFolderFragment = ItemNavigationFolderFragment.newInstance();
                            mBundle.putBoolean(Constants.TOOGLEGRID, check);
                            itemNavigationFolderFragment.setArguments(mBundle);

                            getSupportFragmentManager().beginTransaction().replace(R.id.container, itemNavigationFolderFragment).addToBackStack(null).commit();

                        } else if (mSelectedItem == SHARED_FRAGMENT) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();

                        } else if (mSelectedItem == SETTINGS_FRAGMENT) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
                        }
                    }

                    check = true;
                }
            }
        });
    }

    public void openBottomSheet(View v) {
        /*BottomSheet dialog = new BottomSheet(MyFoldersDMSActivity.this);
        dialog.show();*/

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);

        TextView sortByName = (TextView) view.findViewById(R.id.sort_by_name);
        TextView sortByNewest = (TextView) view.findViewById(R.id.sort_by_newest);
        TextView sortBySize = (TextView) view.findViewById(R.id.sort_by_size);
        TextView sortByDate = (TextView) view.findViewById(R.id.sort_by_date);

        final Dialog mBottomSheetDialog = new Dialog(MyFoldersDMSActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        sortByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                getCategoryDocumentsBySortName();
            }
        });

        sortByNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                getCategoryDocumentsBySortNewest();
            }
        });

        sortBySize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                getCategoryDocumentsBySortSize();
            }
        });

        sortByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                getCategoryDocumentsBySortDate();
            }
        });
    }

    private void getCategoryDocumentsBySortName() {

        if(mSelectedItem != 0) {
            if (mSelectedItem == FOLDER_FRAGMENT) {
                Bundle mBundle = new Bundle();
                ItemNavigationFolderFragment itemNavigationFolderFragment = ItemNavigationFolderFragment.newInstance();
                mBundle.putBoolean(Constants.SORT_BY_NAME, true);
                itemNavigationFolderFragment.setArguments(mBundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.container, itemNavigationFolderFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SHARED_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SETTINGS_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
            }
        }
    }

    private void getCategoryDocumentsBySortNewest() {

        if(mSelectedItem != 0) {
            if (mSelectedItem == FOLDER_FRAGMENT) {
                Bundle mBundle = new Bundle();
                ItemNavigationFolderFragment itemNavigationFolderFragment = ItemNavigationFolderFragment.newInstance();
                mBundle.putBoolean(Constants.SORT_BY_NEWEST, true);
                itemNavigationFolderFragment.setArguments(mBundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.container, itemNavigationFolderFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SHARED_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SETTINGS_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
            }
        }
    }

    private void getCategoryDocumentsBySortSize() {

        if(mSelectedItem != 0) {
            if (mSelectedItem == FOLDER_FRAGMENT) {
                Bundle mBundle = new Bundle();
                ItemNavigationFolderFragment itemNavigationFolderFragment = ItemNavigationFolderFragment.newInstance();
                mBundle.putBoolean(Constants.SORT_BY_SIZE, true);
                itemNavigationFolderFragment.setArguments(mBundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.container, itemNavigationFolderFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SHARED_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SETTINGS_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
            }
        }
    }


    private void getCategoryDocumentsBySortDate() {

        if(mSelectedItem != 0) {
            if (mSelectedItem == FOLDER_FRAGMENT) {
                Bundle mBundle = new Bundle();
                ItemNavigationFolderFragment itemNavigationFolderFragment = ItemNavigationFolderFragment.newInstance();
                mBundle.putBoolean(Constants.SORT_BY_DATE, true);
                itemNavigationFolderFragment.setArguments(mBundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.container, itemNavigationFolderFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SHARED_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SETTINGS_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
            }
        }
    }


    private void loadBottomNavigation() {

        mBottomNavigationMenu = mBottomNavigationView.getMenu();
        mFolderMenuItem = mBottomNavigationMenu.findItem(R.id.navigation_folder);
        mSharedMenuItem = mBottomNavigationMenu.findItem(R.id.navigation_shared);
        mSettingsMenuItem = mBottomNavigationMenu.findItem(R.id.navigation_settings);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_folder:
                        switchFragment(FOLDER_FRAGMENT);
                        break;
                    case R.id.navigation_shared:
                        switchFragment(SHARED_FRAGMENT);
                        break;
                    case R.id.navigation_settings:
                        switchFragment(SETTINGS_FRAGMENT);
                        break;
                }
                return false;
            }
        });
    }

    private void switchFragment(int fragment){
        getSupportFragmentManager().popBackStackImmediate();
        switch (fragment) {
            case FOLDER_FRAGMENT:
                mSelectedItem = FOLDER_FRAGMENT;
                if (mFolderFragment == null) {
                    mFolderFragment = ItemNavigationFolderFragment.newInstance();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mFolderFragment).addToBackStack(null).commit();
                break;

            case R.id.navigation_shared:
                mSelectedItem = SHARED_FRAGMENT;
                if (mSharedFragment == null) {
                    mSharedFragment = ItemNavigationSharedFragment.newInstance();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();
                break;

            case R.id.navigation_settings:
                mSelectedItem = SETTINGS_FRAGMENT;
                if (mSettingsFragment == null) {
                    mSettingsFragment = ItemNavigationSettingsFragment.newInstance();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
                break;
        }
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
