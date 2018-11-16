package com.mwc.docportal.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.AddFTLDetailsRequest;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.FTLPINResponse;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.GetUserPreferencesResponse;
import com.mwc.docportal.API.Model.SetTermsAcceptanceRequest;
import com.mwc.docportal.API.Model.VerifyFTLResponse;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.FTLProcessService;
import com.mwc.docportal.API.Service.GetUserPreferencesService;
import com.mwc.docportal.API.Service.SetTermsAcceptanceService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.FileDownloadManager;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.FTL.FTLAgreeTermsAcceptanceActivity;
import com.mwc.docportal.FTL.WebviewLoaderTermsActivity;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Login.Notifiy;
import com.mwc.docportal.Login.Touchid;
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
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    public static final int REQUEST_STORAGE_PERMISSION = 111;

    String company_name;
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
        SpannableString spannableString = new SpannableString(getString(R.string.set_terms_acceptance_txt));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                String mUri = PreferenceUtils.getTermsURL(mActivity);
               /* Intent mIntent = new Intent(mActivity, WebviewLoaderTermsActivity.class);
                mIntent.putExtra(Constants.SETTERMS, mUri);
                mIntent.putExtra("Terms_Title", "Terms & Privacy Policy");
                startActivity(mIntent);*/

                String extension = "";
                if(mUri != null && !mUri.isEmpty())
                {
                    extension = mUri.substring(mUri.lastIndexOf(".")+1);

                }

                if(extension.equalsIgnoreCase("pdf"))
                {
                    Intent intent = new Intent(mActivity, Online_PdfView_Activity.class);
                    intent.putExtra("mode",1);
                    intent.putExtra("url", mUri);
                    intent.putExtra("Terms_Title", "Terms & Privacy Policy");
                    mActivity.startActivity(intent);
                }
                else {
                    Intent mIntent = new Intent(mActivity, WebviewLoaderTermsActivity.class);
                    mIntent.putExtra(Constants.SETASSISTANCEPOPUPCONTENTURL, mUri);
                    mIntent.putExtra("Terms_Title", "Terms & Privacy Policy");
                    startActivity(mIntent);
                }


            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };
       /* spannableString.setSpan(clickableSpan, 41,
                59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/
        spannableString.setSpan(clickableSpan, 49,
                73, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

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

        if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
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

    private void getAccountSettings()
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

    private void getStoragePermissionForLogoDownload(String company_name)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int storagePermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (storagePermission == PackageManager.PERMISSION_GRANTED) {
                downloadLogoImage(Constants.LOGO_IMAGE_BASE_URL+Constants.Logo_ImagePath+company_name+Constants.Logo_Image_Name);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        } else {
            downloadLogoImage(Constants.LOGO_IMAGE_BASE_URL+Constants.Logo_ImagePath+company_name+Constants.Logo_Image_Name);
        }
    }

    private void downloadLogoImage(String imageUrl)
    {
        if (!TextUtils.isEmpty(imageUrl)) {
            FileDownloadManager fileDownloadManager = new FileDownloadManager(mActivity);
            GetCategoryDocumentsResponse categoryDocumentsResponse = new GetCategoryDocumentsResponse();
            categoryDocumentsResponse.setDownloadUrl(imageUrl);
            categoryDocumentsResponse.setDocument_version_id("12345");
            categoryDocumentsResponse.setName("Logo");

            fileDownloadManager.setFileTitle("Logo");
            fileDownloadManager.setDownloadUrl(imageUrl);
            fileDownloadManager.setDigitalAssets(categoryDocumentsResponse);
            fileDownloadManager.setmFileDownloadListener(new FileDownloadManager.FileDownloadListener() {
                @Override
                public void fileDownloadSuccess(String path) {

                    if(path != null && !path.isEmpty())
                    {
                        PreferenceUtils.setLogoImagePath(mActivity, path);
                    }

                    KeyguardManager keyguardManager = (KeyguardManager) mActivity.getSystemService(Context.KEYGUARD_SERVICE);
                    if (keyguardManager.isKeyguardSecure() == true) {
                        Intent intent = new Intent(mActivity, Touchid.class);
                        intent.putExtra(Constants.IS_FROM_FTL, true);
                        startActivity(intent);
                        mActivity.finish();
                    } else {
                        Intent intent = new Intent(mActivity, Notifiy.class);
                        intent.putExtra(Constants.IS_FROM_FTL, true);
                        startActivity(intent);
                        mActivity.finish();
                    }

                }

                @Override
                public void fileDownloadFailure() {

                }
            });
            fileDownloadManager.downloadTheFile();
        }

    }

    private void addFTLDetails() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            /*final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
            dialog.show();*/

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final FTLProcessService ftlProcessService = retrofitAPI.create(FTLProcessService.class);

            final AddFTLDetailsRequest mAddFTLDetailsRequest = new AddFTLDetailsRequest(mUserName, mPassword, "ftl_link");
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

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {
                            setTermsAcceptance(transparentProgressDialog);
                        }


                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeoutAlert(mActivity);
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

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {
                            insertAccountSettings();
                        }

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    dialog.dismiss();
                    CommonFunctions.showTimeoutAlert(mActivity);
                }
            });
        }
    }

    private void insertAccountSettings() {
        getUserPreferences();
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
                            String assistance_popup = "";
                            GetUserPreferencesResponse mGetUserPreferencesResponse = response.body().getData();
                            if (mGetUserPreferencesResponse != null) {
                                assistance_popup = mGetUserPreferencesResponse.getAssistance_popup();
                                PreferenceUtils.setAssist(mActivity,assistance_popup);

                                Gson gson = new Gson();
                                mLoggedInObj = gson.fromJson(PreferenceUtils.getDocPortalFTLLoggedObj(mActivity), FTLPINResponse.class);

                                AccountSettingsResponse accountSettingsResponse = new AccountSettingsResponse();
                                accountSettingsResponse.setUser_Id(mLoggedInObj.getUserId());
                              //  accountSettingsResponse.setUser_Name(mLoggedInObj.getUserName());
                                // As per Changes
                                accountSettingsResponse.setUser_Name(mUserName);
                                accountSettingsResponse.setAccess_Token(mLoggedInObj.getAccessToken());
                                accountSettingsResponse.setCompany_Name(mLoggedInObj.getCompany_name());
                                accountSettingsResponse.setIs_Terms_Accepted("1");
                                accountSettingsResponse.setIs_Help_Accepted(assistance_popup);
                                accountSettingsResponse.setTerms_URL(PreferenceUtils.getTermsURL(mActivity));
                                accountSettingsResponse.setLogin_Complete_Status(String.valueOf(Constants.Login_Completed));
                                accountSettingsResponse.setIs_Local_Auth_Enabled("0");
                                accountSettingsResponse.setIs_Push_Notification_Enabled("0");

                                AccountSettings accountSettings = new AccountSettings(mActivity);
                                accountSettings.InsertAccountSettings(accountSettingsResponse);

                                company_name = mLoggedInObj.getCompany_name();
                                getStoragePermissionForLogoDownload(mLoggedInObj.getCompany_name());

                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("ErrorLog", t.getMessage());
                    CommonFunctions.showTimeoutAlert(mActivity);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setButtonBackgroundColor();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getStoragePermissionForLogoDownload(company_name);
                } else {
                    getStoragePermissionForLogoDownload(company_name);
                }
                break;
        }
    }
}
