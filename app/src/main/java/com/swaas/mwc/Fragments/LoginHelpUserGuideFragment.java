package com.swaas.mwc.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.koushikdutta.ion.builder.Builders;
import com.swaas.mwc.Login.Dashboard;
import com.swaas.mwc.Login.LoginHelpUserGuideActivity;
import com.swaas.mwc.R;

/**
 * Created by harika on 05-07-2018.
 */

public class LoginHelpUserGuideFragment extends Fragment {

    LoginHelpUserGuideActivity mActivity;
    View mView;
    Button submit;
    TextView cancel;
    CheckBox checkBox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (LoginHelpUserGuideActivity) getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.login_help_user_guide_fragment, container, false);
        intializeViews();
        addListenersToViews();


        return mView;
    }

    private void intializeViews() {
        submit = (Button)mView.findViewById(R.id.got_it_button);
        cancel =(TextView)mView.findViewById(R.id.cancel_action);
        checkBox=(CheckBox)mView.findViewById(R.id.checkbox_user_guide);
    }
    private void addListenersToViews(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (mActivity, Dashboard.class);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (mActivity, Dashboard.class);
                startActivity(intent);
            }
        });


    }
}
