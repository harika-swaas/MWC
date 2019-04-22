package com.mwc.docportal.ForgotPassword;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.VerifyFTLResponse;
import com.mwc.docportal.API.Service.UpdateFTLStatusService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class NewPasswordActivity extends AppCompatActivity {

    Button mNext;
    TextInputLayout inputLayoutPassword, inputLayoutConfirmPassword;
    EditText inputPassword, inputConfirmPassword;
    String mAccessToken;
    ImageView mBackIv;
    View inputPasswordView, inputConfirmPasswordView;
    Context context = this;
    AlertDialog mAlertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftl_password_validation_fragment);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        intializeViews();
        getIntentData();
        onClickListener();

    }

    private void onClickListener()
    {

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

                inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
                inputConfirmPassword.addTextChangedListener(new MyTextWatcher(inputConfirmPassword));

                verifyPasswordDetails();
                sendPasswordDetails();
            }
        });

        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

    private void sendPasswordDetails()
    {
        String password = inputConfirmPassword.getText().toString().trim();
        if (NetworkUtils.isNetworkAvailable(context)) {
            if (validatePassword() && validateConfirmPassword()) {

                final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
                transparentProgressDialog.show();

                Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                final UpdateFTLStatusService updateFTLStatusService = retrofitAPI.create(UpdateFTLStatusService.class);

                PasswordVerifyRequest mUpdateFTLStatusRequest = new PasswordVerifyRequest("self_reset", password);
                String request = new Gson().toJson(mUpdateFTLStatusRequest);
                //Here the json data is add to a hash map with key data
                Map<String, String> params = new HashMap<String, String>();
                params.put("data", request);

                Call call = updateFTLStatusService.getUpdateNewPassword(params, mAccessToken);

                call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                    @Override
                    public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                        BaseApiResponse apiResponse = response.body();
                        transparentProgressDialog.dismiss();
                        if (apiResponse != null) {

                            String message = "";
                            if(apiResponse.status.getMessage() != null)
                            {
                                message = apiResponse.status.getMessage().toString();
                            }

                            if(CommonFunctions.isApiSuccess(NewPasswordActivity.this, message, apiResponse.status.getCode())) {
                                passwordSuccessAlertMessage(apiResponse.status.getMessage());
                            }
                        }
                        else {
                            CommonFunctions.serverErrorExceptions(context, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        transparentProgressDialog.dismiss();
                        CommonFunctions.showTimeOutError(context, t);
                    }
                });
            }
        }
    }

    private void passwordSuccessAlertMessage(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText(message);

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setVisibility(View.GONE);

        okButton.setText("Ok");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

                AccountSettings accountSettings = new AccountSettings(context);
                accountSettings.LogouData();

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    public void hideKeyboard(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void getIntentData()
    {
        if(getIntent().getStringExtra("AccessToken") != null)
        {
            mAccessToken = getIntent().getStringExtra("AccessToken");
        }

    }

    private void verifyPasswordDetails() {

        if (!validatePassword()) {
            return;
        }

        if (!validateConfirmPassword()) {
            return;
        }
    }

    private void intializeViews() {
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputConfirmPassword = (EditText) findViewById(R.id.input_confirm_password);
        mNext = (Button) findViewById(R.id.next_button);
        mBackIv = (ImageView) findViewById(R.id.back_image_view);
        inputPasswordView = (View) findViewById(R.id.input_password_view);
        inputConfirmPasswordView = (View) findViewById(R.id.input_confirm_password_view);
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

    public static boolean isValidPassword(final String password) {

        final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[0-9])[A-Za-z0-9!@*#$%&()`.+,/\"-]{8,15}$";
        Pattern mPattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = mPattern.matcher(password.toString());

        return matcher.matches();
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

    private void requestFocus(View view) {

        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
