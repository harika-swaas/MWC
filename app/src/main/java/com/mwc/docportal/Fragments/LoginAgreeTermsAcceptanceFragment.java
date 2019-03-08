package com.mwc.docportal.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.GetTermsPageContentResponse;
import com.mwc.docportal.API.Model.SetTermsAcceptanceRequest;
import com.mwc.docportal.API.Model.VerifyFTLResponse;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.GetTermsPageContentService;
import com.mwc.docportal.API.Service.SetTermsAcceptanceService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.FTL.WebviewLoaderTermsActivity;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Login.LoginAgreeTermsAcceptanceActivity;
import com.mwc.docportal.Login.LoginHelpUserGuideActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Online_PdfView_Activity;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (LoginAgreeTermsAcceptanceActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.login_terms_acceptance_fragment, container, false);

        getHelpUserGuideStatus();
        intializeViews();
        getTermsPageContent();
        setButtonBackgroundColor();
        addListenersToViews();
        return mView;
    }

    private void getHelpUserGuideStatus()
    {
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

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {
                            GetTermsPageContentResponse mGetTermsPageContentResponse = response.body().getData();
                            if (mGetTermsPageContentResponse != null) {
                                mTermsBody = mGetTermsPageContentResponse.getTerms_body();
                                mTermsURL = mGetTermsPageContentResponse.getTerms_url();

                                setTermsBody(mTermsBody, mTermsURL);
                            }
                        }

                    }else {
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

                String extension = "";
                if(mUri != null && !mUri.isEmpty())
                {
                    extension = mUri.substring(mUri.lastIndexOf(".")+1);

                }

                if(extension.equalsIgnoreCase("pdf"))
                {
                    /*String access_Token = PreferenceUtils.getAccessToken(mActivity);
                    byte[] encodeValue = Base64.encode(access_Token.getBytes(), Base64.DEFAULT);
                    String base64AccessToken = new String(encodeValue);
                    String urlData = mUri+"&token="+base64AccessToken;*/

                    Intent intent = new Intent(mActivity, Online_PdfView_Activity.class);
                    intent.putExtra("mode",1);
                    intent.putExtra("url", mUri);
                    intent.putExtra("Terms_Title", "Terms & Privacy Policy");
                    mActivity.startActivity(intent);
                }
                else {
                    Intent mIntent = new Intent(mActivity, WebviewLoaderTermsActivity.class);
                    mIntent.putExtra(Constants.SETASSISTANCEPOPUPCONTENTURL,mUri);
                    mIntent.putExtra("Terms_Title", "Terms & Privacy Policy");
                    startActivity(mIntent);
                }

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

    private void setButtonBackgroundColor() {

        getWhiteLabelProperities();

        if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
            String mobileItemEnableColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            String mobileItemDisableColor = mWhiteLabelResponses.get(0).getItem_Unselected_Color();

            int itemEnableColor = Color.parseColor(mobileItemEnableColor);
            int itemDisableColor = Color.parseColor(mobileItemDisableColor);

            if (mobileItemEnableColor != null) {
                // Initialize a new GradientDrawable
                GradientDrawable shape = new GradientDrawable();

                // Specify the shape of drawable
                shape.setShape(GradientDrawable.RECTANGLE);

                // Make the border rounded
                shape.setCornerRadius(50f);

                // Set the fill color of drawable
                shape.setColor(itemEnableColor);

                mIAgreeButton.setBackgroundDrawable(shape);
            } else if (mobileItemDisableColor != null) {
                // Initialize a new GradientDrawable
                GradientDrawable shape = new GradientDrawable();

                // Specify the shape of drawable
                shape.setShape(GradientDrawable.RECTANGLE);

                // Make the border rounded
                shape.setCornerRadius(50f);

                // Set the fill color of drawable
                shape.setColor(itemDisableColor);

                mIAgreeButton.setBackgroundDrawable(shape);
            }
        } else {
            mIAgreeButton.setBackgroundResource(R.drawable.next);
        }
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

                AccountSettings accountSettings = new AccountSettings(mActivity);
                accountSettings.LogouData();

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
            //Here the json data is add to a hash map with gkey data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = setTermsAcceptanceService.getTermsAcceptance(params, PreferenceUtils.getAccessToken(mActivity));

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

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {

                            updateIsTermsAcceptedAndLoggedInStatus();
                            //update help user guide status

                            if(mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0)
                            {
                                if(mAccountSettingsResponses.get(0).getIs_Help_Accepted().equals("1"))
                                {
                                    Intent mIntent = new Intent(mActivity, LoginHelpUserGuideActivity.class);
                                    startActivity(mIntent);
                                    mActivity.finish();
                                }
                                else
                                {
                                    Intent mIntent = new Intent(mActivity, NavigationMyFolderActivity.class);
                                    startActivity(mIntent);
                                    mActivity.finish();
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
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(mActivity, t);
                }
            });
        }
    }

    private void updateIsTermsAcceptedAndLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(mActivity);
        accountSettings.updateTermsAcceptedAndLoggedInStatus(String.valueOf(Constants.GDPR_Completed), "1");
    }
}
