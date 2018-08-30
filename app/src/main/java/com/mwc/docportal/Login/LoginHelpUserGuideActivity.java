package com.mwc.docportal.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.mwc.docportal.Fragments.LoginHelpUserGuideFragment;
import com.mwc.docportal.R;
import com.mwc.docportal.RootActivity;

import java.util.List;

/**
 * Created by harika on 05-07-2018.
 */

public class LoginHelpUserGuideActivity extends RootActivity {

    LoginHelpUserGuideFragment mLoginHelpUserGuideFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_help_user_guide_activity);

        mLoginHelpUserGuideFragment = new LoginHelpUserGuideFragment();
        loadFTLFragment();
    }

    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.login_help_user_guide_fragment, mLoginHelpUserGuideFragment).
                addToBackStack(null).commit();
    }

    private Fragment getVisibleFragment(){
        @SuppressLint("RestrictedApi") List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if(fragmentList != null && fragmentList .size() > 0) {
            return fragmentList.get(fragmentList.size()-1);
        }
        return null;
    }

    @Override
    public void onBackPressed() {

    }
}
