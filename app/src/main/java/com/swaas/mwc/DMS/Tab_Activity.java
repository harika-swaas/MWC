package com.swaas.mwc.DMS;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.swaas.mwc.Fragments.History_Fragment;
import com.swaas.mwc.Fragments.Notes_Fragment;
import com.swaas.mwc.Fragments.Properties_Fragment;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by barath on 7/19/2018.
 */

public class Tab_Activity extends RootActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMainContentView(R.layout.document_history_tab_layout);

        viewPager = (ViewPager) findViewById(R.id.simpleViewPager);
        addTabs(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout);
        tabLayout.setupWithViewPager(viewPager);
        toolbar = (Toolbar) findViewByIdInContent(R.id.toolbar);
    }

    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Properties_Fragment(), "Properties");
        adapter.addFrag(new Notes_Fragment(), "Notes");
        adapter.addFrag(new History_Fragment(), "History");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                startActivity(new Intent(Tab_Activity.this, MyFoldersDMSActivity.class));
                return true;

        }
        return super.onOptionsItemSelected(item);

    }
}