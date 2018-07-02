package com.swaas.mwc.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.FTLProcessResponse;
import com.swaas.mwc.API.Model.VerifyFTLRequestWithEMail;
import com.swaas.mwc.API.Service.FTLProcessService;
import com.swaas.mwc.API.Service.VerifyFTLDetailsService;
import com.swaas.mwc.FTL.FTLPasswordValidationActivity;
import com.swaas.mwc.FTL.FTLUserValidationActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLUserValidationFragment extends Fragment {

    FTLUserValidationActivity mActivity;
    View mView;
    Button mNext;
    String mTextBackgroundColor,mTextForeGroundColor,mAppBackGroundColor;
    TextInputLayout inputLayoutUserName;
    EditText inputUserName;
    TextView welcomeMsg;
    String mUserName,mEmail,mWelcomeMsg,mTerms;
    String mAccessToken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FTLUserValidationActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ftl_user_validation_fragment, container, false);

        intializeViews();
        getIntentData();
        getFTLProcess();
      //  setUserName();
       // setButtonBackgroundColor();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        welcomeMsg = (TextView) mView.findViewById(R.id.welcome_msg);
        inputLayoutUserName = (TextInputLayout) mView.findViewById(R.id.input_layout_username);
        inputUserName = (EditText) mView.findViewById(R.id.input_username);
        mNext = (Button) mView.findViewById(R.id.next_button);
    }

    private void getIntentData() {

        if(mActivity.getIntent() != null){
            mAccessToken = mActivity.getIntent().getStringExtra(Constants.ACCESSTOKEN);
        }
    }

    private void setUserName() {

        if(mWelcomeMsg != null && !TextUtils.isEmpty(mWelcomeMsg)){
            welcomeMsg.setText(mWelcomeMsg);
        } else {
            welcomeMsg.setText(getString(R.string.welcome));
        }

        if(mUserName != null && !TextUtils.isEmpty(mUserName)){
            inputUserName.setHint(mUserName);
        } else if(!TextUtils.isEmpty(mEmail)){
            inputUserName.setHint(mEmail);
        } else {
            inputUserName.setHint(getString(R.string.user_name));
        }
    }

    private void setButtonBackgroundColor() {

        if(mAppBackGroundColor != null && !mAppBackGroundColor.isEmpty()) {
            mNext.setBackgroundColor(Integer.parseInt(mAppBackGroundColor));
        } else {
            mNext.setBackgroundResource(R.drawable.next);
        }
    }

    private void addListenersToViews() {

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputUserName.addTextChangedListener(new FTLUserValidationFragment.MyTextWatcher(inputUserName));

                verifyFTLUserDetails();
            }
        });
    }

    private void verifyFTLUserDetails() {

        if (!validateUserName()) {
            return;
        } else {
            Intent mIntent = new Intent(mActivity,FTLPasswordValidationActivity.class);
            mIntent.putExtra(Constants.USERNAME, inputUserName.getText().toString().trim());
            mIntent.putExtra(Constants.WELCOME_MSG, mWelcomeMsg);
            mIntent.putExtra(Constants.ACCESSTOKEN, mAccessToken);
            mIntent.putExtra(Constants.SETTERMS, mTerms);
            mActivity.startActivity(mIntent);
            mActivity.finish();
        }
    }

    private boolean validateUserName() {

        String username = inputUserName.getText().toString().trim();

        if (username.isEmpty()) {
            inputLayoutUserName.setError(getString(R.string.err_msg_user_name));
            requestFocus(inputUserName);
            return false;
        } else {
            inputLayoutUserName.setErrorEnabled(false);
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
                case R.id.input_username:
                    validateUserName();
                    break;
            }
        }
    }

    private void getFTLProcess() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final FTLProcessService ftlProcessService = retrofitAPI.create(FTLProcessService.class);
            Call call = ftlProcessService.getFTLProcess(mAccessToken);
            call.enqueue(new Callback<BaseApiResponse<FTLProcessResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<FTLProcessResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status.isCode() == false) {
                            FTLProcessResponse mFTLProcessResponse = response.body().getData();
                            if(mFTLProcessResponse.user_details != null) {
                                mUserName = mFTLProcessResponse.user_details.getUsername();
                                mEmail = mFTLProcessResponse.user_details.getEmail();
                                mWelcomeMsg = mFTLProcessResponse.user_details.getEu_ftl_welcome_msg();
                                mTerms = mFTLProcessResponse.user_details.getTerms();
                                PreferenceUtils.setTermsURL(mActivity, mTerms);
                                setUserName();
                            }
                        } else {

                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("FTLProcessErr",t.getMessage());
                }
            });
        }
    }
}
