package com.mwc.docportal.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import com.mwc.docportal.API.Model.UpdateFTLStatusRequest;
import com.mwc.docportal.API.Model.VerifyFTLResponse;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.UpdateFTLStatusService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.FTL.FTLAgreeTermsAcceptanceActivity;
import com.mwc.docportal.FTL.FTLPasswordValidationActivity;
import com.mwc.docportal.FTL.FTLUserValidationActivity;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    TextInputLayout inputLayoutPassword, inputLayoutConfirmPassword;
    EditText inputPassword, inputConfirmPassword;
    TextView welcomeMsg;
    String mAccessToken;
    ImageView mBackIv;
    View inputPasswordView, inputConfirmPasswordView;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();

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
        setButtonGradientDrawable();
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
        mBackIv = (ImageView) mView.findViewById(R.id.back_image_view);
        inputPasswordView = (View) mView.findViewById(R.id.input_password_view);
        inputConfirmPasswordView = (View) mView.findViewById(R.id.input_confirm_password_view);
    }

    private void getIntentData() {

        if (mActivity.getIntent() != null) {
            mUserName = mActivity.getIntent().getStringExtra(Constants.USERNAME);
            mWelcomeMsg = mActivity.getIntent().getStringExtra(Constants.WELCOME_MSG);
            mAccessToken = mActivity.getIntent().getStringExtra(Constants.ACCESSTOKEN);
            mTerms = mActivity.getIntent().getStringExtra(Constants.SETTERMS);
        }
    }

    private void setWelcomeMsg() {

        if (mWelcomeMsg != null && !TextUtils.isEmpty(mWelcomeMsg)) {
            welcomeMsg.setText(mWelcomeMsg);
        } else {
            welcomeMsg.setText(getString(R.string.welcome));
        }
    }

    private void addListenersToViews() {

        inputPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    // inputPassword.getBackground().setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
                    inputPasswordView.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = inputPassword.getText().toString().trim();
                if (s.length() > 0) {
                    if (password.length() < 8 || password.length() > 15 || !isValidPassword(password)) {
                        //   inputPassword.getBackground().setColorFilter(getResources().getColor(R.color.dark_red), PorterDuff.Mode.SRC_IN);
                        inputPasswordView.setBackgroundColor(getResources().getColor(R.color.dark_red));
                    } else if (isValidPassword(password)) {
                        //  inputPassword.getBackground().setColorFilter(getResources().getColor(R.color.blue_non_pressed), PorterDuff.Mode.SRC_IN);
                        inputPasswordView.setBackgroundColor(getResources().getColor(R.color.green));
                    } else {
                        // inputPassword.getBackground().setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
                        inputPasswordView.setBackgroundColor(getResources().getColor(R.color.grey));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = inputPassword.getText().toString().trim();
                if (!isValidPassword(password)) {
                    //  inputPassword.getBackground().setColorFilter(getResources().getColor(R.color.dark_red), PorterDuff.Mode.SRC_IN);
                    inputPasswordView.setBackgroundColor(getResources().getColor(R.color.dark_red));
                } else if (isValidPassword(password)) {
                    //  inputPassword.getBackground().setColorFilter(getResources().getColor(R.color.blue_non_pressed), PorterDuff.Mode.SRC_IN);
                    inputPasswordView.setBackgroundColor(getResources().getColor(R.color.green));
                } else {
                    // inputPassword.getBackground().setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
                    inputPasswordView.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        inputConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    //   inputConfirmPassword.getBackground().setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
                    inputConfirmPasswordView.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = inputPassword.getText().toString().trim();
                String confirmPassword = inputConfirmPassword.getText().toString().trim();
                if (s.length() > 0) {
                    if (!password.equals(confirmPassword)) {
                        //  inputConfirmPassword.getBackground().setColorFilter(getResources().getColor(R.color.dark_red), PorterDuff.Mode.SRC_IN);
                        inputConfirmPasswordView.setBackgroundColor(getResources().getColor(R.color.dark_red));
                    } else {
                        // inputConfirmPassword.getBackground().setColorFilter(getResources().getColor(R.color.blue_non_pressed), PorterDuff.Mode.SRC_IN);
                        inputConfirmPasswordView.setBackgroundColor(getResources().getColor(R.color.green));
                    }
                }

                if (!TextUtils.isEmpty(confirmPassword) && !TextUtils.isEmpty(password) && password.equals(confirmPassword)) {
                    getWhiteLabelProperities();

                    if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                        String mobileItemEnableColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();

                        int itemEnableColor = Color.parseColor(mobileItemEnableColor);

                        if (mobileItemEnableColor != null) {
                            // Initialize a new GradientDrawable
                            GradientDrawable shape = new GradientDrawable();

                            // Specify the shape of drawable
                            shape.setShape(GradientDrawable.RECTANGLE);

                            // Make the border rounded
                            shape.setCornerRadius(50f);

                            // Set the fill color of drawable
                            shape.setColor(itemEnableColor);

                            mNext.setBackgroundDrawable(shape);
                        } else {
                            mNext.setBackgroundResource(R.drawable.next);
                        }
                    } else {
                        mNext.setBackgroundResource(R.drawable.next);
                    }

                    /*String mobileItemEnableColor = PreferenceUtils.getMobileItemEnableColor(mActivity);
                    int itemEnableColor = Color.parseColor(mobileItemEnableColor);

                    if (mobileItemEnableColor != null) {
                        // Initialize a new GradientDrawable
                        GradientDrawable shape = new GradientDrawable();

                        // Specify the shape of drawable
                        shape.setShape(GradientDrawable.RECTANGLE);

                        // Make the border rounded
                        shape.setCornerRadius(50f);

                        // Set the fill color of drawable
                        shape.setColor(itemEnableColor);

                        mNext.setBackgroundDrawable(shape);
                    } else {
                        mNext.setBackgroundResource(R.drawable.next);
                    }*/
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = inputPassword.getText().toString().trim();
                String confirmPassword = inputConfirmPassword.getText().toString().trim();
                if (password.equals(confirmPassword)) {
                    //  inputPassword.getBackground().setColorFilter(getResources().getColor(R.color.blue_non_pressed), PorterDuff.Mode.SRC_IN);
                    inputConfirmPasswordView.setBackgroundColor(getResources().getColor(R.color.green));
                } else {

                }
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputPassword.addTextChangedListener(new FTLPasswordValidationFragment.MyTextWatcher(inputPassword));
                inputConfirmPassword.addTextChangedListener(new FTLPasswordValidationFragment.MyTextWatcher(inputConfirmPassword));

                verifyFTLPasswordDetails();
                updateFTLStatus();
            }
        });

        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
               /* Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.USERNAME, mUserName);
                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                mActivity.finish();*/
            }
        });

        inputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                    String password = inputPassword.getText().toString().trim();
                    if (password.length() < 8 || password.length() > 15 || !isValidPassword(password)) {
                        // inputPassword.getBackground().setColorFilter(getResources().getColor(R.color.dark_red), PorterDuff.Mode.SRC_IN);
                        inputPasswordView.setBackgroundColor(getResources().getColor(R.color.dark_red));
                    } else if (isValidPassword(password)) {
                        //   inputPassword.getBackground().setColorFilter(getResources().getColor(R.color.blue_non_pressed), PorterDuff.Mode.SRC_IN);
                        inputPasswordView.setBackgroundColor(getResources().getColor(R.color.green));
                    } else {
                        //  inputPassword.getBackground().setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
                        inputPasswordView.setBackgroundColor(getResources().getColor(R.color.grey));
                    }
                }
            }
        });

        inputConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                    String password = inputPassword.getText().toString().trim();
                    String confirmPassword = inputConfirmPassword.getText().toString().trim();
                    if (!password.equals(confirmPassword)) {
                        // inputConfirmPassword.getBackground().setColorFilter(getResources().getColor(R.color.dark_red), PorterDuff.Mode.SRC_IN);
                        inputConfirmPasswordView.setBackgroundColor(getResources().getColor(R.color.dark_red));
                    } else {
                        //  inputConfirmPassword.getBackground().setColorFilter(getResources().getColor(R.color.blue_non_pressed), PorterDuff.Mode.SRC_IN);
                        inputConfirmPasswordView.setBackgroundColor(getResources().getColor(R.color.green));
                    }
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

    private void setButtonGradientDrawable() {

        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        getWhiteLabelProperities();

        if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
            String mobileItemEnableColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            String mobileItemDisableColor = mWhiteLabelResponses.get(0).getItem_Unselected_Color();

            int itemEnableColor = Color.parseColor(mobileItemEnableColor);
            int itemDisableColor = Color.parseColor(mobileItemDisableColor);

            if (!TextUtils.isEmpty(confirmPassword) && !TextUtils.isEmpty(password) && password.equals(confirmPassword)) {
                if (mobileItemEnableColor != null) {
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
            } else {
                if (mobileItemDisableColor != null) {
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
            }
        } else {
            mNext.setBackgroundResource(R.drawable.next);
        }

        /*String mobileItemEnableColor = PreferenceUtils.getMobileItemEnableColor(mActivity);
        int itemEnableColor = Color.parseColor(mobileItemEnableColor);

        String mobileItemDisableColor = PreferenceUtils.getMobileItemDisableColor(mActivity);
        int itemDisableColor = Color.parseColor(mobileItemDisableColor);

        if (!TextUtils.isEmpty(confirmPassword) && !TextUtils.isEmpty(password) && password.equals(confirmPassword)) {
            if (mobileItemEnableColor != null) {
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
        } else {
            if(mobileItemDisableColor != null){
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
        }*/
    }

    private void verifyFTLPasswordDetails() {

        if (!validatePassword()) {
            return;
        }

        if (!validateConfirmPassword()) {
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

        if (!password.isEmpty() && password.length() < 8) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password_min_length));
            requestFocus(inputPassword);
            return false;
        } else if (!password.isEmpty() && password.length() > 15) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password_max_length));
            requestFocus(inputPassword);
            return false;
        } else if (password.isEmpty() || !isValidPassword(password)) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        }
        else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConfirmPassword() {

        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        if (confirmPassword.isEmpty()) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_confirm_password));
            requestFocus(inputConfirmPassword);
            return false;
        } else if (confirmPassword.length() < 8) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_password_min_length));
            requestFocus(inputConfirmPassword);
            return false;
        } else if (confirmPassword.length() > 15) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_password_max_length));
            requestFocus(inputConfirmPassword);
            return false;
        } else if (!password.equals(confirmPassword)) {
            inputLayoutConfirmPassword.setError(getString(R.string.err_msg_confirm_password_match));
            requestFocus(inputConfirmPassword);
            return false;
        } else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
        }

        return true;
    }

    public static boolean isValidPassword(final String password) {

        final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[0-9])[A-Za-z0-9!@*#$%&()`.+,/\"-]{8,15}$";
        Pattern mPattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = mPattern.matcher(password.toString());

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
                /*final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
                dialog.show();*/

                final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
                transparentProgressDialog.show();

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
                            transparentProgressDialog.dismiss();

                            String message = "";
                            if(apiResponse.status.getMessage() != null)
                            {
                                message = apiResponse.status.getMessage().toString();
                            }

                            if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {
                                Intent mIntent = new Intent(mActivity, FTLAgreeTermsAcceptanceActivity.class);
                                mIntent.putExtra(Constants.USERNAME, mUserName);
                                mIntent.putExtra(Constants.PASSWORD, inputPassword.getText().toString().trim());
                                mIntent.putExtra(Constants.ACCESSTOKEN, mAccessToken);
                                mIntent.putExtra(Constants.SETTERMS, mTerms);
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
    }

}
