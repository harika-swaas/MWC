package com.swaas.mwc.Login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.swaas.mwc.R;


/**
 * Created by barath on 6/24/2018.
 */

public class Touchid extends Notifiy {


    Button button2;
    TextView skip1;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enable_touch_id);
        skip1 = (TextView)findViewById(R.id.skip_button);
        button2 = (Button)findViewById(R.id.enable_touch_button);

        button2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent intent = new Intent(Touchid.this,Notifiy.class);
                startActivity(intent);
                checkCredentials();
            }
        });


        skip1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Touchid.this,Notifiy.class);
                startActivity(intent);

            }
        });
    }

}

