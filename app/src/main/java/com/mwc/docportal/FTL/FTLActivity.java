package com.mwc.docportal.FTL;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v4.app.Fragment;

import com.mwc.docportal.Fragments.FTLFragment;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.R;

import java.util.List;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLActivity extends AppCompatActivity {

    FTLFragment mFTLFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftl_activity);

        mFTLFragment = new FTLFragment();
        loadFTLFragment();
    }

    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.ftl_fragment, mFTLFragment).addToBackStack(null).commit();
    }

    /*public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else if (getFragmentManager().getBackStackEntryCount() == 1) {
            moveTaskToBack(false);
        } else {
            getFragmentManager().popBackStack();
        }
    }*/

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
            if (getVisibleFragment() instanceof FTLFragment) {
                startActivity(new Intent(FTLActivity.this, LoginActivity.class));
                finish();
            }else{

            }
        }else{
            startActivity(new Intent(FTLActivity.this, LoginActivity.class));
            finish();
        }
    }
}
