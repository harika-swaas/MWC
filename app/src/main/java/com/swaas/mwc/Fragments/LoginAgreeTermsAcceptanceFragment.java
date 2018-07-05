package com.swaas.mwc.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swaas.mwc.Login.LoginAgreeTermsAcceptanceActivity;
import com.swaas.mwc.R;

/**
 * Created by harika on 05-07-2018.
 */

public class LoginAgreeTermsAcceptanceFragment extends Fragment{

    LoginAgreeTermsAcceptanceActivity mActivity;
    View mView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (LoginAgreeTermsAcceptanceActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.login_terms_acceptance_fragment, container, false);

        return mView;
    }
}
