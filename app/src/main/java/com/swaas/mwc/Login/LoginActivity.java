package com.swaas.mwc.Login;

import android.os.Bundle;
import android.view.MenuItem;

import com.swaas.mwc.Fragments.LoginFragment;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;


/**
 * Created by harika on 13-06-2018.
 */

public class LoginActivity extends RootActivity {

    LoginFragment mLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginFragment = new LoginFragment();
        loadFTLFragment();
    }

    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment, mLoginFragment).
                addToBackStack(null).commit();
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
