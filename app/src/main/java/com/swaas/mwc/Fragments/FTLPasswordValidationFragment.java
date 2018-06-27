package com.swaas.mwc.Fragments;

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

import com.swaas.mwc.FTL.FTLAgreeTermsAcceptanceActivity;
import com.swaas.mwc.FTL.FTLPasswordValidationActivity;
import com.swaas.mwc.R;
import com.swaas.mwc.Utils.Constants;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLPasswordValidationFragment extends Fragment {

    FTLPasswordValidationActivity mActivity;
    View mView;
    Button mNext;
    String mUserName;
    TextInputLayout inputLayoutPassword,inputLayoutConfirmPassword;
    EditText inputPassword,inputConfirmPassword;

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
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        inputLayoutPassword = (TextInputLayout) mView.findViewById(R.id.input_layout_password);
        inputLayoutConfirmPassword = (TextInputLayout) mView.findViewById(R.id.input_layout_confirm_password);
        inputPassword = (EditText) mView.findViewById(R.id.input_password);
        inputConfirmPassword = (EditText) mView.findViewById(R.id.input_confirm_password);
        mNext = (Button) mView.findViewById(R.id.next_button);
    }

    private void getIntentData() {

        if(mActivity.getIntent() != null){
            mUserName = mActivity.getIntent().getStringExtra(Constants.USERNAME);
        }
    }

    private void addListenersToViews() {

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputPassword.addTextChangedListener(new FTLPasswordValidationFragment.MyTextWatcher(inputPassword));
                inputConfirmPassword.addTextChangedListener(new FTLPasswordValidationFragment.MyTextWatcher(inputConfirmPassword));

                verifyFTLPasswordDetails();

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

        if (password.isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_user_name));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConfirmPassword() {

        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        if (confirmPassword.isEmpty()) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_user_name));
            requestFocus(inputConfirmPassword);
            return false;
        } else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {

        if (view.requestFocus()) {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
