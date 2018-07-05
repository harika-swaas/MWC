package com.swaas.mwc.FTL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.swaas.mwc.Fragments.FTLPasswordValidationFragment;
import com.swaas.mwc.Fragments.FTLRegistrationFragment;
import com.swaas.mwc.Fragments.FTLUserValidationFragment;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;
import com.swaas.mwc.Utils.Constants;

import java.util.List;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLPasswordValidationActivity extends RootActivity {

    FTLPasswordValidationFragment mFTLPasswordValidationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftl_password_validation_activity);

        mFTLPasswordValidationFragment = new FTLPasswordValidationFragment();
        loadFTLFragment();
    }

    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.ftl_password_validation_fragment, mFTLPasswordValidationFragment).addToBackStack(null).commit();
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
            if (getVisibleFragment() instanceof FTLPasswordValidationFragment) {
                startActivity(new Intent(FTLPasswordValidationActivity.this, FTLUserValidationActivity.class));
                finish();
            }else{

            }
        }else{
            startActivity(new Intent(FTLPasswordValidationActivity.this, LoginActivity.class));
            finish();
        }
    }
}
