package com.mwc.docportal.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.mwc.docportal.API.Model.ListPinDevices;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.SendPinRequest;
import com.mwc.docportal.API.Service.ListPinDevicesService;
import com.mwc.docportal.API.Service.SendPinService;
import com.mwc.docportal.Common.SimpleDividerItemDecoration;
import com.mwc.docportal.FTL.FTLAgreeTermsAcceptanceActivity;
import com.mwc.docportal.FTL.FTLPasswordValidationActivity;
import com.mwc.docportal.Fragments.FTLAgreeTermsAcceptanceFragment;
import com.mwc.docportal.Fragments.LoginFragment;
import com.mwc.docportal.Fragments.PinVerificationFragment;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.RootActivity;

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

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

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

