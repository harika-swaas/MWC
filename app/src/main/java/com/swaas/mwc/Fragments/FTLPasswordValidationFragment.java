package com.swaas.mwc.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.UpdateFTLStatusRequest;
import com.swaas.mwc.API.Model.VerifyFTLRequestWithEMail;
import com.swaas.mwc.API.Model.VerifyFTLResponse;
import com.swaas.mwc.API.Service.UpdateFTLStatusService;
import com.swaas.mwc.API.Service.VerifyFTLDetailsService;
import com.swaas.mwc.FTL.FTLAgreeTermsAcceptanceActivity;
import com.swaas.mwc.FTL.FTLPasswordValidationActivity;
import com.swaas.mwc.FTL.FTLUserValidationActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLPasswordValidationFragment extends Fragment {

    FTLPasswordValidationActivity mActivity;
    View mView;
    Button mNext;
    String mUserName, mWelcomeMsg, mTerms;
    TextInputLayout inputLayoutPassword,inputLayoutConfirmPassword;
    EditText inputPassword,inputConfirmPassword;
    TextView welcomeMsg;
    String mAccessToken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FTLPasswordValidationActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ftl_password_validation_fragment, container, false);

        intializeViews();
        getIntentData();
        setWelcomeMsg();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        welcomeMsg = (TextView) mView.findViewById(R.id.welcome_msg);
        inputLayoutPassword = (TextInputLayout) mView.findViewById(R.id.input_layout_password);
        inputLayoutConfirmPassword = (TextInputLayout) mView.findViewById(R.id.input_layout_confirm_password);
        inputPassword = (EditText) mView.findViewById(R.id.input_password);
        inputConfirmPassword = (EditText) mView.findViewById(R.id.input_confirm_password);
        mNext = (Button) mView.findViewById(R.id.next_button);
    }

    private void getIntentData() {

        if(mActivity.getIntent() != null){
            mUserName = mActivity.getIntent().getStringExtra(Constants.USERNAME);
            mWelcomeMsg = mActivity.getIntent().getStringExtra(Constants.WELCOME_MSG);
            mAccessToken = mActivity.getIntent().getStringExtra(Constants.ACCESSTOKEN);
            mTerms = mActivity.getIntent().getStringExtra(Constants.SETTERMS);
        }
    }

    private void setWelcomeMsg() {

        if(mWelcomeMsg != null && !TextUtils.isEmpty(mWelcomeMsg)){
            welcomeMsg.setText(mWelcomeMsg);
        } else {
            welcomeMsg.setText(getString(R.string.welcome));
        }
    }

    private void addListenersToViews() {

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputPassword.addTextChangedListener(new FTLPasswordValidationFragment.MyTextWatcher(inputPassword));
                inputConfirmPassword.addTextChangedListener(new FTLPasswordValidationFragment.MyTextWatcher(inputConfirmPassword));

                verifyFTLPasswordDetails();
                updateFTLStatus();

               // startActivity(new Intent(mActivity,FTLAgreeTermsAcceptanceActivity.class));
               // mActivity.finish();
            }
        });
    }

    private void verifyFTLPasswordDetails() {

        if(!validatePassword()){
            return;
        }

        if(!validateConfirmPassword()){
            return;
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
                case R.id.input_password:
                    validatePassword();
                    break;

                case R.id.input_confirm_password:
                    validateConfirmPassword();
                    break;
            }
        }
    }

    private boolean validatePassword() {

        String password = inputPassword.getText().toString().trim();

        if (password.isEmpty() || password.length() < 8 && !isValidPassword(password)) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConfirmPassword() {

        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        if (confirmPassword.isEmpty() || !password.equals(confirmPassword)) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_confirm_password));
            requestFocus(inputConfirmPassword);
            return false;
        } else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
        }

        return true;
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\\\d)(?=.*[$@$!%*#?&])[A-Za-z\\\\d$@$!%*#?&]{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private void requestFocus(View view) {

        if (view.requestFocus()) {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void updateFTLStatus() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            if (validatePassword() && validateConfirmPassword()) {
                final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
                dialog.show();

                Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                final UpdateFTLStatusService updateFTLStatusService = retrofitAPI.create(UpdateFTLStatusService.class);

                UpdateFTLStatusRequest mUpdateFTLStatusRequest = new UpdateFTLStatusRequest(4, mUserName);
                String request = new Gson().toJson(mUpdateFTLStatusRequest);
                //Here the json data is add to a hash map with key data
                Map<String, String> params = new HashMap<String, String>();
                params.put("data", request);

                Call call = updateFTLStatusService.getUpdateFTLStatus(params, mAccessToken);

                call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                    @Override
                    public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                        BaseApiResponse apiResponse = response.body();
                        if (apiResponse != null) {
                            dialog.dismiss();
                            if (apiResponse.status.isCode() == false) {
                                String mMessage = apiResponse.status.getMessage().toString();
                              //  Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                                Intent mIntent = new Intent(mActivity, FTLAgreeTermsAcceptanceActivity.class);
                                mIntent.putExtra(Constants.USERNAME, mUserName);
                                mIntent.putExtra(Constants.PASSWORD, inputPassword.getText().toString().trim());
                                mIntent.putExtra(Constants.ACCESSTOKEN, mAccessToken);
                                mIntent.putExtra(Constants.SETTERMS, mTerms);
                                startActivity(mIntent);
                                mActivity.finish();
                            } else {
                                String mMessage = apiResponse.status.getMessage().toString();
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
                                        startActivity(new Intent(mActivity,FTLUserValidationActivity.class));
                                    }
                                });

                                // Showing Alert Message
                                mDialog.show();
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
    }

}
