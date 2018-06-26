package com.swaas.mwc.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.VerifyFTLRequest;
import com.swaas.mwc.API.Model.VerifyFTLResponse;
import com.swaas.mwc.API.Service.SendFTLPINService;
import com.swaas.mwc.API.Service.VerifyFTLDetailsService;
import com.swaas.mwc.FTL.FTLPinVerificationActivity;
import com.swaas.mwc.FTL.FTLRegistrationActivity;
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

public class FTLRegistrationFragment extends Fragment {

    FTLRegistrationActivity mActivity;
    View mView;
    Button mNext;
    EditText inputEmail;
    EditText inputMobile;
    TextInputLayout inputLayoutEmail, inputLayoutMobile;

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
    }

    private void addListenersToViews() {
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
                inputMobile.addTextChangedListener(new MyTextWatcher(inputMobile));

                verifyFTLDetails();
                verifyFTLDetailsWithEmail();
            }
        });
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

        if (mobile.isEmpty() || mobile.length() > 10) {
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

        String email = inputEmail.getText().toString().trim();
        if (validateEmail()) {
            if (NetworkUtils.isNetworkAvailable(mActivity)) {
                /*final ProgressDialog mProgressDialog = new ProgressDialog(mActivity);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.show();*/
                final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
                dialog.show();
                Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
                final VerifyFTLDetailsService verifyFTLDetailsService = retrofitAPI.create(VerifyFTLDetailsService.class);
                VerifyFTLRequest mVerifyFTLRequest = new VerifyFTLRequest();
                mVerifyFTLRequest.setEmail(email);
                Call call = verifyFTLDetailsService.getVerifyFTLDetails(mVerifyFTLRequest);
                call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                    @Override
                    public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                        BaseApiResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.status.isCode() == false) {
                                String mMessage = apiResponse.status.getMessage().toString();
                                VerifyFTLResponse mVerifyFTLResponse = response.body().getData();

                                dialog.dismiss();
                                Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();

                                if (mVerifyFTLResponse != null) {
                                    if (mVerifyFTLResponse.isCheck_mobile() == true) {
                                        inputLayoutMobile.setVisibility(View.VISIBLE);
                                        verifyFTLDetailsWithMobile();
                                    } else {
                                        inputLayoutMobile.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                String mMessage = apiResponse.status.getMessage().toString();
                                dialog.dismiss();
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

    private void verifyFTLDetailsWithMobile() {

        String email = inputEmail.getText().toString().trim();
        String mobile = inputMobile.getText().toString().trim();
        if (validateEmail() && validateMobile()) {
            if (NetworkUtils.isNetworkAvailable(mActivity)) {
                /*final ProgressDialog mProgressDialog = new ProgressDialog(mActivity);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.show();*/
                final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
                dialog.show();
                Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
                final VerifyFTLDetailsService verifyFTLDetailsService = retrofitAPI.create(VerifyFTLDetailsService.class);
                VerifyFTLRequest mVerifyFTLRequest = new VerifyFTLRequest();
                mVerifyFTLRequest.setEmail(email);
                mVerifyFTLRequest.setMobile(Integer.parseInt(mobile));
                Call call = verifyFTLDetailsService.getVerifyFTLDetails(mVerifyFTLRequest);
                call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                    @Override
                    public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                        BaseApiResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            if (apiResponse.status.isCode() == false) {
                                String mMessage = apiResponse.status.getMessage().toString();
                                VerifyFTLResponse mVerifyFTLResponse = response.body().getData();

                                dialog.dismiss();
                                Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();

                                if (mVerifyFTLResponse != null) {
                                    MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(mActivity)
                                            .setTitle("Pin verification")
                                            .setDescription(getString(R.string.pin_verification_dialog_msg))
                                           // .setStyle(Style.HEADER_WITH_ICON)
                                            .setStyle(Style.HEADER_WITH_TITLE)
                                            .setPositiveText(R.string.send_pin)
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
                            } else {
                                String mMessage = apiResponse.status.getMessage().toString();
                                dialog.dismiss();
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

    private void sendFTLPin() {

        final String email = inputEmail.getText().toString().trim();
        final String mobile = inputMobile.getText().toString().trim();
        if(NetworkUtils.isNetworkAvailable(mActivity)){
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final SendFTLPINService sendFTLPINService = retrofitAPI.create(SendFTLPINService.class);
            VerifyFTLRequest mVerifyFTLRequest = new VerifyFTLRequest();
            mVerifyFTLRequest.setEmail(email);
            mVerifyFTLRequest.setMobile(Integer.parseInt(mobile));
            Call call = sendFTLPINService.getFTLPIN(mVerifyFTLRequest);
            call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status.isCode() == false) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                        }

                        Intent mIntent = new Intent(mActivity, FTLPinVerificationActivity.class);
                        mIntent.putExtra(Constants.EMAIL, email);
                        mIntent.putExtra(Constants.MOBILE, mobile);
                        startActivity(mIntent);
                        mActivity.finish();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
