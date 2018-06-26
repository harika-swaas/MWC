package com.swaas.mwc.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.VerifyFTLPINRequest;
import com.swaas.mwc.API.Service.VerifyFTLPINService;
import com.swaas.mwc.FTL.FTLPinVerificationActivity;
import com.swaas.mwc.FTL.FTLUserValidationActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;

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

        if (pinNumber.isEmpty()) {
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
        if(NetworkUtils.isNetworkAvailable(mActivity)){
            final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
            dialog.show();
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            VerifyFTLPINService verifyFTLPINService = retrofitAPI.create(VerifyFTLPINService.class);
            VerifyFTLPINRequest mVerifyFTLPINRequest = new VerifyFTLPINRequest();
            mVerifyFTLPINRequest.setEmail(mEmail);
            mVerifyFTLPINRequest.setMobile(Integer.parseInt(mMobile));
            mVerifyFTLPINRequest.setPin(Integer.parseInt(pinNo));
            Call call = verifyFTLPINService.getVerifyFTLPIN(mVerifyFTLPINRequest);
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

                                Intent mIntent = new Intent(mActivity,FTLUserValidationActivity.class);
                                mIntent.putExtra(Constants.TEXT_BACKGROUND_COLOR,text_background_color);
                                mIntent.putExtra(Constants.TEXT_FOREGROUND_COLOR,text_foreground_color);
                                mIntent.putExtra(Constants.APP_BACKGROUND_COLOR,app_background_color);
                                startActivity(mIntent);
                                mActivity.finish();
                            }
                        } else {
                            dialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();
                            Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
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
}
