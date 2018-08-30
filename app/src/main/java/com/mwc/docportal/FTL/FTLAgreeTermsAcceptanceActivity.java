package com.mwc.docportal.FTL;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.mwc.docportal.Fragments.FTLAgreeTermsAcceptanceFragment;
import com.mwc.docportal.Fragments.FTLPasswordValidationFragment;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.R;
import com.mwc.docportal.RootActivity;

import java.util.List;

/**
 * Created by harika on 25-06-2018.
 */

public class FTLAgreeTermsAcceptanceActivity extends RootActivity {

    FTLAgreeTermsAcceptanceFragment mFTLAgreeTermsAcceptanceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftl_agree_terms_acceptance_activity);

        mFTLAgreeTermsAcceptanceFragment = new FTLAgreeTermsAcceptanceFragment();
        loadFTLAgreeTermsAcceptanceFragment();
    }

    private void loadFTLAgreeTermsAcceptanceFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.ftl_agree_terms_acceptance_fragment, mFTLAgreeTermsAcceptanceFragment).addToBackStack(null).commit();
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
            if (getVisibleFragment() instanceof FTLAgreeTermsAcceptanceFragment) {
                startActivity(new Intent(FTLAgreeTermsAcceptanceActivity.this, FTLPasswordValidationActivity.class));
                finish();
            }else{

            }
        }else{
            startActivity(new Intent(FTLAgreeTermsAcceptanceActivity.this, LoginActivity.class));
            finish();
        }
    }
}
