package com.swaas.mwc.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swaas.mwc.Login.LoginHelpUserGuideActivity;
import com.swaas.mwc.R;

/**
 * Created by harika on 05-07-2018.
 */

public class LoginHelpUserGuideFragment extends Fragment {

    LoginHelpUserGuideActivity mActivity;
    View mView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (LoginHelpUserGuideActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.login_help_user_guide_fragment, container, false);

        return mView;
    }
}
