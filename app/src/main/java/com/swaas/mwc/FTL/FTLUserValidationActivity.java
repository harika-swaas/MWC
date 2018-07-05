package com.swaas.mwc.FTL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.swaas.mwc.Fragments.FTLFragment;
import com.swaas.mwc.Fragments.FTLUserValidationFragment;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.R;
import com.swaas.mwc.RootActivity;

import java.util.List;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLUserValidationActivity extends RootActivity {

    FTLUserValidationFragment mFTLUserValidationFragment;
    AlertDialog mBackDialog;

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
            if (getVisibleFragment() instanceof FTLUserValidationFragment) {


                final AlertDialog.Builder builder = new AlertDialog.Builder(FTLUserValidationActivity.this);
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
                        startActivity(new Intent(FTLUserValidationActivity.this, FTLPinVerificationActivity.class));
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
            }else{

            }
        }else{
            startActivity(new Intent(FTLUserValidationActivity.this, LoginActivity.class));
            finish();
        }
    }
}
