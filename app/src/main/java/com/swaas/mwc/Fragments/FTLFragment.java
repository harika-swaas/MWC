package com.swaas.mwc.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.swaas.mwc.FTL.FTLActivity;
import com.swaas.mwc.FTL.FTLRegistrationActivity;
import com.swaas.mwc.R;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLFragment extends Fragment {

    FTLActivity mActivity;
    View mView;
    Button mNext;
    ImageView mBackIv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FTLActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ftl_fragment, container, false);

        intializeViews();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        mBackIv = (ImageView) mView.findViewById(R.id.back_image_view);
        mNext = (Button) mView.findViewById(R.id.next_button);
    }

    private void addListenersToViews() {
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity,FTLRegistrationActivity.class));
                mActivity.finish();
            }
        });

        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
    }

}
