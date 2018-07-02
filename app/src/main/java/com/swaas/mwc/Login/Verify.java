package com.swaas.mwc.Login;

import android.app.Activity;
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
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;

import java.util.HashMap;
import java.util.Map;

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
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verifyPin();

                text = (TextView) findViewById(R.id.resend_pin);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(Verify.this);

                        dialog.setContentView(R.layout.resend_pin_alert);
                        dialog.setTitle("Custom Alert Dialog");

                        final TextView Text = (TextView) dialog.findViewById(R.id.Text);
                        final TextView Text1 = (TextView) dialog.findViewById(R.id.Text1);
                        Button btnallow = (Button) dialog.findViewById(R.id.cancel);
                        Button btnCancel = (Button) dialog.findViewById(R.id.save);
                        dialog.show();

                        btnallow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                        /*Intent intent = new Intent(Verify.this,Dashboard.class);
                        startActivity(intent);*/
                                dialog.dismiss();
                            }
                        });

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                    }

                });

            }
        });
    }

    public void verifyPin() {

        if (NetworkUtils.isNetworkAvailable(vActivity)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final VerifyPinService verifyPinService = retrofitAPI.create(VerifyPinService.class);

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
                        if (apiResponse.status.isCode() == false) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Toast.makeText(vActivity, mMessage, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Verify.this, Verify.class);
                            startActivity(intent);
                            vActivity.finish();
                        } else {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Toast.makeText(vActivity, mMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(vActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}



