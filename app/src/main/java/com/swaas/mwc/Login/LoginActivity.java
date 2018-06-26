package com.swaas.mwc.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.LoginRequest;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Service.LoginService;
import com.swaas.mwc.FTL.FTLActivity;
import com.swaas.mwc.Fragments.LoginFragment;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;
import com.swaas.mwc.Utils.Constants;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
