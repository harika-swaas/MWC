package com.swaas.mwc.FTL;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.swaas.mwc.Fragments.FTLPinVerificationFragment;
import com.swaas.mwc.R;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLPinVerificationActivity extends AppCompatActivity {

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
