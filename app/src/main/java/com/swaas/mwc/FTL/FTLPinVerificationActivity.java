package com.swaas.mwc.FTL;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.swaas.mwc.Fragments.FTLFragment;
import com.swaas.mwc.Fragments.FTLPinVerificationFragment;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;

import java.util.List;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLPinVerificationActivity extends RootActivity {

    FTLPinVerificationFragment mFTLPinVerificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftl_pin_verification_activity);

        mFTLPinVerificationFragment = new FTLPinVerificationFragment();
        loadFTLFragment();
    }

    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.ftl_pin_verification_fragment, mFTLPinVerificationFragment).addToBackStack(null).commit();
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
            if (getVisibleFragment() instanceof FTLPinVerificationFragment) {
                startActivity(new Intent(FTLPinVerificationActivity.this, FTLRegistrationActivity.class));
                finish();
            }else{

            }
        }else{
            startActivity(new Intent(FTLPinVerificationActivity.this, LoginActivity.class));
            finish();
        }
    }
}
