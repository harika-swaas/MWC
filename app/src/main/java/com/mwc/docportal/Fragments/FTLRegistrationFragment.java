package com.mwc.docportal.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.VerifyFTLRequest;
import com.mwc.docportal.API.Model.VerifyFTLRequestWithEMail;
import com.mwc.docportal.API.Model.VerifyFTLResponse;
import com.mwc.docportal.API.Service.SendFTLPINService;
import com.mwc.docportal.API.Service.VerifyFTLDetailsService;
import com.mwc.docportal.Common.CommonFunctions;

import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.FTL.FTLPinVerificationActivity;
import com.mwc.docportal.FTL.FTLRegistrationActivity;

import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by harika on 21-06-2018.
 */

public class FTLRegistrationFragment extends Fragment {

    FTLRegistrationActivity mActivity;
    View mView;
    Button mNext;
    EditText inputEmail;
    EditText inputMobile;
    ImageView mBackIv;
    TextInputLayout inputLayoutEmail, inputLayoutMobile;
    AlertDialog mAlertDialog;
    AlertDialog mCustomAlertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FTLRegistrationActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ftl_registration_fragment, container, false);

        intializeViews();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        mNext = (Button) mView.findViewById(R.id.next_button);
        inputEmail = (EditText) mView.findViewById(R.id.input_email);
        inputMobile = (EditText) mView.findViewById(R.id.input_mobile);
        inputLayoutEmail = (TextInputLayout) mView.findViewById(R.id.input_layout_email);
        inputLayoutMobile = (TextInputLayout) mView.findViewById(R.id.input_layout_mobile);
        mBackIv = (ImageView) mView.findViewById(R.id.back_image_view);
    }

    private void addListenersToViews() {

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
                inputMobile.addTextChangedListener(new MyTextWatcher(inputMobile));

                verifyFTLDetails();
                if(inputLayoutEmail.getVisibility() == View.VISIBLE && inputLayoutMobile.getVisibility() == View.GONE) {
                    verifyFTLDetailsWithEmail();
                } else if(inputLayoutEmail.getVisibility() == View.VISIBLE && inputLayoutMobile.getVisibility() == View.VISIBLE){
                    verifyFTLDetailsWithMobile();
                }
            }
        });

        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });

        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        inputMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void verifyFTLDetails() {

        if (!validateEmail()) {
            return;
        }

        if (!validateMobile()) {
            return;
        }
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMobile() {
        String mobile = inputMobile.getText().toString().trim();

        if (mobile.isEmpty() || mobile.length() > 11) {
            inputLayoutMobile.setError(getString(R.string.err_msg_mobile));
            requestFocus(inputMobile);
            return false;
        } else {
            inputLayoutMobile.setErrorEnabled(false);
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
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_mobile:
                    validateMobile();
                    break;
            }
        }
    }

    private void verifyFTLDetailsWithEmail() {

        final String email = inputEmail.getText().toString().trim();
        if (validateEmail()) {
            if (NetworkUtils.isNetworkAvailable(mActivity)) {

                /*final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setLayout(600, 400);
                dialog.show();*/

                final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
                transparentProgressDialog.show();

                Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                final VerifyFTLDetailsService verifyFTLDetailsService = retrofitAPI.create(VerifyFTLDetailsService.class);

                VerifyFTLRequestWithEMail mVerifyFTLRequest = new VerifyFTLRequestWithEMail(email);
                String request = new Gson().toJson(mVerifyFTLRequest);
                //Here the json data is add to a hash map with key data
                Map<String, String> params = new HashMap<String, String>();
                params.put("data", request);

                Call call = verifyFTLDetailsService.getVerifyFTLDetailsWithEmail(params);

                call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                    @Override
                    public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                        BaseApiResponse apiResponse = response.body();
                        if (apiResponse != null) {

                            transparentProgressDialog.dismiss();
                            String message = "";

                            if(apiResponse.status.getMessage() != null)
                            {
                                message = apiResponse.status.getMessage().toString();
                            }

                            if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {

                                VerifyFTLResponse mVerifyFTLResponse = response.body().getData();

                                if (mVerifyFTLResponse != null) {
                                    if (mVerifyFTLResponse.isCheck_mobile() == true) {
                                        String mMessage = apiResponse.status.getMessage().toString();
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                        builder.setView(view);
                                        builder.setCancelable(false);

                                        TextView txtTitle = (TextView) view.findViewById(R.id.title);
                                        txtTitle.setText("Alert");

                                        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);
                                        txtMessage.setText(mMessage);

                                        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                        cancelButton.setVisibility(View.GONE);
                                        sendPinButton.setText("Ok");

                                        sendPinButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mCustomAlertDialog.dismiss();
                                                inputLayoutMobile.setVisibility(View.VISIBLE);
                                                // verifyFTLDetailsWithMobile();
                                            }
                                        });

                                        mCustomAlertDialog = builder.create();
                                        mCustomAlertDialog.show();

                                    } else {
                                        inputLayoutMobile.setVisibility(View.GONE);
                                    }
                                } else {

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                    LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                    builder.setView(view);
                                    builder.setCancelable(false);

                                    TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                                    txtMessage.setText(getString(R.string.pin_verification_dialog_msg));

                                    Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                    Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                    sendPinButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mAlertDialog.dismiss();
                                            sendFTLPin(email);
                                        }
                                    });

                                    cancelButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mAlertDialog.dismiss();
                                        }
                                    });

                                    mAlertDialog = builder.create();
                                    mAlertDialog.show();
                                }


                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        transparentProgressDialog.dismiss();
                        CommonFunctions.retrofitBadGatewayFailure(mActivity, t);
                    }
                });
            }
        }
    }

    private void verifyFTLDetailsWithMobile() {

        final String email = inputEmail.getText().toString().trim();
        final String mobile = inputMobile.getText().toString().trim();

        inputEmail.setEnabled(false);

        if (validateEmail() && validateMobile()) {
            if (NetworkUtils.isNetworkAvailable(mActivity)) {

                final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
                transparentProgressDialog.show();

                Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                final VerifyFTLDetailsService verifyFTLDetailsService = retrofitAPI.create(VerifyFTLDetailsService.class);

                VerifyFTLRequest mVerifyFTLRequest = new VerifyFTLRequest(email, mobile);
                String request = new Gson().toJson(mVerifyFTLRequest);

                //Here the json data is add to a hash map with key data
                Map<String, String> params = new HashMap<String, String>();
                params.put("data", request);

                Call call = verifyFTLDetailsService.getVerifyFTLDetails(params);

                call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                    @Override
                    public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                        BaseApiResponse apiResponse = response.body();

                        if (apiResponse != null) {
                            transparentProgressDialog.dismiss();

                            String message = "";
                            if(apiResponse.status.getMessage() != null)
                            {
                                message = apiResponse.status.getMessage().toString();
                            }

                            if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {

                                VerifyFTLResponse mVerifyFTLResponse = response.body().getData();

                                if (mVerifyFTLResponse != null) {

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                    LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                    builder.setView(view);
                                    builder.setCancelable(false);

                                    TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                                    txtMessage.setText(getString(R.string.pin_verification_dialog_msg));

                                    Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                    Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                    sendPinButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mAlertDialog.dismiss();
                                            sendFTLPinWithMobile(email, mobile);
                                        }
                                    });

                                    cancelButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mAlertDialog.dismiss();
                                        }
                                    });

                                    mAlertDialog = builder.create();
                                    mAlertDialog.show();

                                } else {

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                    LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                    builder.setView(view);
                                    builder.setCancelable(false);

                                    TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                                    txtMessage.setText(getString(R.string.pin_verification_dialog_msg));

                                    Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                    Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                    sendPinButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mAlertDialog.dismiss();
                                            sendFTLPinWithMobile(email, mobile);
                                        }
                                    });

                                    cancelButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mAlertDialog.dismiss();
                                        }
                                    });

                                    mAlertDialog = builder.create();
                                    mAlertDialog.show();
                                }


                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        transparentProgressDialog.dismiss();
                        CommonFunctions.retrofitBadGatewayFailure(mActivity, t);
                    }
                });
            }
        }
    }

    private void sendFTLPin(final String email) {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            /*final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();*/

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final SendFTLPINService sendFTLPINService = retrofitAPI.create(SendFTLPINService.class);

            VerifyFTLRequest mVerifyFTLRequest = new VerifyFTLRequest(email, null);

            String request = new Gson().toJson(mVerifyFTLRequest);
            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = sendFTLPINService.getFTLPIN(params);

            call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();
                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode()))
                        {
                            Intent mIntent = new Intent(mActivity, FTLPinVerificationActivity.class);
                            mIntent.putExtra(Constants.EMAIL, email);
                            startActivity(mIntent);
                            mActivity.finish();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.retrofitBadGatewayFailure(mActivity, t);
                }
            });
        }
    }

    private void sendFTLPinWithMobile(final String email, final String mobile) {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final SendFTLPINService sendFTLPINService = retrofitAPI.create(SendFTLPINService.class);

            VerifyFTLRequest mVerifyFTLRequest = new VerifyFTLRequest(email, mobile);

            String request = new Gson().toJson(mVerifyFTLRequest);
            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = sendFTLPINService.getFTLPIN(params);
            call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        transparentProgressDialog.dismiss();

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {
                            Intent mIntent = new Intent(mActivity, FTLPinVerificationActivity.class);
                            mIntent.putExtra(Constants.EMAIL, email);
                            mIntent.putExtra(Constants.MOBILE, mobile);
                            startActivity(mIntent);
                            mActivity.finish();
                        }

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.retrofitBadGatewayFailure(mActivity, t);
                }
            });
        }
    }


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
