package com.mwc.docportal.ForgotPassword;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.ListPinDevices;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.PasswordResetModel.PinDeviceRequestModel;
import com.mwc.docportal.API.Model.SendPinRequest;
import com.mwc.docportal.API.Service.ListPinDevicesService;
import com.mwc.docportal.API.Service.SendPinService;
import com.mwc.docportal.Adapters.PassWordResetPinListAdapter;
import com.mwc.docportal.BuildConfig;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PinDeviceListActivity extends AppCompatActivity
{
    RecyclerView mRecyclerView;
    PassWordResetPinListAdapter mAdapter;
    Context context = this;
    Button next;
    ImageView mBackIv;
    String accessToken;
    String selectedDeviceType = "";
    String userPinDeviceId = "";
    AlertDialog mAlertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_pincode_to_activity);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        getIntentData();
        intializeViews();
        getPinDevice();
        onClickListeners();

    }

    private void onClickListeners()
    {
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    sendPin();
            }
        });
    }

    private void getIntentData()
    {
        if(getIntent().getStringExtra("AccessToken") != null)
        {
            accessToken = getIntent().getStringExtra("AccessToken");
        }
    }

    private void intializeViews() {

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        next = (Button) findViewById(R.id.next_button);
        mBackIv=(ImageView) findViewById(R.id.back_image_view);
    }

    private void getPinDevice() {

        if (NetworkUtils.isNetworkAvailable(context)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final LoadingProgressDialog loadingProgressDialog = new LoadingProgressDialog(context);
            loadingProgressDialog.show();

            final PinDeviceRequestModel pinDeviceRequestModel = new PinDeviceRequestModel(1);

            String request = new Gson().toJson(pinDeviceRequestModel);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final ListPinDevicesService listPinDevicesService = retrofitAPI.create(ListPinDevicesService.class);
            Call call = listPinDevicesService.getPinDevicesList(params, accessToken);
            call.enqueue(new Callback<ListPinDevicesResponse<ListPinDevices>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<ListPinDevices>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    loadingProgressDialog.dismiss();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(PinDeviceListActivity.this, message, apiResponse.status.getCode())) {
                            List<ListPinDevices> mListPinDevices = response.body().getData();
                            setAdapter(mListPinDevices);
                        }
                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    loadingProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                }
            });
        }
    }

    private void setAdapter(List<ListPinDevices> mListPinDevices) {

        PassWordResetPinListAdapter.AdapterInterface listener = new PassWordResetPinListAdapter.AdapterInterface()
        {
            @Override
            public void onClick(String deviceType, String userDeviceId)
            {
                selectedDeviceType = deviceType;
                userPinDeviceId = userDeviceId;
            }
        };

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new PassWordResetPinListAdapter(PinDeviceListActivity.this, mListPinDevices, listener);
        mRecyclerView.setAdapter(mAdapter); // set the Adapter to RecyclerView
    }

    private void sendPin() {

        if(selectedDeviceType.isEmpty())
        {
            showSelectOneDeviceAlertMessage();
            return;
        }

        if (NetworkUtils.isNetworkAvailable(context)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final SendPinService sendPinService = retrofitAPI.create(SendPinService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            PinRequestModel sendPinRequest = new PinRequestModel(userPinDeviceId, true,1, String.valueOf(BuildConfig.VERSION_CODE));

            String request = new Gson().toJson(sendPinRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = sendPinService.getSendPin(params, accessToken);

            call.enqueue(new Callback<BaseApiResponse>() {
                @Override
                public void onResponse(Response<BaseApiResponse> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(PinDeviceListActivity.this, message, apiResponse.status.getCode())) {
                            Intent intent = new Intent(context, ForgetPasswordPinVerificationActivity.class);
                            intent.putExtra("pinDeviceId", userPinDeviceId);
                            intent.putExtra("AccessToken", accessToken);
                            startActivity(intent);

                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                }
            });
        }
    }

    private void showSelectOneDeviceAlertMessage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public void onBackPressed() {
        Intent intent = new Intent(context, UserNameActivity.class);
        startActivity(intent);
        finish();
    }


}
