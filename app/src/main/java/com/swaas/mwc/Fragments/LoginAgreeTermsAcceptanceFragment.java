package com.swaas.mwc.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Login.LoginAgreeTermsAcceptanceActivity;
import com.swaas.mwc.Login.LoginHelpUserGuideActivity;
import com.swaas.mwc.R;

/**
 * Created by harika on 05-07-2018.
 */

public class LoginAgreeTermsAcceptanceFragment extends Fragment{

    LoginAgreeTermsAcceptanceActivity mActivity;
    View mView;
    Button mIAgreeButton;
    TextView cancel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (LoginAgreeTermsAcceptanceActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.login_terms_acceptance_fragment, container, false);

        intializeViews();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        mIAgreeButton = (Button) mView.findViewById(R.id.agree_button);
        cancel = (TextView) mView.findViewById(R.id.sign_out);
    }

    private void addListenersToViews() {

        mIAgreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, LoginHelpUserGuideActivity.class));
                mActivity.finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, LoginActivity.class));
                mActivity.finish();
            }
        });
    }
}
