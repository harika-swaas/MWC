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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.SendPinRequest;
import com.swaas.mwc.API.Model.SetTermsAcceptanceRequest;
import com.swaas.mwc.API.Model.SetTermsAcceptanceResponse;
import com.swaas.mwc.API.Model.VerifyFTLResponse;
import com.swaas.mwc.API.Service.SendPinService;
import com.swaas.mwc.API.Service.SetTermsAcceptanceService;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.FTL.FTLPinVerificationActivity;
import com.swaas.mwc.Login.Dashboard;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Login.LoginAgreeTermsAcceptanceActivity;
import com.swaas.mwc.Login.LoginHelpUserGuideActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
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

public class LoginAgreeTermsAcceptanceFragment extends Fragment {

    LoginAgreeTermsAcceptanceActivity mActivity;
    View mView;
    Button mIAgreeButton;
    TextView cancel;
    String mAccessToken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (LoginAgreeTermsAcceptanceActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.login_terms_acceptance_fragment, container, false);

        intializeViews();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        mIAgreeButton = (Button) mView.findViewById(R.id.agree_button);
        cancel = (TextView) mView.findViewById(R.id.sign_out);
    }

    private void addListenersToViews() {

        mIAgreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                acceptTerms();
                startActivity(new Intent(mActivity, LoginHelpUserGuideActivity.class));
                mActivity.finish();

                updateLocalAuthAndLoggedInStatus();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, LoginActivity.class));
                mActivity.finish();
                updateLoggedInStatus();
            }
        });
    }

    private void acceptTerms() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            mAccessToken = mActivity.getIntent().getStringExtra(Constants.ACCESSTOKEN);
            final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
            dialog.show();
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final SetTermsAcceptanceService setTermsAcceptanceService = retrofitAPI.create(SetTermsAcceptanceService.class);

            SetTermsAcceptanceRequest mSetTermsAcceptanceRequest = new SetTermsAcceptanceRequest(1);
            String request = new Gson().toJson(mSetTermsAcceptanceRequest);
            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = setTermsAcceptanceService.getTermsAcceptance(params, mAccessToken);

            call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        dialog.dismiss();
                        if (apiResponse.status.isCode() == false) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            // Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                            Intent mIntent = new Intent(mActivity, LoginHelpUserGuideActivity.class);
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
        accountSettings.updateLocalAuthEnableStatus(String.valueOf(Constants.GDPR_Completed));
    }

    private void updateLocalAuthAndLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(mActivity);
        accountSettings.updateLocalAuthEnableAndLoggedInStatus(String.valueOf(Constants.GDPR_Completed), String.valueOf(Constants.GDPR_Completed));
    }
}
