package com.swaas.mwc.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
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
import com.squareup.okhttp.OkHttpClient;
import com.swaas.mwc.API.Model.AccountSettingsResponse;
import com.swaas.mwc.API.Model.ApiResponse;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.LoginRequest;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Service.LoginService;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.FTL.FTLActivity;
import com.swaas.mwc.FTL.FTLUserValidationActivity;
import com.swaas.mwc.Login.Authenticate;
import com.swaas.mwc.Login.Dashboard;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Login.LoginAgreeTermsAcceptanceActivity;
import com.swaas.mwc.Login.LoginHelpUserGuideActivity;
import com.swaas.mwc.Login.Notifiy;
import com.swaas.mwc.Login.PinVerificationActivity;
import com.swaas.mwc.Login.Touchid;
import com.swaas.mwc.MessageDialog;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;

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
    Authenticate authenticate;
    LoginActivity mActivity;
    View mView;
    Retrofit retrofit;
    Button mSignInButton;
    TextView mNotLoggedInBefore;
    EditText mUserName, mPassword;
    MessageDialog messageDialog;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();

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
        mNotLoggedInBefore = (TextView) mView.findViewById(R.id.not_logged_in_before);
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
                startActivity(new Intent(mActivity, FTLActivity.class));
                mActivity.finish();
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUserName.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (username.equals("")) {
                    String message = "Please provide username";
                    mActivity.showMessagebox(mActivity, message, null, false);
                } else if (password.equals("")) {
                    String message = "Please provide password";
                    mActivity.showMessagebox(mActivity, message, null, false);
                } else {

                    if (NetworkUtils.isNetworkAvailable(mActivity)) {
                        final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
                        dialog.show();
                        Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
                        final LoginService loginService = retrofitAPI.create(LoginService.class);

                        LoginRequest mLoginRequest = new LoginRequest(username, password);

                        String request = new Gson().toJson(mLoginRequest);
                        //Here the json data is add to a hash map with key data
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("data", request);
                        mLoginRequest.setUserName(username);
                        mLoginRequest.setPassword(password);

                        Call call = loginService.getLogin(params);
                        call.enqueue(new Callback<BaseApiResponse<LoginResponse>>() {
                            @Override
                            public void onResponse(Response<BaseApiResponse<LoginResponse>> response, Retrofit retrofit) {
                                BaseApiResponse apiResponse = response.body();
                                if (apiResponse != null) {
                                    if (apiResponse.status.isCode() == false) {
                                        LoginResponse mLoginResponse = response.body().getData();

                                        //setting login response obj to preference utils
                                        Gson gson = new Gson();
                                        PreferenceUtils.setDocPortalLoggedInObj(mActivity, mLoginResponse);

                                        if (mLoginResponse != null) {
                                            dialog.dismiss();
                                            String accessToken = mLoginResponse.getAccessToken();
                                            PreferenceUtils.setAccessToken(mActivity, accessToken);

                                            if (mLoginResponse.nextStep != null) {

                                                if (mLoginResponse.nextStep.isPin_authentication_required() == true) {
                                                    Intent intent = new Intent(mActivity, PinVerificationActivity.class);
                                                    startActivity(intent);
                                                } else if (mLoginResponse.nextStep.isFtl_required() == true) {
                                                    Intent intent = new Intent(mActivity, FTLUserValidationActivity.class);
                                                    startActivity(intent);
                                                }
                                            } else {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                    checkSecurity();
                                                }
                                            }

                                        } else {
                                            String mMessage = apiResponse.status.getMessage().toString();

                                            dialog.dismiss();
                                            Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        String mMessage = apiResponse.status.getMessage().toString();
                                        dialog.dismiss();
                                        Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    String mMessage = apiResponse.status.getMessage().toString();
                                    dialog.dismiss();
                                    Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.e("LoginErr", t.toString());
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }
        });
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
        } else {
            Intent intent = new Intent(mActivity, Notifiy.class);
            startActivity(intent);
            mActivity.finish();
        }
    }
}

