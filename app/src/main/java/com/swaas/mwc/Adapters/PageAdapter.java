package com.swaas.mwc.Adapters;

/**
 * Created by barath on 7/19/2018.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.swaas.mwc.Fragments.History_Fragment;
import com.swaas.mwc.Fragments.Notes_Fragment;
import com.swaas.mwc.Fragments.Properties_Fragment;

public class PageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Fragment mfragment;
    public PageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Properties_Fragment tab1 = new Properties_Fragment();
                return tab1;
            case 1:
                Notes_Fragment tab2 = new Notes_Fragment();
                return tab2;
            case 2:
                History_Fragment tab3 = new History_Fragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
