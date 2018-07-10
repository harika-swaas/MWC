package com.swaas.mwc.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.koushikdutta.ion.builder.Builders;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.SetTermsAcceptanceRequest;
import com.swaas.mwc.API.Model.UserPreference_GuideRequest;
import com.swaas.mwc.API.Model.VerifyFTLResponse;
import com.swaas.mwc.API.Service.SetTermsAcceptanceService;
import com.swaas.mwc.API.Service.SetUserPreference_GuideService;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Login.Dashboard;
import com.swaas.mwc.Login.LoginHelpUserGuideActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by harika on 05-07-2018.
 */

public class LoginHelpUserGuideFragment extends Fragment {

    LoginHelpUserGuideActivity mActivity;
    View mView;
    Button submit;
    TextView cancel;
    CheckBox checkBox;
    Boolean check= false;
    String mAccessToken;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (LoginHelpUserGuideActivity) getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.login_help_user_guide_fragment, container, false);
        intializeViews();
        addListenersToViews();


        return mView;
    }

    private void intializeViews() {
        submit = (Button)mView.findViewById(R.id.got_it_button);
        cancel =(TextView)mView.findViewById(R.id.cancel_action);
        checkBox=(CheckBox)mView.findViewById(R.id.checkbox_user_guide);
    }
    private void addListenersToViews(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkBox.isChecked()){
                    setUserPreferences();
                }
                //API  API : set-user-preferences Body : {"assistance_popup":0}
                Intent intent = new Intent(mActivity, Dashboard.class);
                startActivity(intent);
                updateLocalAuthAndLoggedInStatus();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (mActivity, Dashboard.class);
                startActivity(intent);
                updateLoggedInStatus();
            }
        });


    }

    public void setUserPreferences(){
     if (NetworkUtils.isNetworkAvailable(mActivity)) {
        mAccessToken = mActivity.getIntent().getStringExtra(Constants.ACCESSTOKEN);
        final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
        dialog.show();
        Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

        final SetUserPreference_GuideService setUserPreference_guideService = retrofitAPI.create(SetUserPreference_GuideService.class);

         UserPreference_GuideRequest userPreference_guideRequest = new UserPreference_GuideRequest(1);
        String request = new Gson().toJson(userPreference_guideRequest);
        //Here the json data is add to a hash map with key data
        Map<String, String> params = new HashMap<String, String>();
        params.put("data", request);

        Call call = setUserPreference_guideService.getSetUserPreferences(params, mAccessToken);

        call.enqueue(new Callback<BaseApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Response<BaseApiResponse<LoginResponse>> response, Retrofit retrofit) {
                BaseApiResponse apiResponse = response.body();
                if (apiResponse != null) {
                    dialog.dismiss();
                    if (apiResponse.status.isCode() == false) {
                        String mMessage = apiResponse.status.getMessage().toString();
                        // Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                        Intent mIntent = new Intent(mActivity, Dashboard.class);
                        startActivity(mIntent);
                        mActivity.finish();
                    } else {
                        String mMessage = apiResponse.status.getMessage().toString();
                        mActivity.showMessagebox(mActivity, mMessage, null, false);
                        // Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

    private void updateLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(mActivity);
        accountSettings.updateLocalAuthEnableStatus(String.valueOf(Constants.Assistance_Popup_Completed));
    }

    private void updateLocalAuthAndLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(mActivity);
        accountSettings.updateLocalAuthEnableAndLoggedInStatus(String.valueOf(Constants.Assistance_Popup_Completed), String.valueOf(Constants.Assistance_Popup_Completed));
    }
}
