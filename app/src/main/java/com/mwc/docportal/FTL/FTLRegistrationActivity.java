package com.mwc.docportal.FTL;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.mwc.docportal.Fragments.FTLFragment;
import com.mwc.docportal.Fragments.FTLRegistrationFragment;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.R;
import com.mwc.docportal.RootActivity;

import java.util.List;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLRegistrationActivity extends RootActivity {

    FTLRegistrationFragment mFTLRegistrationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftl_registration_activity);

        mFTLRegistrationFragment = new FTLRegistrationFragment();
        loadFTLFragment();
    }

    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.ftl_registration_fragment, mFTLRegistrationFragment).addToBackStack(null).commit();
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
            if (getVisibleFragment() instanceof FTLRegistrationFragment) {
                startActivity(new Intent(FTLRegistrationActivity.this, FTLActivity.class));
                finish();
            }else{

            }
        }else{
            startActivity(new Intent(FTLRegistrationActivity.this, LoginActivity.class));
            finish();
        }
    }
}
