package com.mwc.docportal.ForgotPassword;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.VerifyPinRequest;
import com.mwc.docportal.API.Service.SendPinService;
import com.mwc.docportal.API.Service.VerifyPinService;
import com.mwc.docportal.BuildConfig;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.MySMSBroadcastReceiver;
import com.mwc.docportal.Components.LinkTextView;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ForgetPasswordPinVerificationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MyForgotPasswordBroadCastReceiver.OTPMessageReceiveListener {

    TextInputLayout inputLayoutPINNumber;
    static EditText inputPIN;
    LinkTextView linkResendView;
    ImageView mBackIv;
    AlertDialog mAlertDialog;
    TextView pin_verification_txt, pin_verification_title;
    Button mNext;
    Context context = this;
    String accessToken, userPinDeviceId;

    // SMS Retriever api purpos
   // GoogleApiClient googleApiClient;
    MyForgotPasswordBroadCastReceiver smsBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftl_pin_verification_fragment);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        getIntentData();
        intializeViews();
        onClickListeners();


       /* googleApiClient = new GoogleApiClient.Builder(ForgetPasswordPinVerificationActivity.this)
                .addConnectionCallbacks(this)
                .enableAutoManage(ForgetPasswordPinVerificationActivity.this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();*/

        callToSMSRetriveAPI();


    }

    private void getIntentData()
    {
        if(getIntent().getStringExtra("AccessToken") != null)
        {
            accessToken = getIntent().getStringExtra("AccessToken");
        }
        if(getIntent().getStringExtra("pinDeviceId") != null)
        {
            userPinDeviceId = getIntent().getStringExtra("pinDeviceId");
        }


    }

    private void onClickListeners()
    {
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPIN.addTextChangedListener(new MyTextWatcher(inputPIN));
                verifyPinProcess();
            }
        });

        linkResendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* String message = "com.google.android.gms.auth.api.phone.SMS_RETRIEVED";
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(message);
                LocalBroadcastManager.getInstance(context).registerReceiver(smsBroadcast, intentFilter);*/
                callToSMSRetriveAPI();

                sendPin();

            }
        });

        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 finish();
            }
        });

        inputPIN.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    private void sendPin()
    {
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

            Call call = sendPinService.getSendPin(params,accessToken);

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

                        if(CommonFunctions.isApiSuccess(ForgetPasswordPinVerificationActivity.this, message, apiResponse.status.getCode())) {
                            showPINSentSuccessMessage();
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


    private void verifyPinProcess() {

        if (validatePIN()) {
             verifyForgetPasswordPINProcess();
        }
    }

    private void verifyForgetPasswordPINProcess()
    {
        String pinNo = inputPIN.getText().toString().trim();

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final VerifyPinService verifyPinService = retrofitAPI.create(VerifyPinService.class);

            final VerifyPinRequest mVerifyPinRequest = new VerifyPinRequest(Long.parseLong(pinNo), 1);


            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            String request = new Gson().toJson(mVerifyPinRequest);
            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = verifyPinService.getForgetPasswordVerifyPin(params, accessToken);

            call.enqueue(new Callback<BaseApiResponse<LoginResponse>>() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onResponse(Response<BaseApiResponse<LoginResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {
                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(ForgetPasswordPinVerificationActivity.this, message, apiResponse.status.getCode())) {
                            Intent intent = new Intent(context, NewPasswordActivity.class);
                            intent.putExtra("AccessToken", accessToken);
                            startActivity(intent);
                            inputPIN.setText("");
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


    private void intializeViews() {
        linkResendView = (LinkTextView) findViewById(R.id.resend_pin);
        inputLayoutPINNumber = (TextInputLayout) findViewById(R.id.input_layout_pin_number);
        inputPIN = (EditText) findViewById(R.id.input_pin_number);
        mNext = (Button) findViewById(R.id.next_button);
        mBackIv = (ImageView) findViewById(R.id.back_image_view);
        pin_verification_txt = (TextView) findViewById(R.id.pin_verification_txt);
        pin_verification_title = (TextView) findViewById(R.id.pin_verification_title);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onOTPMessageReceived(String otp) {

        try {
            if(smsBroadcast != null)
            {
                LocalBroadcastManager.getInstance(ForgetPasswordPinVerificationActivity.this).unregisterReceiver(smsBroadcast);
            }
        } catch (Exception e){
            // already unregistered
        }

        inputPIN.setText(otp);

    }

    @Override
    public void onOTPMessageTimeOut() {

    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_pin_number:
                    validatePIN();
                    break;
            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void callToSMSRetriveAPI()
    {
        smsBroadcast = new MyForgotPasswordBroadCastReceiver();
        smsBroadcast.setOTPListener(this);

        String message = "com.google.android.gms.auth.api.phone.SMS_RETRIEVED";
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(message);
        this.registerReceiver(smsBroadcast, intentFilter);


        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                //    Toast.makeText(mActivity, "SMS Retriever starts", Toast.LENGTH_LONG).show();

            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                //     Toast.makeText(mActivity, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if(smsBroadcast != null)
            {
                unregisterReceiver(smsBroadcast);
            }
        } catch (Exception e){
            // already unregistered
        }
    }

    private boolean validatePIN() {
        String pinNumber = inputPIN.getText().toString().trim();

        if (pinNumber.isEmpty() && pinNumber.length() == 0) {
            inputLayoutPINNumber.setError(getString(R.string.err_msg_pin_number));
            requestFocus(inputPIN);
            return false;
        } else {
            inputLayoutPINNumber.setErrorEnabled(false);
        }

        return true;
    }

    private void showPINSentSuccessMessage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText("PIN sent");

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
}
