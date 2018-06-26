package com.swaas.mwc.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.swaas.mwc.FTL.FTLAgreeTermsAcceptanceActivity;
import com.swaas.mwc.R;

/**
 * Created by harika on 25-06-2018.
 */

public class FTLAgreeTermsAcceptanceFragment extends Fragment {

    FTLAgreeTermsAcceptanceActivity mActivity;
    View mView;
    Button letsStartButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FTLAgreeTermsAcceptanceActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ftl_agree_terms_acceptance_fragment, container, false);

        intializeViews();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        letsStartButton = (Button) mView.findViewById(R.id.lets_start);
    }

    private void addListenersToViews() {
        letsStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
