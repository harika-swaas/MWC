package com.mwc.docportal.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.FTLProcessResponse;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.FTLProcessService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.FTL.FTLPasswordValidationActivity;
import com.mwc.docportal.FTL.FTLPinVerificationActivity;
import com.mwc.docportal.FTL.FTLUserValidationActivity;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

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
    String mTextBackgroundColor, mTextForeGroundColor, mAppBackGroundColor;
    TextInputLayout inputLayoutUserName;
    EditText inputUserName;
    TextView welcomeMsg;
    String mUserName, mEmail, mWelcomeMsg, mTerms;
    String mAccessToken;
    ImageView mBackIv;
    AlertDialog mBackDialog;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();

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
      /*  getIntentData();
        getFTLProcess();
        //  setUserName();
        setButtonBackgroundColor();
        addListenersToViews();*/

        /*inputUserName.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start, int end,
                                               Spanned spanned, int dStart, int dEnd) {

                        if (cs.equals("")) {
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z0-9_.,-@]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });*/


        return mView;
    }

    private void intializeViews() {

        welcomeMsg = (TextView) mView.findViewById(R.id.welcome_msg);
        inputLayoutUserName = (TextInputLayout) mView.findViewById(R.id.input_layout_username);
        inputUserName = (EditText) mView.findViewById(R.id.input_username);
        mNext = (Button) mView.findViewById(R.id.next_button);
        mBackIv = (ImageView) mView.findViewById(R.id.back_image_view);
    }

    private void getIntentData() {

        if (mActivity.getIntent() != null) {
            mAccessToken = mActivity.getIntent().getStringExtra(Constants.ACCESSTOKEN);
        }
    }

    private void setUserName() {

        if (mWelcomeMsg != null && !TextUtils.isEmpty(mWelcomeMsg)) {
            welcomeMsg.setText(mWelcomeMsg);
        } else {
            welcomeMsg.setText(getString(R.string.welcome));
        }

        if (mEmail != null && !TextUtils.isEmpty(mEmail)) {
            inputUserName.setText(mEmail);
        } else if (mUserName != null && !TextUtils.isEmpty(mUserName)) {
            inputUserName.setText(mUserName);
        } else {
            inputUserName.setHint(getString(R.string.user_name));
        }
    }

    private void setButtonBackgroundColor() {
        String username = inputUserName.getText().toString().trim();

        /*String mobileItemEnableColor = PreferenceUtils.getMobileItemEnableColor(mActivity);
        String mobileItemDisableColor = PreferenceUtils.getMobileItemDisableColor(mActivity);

        int itemEnableColor = 0;
        int itemDisableColor = 0;

        if(mobileItemEnableColor != null){
            itemEnableColor = Color.parseColor(mobileItemEnableColor);
        }
        if(mobileItemDisableColor != null){
            itemDisableColor = Color.parseColor(mobileItemDisableColor);
        }

        if (TextUtils.isEmpty(username) && username.length() == 0) {
            if(mobileItemDisableColor != null) {
              //  mNext.setBackgroundColor(itemDisableColor);

                // Initialize a new GradientDrawable
                GradientDrawable shape = new GradientDrawable();

                // Specify the shape of drawable
                shape.setShape(GradientDrawable.RECTANGLE);

                // Make the border rounded
                shape.setCornerRadius(50f);

                // Set the fill color of drawable
                shape.setColor(itemDisableColor);

                mNext.setBackgroundDrawable(shape);
            }
        } else {
            if(mobileItemEnableColor != null) {
             //   mNext.setBackgroundColor(itemEnableColor);

                // Initialize a new GradientDrawable
                GradientDrawable shape = new GradientDrawable();

                // Specify the shape of drawable
                shape.setShape(GradientDrawable.RECTANGLE);

                // Make the border rounded
                shape.setCornerRadius(50f);

                // Set the fill color of drawable
                shape.setColor(itemEnableColor);

                mNext.setBackgroundDrawable(shape);
            }
        }*/

        getWhiteLabelProperities();

        if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
            String mobileItemEnableColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            String mobileItemDisableColor = mWhiteLabelResponses.get(0).getItem_Unselected_Color();

            int itemEnableColor = Color.parseColor(mobileItemEnableColor);
            int itemDisableColor = Color.parseColor(mobileItemDisableColor);

            if (TextUtils.isEmpty(username) && username.length() == 0) {
                if (mobileItemDisableColor != null) {
                    //   mNext.setBackgroundColor(itemDisableColor);

                    // Initialize a new GradientDrawable
                    GradientDrawable shape = new GradientDrawable();

                    // Specify the shape of drawable
                    shape.setShape(GradientDrawable.RECTANGLE);

                    // Make the border rounded
                    shape.setCornerRadius(50f);

                    // Set the fill color of drawable
                    shape.setColor(itemDisableColor);

                    mNext.setBackgroundDrawable(shape);
                }
            } else {
                if (mobileItemEnableColor != null) {
                    // mNext.setBackgroundColor(itemEnableColor);

                    // Initialize a new GradientDrawable
                    GradientDrawable shape = new GradientDrawable();

                    // Specify the shape of drawable
                    shape.setShape(GradientDrawable.RECTANGLE);

                    // Make the border rounded
                    shape.setCornerRadius(50f);

                    // Set the fill color of drawable
                    shape.setColor(itemEnableColor);

                    mNext.setBackgroundDrawable(shape);
                }
            }
        } else {
            mNext.setBackgroundResource(R.drawable.next);
        }
    }

    private void addListenersToViews() {

        inputUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = inputUserName.getText().toString().trim();

                /*String mobileItemEnableColor = PreferenceUtils.getMobileItemEnableColor(mActivity);
                String mobileItemDisableColor = PreferenceUtils.getMobileItemDisableColor(mActivity);*/

                getWhiteLabelProperities();

                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String mobileItemEnableColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                    String mobileItemDisableColor = mWhiteLabelResponses.get(0).getItem_Unselected_Color();

                    int itemEnableColor = Color.parseColor(mobileItemEnableColor);
                    int itemDisableColor = Color.parseColor(mobileItemDisableColor);

                    if (TextUtils.isEmpty(username) && username.length() == 0) {
                        if (mobileItemDisableColor != null) {
                            //   mNext.setBackgroundColor(itemDisableColor);

                            // Initialize a new GradientDrawable
                            GradientDrawable shape = new GradientDrawable();

                            // Specify the shape of drawable
                            shape.setShape(GradientDrawable.RECTANGLE);

                            // Make the border rounded
                            shape.setCornerRadius(50f);

                            // Set the fill color of drawable
                            shape.setColor(itemDisableColor);

                            mNext.setBackgroundDrawable(shape);
                        }
                    } else {
                        if (mobileItemEnableColor != null) {
                            // mNext.setBackgroundColor(itemEnableColor);

                            // Initialize a new GradientDrawable
                            GradientDrawable shape = new GradientDrawable();

                            // Specify the shape of drawable
                            shape.setShape(GradientDrawable.RECTANGLE);

                            // Make the border rounded
                            shape.setCornerRadius(50f);

                            // Set the fill color of drawable
                            shape.setColor(itemEnableColor);

                            mNext.setBackgroundDrawable(shape);
                        }
                    }
                } else {
                    mNext.setBackgroundResource(R.drawable.next);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputUserName.addTextChangedListener(new FTLUserValidationFragment.MyTextWatcher(inputUserName));
                verifyFTLUserDetails();
            }
        });

        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mActivity.onBackPressed();

                final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.back_custom_alert_layout, null);
                builder.setView(view);
                builder.setCancelable(false);

                Button yesButton = (Button) view.findViewById(R.id.yes_button);
                Button noButton = (Button) view.findViewById(R.id.no_button);

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();
                        startActivity(new Intent(mActivity, FTLPinVerificationActivity.class));
                    }
                });

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();
                    }
                });

                mBackDialog = builder.create();
                mBackDialog.show();
            }
        });

        inputUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    private void getWhiteLabelProperities() {

        AccountSettings accountSettings = new AccountSettings(mActivity);
        accountSettings.SetWhiteLabelCB(new AccountSettings.GetWhiteLabelCB() {
            @Override
            public void getWhiteLabelSuccessCB(List<WhiteLabelResponse> whiteLabelResponses) {
                if (whiteLabelResponses != null && whiteLabelResponses.size() > 0) {
                    mWhiteLabelResponses = whiteLabelResponses;
                }
            }

            @Override
            public void getWhiteLabelFailureCB(String message) {

            }
        });

        accountSettings.getWhiteLabelProperties();
    }

    public void hideKeyboard(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void verifyFTLUserDetails() {

        if (!validateUserName()) {
            return;
        } else {
            Intent mIntent = new Intent(mActivity, FTLPasswordValidationActivity.class);
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

        if (TextUtils.isEmpty(username) && username.length() == 0) {
            inputLayoutUserName.setError(getString(R.string.err_msg_user_name));
            requestFocus(inputUserName);
            return false;
        } else if (username.length() < 5) {
            inputLayoutUserName.setError(getString(R.string.err_msg_user_name_min_length));
            requestFocus(inputUserName);
            return false;
        }else if (username.length() > 45) {
            inputLayoutUserName.setError(getString(R.string.err_msg_user_name_max_length));
            requestFocus(inputUserName);
            return false;
        }
        else {
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

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {

                            FTLProcessResponse mFTLProcessResponse = response.body().getData();
                            if (mFTLProcessResponse.user_details != null) {
                                mUserName = mFTLProcessResponse.user_details.getUsername();
                                mEmail = mFTLProcessResponse.user_details.getEmail();
                                mWelcomeMsg = mFTLProcessResponse.user_details.getEu_ftl_welcome_msg();
                                if(mFTLProcessResponse.user_details.getTerms() != null && !mFTLProcessResponse.user_details.getDefault_terms_url().isEmpty())
                                {
                                    mTerms = mFTLProcessResponse.user_details.getTerms();
                                }
                                else
                                {
                                    mTerms = mFTLProcessResponse.user_details.getDefault_terms_url();
                                }
                                PreferenceUtils.setTermsURL(mActivity, mTerms);
                                setUserName();
                            }

                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(mActivity, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("FTLProcessErr", t.getMessage());
                    CommonFunctions.showTimeOutError(mActivity, t);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getIntentData();
        getFTLProcess();
        setButtonBackgroundColor();
        addListenersToViews();
    }
}
