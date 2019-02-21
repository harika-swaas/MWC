package com.mwc.docportal.FTL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mwc.docportal.Fragments.FTLPinVerificationFragment;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.R;
import com.mwc.docportal.RootActivity;
import com.mwc.docportal.Utils.Constants;

import java.util.List;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLPinVerificationActivity extends RootActivity {

    FTLPinVerificationFragment mFTLPinVerificationFragment;
    AlertDialog mBackDialog;
    boolean isFromLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftl_pin_verification_activity);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mFTLPinVerificationFragment = new FTLPinVerificationFragment();
        loadFTLFragment();
    }

    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.ftl_pin_verification_fragment, mFTLPinVerificationFragment).addToBackStack(null).commit();
    }

    private Fragment getVisibleFragment() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null && fragmentList.size() > 0) {
            return fragmentList.get(fragmentList.size() - 1);
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (getVisibleFragment() != null) {
            if (getVisibleFragment() instanceof FTLPinVerificationFragment) {
                if (!isFromLogin) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(FTLPinVerificationActivity.this);
                    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.back_custom_alert_layout, null);
                    builder.setView(view);
                    builder.setCancelable(false);

                    Button yesButton = (Button) view.findViewById(R.id.yes_button);
                    Button noButton = (Button) view.findViewById(R.id.no_button);

                    yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBackDialog.dismiss();
                            startActivity(new Intent(FTLPinVerificationActivity.this, FTLRegistrationActivity.class));
                            finish();
                        }
                    });

                    noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBackDialog.dismiss();
                        }
                    });

                    mBackDialog = builder.create();
                    mBackDialog.show();
                } else {
                    startActivity(new Intent(FTLPinVerificationActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFromLogin = getIntent().getBooleanExtra(Constants.IS_FROM_LOGIN, false);
    }
}
