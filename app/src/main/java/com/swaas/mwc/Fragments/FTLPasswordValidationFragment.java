package com.swaas.mwc.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.swaas.mwc.FTL.FTLAgreeTermsAcceptanceActivity;
import com.swaas.mwc.FTL.FTLPasswordValidationActivity;
import com.swaas.mwc.R;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLPasswordValidationFragment extends Fragment {

    FTLPasswordValidationActivity mActivity;
    View mView;
    Button mNext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FTLPasswordValidationActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ftl_password_validation_fragment, container, false);

        intializeViews();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {
        mNext = (Button) mView.findViewById(R.id.next_button);
    }

    private void addListenersToViews() {
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity,FTLAgreeTermsAcceptanceActivity.class));
                mActivity.finish();
            }
        });
    }
}
