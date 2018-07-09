package com.swaas.mwc.Login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Utils.Constants;


/**
 * Created by barath on 6/24/2018.
 */

public class Touchid extends Authenticate {

    Button button2;
    TextView skip1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enable_touch_id);
        skip1 = (TextView) findViewById(R.id.skip_button);
        button2 = (Button) findViewById(R.id.enable_touch_button);

        setButtonBackgroundColor();

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent(Touchid.this, Notifiy.class);
                startActivity(intent);
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
            }
        });
    }

    private void setButtonBackgroundColor() {

        String mobileItemEnableColor = PreferenceUtils.getMobileItemEnableColor(this);
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
            /*// Initialize a new GradientDrawable
            GradientDrawable shape = new GradientDrawable();

            // Specify the shape of drawable
            shape.setShape(GradientDrawable.RECTANGLE);

            // Make the border rounded
            shape.setCornerRadius(50f);

            // Set the fill color of drawable
            shape.setColor(itemDisableColor);

            button2.setBackgroundDrawable(shape);*/
        }
    }

    private void updateLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(this);
        accountSettings.updateLocalAuthEnableStatus(String.valueOf(Constants.Local_Auth_Completed));
    }

    private void updateLocalAuthAndLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(this);
        accountSettings.updateLocalAuthEnableAndLoggedInStatus(String.valueOf(Constants.Local_Auth_Completed), String.valueOf(Constants.Local_Auth_Completed));
    }
}

