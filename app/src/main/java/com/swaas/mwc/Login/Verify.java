package com.swaas.mwc.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.FTLProcessResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.VerifyPinRequest;
import com.swaas.mwc.API.Service.FTLProcessService;
import com.swaas.mwc.API.Service.SendPinService;
import com.swaas.mwc.API.Service.VerifyPinService;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by barath on 6/24/2018.
 */

public class Verify extends Activity {
    Verify vActivity;
    Button button3;
    TextView text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify);
        button3 = (Button) findViewById(R.id.verify_button);
        text = (TextView) findViewById(R.id.resend_pin);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verifyPin();

            }
        });

                text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog dialog = new Dialog(Verify.this);
                    dialog.setContentView(R.layout.message_pin_resent);
                    dialog.setTitle("Custom Alert Dialog");
                    final Timer timer2 = new Timer();
                    timer2.schedule(new TimerTask() {
                        public void run() {
                            dialog.dismiss();
                            timer2.cancel(); //this will cancel the timer of the system
                        }
                    }, 1000);


                    final TextView Text = (TextView) dialog.findViewById(R.id.Text1);

                }

            });

    }

    public void verifyPin() {

        if (NetworkUtils.isNetworkAvailable(vActivity)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final VerifyPinService verifyPinService = retrofitAPI.create(VerifyPinService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(vActivity);
            transparentProgressDialog.show();

            String request = new Gson().toJson(verifyPinService);
            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = verifyPinService.getVerifyPin(params, PreferenceUtils.getAccessToken(vActivity));

            call.enqueue(new Callback<BaseApiResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<LoginResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        /*if (apiResponse.status.isCode() == false) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Toast.makeText(vActivity, mMessage, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Intent intent = new Intent(Verify.this, Touchid.class);
                            startActivity(intent);
                        } else {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Toast.makeText(vActivity, mMessage, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }*/
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(vActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                    transparentProgressDialog.dismiss();
                }
            });
        }
    }
}



