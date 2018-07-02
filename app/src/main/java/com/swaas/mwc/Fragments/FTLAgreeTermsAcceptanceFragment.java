package com.swaas.mwc.Fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.AddFTLDetailsRequest;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.SetTermsAcceptanceRequest;
import com.swaas.mwc.API.Model.UpdateFTLStatusRequest;
import com.swaas.mwc.API.Model.VerifyFTLResponse;
import com.swaas.mwc.API.Service.FTLProcessService;
import com.swaas.mwc.API.Service.SetTermsAcceptanceService;
import com.swaas.mwc.API.Service.UpdateFTLStatusService;
import com.swaas.mwc.FTL.FTLAgreeTermsAcceptanceActivity;
import com.swaas.mwc.Login.LoginActivity;
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
 * Created by harika on 25-06-2018.
 */

public class FTLAgreeTermsAcceptanceFragment extends Fragment {

    FTLAgreeTermsAcceptanceActivity mActivity;
    View mView;
    Button letsStartButton;
    String mUserName, mPassword;
    String mAccessToken, mTerms;
    TextView setAcceptanceTerms;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FTLAgreeTermsAcceptanceActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ftl_agree_terms_acceptance_fragment, container, false);

        intializeViews();
        getIntentData();
        setLinkTextView();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        setAcceptanceTerms = (TextView) mView.findViewById(R.id.set_terms_acceptance_txt);
        letsStartButton = (Button) mView.findViewById(R.id.lets_start);
    }

    private void getIntentData() {

        if(mActivity.getIntent() != null){
            mUserName = mActivity.getIntent().getStringExtra(Constants.USERNAME);
            mPassword = mActivity.getIntent().getStringExtra(Constants.PASSWORD);
            mAccessToken = mActivity.getIntent().getStringExtra(Constants.ACCESSTOKEN);
            mTerms = mActivity.getIntent().getStringExtra(Constants.SETTERMS);
        }
    }

    private void setLinkTextView() {
        /*String completeString = getResources().getString(R.string.set_terms_acceptance_txt);
        String partToClick = "Terms,Data Policy";
        setClickableString(partToClick,completeString,setAcceptanceTerms);*/

        SpannableString spannableString = new SpannableString(getString(R.string.set_terms_acceptance_txt));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                String mUri = PreferenceUtils.getTermsURL(mActivity);
                Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUri));
                startActivity(mIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };
        spannableString.setSpan(clickableSpan, 41,
                59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setAcceptanceTerms.setText(spannableString,
                TextView.BufferType.SPANNABLE);
        setAcceptanceTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setClickableString(String clickableValue, String wholeValue, TextView yourTextView){
        String value = wholeValue;
        SpannableString spannableString = new SpannableString(value);
        int startIndex = value.indexOf(clickableValue);
        int endIndex = startIndex + clickableValue.length();
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // this is where you set link color, underline, typeface etc.
                int linkColor = ContextCompat.getColor(mActivity, R.color.applied);
                ds.setColor(linkColor);
                ds.setUnderlineText(false); // <-- this will remove automatic underline in set span
            }

            @Override
            public void onClick(View widget) {
                // do what you want with clickable value
                String mUri = PreferenceUtils.getTermsURL(mActivity);
                Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUri));
                startActivity(mIntent);
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        yourTextView.setText(spannableString);
        yourTextView.setMovementMethod(LinkMovementMethod.getInstance()); // <-- important, onClick in ClickableSpan won't work without this
    }

    private void addListenersToViews() {

        letsStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFTLDetails();
            }
        });
    }

    private void addFTLDetails() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
            dialog.show();

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final FTLProcessService ftlProcessService = retrofitAPI.create(FTLProcessService.class);

            AddFTLDetailsRequest mAddFTLDetailsRequest = new AddFTLDetailsRequest(mUserName, mPassword, "ftl_link");
            String request = new Gson().toJson(mAddFTLDetailsRequest);
            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = ftlProcessService.getAddFTLDetails(params, mAccessToken);

            call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status.isCode() == false) {
                            setTermsAcceptance(dialog);
                        } else {
                            dialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();
                            mActivity.showMessagebox(mActivity,mMessage,null,false);
                          //  Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    dialog.dismiss();
                   // Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setTermsAcceptance(final AlertDialog dialog) {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

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
                            Intent mIntent = new Intent(mActivity, LoginActivity.class);
                            startActivity(mIntent);
                            mActivity.finish();
                        } else {
                            String mMessage = apiResponse.status.getMessage().toString();
                            mActivity.showMessagebox(mActivity,mMessage,null,false);
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
}
