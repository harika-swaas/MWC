package com.swaas.mwc.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.ListPinDevices;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.VerifyFTLRequest;
import com.swaas.mwc.API.Model.VerifyFTLResponse;
import com.swaas.mwc.API.Service.ListPinDevicesService;
import com.swaas.mwc.API.Service.SendFTLPINService;
import com.swaas.mwc.API.Service.SendPinService;
import com.swaas.mwc.FTL.FTLUserValidationActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by barath on 6/24/2018.
 */

public class Pin extends Activity {
    Button button1;
    RecyclerView recyclerView;
    Pin pinActivity;
    List<ListPinDevices> mListPinDevices;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_pincode_to);
        getPinDevice();

        button1 = (Button) findViewById(R.id.pin_button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendPin();
                /*Intent intent = new Intent(Pin.this,Verify.class);
                startActivity(intent);*/
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RadioAdapter customAdapter = new RadioAdapter(Pin.this, mListPinDevices);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView


    }


    private void getPinDevice() {

        if (NetworkUtils.isNetworkAvailable(this)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final ListPinDevicesService listPinDevicesService = retrofitAPI.create(ListPinDevicesService.class);
            Call call = listPinDevicesService.getPinDevices(PreferenceUtils.getAccessToken(this));
            call.enqueue(new Callback<BaseApiResponse<ListPinDevices>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<ListPinDevices>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    {
                        Log.d("Pindevice error", t.getMessage());
                    }
                }


            });
        }
    }

    public void sendPin() {

            if (NetworkUtils.isNetworkAvailable(pinActivity)) {
                Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
                final SendPinService sendPinService = retrofitAPI.create(SendPinService.class);

                final AlertDialog dialog = new SpotsDialog(pinActivity, R.style.Custom);
                dialog.show();

                String request = new Gson().toJson(sendPinService);
                //Here the json data is add to a hash map with key data
                Map<String,String> params = new HashMap<String, String>();
                params.put("data", request);

                Call call = sendPinService.getSendPin(params,PreferenceUtils.getAccessToken(this));

                call.enqueue(new Callback<BaseApiResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Response<BaseApiResponse<LoginResponse>> response, Retrofit retrofit) {
                        BaseApiResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            /*if (apiResponse.status.isCode() == false) {
                                dialog.dismiss();
                                String mMessage = apiResponse.status.getMessage().toString();
                                Toast.makeText(pinActivity, mMessage, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Pin.this,Verify.class);
                                startActivity(intent);
                                pinActivity.finish();
                            } else {
                                String mMessage = apiResponse.status.getMessage().toString();
                                Toast.makeText(pinActivity, mMessage, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }*/
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(pinActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        }

    }

