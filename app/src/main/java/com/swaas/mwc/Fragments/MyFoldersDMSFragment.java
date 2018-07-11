package com.swaas.mwc.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swaas.mwc.DMS.MyFoldersDMSActivity;
import com.swaas.mwc.Login.LoginHelpUserGuideActivity;
import com.swaas.mwc.R;

/**
 * Created by harika on 11-07-2018.
 */

public class MyFoldersDMSFragment extends Fragment {

    MyFoldersDMSActivity mActivity;
    View mView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MyFoldersDMSActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.my_folders_dms_fragment, container, false);
 //       intializeViews();

        return mView;
    }
}
