package com.mwc.docportal.FTL;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.mwc.docportal.Fragments.FTLPasswordValidationFragment;
import com.mwc.docportal.Fragments.FTLRegistrationFragment;
import com.mwc.docportal.Fragments.FTLUserValidationFragment;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.R;
import com.mwc.docportal.RootActivity;
import com.mwc.docportal.Utils.Constants;

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

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

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
                Intent intent = new Intent(FTLPasswordValidationActivity.this, FTLUserValidationActivity.class);
                intent.putExtra(Constants.ACCESSTOKEN, getIntent().getStringExtra(Constants.ACCESSTOKEN));
                startActivity(intent);
                finish();
            }else{

            }
        }else{
            startActivity(new Intent(FTLPasswordValidationActivity.this, LoginActivity.class));
            finish();
        }
    }
}
