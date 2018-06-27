package com.swaas.mwc.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.VerifyFTLPINRequest;
import com.swaas.mwc.API.Model.VerifyFTLRequest;
import com.swaas.mwc.API.Model.VerifyFTLResponse;
import com.swaas.mwc.API.Service.SendFTLPINService;
import com.swaas.mwc.API.Service.VerifyFTLPINService;
import com.swaas.mwc.FTL.FTLPinVerificationActivity;
import com.swaas.mwc.FTL.FTLUserValidationActivity;
import com.swaas.mwc.Network.NetworkUtils;
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

        inputLayoutPINNumber = (TextInputLayout) mView.findViewById(R.id.input_layout_pin_number);
        inputPIN = (EditText) mView.findViewById(R.id.input_pin_number);
        mNext = (Button) mView.findViewById(R.id.next_button);
    }

    private void getIntentData() {

        if(mActivity.getIntent() != null){
            mEmail = mActivity.getIntent().getStringExtra(Constants.EMAIL);
            mMobile = mActivity.getIntent().getStringExtra(Constants.MOBILE);
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
                verifyPINInFTLProcess();
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
            inputLayoutPINNumber.setError(getString(R.string.err_msg_mobile));
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


    private void verifyPINInFTLProcess() {
        String pinNo = inputPIN.getText().toString().trim();


        int mPin = Integer.parseInt(pinNo);

        if(NetworkUtils.isNetworkAvailable(mActivity)){
            final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
            dialog.show();
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            VerifyFTLPINService verifyFTLPINService = retrofitAPI.create(VerifyFTLPINService.class);

            VerifyFTLPINRequest mVerifyFTLPINRequest = null;

            if(mobileNo != null){
                mVerifyFTLPINRequest = new VerifyFTLPINRequest(mEmail, null, mPin);
            } else {
                mVerifyFTLPINRequest = new VerifyFTLPINRequest(mEmail, mobileNo, mPin);
            }

            String request = new Gson().toJson(mVerifyFTLPINRequest);

            //Here the json data is add to a hash map with key data
            Map<String,String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = verifyFTLPINService.getVerifyFTLPIN(params);

            call.enqueue(new Callback<BaseApiResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<LoginResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status.isCode() == false) {
                            dialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();
                            Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                            LoginResponse mLoginResponse = response.body().getData();
                            if(mLoginResponse != null){
                                String text_background_color = mLoginResponse.getText_background_color();
                                String text_foreground_color = mLoginResponse.getText_foreground_color();
                                String app_background_color = mLoginResponse.getApp_background_color();

                                if(mLoginResponse.nextStep != null){
                                    if(mLoginResponse.nextStep.isFtl_required() == true){
                                        Intent mIntent = new Intent(mActivity,FTLUserValidationActivity.class);
                                        mIntent.putExtra(Constants.TEXT_BACKGROUND_COLOR,text_background_color);
                                        mIntent.putExtra(Constants.TEXT_FOREGROUND_COLOR,text_foreground_color);
                                        mIntent.putExtra(Constants.APP_BACKGROUND_COLOR,app_background_color);
                                        startActivity(mIntent);
                                        mActivity.finish();
                                    }
                                }
                            }
                        } else {
                            dialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();
                            Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                            LoginResponse mLoginResponse = response.body().getData();
                            if(mLoginResponse != null) {
                                if (mLoginResponse.isRequest_pin() == true) {
                                    MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(mActivity)
                                            .setTitle("Pin verification")
                                            .setDescription(getString(R.string.pin_verification_dialog_msg))
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
                                }
                            }
                           // Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
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
                mVerifyFTLRequest = new VerifyFTLRequest(mEmail, null);
            } else {
                mVerifyFTLRequest = new VerifyFTLRequest(mEmail, mobileNo);
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
                            Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                            Intent mIntent = new Intent(mActivity,FTLUserValidationActivity.class);
                            startActivity(mIntent);
                            mActivity.finish();
                        } else {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
