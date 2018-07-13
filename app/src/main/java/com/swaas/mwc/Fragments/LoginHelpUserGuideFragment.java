package com.swaas.mwc.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.GetAssistancePopupContentResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.UserPreferenceGuideRequest;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.API.Service.GetAssistancePopupService;
import com.swaas.mwc.API.Service.SetUserPreferenceGuideService;
import com.swaas.mwc.DMS.MyFoldersDMSActivity;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.FTL.WebviewLoaderTermsActivity;
import com.swaas.mwc.Login.Dashboard;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Login.LoginHelpUserGuideActivity;
import com.swaas.mwc.Login.Notifiy;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;

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

public class LoginHelpUserGuideFragment extends Fragment {

    LoginHelpUserGuideActivity mActivity;
    View mView;
    Button submit;
    TextView cancel;
    CheckBox checkBox;
    TextView assistancePopupBody;
    String mAssistancePopupBody, mHelpGuideURL;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();

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
        getAssistancePopupContent();
        setButtonBackgroundColor();
        addListenersToViews();

        return mView;
    }

    private void getAssistancePopupContent() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final GetAssistancePopupService mGetAssistancePopupService = retrofitAPI.create(GetAssistancePopupService.class);

            Call call = mGetAssistancePopupService.getAssistancePopupContent(PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<BaseApiResponse<GetAssistancePopupContentResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<GetAssistancePopupContentResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                GetAssistancePopupContentResponse mGetAssistancePopupContentResponse = response.body().getData();
                                if (mGetAssistancePopupContentResponse != null) {
                                    mAssistancePopupBody = mGetAssistancePopupContentResponse.getAssistance_popup_message();
                                    mHelpGuideURL = mGetAssistancePopupContentResponse.getHelp_guide_url();

                                    setAssistancePopupBody(mAssistancePopupBody, mHelpGuideURL);
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

                submit.setBackgroundDrawable(shape);
            } else if (mobileItemDisableColor != null) {
                // Initialize a new GradientDrawable
                GradientDrawable shape = new GradientDrawable();

                // Specify the shape of drawable
                shape.setShape(GradientDrawable.RECTANGLE);

                // Make the border rounded
                shape.setCornerRadius(50f);

                // Set the fill color of drawable
                shape.setColor(itemDisableColor);

                submit.setBackgroundDrawable(shape);
            }
        } else {
            submit.setBackgroundResource(R.drawable.next);
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

    private void setAssistancePopupBody(String mAssistancePopupBody, String mHelpGuideURL) {
        if (!TextUtils.isEmpty(mAssistancePopupBody) && !TextUtils.isEmpty(mHelpGuideURL)) {
            assistancePopupBody.setText(mAssistancePopupBody);
            setLinkTextView(mAssistancePopupBody, mHelpGuideURL);
        }
    }

    private void setLinkTextView(String mAssistancePopupBody, final String mHelpGuideURL) {
        SpannableString spannableString = new SpannableString(mAssistancePopupBody);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                String mUri = mHelpGuideURL;
                Intent mIntent = new Intent(mActivity, WebviewLoaderTermsActivity.class);
                mIntent.putExtra(Constants.SETASSISTANCEPOPUPCONTENTURL, mUri);
                startActivity(mIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.sky_blue));
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };
        spannableString.setSpan(clickableSpan, 83, 93, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        assistancePopupBody.setText(spannableString, TextView.BufferType.SPANNABLE);
        assistancePopupBody.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void intializeViews() {
        assistancePopupBody = (TextView) mView.findViewById(R.id.assistance_popup_body);
        submit = (Button) mView.findViewById(R.id.got_it_button);
        cancel = (TextView) mView.findViewById(R.id.cancel_action);
        checkBox = (CheckBox) mView.findViewById(R.id.checkbox_user_guide);
    }

    private void addListenersToViews() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHelpAcceptedAndLoggedInStatus();
                startActivity(new Intent(mActivity, Dashboard.class));
                mActivity.finish();

                if (checkBox.isChecked()) {
                    setUserPreferences();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, Dashboard.class);
                startActivity(intent);
                mActivity.finish();
                updateLoggedInStatus();
            }
        });
    }

    public void setUserPreferences() {
        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            /*final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
            dialog.show();*/

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final SetUserPreferenceGuideService setUserPreference_guideService = retrofitAPI.create(SetUserPreferenceGuideService.class);

            UserPreferenceGuideRequest mUserPreferenceGuideRequest = new UserPreferenceGuideRequest(0);
            String request = new Gson().toJson(mUserPreferenceGuideRequest);
            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = setUserPreference_guideService.getSetUserPreferences(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<BaseApiResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<LoginResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {

                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                Intent mIntent = new Intent(mActivity, Dashboard.class);
                                startActivity(mIntent);
                                mActivity.finish();
                                updateHelpAcceptedAndLoggedInStatus();
                            } else {
                                String mMessage = apiResponse.status.getMessage().toString();
                                mActivity.showMessagebox(mActivity, mMessage, null, false);
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
                    transparentProgressDialog.dismiss();
                    Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(mActivity);
        accountSettings.updateLocalAuthEnableStatus(String.valueOf(Constants.Assistance_Popup_Completed));
    }

    private void updateHelpAcceptedAndLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(mActivity);
        accountSettings.updateIsHelpAcceptedAndLoggedInStatus(String.valueOf(Constants.Assistance_Popup_Completed), "0");
    }
}
