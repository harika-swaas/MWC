package com.swaas.mwc.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.ListPinDevices;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.SendPinRequest;
import com.swaas.mwc.API.Service.ListPinDevicesService;
import com.swaas.mwc.API.Service.SendPinService;
import com.swaas.mwc.Common.SimpleDividerItemDecoration;
import com.swaas.mwc.FTL.FTLAgreeTermsAcceptanceActivity;
import com.swaas.mwc.FTL.FTLPasswordValidationActivity;
import com.swaas.mwc.Fragments.FTLAgreeTermsAcceptanceFragment;
import com.swaas.mwc.Fragments.LoginFragment;
import com.swaas.mwc.Fragments.PinVerificationFragment;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.RootActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by barath on 6/24/2018.
 */

public class PinVerificationActivity extends RootActivity {

    PinVerificationFragment mPinVerificationFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_verification_activity);

        mPinVerificationFragment = new PinVerificationFragment();
        loadFTLFragment();
    }

    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.pin_verification_fragment, mPinVerificationFragment).
                addToBackStack(null).commit();
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
            if (getVisibleFragment() instanceof PinVerificationFragment) {
                startActivity(new Intent(PinVerificationActivity.this, LoginActivity.class));
                finish();
            }else{

            }
        }else{
            startActivity(new Intent(PinVerificationActivity.this, LoginActivity.class));
            finish();
        }
    }
}

