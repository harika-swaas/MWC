package com.mwc.docportal.ForgotPassword;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.PasswordResetModel.UserNameResponseModel;
import com.mwc.docportal.API.Service.LoginService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;


import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class UserNameActivity extends AppCompatActivity {

    TextInputLayout inputLayoutUserName;
    EditText inputUserName;
    Button mNext;
    Context context = this;
    TextView choose_user_name;
    ImageView mBackIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftl_user_validation_fragment);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        intializeViews();
        onClickListeners();

    }

    private void onClickListeners()
    {
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputUserName.addTextChangedListener(new MyTextWatcher(inputUserName));
                verifyUserDetails();
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
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

    }

    public void hideKeyboard(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void verifyUserDetails() {

        if (!validateUserName()) {
            return;
        } else {
            checkUserNameDetailsFromAPI(inputUserName.getText().toString().trim());
        }
    }

    private void checkUserNameDetailsFromAPI(String userName)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final LoginService loginService = retrofitAPI.create(LoginService.class);

            UserNameRequestModel userNameRequestModel = new UserNameRequestModel(userName);


            final String request = new Gson().toJson(userNameRequestModel);
            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);
            userNameRequestModel.setUsername(userName);

            Call call = loginService.checkUserName(params);

            call.enqueue(new Callback<UserNameResponseModel>() {
                @Override
                public void onResponse(Response<UserNameResponseModel> response, Retrofit retrofit) {
                    UserNameResponseModel apiResponse = response.body();
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.getStatus().getMessage() != null)
                        {
                            message = apiResponse.getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(UserNameActivity.this, message, apiResponse.getStatus().getCode())) {

                            UserNameResponseModel.Data userDetail = response.body().getData();

                            if (userDetail != null) {
                                Intent intent = new Intent(UserNameActivity.this, PinDeviceListActivity.class);
                                intent.putExtra("AccessToken", userDetail.getAccessToken());
                                startActivity(intent);

                            }
                        }
                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    CommonFunctions.showTimeOutError(context, t);
                }
            });
        }
    }

    private void intializeViews()
    {
        inputLayoutUserName = (TextInputLayout) findViewById(R.id.input_layout_username);
        inputUserName = (EditText) findViewById(R.id.input_username);
        mNext = (Button) findViewById(R.id.next_button);
        choose_user_name = (TextView) findViewById(R.id.choose_user_name);
        choose_user_name.setText(getResources().getString(R.string.enter_userName_txt));
        mBackIv = (ImageView) findViewById(R.id.back_image_view);
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

    private boolean validateUserName() {

        String username = inputUserName.getText().toString().trim();

        if (TextUtils.isEmpty(username) && username.length() == 0) {
            inputLayoutUserName.setError(getString(R.string.err_msg_user_name));
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
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


}
