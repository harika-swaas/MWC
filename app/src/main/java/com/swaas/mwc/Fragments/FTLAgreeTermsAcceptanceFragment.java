package com.swaas.mwc.Fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.AccountSettingsResponse;
import com.swaas.mwc.API.Model.AddFTLDetailsRequest;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.FTLPINResponse;
import com.swaas.mwc.API.Model.GetUserPreferencesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.SetTermsAcceptanceRequest;
import com.swaas.mwc.API.Model.UpdateFTLStatusRequest;
import com.swaas.mwc.API.Model.VerifyFTLResponse;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.API.Service.FTLProcessService;
import com.swaas.mwc.API.Service.GetUserPreferencesService;
import com.swaas.mwc.API.Service.SetTermsAcceptanceService;
import com.swaas.mwc.API.Service.UpdateFTLStatusService;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.FTL.FTLAgreeTermsAcceptanceActivity;
import com.swaas.mwc.FTL.WebviewLoaderTermsActivity;
import com.swaas.mwc.Login.Authenticate;
import com.swaas.mwc.Login.Dashboard;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Login.Touchid;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    ImageView mBackIv;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    FTLPINResponse mLoggedInObj;

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
        setButtonBackgroundColor();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        setAcceptanceTerms = (TextView) mView.findViewById(R.id.set_terms_acceptance_txt);
        letsStartButton = (Button) mView.findViewById(R.id.lets_start);
        mBackIv = (ImageView) mView.findViewById(R.id.back_image_view);
    }

    private void getIntentData() {

        if (mActivity.getIntent() != null) {
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
                Intent mIntent = new Intent(mActivity,WebviewLoaderTermsActivity.class);
                mIntent.putExtra(Constants.SETTERMS,mUri);
                startActivity(mIntent);
               // Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUri));
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

    public void setClickableString(String clickableValue, String wholeValue, TextView yourTextView) {
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

    private void setButtonBackgroundColor() {

        getWhiteLabelProperities();

        if(mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
            String mobileItemEnableColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            int itemEnableColor = Color.parseColor(mobileItemEnableColor);

            if (mobileItemEnableColor != null) {

                // Initialize a new GradientDrawable
                GradientDrawable shape = new GradientDrawable();

                // Specify the shape of drawable
                shape.setShape(GradientDrawable.RECTANGLE);

                // Make the border rounded
                shape.setCornerRadius(50f);

                // Set the fill color of drawable
                shape.setColor(itemEnableColor);

                letsStartButton.setBackgroundDrawable(shape);
            } else {
                letsStartButton.setBackgroundResource(R.drawable.next);
            }
        } else {
            letsStartButton.setBackgroundResource(R.drawable.next);
        }

        /*String mobileItemEnableColor = PreferenceUtils.getMobileItemEnableColor(mActivity);
        String mobileItemDisableColor = PreferenceUtils.getMobileItemDisableColor(mActivity);

        int itemEnableColor = Color.parseColor(mobileItemEnableColor);
        int itemDisableColor = Color.parseColor(mobileItemDisableColor);*/
    }

    private void getWhiteLabelProperities() {

        AccountSettings accountSettings = new AccountSettings(mActivity);
        accountSettings.SetWhiteLabelCB(new AccountSettings.GetWhiteLabelCB() {
            @Override
            public void getWhiteLabelSuccessCB(List<WhiteLabelResponse> whiteLabelResponses) {
                if(whiteLabelResponses != null && whiteLabelResponses.size() > 0){
                    mWhiteLabelResponses = whiteLabelResponses;
                }
            }

            @Override
            public void getWhiteLabelFailureCB(String message) {

            }
        });

        accountSettings.getWhiteLabelProperties();
    }

    private void addListenersToViews() {

        letsStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFTLDetails();
            }
        });

        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
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
                            mActivity.showMessagebox(mActivity, mMessage, null, false);
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
                            insertAccountSettings();
                            Intent mIntent = new Intent(mActivity, Touchid.class);
                            mIntent.putExtra(Constants.IS_FROM_FTL,true);
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

    private void insertAccountSettings() {
        getUserPreferences();
    }

    private void getUserPreferences() {

        if(NetworkUtils.isNetworkAvailable(mActivity)){

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final GetUserPreferencesService getUserPreferencesService = retrofitAPI.create(GetUserPreferencesService.class);

            Call call = getUserPreferencesService.getUserPreferences(PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<BaseApiResponse<GetUserPreferencesResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<GetUserPreferencesResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        String assistance_popup = "";
                        if (apiResponse.status.isCode() == false) {
                            GetUserPreferencesResponse mGetUserPreferencesResponse = response.body().getData();
                            if(mGetUserPreferencesResponse != null) {
                                assistance_popup = mGetUserPreferencesResponse.getAssistance_popup();
                            }

                        } else {

                        }

                        Gson gson = new Gson();
                        mLoggedInObj = gson.fromJson(PreferenceUtils.getDocPortalFTLLoggedObj(mActivity), FTLPINResponse.class);

                        AccountSettingsResponse accountSettingsResponse = new AccountSettingsResponse();
                        accountSettingsResponse.setUser_Id(mLoggedInObj.getUserId());
                        accountSettingsResponse.setUser_Name(mLoggedInObj.getUserName());
                        accountSettingsResponse.setAccess_Token(mLoggedInObj.getAccessToken());
                        accountSettingsResponse.setCompany_Name(mLoggedInObj.getCompany_name());
                        accountSettingsResponse.setIs_Terms_Accepted("1");
                        accountSettingsResponse.setIs_Help_Accepted(assistance_popup);
                        accountSettingsResponse.setTerms_URL("");
                        accountSettingsResponse.setLogin_Complete_Status(String.valueOf(Constants.Login_Completed));
                        accountSettingsResponse.setIs_Local_Auth_Enabled("0");
                        accountSettingsResponse.setIs_Push_Notification_Enabled("0");

                        AccountSettings accountSettings = new AccountSettings(mActivity);
                        accountSettings.InsertAccountSettings(accountSettingsResponse);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("PINVerErr",t.getMessage());
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setButtonBackgroundColor();
    }
}
