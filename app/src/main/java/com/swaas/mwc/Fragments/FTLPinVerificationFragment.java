package com.swaas.mwc.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.gson.Gson;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.FTLPINResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.SendPinRequest;
import com.swaas.mwc.API.Model.VerifyFTLPINRequest;
import com.swaas.mwc.API.Model.VerifyFTLRequest;
import com.swaas.mwc.API.Model.VerifyFTLResponse;
import com.swaas.mwc.API.Model.VerifyPinRequest;
import com.swaas.mwc.API.Service.SendFTLPINService;
import com.swaas.mwc.API.Service.SendPinService;
import com.swaas.mwc.API.Service.VerifyFTLPINService;
import com.swaas.mwc.API.Service.VerifyPinService;
import com.swaas.mwc.Components.LinkTextView;
import com.swaas.mwc.FTL.FTLPinVerificationActivity;
import com.swaas.mwc.FTL.FTLUserValidationActivity;
import com.swaas.mwc.Login.Authenticate;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Login.Verify;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLPinVerificationFragment extends Fragment {

    FTLPinVerificationActivity mActivity;
    View mView;
    Button mNext;
    TextInputLayout inputLayoutPINNumber;
    EditText inputPIN;
    String mEmail,mMobile;
    Long mobileNo;
    LinkTextView linkResendView;
    boolean isFromLogin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FTLPinVerificationActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ftl_pin_verification_fragment, container, false);

        intializeViews();
        getIntentData();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        linkResendView = (LinkTextView) mView.findViewById(R.id.resend_pin);
        inputLayoutPINNumber = (TextInputLayout) mView.findViewById(R.id.input_layout_pin_number);
        inputPIN = (EditText) mView.findViewById(R.id.input_pin_number);
        mNext = (Button) mView.findViewById(R.id.next_button);
    }

    private void getIntentData() {

        if(mActivity.getIntent() != null){
            mEmail = mActivity.getIntent().getStringExtra(Constants.EMAIL);
            mMobile = mActivity.getIntent().getStringExtra(Constants.MOBILE);
            isFromLogin = mActivity.getIntent().getBooleanExtra(Constants.IS_FROM_LOGIN,false);
            if(mMobile != null) {
                mobileNo = Long.parseLong(mMobile);
            }
        }
    }

    private void addListenersToViews() {
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   startActivity(new Intent(mActivity,FTLUserValidationActivity.class));
              //  mActivity.finish();

                inputPIN.addTextChangedListener(new FTLPinVerificationFragment.MyTextWatcher(inputPIN));

                verifyFTLPINDetails();
                verifyPinProcess();
            }
        });

        linkResendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFromLogin){
                    sendPin();
                } else {
                    sendFTLPin();
                }
            }
        });
    }
    private void verifyFTLPINDetails() {

        if (!validatePIN()) {
            return;
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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


    private void verifyPinProcess() {

        if(validatePIN()) {
            if(isFromLogin) {
                verifyPINInLoginProcess();
            } else {
                verifyPINInFTLProcess();
            }
        }
    }

    private void verifyPINInFTLProcess() {

        String pinNo = inputPIN.getText().toString().trim();

        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
            dialog.show();
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            VerifyFTLPINService verifyFTLPINService = retrofitAPI.create(VerifyFTLPINService.class);

            VerifyFTLPINRequest mVerifyFTLPINRequest = null;

            if (mobileNo != null) {
                mVerifyFTLPINRequest = new VerifyFTLPINRequest(mEmail, null, Integer.parseInt(pinNo));
            } else {
                mVerifyFTLPINRequest = new VerifyFTLPINRequest(mEmail, mobileNo, Integer.parseInt(pinNo));
            }

            String request = new Gson().toJson(mVerifyFTLPINRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = verifyFTLPINService.getVerifyFTLPIN(params);

            call.enqueue(new Callback<BaseApiResponse<FTLPINResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<FTLPINResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status.isCode() == false) {
                            dialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();
                            //   Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                            FTLPINResponse mFTLPINResponse = response.body().getData();
                            if (mFTLPINResponse != null) {
                                String accessToken = mFTLPINResponse.getAccessToken();
                                PreferenceUtils.setAccessToken(mActivity, accessToken);

                                if (mFTLPINResponse.nextStep != null) {
                                    if (mFTLPINResponse.nextStep.isFtl_required() == true) {
                                        Intent mIntent = new Intent(mActivity, FTLUserValidationActivity.class);
                                        mIntent.putExtra(Constants.ACCESSTOKEN, accessToken);
                                        startActivity(mIntent);
                                        mActivity.finish();
                                    }
                                }
                            }
                        } else {
                            dialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            FTLPINResponse mFTLPINResponse = response.body().getData();
                            if (mFTLPINResponse != null) {
                                if (mFTLPINResponse.isRequest_pin() == true) {
                                    MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(mActivity)
                                            .setTitle("PinVerificationActivity verification")
                                            .setDescription(mMessage)
                                            // .setStyle(Style.HEADER_WITH_ICON)
                                            .setStyle(Style.HEADER_WITH_TITLE)
                                            .setPositiveText(R.string.send_again_pin)
                                            .setNegativeText(R.string.cancel)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                    sendFTLPin();
                                                }
                                            })
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .build();
                                    dialog.show();
                                } else if (mFTLPINResponse.isFtl_complete() == true) {
                                    // Showing Alert Dialog
                                    AlertDialog mDialog = new AlertDialog.Builder(mActivity).create();
                                    // Setting Dialog Title
                                    mDialog.setTitle("Alert");
                                    // Setting Dialog Message
                                    mDialog.setMessage(mMessage);

                                    // Setting OK Button
                                    mDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Write your code here to execute after dialog closed
                                            startActivity(new Intent(mActivity, LoginActivity.class));
                                        }
                                    });

                                    // Showing Alert Message
                                    mDialog.show();
                                }
                            } else {
                                mActivity.showMessagebox(mActivity, mMessage, null, false);
                                //  Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    dialog.dismiss();
                }
            });
        }

    }

    private void verifyPINInLoginProcess() {

        String pinNo = inputPIN.getText().toString().trim();

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final VerifyPinService verifyPinService = retrofitAPI.create(VerifyPinService.class);

            final VerifyPinRequest mVerifyPinRequest = new VerifyPinRequest(Integer.parseInt(pinNo));

            String request = new Gson().toJson(mVerifyPinRequest);
            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = verifyPinService.getVerifyPin(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<BaseApiResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<LoginResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status.isCode() == false) {
                            String mMessage = apiResponse.status.getMessage().toString();
                           // Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mActivity, Authenticate.class);
                            startActivity(intent);
                            mActivity.finish();
                        } else {
                            String mMessage = apiResponse.status.getMessage().toString();
                            mActivity.showMessagebox(mActivity,mMessage,null,false);
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                   // Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendFTLPin() {

        if(NetworkUtils.isNetworkAvailable(mActivity)){
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final SendFTLPINService sendFTLPINService = retrofitAPI.create(SendFTLPINService.class);

            VerifyFTLRequest mVerifyFTLRequest = null;
            if(mobileNo != null){
                mVerifyFTLRequest = new VerifyFTLRequest(mEmail, mobileNo);
            } else {
                mVerifyFTLRequest = new VerifyFTLRequest(mEmail, null);
            }

            String request = new Gson().toJson(mVerifyFTLRequest);
            //Here the json data is add to a hash map with key data
            Map<String,String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = sendFTLPINService.getFTLPIN(params);

            call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status.isCode() == false) {
                            String mMessage = apiResponse.status.getMessage().toString();
                           // Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                            Intent mIntent = new Intent(mActivity,FTLUserValidationActivity.class);
                            startActivity(mIntent);
                            mActivity.finish();
                        } else {
                            String mMessage = apiResponse.status.getMessage().toString();
                            mActivity.showMessagebox(mActivity,mMessage,null,false);
                           // Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("PINVerErr",t.getMessage());
                }
            });
        }
    }

    private void sendPin() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final SendPinService sendPinService = retrofitAPI.create(SendPinService.class);

            SendPinRequest sendPinRequest = new SendPinRequest(PreferenceUtils.getUserPinDeviceId(mActivity));

            String request = new Gson().toJson(sendPinRequest);

            //Here the json data is add to a hash map with key data
            Map<String,String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = sendPinService.getSendPin(params,PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status.isCode() == false) {
                            // String mMessage = apiResponse.status.getMessage().toString();
                            //  Toast.makeText(pinActivity, mMessage, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mActivity,FTLPinVerificationActivity.class);
                            intent.putExtra(Constants.IS_FROM_LOGIN,true);
                            startActivity(intent);
                            mActivity.finish();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    // Toast.makeText(pinActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
