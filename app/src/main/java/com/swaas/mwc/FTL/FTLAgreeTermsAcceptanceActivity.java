package com.swaas.mwc.FTL;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.swaas.mwc.Fragments.FTLAgreeTermsAcceptanceFragment;
import com.swaas.mwc.R;

/**
 * Created by harika on 25-06-2018.
 */

public class FTLAgreeTermsAcceptanceActivity extends AppCompatActivity {

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
