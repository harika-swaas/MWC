package com.swaas.mwc.FTL;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.swaas.mwc.Fragments.FTLFragment;
import com.swaas.mwc.Fragments.FTLUserValidationFragment;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;

import java.util.List;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLUserValidationActivity extends RootActivity {

    FTLUserValidationFragment mFTLUserValidationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftl_user_validation_activity);

        mFTLUserValidationFragment = new FTLUserValidationFragment();
        loadFTLFragment();
    }

    private void loadFTLFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.ftl_user_validation_fragment, mFTLUserValidationFragment)
                .commit();
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
            if (getVisibleFragment() instanceof FTLUserValidationFragment) {
                startActivity(new Intent(FTLUserValidationActivity.this, FTLPinVerificationActivity.class));
                finish();
            }else{

            }
        }else{
            startActivity(new Intent(FTLUserValidationActivity.this, LoginActivity.class));
            finish();
        }
    }
}
