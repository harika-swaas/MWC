package com.swaas.mwc.Login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.R;
import com.swaas.mwc.Utils.Constants;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by barath on 6/24/2018.
 */

public class Touchid extends Authenticate {

    Button button2;
    TextView skip1;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    boolean mIsFromFTL;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enable_touch_id);
        skip1 = (TextView) findViewById(R.id.skip_button);
        button2 = (Button) findViewById(R.id.enable_touch_button);

        getIntentData();
        setButtonBackgroundColor();

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent(Touchid.this, Notifiy.class);
                intent.putExtra(Constants.IS_FROM_FTL,mIsFromFTL);
                startActivity(intent);
                finish();
                checkCredentials();
                updateLocalAuthAndLoggedInStatus();
            }
        });

        skip1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                updateLoggedInStatus();
                Intent intent = new Intent(Touchid.this, Notifiy.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getIntentData() {

        if(getIntent() != null) {
            mIsFromFTL = getIntent().getBooleanExtra(Constants.IS_FROM_FTL,false);
        }
    }

    private void setButtonBackgroundColor() {

        getWhiteLabelProperities();

        if(mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
            String mobileItemEnableColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            String mobileItemDisableColor = mWhiteLabelResponses.get(0).getItem_Unselected_Color();

            int itemEnableColor = Color.parseColor(mobileItemEnableColor);
            int itemDisableColor = Color.parseColor(mobileItemDisableColor);

            if (mobileItemEnableColor != null) {
                // Initialize a new GradientDrawable
                GradientDrawable shape = new GradientDrawable();

                // Specify the shape of drawable
                shape.setShape(GradientDrawable.RECTANGLE);

                // Make the border rounded
                shape.setCornerRadius(50f);

                // Set the fill color of drawable
                shape.setColor(itemEnableColor);

                button2.setBackgroundDrawable(shape);
            } else if(mobileItemDisableColor != null){
                // Initialize a new GradientDrawable
                GradientDrawable shape = new GradientDrawable();

                // Specify the shape of drawable
                shape.setShape(GradientDrawable.RECTANGLE);

                // Make the border rounded
                shape.setCornerRadius(50f);

                // Set the fill color of drawable
                shape.setColor(itemDisableColor);

                button2.setBackgroundDrawable(shape);
            }
        } else {
            button2.setBackgroundResource(R.drawable.next);
        }

        /*String mobileItemEnableColor = PreferenceUtils.getMobileItemEnableColor(this);
        String mobileItemDisableColor = PreferenceUtils.getMobileItemDisableColor(this);

        int itemEnableColor = 0;
        int itemDisableColor = 0;

        if (mobileItemEnableColor != null) {
            itemEnableColor = Color.parseColor(mobileItemEnableColor);
        }
        if (mobileItemDisableColor != null) {
            itemDisableColor = Color.parseColor(mobileItemDisableColor);
        }

        if (mobileItemEnableColor != null) {
            // Initialize a new GradientDrawable
            GradientDrawable shape = new GradientDrawable();

            // Specify the shape of drawable
            shape.setShape(GradientDrawable.RECTANGLE);

            // Make the border rounded
            shape.setCornerRadius(50f);

            // Set the fill color of drawable
            shape.setColor(itemEnableColor);

            button2.setBackgroundDrawable(shape);

        } else {

        }*/
    }

    private void getWhiteLabelProperities() {

        AccountSettings accountSettings = new AccountSettings(Touchid.this);
        accountSettings.SetWhiteLabelCB(new AccountSettings.GetWhiteLabelCB() {
            @Override
            public void getWhiteLabelSuccessCB(List<WhiteLabelResponse> whiteLabelResponses) {
                if(whiteLabelResponses != null && whiteLabelResponses.size() > 0){
                    mWhiteLabelResponses = whiteLabelResponses;
                }
            }

            @Override
            public void getWhiteLabelFailureCB(String message) {

            }
        });

        accountSettings.getWhiteLabelProperties();
    }

    private void updateLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(this);
        accountSettings.updateLocalAuthEnableStatus(String.valueOf(Constants.Local_Auth_Completed));
    }

    private void updateLocalAuthAndLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(this);
        accountSettings.updateLocalAuthEnableAndLoggedInStatus(String.valueOf(Constants.Local_Auth_Completed), String.valueOf(Constants.Local_Auth_Completed));
    }
    public void onBackPressed() { }
}

