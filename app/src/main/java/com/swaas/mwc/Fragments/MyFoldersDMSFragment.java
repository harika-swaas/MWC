package com.swaas.mwc.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.swaas.mwc.DMS.MyFoldersDMSActivity;
import com.swaas.mwc.R;

/**
 * Created by harika on 11-07-2018.
 */

public class MyFoldersDMSFragment extends Fragment {

    MyFoldersDMSActivity mActivity;
    View mView;
    BottomNavigationView mBottomNavigationView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MyFoldersDMSActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.my_folders_dms_fragment, container, false);
        intializeViews();
        addListenersToViews();

        return mView;
    }

    private void intializeViews() {

        mBottomNavigationView = (BottomNavigationView) mView.findViewById(R.id.navigation);
    }

    private void addListenersToViews() {

        mBottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.navigation_folder:
                                selectedFragment = ItemNavigationFolderFragment.newInstance();
                                break;
                            case R.id.navigation_shared:
                                selectedFragment = ItemNavigationSharedFragment.newInstance();
                                break;
                            case R.id.navigation_settings:
                                selectedFragment = ItemNavigationSettingsFragment.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ItemNavigationFolderFragment.newInstance());
        transaction.commit();
    }
}
