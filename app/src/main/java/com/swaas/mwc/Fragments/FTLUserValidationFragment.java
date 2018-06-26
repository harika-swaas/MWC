package com.swaas.mwc.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.swaas.mwc.FTL.FTLPasswordValidationActivity;
import com.swaas.mwc.FTL.FTLUserValidationActivity;
import com.swaas.mwc.R;
import com.swaas.mwc.Utils.Constants;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLUserValidationFragment extends Fragment {

    FTLUserValidationActivity mActivity;
    View mView;
    Button mNext;
    String mTextBackgroundColor,mTextForeGroundColor,mAppBackGroundColor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FTLUserValidationActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ftl_user_validation_fragment, container, false);

        intializeViews();
        getIntentData();
        setButtonBackgroundColor();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        mNext = (Button) mView.findViewById(R.id.next_button);
    }

    private void getIntentData() {

        if(mActivity.getIntent() != null){
            mTextBackgroundColor = mActivity.getIntent().getStringExtra(Constants.TEXT_BACKGROUND_COLOR);
            mTextForeGroundColor = mActivity.getIntent().getStringExtra(Constants.TEXT_FOREGROUND_COLOR);
            mAppBackGroundColor = mActivity.getIntent().getStringExtra(Constants.APP_BACKGROUND_COLOR);
        }
    }

    private void setButtonBackgroundColor() {

        if(!mAppBackGroundColor.isEmpty()) {
            mNext.setBackgroundColor(Integer.parseInt(mAppBackGroundColor));
        } else {
            mNext.setBackgroundResource(R.drawable.next);
        }
    }

    private void addListenersToViews() {
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity,FTLPasswordValidationActivity.class));
                mActivity.finish();
            }
        });
    }
}
