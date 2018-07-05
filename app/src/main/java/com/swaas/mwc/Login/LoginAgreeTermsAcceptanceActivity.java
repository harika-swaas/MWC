package com.swaas.mwc.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.swaas.mwc.Fragments.LoginAgreeTermsAcceptanceFragment;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;

import java.util.List;

/**
 * Created by harika on 05-07-2018.
 */

public class LoginAgreeTermsAcceptanceActivity extends RootActivity {

    LoginAgreeTermsAcceptanceFragment mLoginAgreeTermsAcceptanceFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_terms_acceptance_activity);

        mLoginAgreeTermsAcceptanceFragment = new LoginAgreeTermsAcceptanceFragment();
        loadFTLFragment();
    }

    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.login_terms_acceptance_fragment, mLoginAgreeTermsAcceptanceFragment).
                addToBackStack(null).commit();
    }

    private Fragment getVisibleFragment(){
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if(fragmentList != null && fragmentList .size() > 0) {
            return fragmentList.get(fragmentList.size()-1);
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if(getVisibleFragment() != null) {
            if (getVisibleFragment() instanceof LoginAgreeTermsAcceptanceFragment) {
                startActivity(new Intent(LoginAgreeTermsAcceptanceActivity.this, Notifiy.class));
                finish();
            }else{

            }
        }else{
            startActivity(new Intent(LoginAgreeTermsAcceptanceActivity.this, LoginActivity.class));
            finish();
        }
    }
}
