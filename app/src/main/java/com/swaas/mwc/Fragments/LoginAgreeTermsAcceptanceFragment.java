package com.swaas.mwc.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.FTLProcessResponse;
import com.swaas.mwc.API.Model.GetTermsPageContentResponse;
import com.swaas.mwc.API.Model.SetTermsAcceptanceRequest;
import com.swaas.mwc.API.Model.VerifyFTLResponse;
import com.swaas.mwc.API.Service.GetTermsPageContentService;
import com.swaas.mwc.API.Service.SetTermsAcceptanceService;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.FTL.WebviewLoaderTermsActivity;
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
    TextView termsBody;
    Button mIAgreeButton;
    TextView cancel;
    String mTermsBody, mTermsURL;

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
        getTermsPageContent();
        addListenersToViews();
        return mView;
    }

    private void getTermsPageContent() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final GetTermsPageContentService mGetTermsPageContentService = retrofitAPI.create(GetTermsPageContentService.class);

            Call call = mGetTermsPageContentService.getTermsPageContent(PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<BaseApiResponse<GetTermsPageContentResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<GetTermsPageContentResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() instanceof Boolean) {

                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                GetTermsPageContentResponse mGetTermsPageContentResponse = response.body().getData();
                                if (mGetTermsPageContentResponse != null) {
                                    mTermsBody = mGetTermsPageContentResponse.getTerms_body();
                                    mTermsURL = mGetTermsPageContentResponse.getTerms_url();

                                    setTermsBody(mTermsBody, mTermsURL);
                                }
                            } else {

                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {

                            String mMessage = apiResponse.status.getMessage().toString();
                            mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(mActivity, LoginActivity.class));
                                    mActivity.finish();
                                }
                            }, false);
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                }
            });
        }
    }

    private void setTermsBody(String mTermsBody, String mTermsURL) {
        if (!TextUtils.isEmpty(mTermsBody) && !TextUtils.isEmpty(mTermsURL)) {
            termsBody.setText(mTermsBody);
            setLinkTextView(mTermsBody, mTermsURL);
        }
    }

    private void setLinkTextView(String mTermsBody, final String mTermsURL) {
        SpannableString spannableString = new SpannableString(mTermsBody);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                String mUri = mTermsURL;
                Intent mIntent = new Intent(mActivity, WebviewLoaderTermsActivity.class);
                mIntent.putExtra(Constants.SETTERMSPAGECONTENTURL, mUri);
                startActivity(mIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.sky_blue));
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };
        spannableString.setSpan(clickableSpan, 153, 170, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsBody.setText(spannableString, TextView.BufferType.SPANNABLE);
        termsBody.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void intializeViews() {

        termsBody = (TextView) mView.findViewById(R.id.set_terms_acceptance_txt);
        mIAgreeButton = (Button) mView.findViewById(R.id.agree_button);
        cancel = (TextView) mView.findViewById(R.id.sign_out);
    }

    private void addListenersToViews() {

        mIAgreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                acceptTerms();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(mActivity, LoginActivity.class));
                mActivity.finish();
            }
        });
    }

    private void acceptTerms() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final SetTermsAcceptanceService setTermsAcceptanceService = retrofitAPI.create(SetTermsAcceptanceService.class);

            SetTermsAcceptanceRequest mSetTermsAcceptanceRequest = new SetTermsAcceptanceRequest(1);
            String request = new Gson().toJson(mSetTermsAcceptanceRequest);
            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = setTermsAcceptanceService.getTermsAcceptance(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        transparentProgressDialog.dismiss();

                        if(apiResponse.status.getCode() instanceof Boolean) {

                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                Intent mIntent = new Intent(mActivity, LoginHelpUserGuideActivity.class);
                                startActivity(mIntent);
                                mActivity.finish();
                                updateIsTermsAcceptedAndLoggedInStatus();
                            } else {
                                String mMessage = apiResponse.status.getMessage().toString();
                                mActivity.showMessagebox(mActivity, mMessage, null, false);
                            }

                        } else if(apiResponse.status.getCode() instanceof Integer) {

                            String mMessage = apiResponse.status.getMessage().toString();
                            mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(mActivity, LoginActivity.class));
                                    mActivity.finish();
                                }
                            }, false);
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                }
            });
        }
    }

    private void updateIsTermsAcceptedAndLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(mActivity);
        accountSettings.updateTermsAcceptedAndLoggedInStatus(String.valueOf(Constants.GDPR_Completed), "1");
    }
}
