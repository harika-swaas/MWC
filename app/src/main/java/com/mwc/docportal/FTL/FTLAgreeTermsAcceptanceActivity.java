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

import com.mwc.docportal.Fragments.FTLAgreeTermsAcceptanceFragment;
import com.mwc.docportal.Fragments.FTLPasswordValidationFragment;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.R;
import com.mwc.docportal.RootActivity;
import com.mwc.docportal.Utils.Constants;

import java.util.List;

/**
 * Created by harika on 25-06-2018.
 */

public class FTLAgreeTermsAcceptanceActivity extends RootActivity {

    FTLAgreeTermsAcceptanceFragment mFTLAgreeTermsAcceptanceFragment;
    AlertDialog mBackDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftl_agree_terms_acceptance_activity);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mFTLAgreeTermsAcceptanceFragment = new FTLAgreeTermsAcceptanceFragment();
        loadFTLAgreeTermsAcceptanceFragment();
    }

    private void loadFTLAgreeTermsAcceptanceFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.ftl_agree_terms_acceptance_fragment, mFTLAgreeTermsAcceptanceFragment).addToBackStack(null).commit();
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
            if (getVisibleFragment() instanceof FTLAgreeTermsAcceptanceFragment) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(FTLAgreeTermsAcceptanceActivity.this);
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
                        Intent intent = new Intent(FTLAgreeTermsAcceptanceActivity.this, FTLPasswordValidationActivity.class);
                        intent.putExtra(Constants.USERNAME,getIntent().getStringExtra(Constants.USERNAME));
                        intent.putExtra(Constants.WELCOME_MSG, getIntent().getStringExtra(Constants.WELCOME_MSG));
                        intent.putExtra(Constants.ACCESSTOKEN, getIntent().getStringExtra(Constants.ACCESSTOKEN));
                        intent.putExtra(Constants.SETTERMS, getIntent().getStringExtra(Constants.SETTERMS));
                        startActivity(intent);
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

            }
        }else{
            startActivity(new Intent(FTLAgreeTermsAcceptanceActivity.this, LoginActivity.class));
            finish();
        }
    }
}
