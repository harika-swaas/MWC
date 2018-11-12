package com.mwc.docportal.DMS;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Fragments.History_Fragment;
import com.mwc.docportal.Fragments.Notes_Fragment;
import com.mwc.docportal.Fragments.Properties_Fragment;
import com.mwc.docportal.R;
import com.mwc.docportal.RootActivity;
import com.mwc.docportal.Utils.Constants;
import com.mwc.docportal.pdf.PdfViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by barath on 7/19/2018.
 */

public class Tab_Activity extends RootActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    Toolbar toolbar;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    public static boolean isFromShared;
    boolean isFrommyFolder;
    public static boolean isNameEditAvailable;
    String documentName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_history_tab_layout);

       /* if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }*/

        viewPager = (ViewPager) findViewById(R.id.simpleViewPager);
        addTabs(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout);
        tabLayout.setupWithViewPager(viewPager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back));
      //  getSupportActionBar().setTitle("Document Info");

        isFromShared = getIntent().getBooleanExtra("IsFromShared", false);
        isFrommyFolder = getIntent().getBooleanExtra("IsFromMyFolder", false);
        isNameEditAvailable =  getIntent().getBooleanExtra("IsNameEditEnable", false);

        if(getIntent().getStringExtra(Constants.DOCUMENT_NAME) != null)
        {
            documentName = getIntent().getStringExtra(Constants.DOCUMENT_NAME);
            getSupportActionBar().setTitle(documentName);
        }



    }

    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Properties_Fragment(), "Properties");
        adapter.addFrag(new Notes_Fragment(), "Notes");
        adapter.addFrag(new History_Fragment(), "History");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {


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
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed()
    {
        if(isFrommyFolder)
        {
            GlobalVariables.refreshDMS = true;
        }

        finish();

    }

}