package com.swaas.mwc.FTL;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.swaas.mwc.Fragments.FTLUserValidationFragment;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;

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
