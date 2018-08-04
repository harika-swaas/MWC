package com.swaas.mwc.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.ListPinDevices;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.SendPinRequest;
import com.swaas.mwc.API.Service.ListPinDevicesService;
import com.swaas.mwc.API.Service.SendPinService;
import com.swaas.mwc.Adapters.PinDeviceAdapter;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.FTL.FTLPinVerificationActivity;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Login.PinVerificationActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by harika on 02-07-2018.
 */

public class  PinVerificationFragment extends Fragment {

    PinVerificationActivity mActivity;
    View mView;
    RecyclerView mRecyclerView;
    PinDeviceAdapter mAdapter;
    Button next;
    ImageView mBackIv;
    List<ListPinDevices> mListPinDevices;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (PinVerificationActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.send_pincode_to_activity, container, false);

        intializeViews();
        getPinDevice();
        addListenersToViews();

        return mView;
    }

    private void intializeViews() {

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        next = (Button) mView.findViewById(R.id.next_button);
        mBackIv=(ImageView) mView.findViewById(R.id.back_image_view);
    }

    private void addListenersToViews() {

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendPin();
            }
        });

        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
    }

    private void getPinDevice() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final LoadingProgressDialog loadingProgressDialog = new LoadingProgressDialog(mActivity);
            loadingProgressDialog.show();
            final ListPinDevicesService listPinDevicesService = retrofitAPI.create(ListPinDevicesService.class);
            Call call = listPinDevicesService.getPinDevices(PreferenceUtils.getAccessToken(mActivity));
            call.enqueue(new Callback<ListPinDevicesResponse<ListPinDevices>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<ListPinDevices>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        loadingProgressDialog.dismiss();
                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                mListPinDevices = response.body().getData();
                                setAdapter(mListPinDevices);
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(mActivity, LoginActivity.class));
                                    }
                                }, false);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    loadingProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }

    private void setAdapter(List<ListPinDevices> mListPinDevices) {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new PinDeviceAdapter(mActivity, mListPinDevices);
        mRecyclerView.setAdapter(mAdapter); // set the Adapter to RecyclerView
    }

    private void sendPin() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final SendPinService sendPinService = retrofitAPI.create(SendPinService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            SendPinRequest sendPinRequest = new SendPinRequest(PreferenceUtils.getUserPinDeviceId(mActivity));

            String request = new Gson().toJson(sendPinRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = sendPinService.getSendPin(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() instanceof Boolean) {

                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                Intent intent = new Intent(mActivity, FTLPinVerificationActivity.class);
                                intent.putExtra(Constants.IS_FROM_LOGIN, true);
                                startActivity(intent);
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(mActivity, LoginActivity.class));
                                    }
                                }, false);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                }
            });
        }
    }

}
