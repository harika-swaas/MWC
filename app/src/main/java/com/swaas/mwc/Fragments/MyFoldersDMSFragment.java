package com.swaas.mwc.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.swaas.mwc.Adapters.DmsAdapter;
import com.swaas.mwc.Adapters.DmsAdapterList;
import com.swaas.mwc.DMS.MyFoldersDMSActivity;
import com.swaas.mwc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harika on 11-07-2018.
 */

public class MyFoldersDMSFragment extends Fragment {
    RecyclerView recyclerView;
    MyFoldersDMSActivity mActivity;
    DmsAdapter dmsAdapter;
    View mView;
    BottomNavigationView mBottomNavigationView;
    ImageView toggle;
    public boolean check = true;
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
        recyclerView= (RecyclerView) mView.findViewById(R.id.recycler_dms);
        display();
        CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout)mView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.white));

        return mView;
    }
    private void intializeViews() {

        mBottomNavigationView = (BottomNavigationView) mView.findViewById(R.id.navigation);
        toggle =(ImageView) mView.findViewById(R.id.toggle);




    }

    private void addListenersToViews() {

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check==true) {
                    toggle.setImageResource(R.mipmap.ic_list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                    List<String> input = new ArrayList<>();
                    for (int i = 0; i < 20; i++) {
                        input.add("Test" + i);
                    }
                    DmsAdapterList dmsAdapterList = new DmsAdapterList(input);
                    recyclerView.setAdapter(dmsAdapterList);
                    check=false;
                }
                else {
                    toggle.setImageResource(R.mipmap.ic_grid);
                    recyclerView.setLayoutManager(new GridLayoutManager(mActivity,3));
                    List<String> input = new ArrayList<>();
                    for (int i = 0; i < 20; i++) {
                        input.add("Test" + i);
                    }
                    DmsAdapter dmsAdapter = new DmsAdapter(input);
                    recyclerView.setAdapter(dmsAdapter);
                    check=true;
                }
            }
        });

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
                        transaction.replace(R.id.container, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });
        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, ItemNavigationFolderFragment.newInstance());
        transaction.commit();
    }
   public View display(){
       recyclerView= (RecyclerView) mView.findViewById(R.id.recycler_dms);
       recyclerView.setLayoutManager(new GridLayoutManager(mActivity,3));

       List<String> input = new ArrayList<>();
       for (int i = 0; i < 20; i++) {
           input.add("Test" + i);
       }
       DmsAdapter dmsAdapter = new DmsAdapter(input);
       recyclerView.setAdapter(dmsAdapter);
       return mView;

    }
}
