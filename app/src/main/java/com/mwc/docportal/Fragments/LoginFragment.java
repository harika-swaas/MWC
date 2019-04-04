package com.mwc.docportal.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.FTLPINResponse;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.FTL.FTLRegistrationActivity;
import com.squareup.okhttp.OkHttpClient;
import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.ApiResponse;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.GetUISettingsResponse;
import com.mwc.docportal.API.Model.GetUserPreferencesResponse;
import com.mwc.docportal.API.Model.LoginRequest;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.GetUISettingsService;
import com.mwc.docportal.API.Service.GetUserPreferencesService;
import com.mwc.docportal.API.Service.LoginService;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.FTL.FTLActivity;
import com.mwc.docportal.FTL.FTLUserValidationActivity;
import com.mwc.docportal.Login.Dashboard;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Login.LoginAgreeTermsAcceptanceActivity;
import com.mwc.docportal.Login.LoginHelpUserGuideActivity;
import com.mwc.docportal.Login.Notifiy;
import com.mwc.docportal.Login.PinVerificationActivity;
import com.mwc.docportal.Login.Touchid;

import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by harika on 22-06-2018.
 */

public class LoginFragment extends Fragment {

    LoginActivity mActivity;
    View mView;
    Retrofit retrofit;
    Button mSignInButton;
    Button mNotLoggedInBefore;
    EditText mUserName, mPassword;
    private LoginResponse mLoggedInObj;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    public static final int REQUEST_STORAGE_PERMISSION = 111;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (LoginActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.login_fragment, container, false);

        intializeViews();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {
        mUserName = (EditText) mView.findViewById(R.id.edit_username);
        mPassword = (EditText) mView.findViewById(R.id.edit_password);
        mNotLoggedInBefore = (Button) mView.findViewById(R.id.not_logged_in_before);
        mSignInButton = (Button) mView.findViewById(R.id.sign_in);
    }

    private void addListenersToViews() {

        mUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


        mNotLoggedInBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, FTLRegistrationActivity.class));
                mActivity.finish();
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int storagePermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (storagePermission == PackageManager.PERMISSION_GRANTED) {
                        loginInButtonClick();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                    }
                } else {
                    loginInButtonClick();
                }


            }
        });
    }

    private void loginInButtonClick()
    {

        String username = mUserName.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (username.equals("")) {
            String message = "Please enter username and password";
            mActivity.showMessagebox(mActivity, message, null, false);
        } else if (password.equals("")) {
            String message = "Please enter username and password";
            mActivity.showMessagebox(mActivity, message, null, false);
        } else {

            if (NetworkUtils.isNetworkAvailable(mActivity)) {
                        /*final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
                        dialog.show();*/

                final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
                transparentProgressDialog.show();

                Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
                final LoginService loginService = retrofitAPI.create(LoginService.class);

                LoginRequest mLoginRequest = new LoginRequest(username, password,true);
                //LoginRequest mLoginRequest = new LoginRequest(username, password);

                final String request = new Gson().toJson(mLoginRequest);
                //Here the json data is add to a hash map with key data
                Map<String, String> params = new HashMap<String, String>();
                params.put("data", request);
                mLoginRequest.setUserName(username);
                mLoginRequest.setPassword(password);

                Call call = loginService.getLogin(params);
                call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Response<ApiResponse<LoginResponse>> response, Retrofit retrofit) {
                        ApiResponse apiResponse = response.body();
                        transparentProgressDialog.dismiss();
                        if (apiResponse != null) {

                            String message = "";
                            if(apiResponse.status.getMessage() != null)
                            {
                                message = apiResponse.status.getMessage().toString();
                                if (message.contains("]") || message.contains("[")) {
                                    message = message.replace(']',' ');
                                    message = message.replace('[',' ');
                                    message = message.trim();
                                }
                            }

                            if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode()))
                            {

                                if (apiResponse.status.getCode() == false) {
                                    LoginResponse mLoginResponse = response.body().getData();

                                    //setting login response obj to preference utils
                                    Gson gson = new Gson();
                                    PreferenceUtils.setDocPortalLoggedInObj(mActivity, mLoginResponse);

                                    if (mLoginResponse != null) {
                                        String accessToken = mLoginResponse.getAccessToken();
                                        PreferenceUtils.setAccessToken(mActivity, accessToken);

                                        updateLoggedInStatus();
                                        updateHelpAcceptedAndLoggedInStatus();
                                        if (mLoginResponse.nextStep != null) {

                                            if (mLoginResponse.nextStep.isPin_authentication_required() == true) {
                                                Intent intent = new Intent(mActivity, PinVerificationActivity.class);
                                                startActivity(intent);
                                            } else if (mLoginResponse.nextStep.isFtl_required() == true) {
                                                getUiSettingsFromLogin(mLoginResponse);
                                            }
                                        } else {

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                checkSecurity();
                                                updateLoggedInStatus();
                                                updateHelpAcceptedAndLoggedInStatus();
                                            }
                                        }

                                    } else {
                                        transparentProgressDialog.dismiss();
                                        if(apiResponse.status.getMessage() instanceof String){
                                            String mMessage = apiResponse.status.getMessage().toString();
                                            mActivity.showMessagebox(mActivity,mMessage,null,false);
                                        }
                                        else if(apiResponse.status.getMessage() instanceof List){
                                            String mMessage = ((List) apiResponse.status.getMessage()).get(0).toString();
                                            mActivity.showMessagebox(mActivity,mMessage,null,false);
                                        }
                                    }

                                } else {
                                    transparentProgressDialog.dismiss();
                                    if(apiResponse.status.getMessage() instanceof String){
                                        String mMessage = apiResponse.status.getMessage().toString();
                                        mActivity.showMessagebox(mActivity,mMessage,null,false);
                                    }
                                    else if(apiResponse.status.getMessage() instanceof List){
                                        String mMessage = ((List) apiResponse.status.getMessage()).get(0).toString();
                                        mActivity.showMessagebox(mActivity,mMessage,null,false);
                                    }
                                }

                            }

                        }
                        else {
                            CommonFunctions.serverErrorExceptions(mActivity, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("LoginErr", t.toString());
                        transparentProgressDialog.dismiss();
                        CommonFunctions.showTimeOutError(mActivity, t);
                    }
                });
            }
        }
    }


    private void getUiSettings() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final GetUISettingsService getUISettingsService = retrofitAPI.create(GetUISettingsService.class);

            Call call = getUISettingsService.getUISettings(PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<BaseApiResponse<GetUISettingsResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<GetUISettingsResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {

                            GetUISettingsResponse mGetUISettingsResponse = response.body().getData();

                            if (mGetUISettingsResponse != null) {

                                if (mGetUISettingsResponse.ui_properties != null) {

                                    String mobileItemEnableColor = mGetUISettingsResponse.ui_properties.getMobile_item_enable_color();
                                    String mobileItemDisableColor = mGetUISettingsResponse.ui_properties.getMobile_item_disable_color();
                                    String splashScreenColor = mGetUISettingsResponse.ui_properties.getMobile_splash_screen_background_color();
                                    String folderColor = mGetUISettingsResponse.ui_properties.getMobile_folder_color();

                                    AccountSettings accountSettings = new AccountSettings(mActivity);
                                    WhiteLabelResponse whiteLabelResponse = new WhiteLabelResponse();
                                    whiteLabelResponse.setItem_Selected_Color(mobileItemEnableColor);
                                    whiteLabelResponse.setItem_Unselected_Color(mobileItemDisableColor);
                                    whiteLabelResponse.setSplash_Screen_Color(splashScreenColor);
                                    whiteLabelResponse.setFolder_Color(folderColor);

                                    accountSettings.InsertWhiteLabelDetails(whiteLabelResponse);
                                }

                                getUserPreferences();

                            }
                        }
                    }
                    else {
                        CommonFunctions.serverErrorExceptions(mActivity, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    CommonFunctions.showTimeOutError(mActivity, t);
                }
            });
        }

    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void initializeRetrofitBuilder(String apiURL) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .baseUrl(apiURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }


    @Override
    public void onResume() {
        super.onResume();
      //  checkAppStatus();
    }

    private void checkAppStatus() {
        String loginStatus = "";
        getLoggedInStatus();

        if(mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0) {
            loginStatus = mAccountSettingsResponses.get(0).getLogin_Complete_Status();
        } else {
            loginStatus = "";
        }

        if (TextUtils.isEmpty(loginStatus)) {
            startActivity(new Intent(mActivity, LoginActivity.class));
        }
        else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Login_Completed))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkSecurity();
            }
        }
        else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Local_Auth_Completed))) {
            startActivity(new Intent(mActivity, Notifiy.class));
            mActivity.finish();
        }
        else {
            checkAppStatusAfterPushNotification(mAccountSettingsResponses);
        }
    }

    private void checkAppStatusAfterPushNotification(List<AccountSettingsResponse> mAccountSettingsResponses) {

        if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("0")) {
            startActivity(new Intent(mActivity, LoginAgreeTermsAcceptanceActivity.class));
            mActivity.finish();
        }
        else if(mAccountSettingsResponses.get(0).getIs_Help_Accepted().equals("1")) {
            startActivity(new Intent(mActivity, LoginHelpUserGuideActivity.class));
            mActivity.finish();
        }
        else {
            startActivity(new Intent(mActivity, Dashboard.class));
            mActivity.finish();
        }
    }

    private void getLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(mActivity);
        accountSettings.SetLoggedInCB(new AccountSettings.GetLoggedInCB() {
            @Override
            public void getLoggedInSuccessCB(List<AccountSettingsResponse> accountSettingsResponse) {
                if (accountSettingsResponse != null && accountSettingsResponse.size() > 0) {
                    mAccountSettingsResponses = accountSettingsResponse;
                }
            }

            @Override
            public void getLoggedInFailureCB(String message) {

            }
        });

        accountSettings.getLoggedInStatusDetails();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkSecurity() {
        KeyguardManager keyguardManager = (KeyguardManager) mActivity.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure() == true) {
            Intent intent = new Intent(mActivity, Touchid.class);
            startActivity(intent);
            mActivity.finish();
        }
        else
        {
            Intent intent = new Intent(mActivity,Notifiy.class);
            startActivity(intent);
        }
    }

    private void getUiSettingsFromLogin(LoginResponse loginResponse) {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final GetUISettingsService getUISettingsService = retrofitAPI.create(GetUISettingsService.class);

            Call call = getUISettingsService.getUISettings(loginResponse.getAccessToken());

            call.enqueue(new Callback<BaseApiResponse<GetUISettingsResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<GetUISettingsResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }


                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {

                            GetUISettingsResponse mGetUISettingsResponse = response.body().getData();

                            if (mGetUISettingsResponse != null) {

                                if (mGetUISettingsResponse.ui_properties != null) {

                                    String mobileItemEnableColor = mGetUISettingsResponse.ui_properties.getMobile_item_enable_color();
                                    String mobileItemDisableColor = mGetUISettingsResponse.ui_properties.getMobile_item_disable_color();
                                    String splashScreenColor = mGetUISettingsResponse.ui_properties.getMobile_splash_screen_background_color();
                                    String folderColor = mGetUISettingsResponse.ui_properties.getMobile_folder_color();

                                    AccountSettings accountSettings = new AccountSettings(mActivity);
                                    WhiteLabelResponse whiteLabelResponse = new WhiteLabelResponse();
                                    whiteLabelResponse.setItem_Selected_Color(mobileItemEnableColor);
                                    whiteLabelResponse.setItem_Unselected_Color(mobileItemDisableColor);
                                    whiteLabelResponse.setSplash_Screen_Color(splashScreenColor);
                                    whiteLabelResponse.setFolder_Color(folderColor);

                                    accountSettings.InsertWhiteLabelDetails(whiteLabelResponse);

                                    Intent intent = new Intent(mActivity, FTLUserValidationActivity.class);
                                    intent.putExtra(Constants.ACCESSTOKEN, loginResponse.getAccessToken());
                                    intent.putExtra("Is_From_Resend", true);
                                    FTLPINResponse ftlpinResponse = new FTLPINResponse();
                                    ftlpinResponse.setAccessToken(loginResponse.getAccessToken());
                                    ftlpinResponse.setUserId(loginResponse.getUserId());
                                    ftlpinResponse.setCompany_name(loginResponse.getCompany_name());
                                    PreferenceUtils.setDocPortalFTLLoggedObj(mActivity, ftlpinResponse);
                                    startActivity(intent);
                                }
                            }
                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(mActivity, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    CommonFunctions.showTimeOutError(mActivity, t);
                }
            });
        }

    }

    private void getUserPreferences() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final GetUserPreferencesService getUserPreferencesService = retrofitAPI.create(GetUserPreferencesService.class);

            Call call = getUserPreferencesService.getUserPreferences(PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<BaseApiResponse<GetUserPreferencesResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<GetUserPreferencesResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {

                            GetUserPreferencesResponse mGetUserPreferencesResponse = response.body().getData();
                            if (mGetUserPreferencesResponse != null) {
                                String assistance_popup = mGetUserPreferencesResponse.getAssistance_popup();
                                PreferenceUtils.setAssist(mActivity,assistance_popup);

                                Gson gson = new Gson();
                                mLoggedInObj = gson.fromJson(PreferenceUtils.getDocPortalLoggedInObj(mActivity), LoginResponse.class);

                                AccountSettingsResponse accountSettingsResponse = new AccountSettingsResponse();
                                accountSettingsResponse.setUser_Id(mLoggedInObj.getUserId());
                                accountSettingsResponse.setUser_Name(mLoggedInObj.getUserName());
                                accountSettingsResponse.setAccess_Token(mLoggedInObj.getAccessToken());
                                accountSettingsResponse.setCompany_Name(mLoggedInObj.getCompany_name());
                                accountSettingsResponse.setIs_Terms_Accepted(mLoggedInObj.getTerms_accept());
                                accountSettingsResponse.setIs_Help_Accepted(assistance_popup);
                                accountSettingsResponse.setLogin_Complete_Status(String.valueOf(Constants.Login_Completed));
                                accountSettingsResponse.setIs_Local_Auth_Enabled("0");
                                accountSettingsResponse.setIs_Push_Notification_Enabled("0");

                                AccountSettings accountSettings = new AccountSettings(mActivity);
                                accountSettings.InsertAccountSettings(accountSettingsResponse);
                            }
                        }


                    }
                    else {
                        CommonFunctions.serverErrorExceptions(mActivity, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("Message", t.getMessage());
                    CommonFunctions.showTimeOutError(mActivity, t);
                }
            });
        }
    }


    private void updateLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(mActivity);
        accountSettings.updateLocalAuthEnableStatus(String.valueOf(Constants.Login_Completed));
    }

    private void updateHelpAcceptedAndLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(mActivity);
        accountSettings.updateIsHelpAcceptedAndLoggedInStatus(String.valueOf(Constants.Login_Completed), "0");
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loginInButtonClick();
                } else {
                    Toast.makeText(mActivity, "Storage access permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}

