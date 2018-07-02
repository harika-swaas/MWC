package com.swaas.mwc.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.LoginRequest;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Service.LoginService;
import com.swaas.mwc.FTL.FTLActivity;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Login.PinVerificationActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    TextView mNotLoggedInBefore;
    EditText mUserName, mPassword;

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
                if(NetworkUtils.isNetworkAvailable(mActivity)){
                    final ProgressDialog mProgressDialog = new ProgressDialog(mActivity);
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setMessage("Loading...");
                    mProgressDialog.show();
                    Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
                    final LoginService loginService = retrofitAPI.create(LoginService.class);

                    LoginRequest mLoginRequest = new LoginRequest(username,password);

                    String request = new Gson().toJson(mLoginRequest);
                    //Here the json data is add to a hash map with key data
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("data", request);
                    /*mLoginRequest.setUserName(username);
                    mLoginRequest.setPassword(password);*/

                    Call call = loginService.getLogin(params);
                    call.enqueue(new Callback<BaseApiResponse<LoginResponse>>(){
                        @Override
                        public void onResponse(Response<BaseApiResponse<LoginResponse>> response, Retrofit retrofit) {
                            BaseApiResponse apiResponse = response.body();
                            if(apiResponse != null){
                                if(apiResponse.status.isCode() == false) {
                                    LoginResponse mLoginResponse = response.body().getData();

                                    if (mLoginResponse != null) {
                                        mProgressDialog.dismiss();
                                        String accessToken = mLoginResponse.getAccessToken();
                                        PreferenceUtils.setAccessToken(mActivity, accessToken);
                                        startActivity(new Intent(mActivity, FTLActivity.class));
                                        if(mLoginResponse.nextStep != null){
                                            if(mLoginResponse.nextStep.isPin_authentication_required() == true) {
                                                Intent intent = new Intent(mActivity, PinVerificationActivity.class);
                                                startActivity(intent);
                                            }
                                            else
                                            {
                                                Intent intent = new Intent(mActivity, PinVerificationActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    } else {
                                        String mMessage = apiResponse.status.getMessage().toString();
                                        mProgressDialog.dismiss();
                                        Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    String mMessage = apiResponse.status.getMessage().toString();
                                    mProgressDialog.dismiss();
                                    Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mProgressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e("LoginErr", t.toString());
                            mProgressDialog.dismiss();
                        }
                    });
                }
            }
        });
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
}

