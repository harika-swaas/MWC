package com.swaas.mwc.FTL;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.swaas.mwc.Fragments.FTLRegistrationFragment;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;

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
