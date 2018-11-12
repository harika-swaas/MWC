package com.mwc.docportal.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.ApiResponse;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.ListPinDevices;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.SendPinRequest;
import com.mwc.docportal.API.Service.ListPinDevicesService;
import com.mwc.docportal.API.Service.SendPinService;
import com.mwc.docportal.Adapters.PinDeviceAdapter;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.FTL.FTLPinVerificationActivity;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Login.PinVerificationActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;

import java.security.PublicKey;
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
    public static final int REQUEST_STORAGE_PERMISSION = 111;
    public static final int REQUEST_READ_SMS_PERMISSION = 133;
    public static String deviceType = "";
    AlertDialog mAlertDialog;
    String selectedDeviceType = "";


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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int storagePermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (storagePermission == PackageManager.PERMISSION_GRANTED) {
                        if(!deviceType.isEmpty() && deviceType.equalsIgnoreCase("mobile"))
                        {
                            checkPermissionForReadSMS();
                        }
                        else
                        {
                            sendPin();
                        }

                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                    }
                } else {
                    sendPin();
                }

            }
        });

        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
    }

    private void checkPermissionForReadSMS()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int readSMSPersmission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_SMS);
            if (readSMSPersmission == PackageManager.PERMISSION_GRANTED) {
                    sendPin();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_SMS}, REQUEST_READ_SMS_PERMISSION);
            }
        } else {
            sendPin();
        }

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

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {
                            mListPinDevices = response.body().getData();
                            setAdapter(mListPinDevices);
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    loadingProgressDialog.dismiss();
                    CommonFunctions.showTimeoutAlert(mActivity);
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }

    private void setAdapter(List<ListPinDevices> mListPinDevices) {

        PinDeviceAdapter.AdapterInterface listener = new PinDeviceAdapter.AdapterInterface()
        {
            @Override
            public void onClick(String value)
            {
                selectedDeviceType = value;
            }
        };

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new PinDeviceAdapter(mActivity, mListPinDevices, listener);
        mRecyclerView.setAdapter(mAdapter); // set the Adapter to RecyclerView
    }

    private void sendPin() {

        if(selectedDeviceType.isEmpty())
        {
            showSelectOneDeviceAlertMessage();
            return;
        }

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

            call.enqueue(new Callback<BaseApiResponse>() {
                @Override
                public void onResponse(Response<BaseApiResponse> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        transparentProgressDialog.dismiss();
                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {
                            PinVerificationFragment.deviceType = "";
                            Intent intent = new Intent(mActivity, FTLPinVerificationActivity.class);
                            intent.putExtra(Constants.IS_FROM_LOGIN, true);
                            startActivity(intent);
                        }

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeoutAlert(mActivity);
                }
            });
        }
    }

    private void showSelectOneDeviceAlertMessage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText("Please select a device");

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setVisibility(View.GONE);

        okButton.setText("Ok");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendPin();
                } else {
                    Toast.makeText(mActivity, "Storage access permission denied", Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_READ_SMS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendPin();
                } else {
                    sendPin();
                    Toast.makeText(mActivity, "SMS read permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}
