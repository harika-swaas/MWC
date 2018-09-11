package com.mwc.docportal.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.FTLPINResponse;
import com.mwc.docportal.API.Model.GetAssistancePopupContentResponse;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.GetTermsPageContentResponse;
import com.mwc.docportal.API.Model.GetUISettingsResponse;
import com.mwc.docportal.API.Model.GetUserPreferencesResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.SendPinRequest;
import com.mwc.docportal.API.Model.VerifyFTLPINRequest;
import com.mwc.docportal.API.Model.VerifyFTLRequest;
import com.mwc.docportal.API.Model.VerifyFTLResponse;
import com.mwc.docportal.API.Model.VerifyPinRequest;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.GetAssistancePopupService;
import com.mwc.docportal.API.Service.GetTermsPageContentService;
import com.mwc.docportal.API.Service.GetUISettingsService;
import com.mwc.docportal.API.Service.GetUserPreferencesService;
import com.mwc.docportal.API.Service.SendFTLPINService;
import com.mwc.docportal.API.Service.SendPinService;
import com.mwc.docportal.API.Service.VerifyFTLPINService;
import com.mwc.docportal.API.Service.VerifyPinService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.FileDownloadManager;
import com.mwc.docportal.Components.LinkTextView;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.OffLine_Files_Repository;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.FTL.FTLPinVerificationActivity;
import com.mwc.docportal.FTL.FTLRegistrationActivity;
import com.mwc.docportal.FTL.FTLUserValidationActivity;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Login.Notifiy;
import com.mwc.docportal.Login.Touchid;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;
import com.mwc.docportal.pdf.PdfViewActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by harika on 21-06-2018.
 */

public class FTLPinVerificationFragment extends Fragment {

    FTLPinVerificationActivity mActivity;
    View mView;
    Button mNext;
    TextInputLayout inputLayoutPINNumber;
    static EditText inputPIN;
    String mEmail, mMobile;
    LinkTextView linkResendView;
    ImageView mBackIv;
    boolean isFromLogin;
    AlertDialog mAlertDialog;
    AlertDialog mBackDialog;
    private LoginResponse mLoggedInObj;
    String message;
    static String Otp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FTLPinVerificationActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ftl_pin_verification_fragment, container, false);

        intializeViews();
        getIntentData();
        addListenersToViews();
        return mView;
    }

    /*private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                inputPIN.setText(message);
            }
        }
    };*/

    public static void receivedSms(final String message) {

        Otp = message.substring(17, 25);

        if(Otp != null) {
            inputPIN.setText(Otp);
        }
    }

    /*public void receivedSms(String message) {
        try
        {
            Otp= message.substring(17,26);
            if(Otp != null) {
                inputPIN.setText(Otp);
            }
        }

        catch (Exception e) {
        }
    }*/

    private void intializeViews() {

        linkResendView = (LinkTextView) mView.findViewById(R.id.resend_pin);
        inputLayoutPINNumber = (TextInputLayout) mView.findViewById(R.id.input_layout_pin_number);
        inputPIN = (EditText) mView.findViewById(R.id.input_pin_number);
        mNext = (Button) mView.findViewById(R.id.next_button);
        mBackIv = (ImageView) mView.findViewById(R.id.back_image_view);
    }

    private void getIntentData() {

        if (mActivity.getIntent() != null) {
            mEmail = mActivity.getIntent().getStringExtra(Constants.EMAIL);
            mMobile = mActivity.getIntent().getStringExtra(Constants.MOBILE);
            isFromLogin = mActivity.getIntent().getBooleanExtra(Constants.IS_FROM_LOGIN, false);
        }
    }


    private void addListenersToViews() {
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPIN.addTextChangedListener(new FTLPinVerificationFragment.MyTextWatcher(inputPIN));

                verifyFTLPINDetails();
                verifyPinProcess();
            }
        });

        linkResendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFromLogin) {
                    sendPin();
                } else {
                    sendFTLPin();
                }
            }
        });

        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mActivity.onBackPressed();

                if (!isFromLogin) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.back_custom_alert_layout, null);
                    builder.setView(view);
                    builder.setCancelable(false);

                    Button yesButton = (Button) view.findViewById(R.id.yes_button);
                    Button noButton = (Button) view.findViewById(R.id.no_button);

                    yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBackDialog.dismiss();
                            mActivity.startActivity(new Intent(mActivity, FTLRegistrationActivity.class));
                        }
                    });

                    noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBackDialog.dismiss();
                        }
                    });

                    mBackDialog = builder.create();
                    mBackDialog.show();
                } else {
                    mActivity.startActivity(new Intent(mActivity,LoginActivity.class));
                }
            }
        });

        inputPIN.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void verifyFTLPINDetails() {

        if (!validatePIN()) {
            return;
        }
    }

    private boolean validatePIN() {
        String pinNumber = inputPIN.getText().toString().trim();

        if (pinNumber.isEmpty() && pinNumber.length() == 0) {
            inputLayoutPINNumber.setError(getString(R.string.err_msg_pin_number));
            requestFocus(inputPIN);
            return false;
        } else {
            inputLayoutPINNumber.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_pin_number:
                    validatePIN();
                    break;
            }
        }
    }


    private void verifyPinProcess() {

        if (validatePIN()) {
            if (isFromLogin) {
                verifyPINInLoginProcess();
            } else {
                verifyPINInFTLProcess();
            }
        }
    }

    private void verifyPINInFTLProcess() {

        String pinNo = inputPIN.getText().toString().trim();

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            /*final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();*/

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            VerifyFTLPINService verifyFTLPINService = retrofitAPI.create(VerifyFTLPINService.class);

            VerifyFTLPINRequest mVerifyFTLPINRequest = null;

            if (!TextUtils.isEmpty(mMobile)) {
                mVerifyFTLPINRequest = new VerifyFTLPINRequest(mEmail, mMobile, Integer.parseInt(pinNo));
            } else {
                mVerifyFTLPINRequest = new VerifyFTLPINRequest(mEmail, null, Integer.parseInt(pinNo));
            }

            String request = new Gson().toJson(mVerifyFTLPINRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = verifyFTLPINService.getVerifyFTLPIN(params);

            call.enqueue(new Callback<BaseApiResponse<FTLPINResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<FTLPINResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() instanceof Boolean) {

                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                FTLPINResponse mFTLPINResponse = response.body().getData();
                                if (mFTLPINResponse != null) {
                                    String accessToken = mFTLPINResponse.getAccessToken();
                                    PreferenceUtils.setAccessToken(mActivity, accessToken);

                                    if (mFTLPINResponse.nextStep != null) {
                                        if (mFTLPINResponse.nextStep.isFtl_required() == true) {
                                            getFTLUISettings(accessToken, mFTLPINResponse);

                                        }
                                    }
                                }
                            } else {
                                transparentProgressDialog.dismiss();
                                String mMessage = apiResponse.status.getMessage().toString();

                                FTLPINResponse mFTLPINResponse = response.body().getData();
                                if (mFTLPINResponse != null) {
                                    if (mFTLPINResponse.isRequest_pin() == true) {

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                        builder.setView(view);
                                        builder.setCancelable(false);

                                        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                                        txtMessage.setText(mMessage);

                                        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                        sendPinButton.setText("SEND PIN AGAIN");

                                        sendPinButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mAlertDialog.dismiss();
                                                sendFTLPin();
                                            }
                                        });

                                        cancelButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mAlertDialog.dismiss();
                                            }
                                        });

                                        mAlertDialog = builder.create();
                                        mAlertDialog.show();

                                    } else if (mFTLPINResponse.isFtl_complete() == true) {
                                        // Showing Alert Dialog
                                        AlertDialog mDialog = new AlertDialog.Builder(mActivity).create();
                                        // Setting Dialog Title
                                        mDialog.setTitle("Alert");
                                        // Setting Dialog Message
                                        mDialog.setMessage(mMessage);

                                        // Setting OK Button
                                        mDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Write your code here to execute after dialog closed
                                                mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                                            }
                                        });

                                        // Showing Alert Message
                                        mDialog.show();
                                    }
                                } else {
                                    mActivity.showMessagebox(mActivity, mMessage, null, false);
                                    //  Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                                    }
                                }, false);
                            }
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

    private void getFTLUISettings(String accessToken, FTLPINResponse mFTLPINResponse)
    {
        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final GetUISettingsService getUISettingsService = retrofitAPI.create(GetUISettingsService.class);

            Call call = getUISettingsService.getUISettings(PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<BaseApiResponse<GetUISettingsResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<GetUISettingsResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() instanceof Boolean) {

                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                GetUISettingsResponse mGetUISettingsResponse = response.body().getData();

                                if (mGetUISettingsResponse != null) {

                                    if (mGetUISettingsResponse.ui_properties != null) {

                                        String mobileItemEnableColor = mGetUISettingsResponse.ui_properties.getMobile_item_enable_color();
                                        String mobileItemDisableColor = mGetUISettingsResponse.ui_properties.getMobile_item_disable_color();
                                        String splashScreenColor = mGetUISettingsResponse.ui_properties.getMobile_splash_screen_background_color();
                                        String folderColor = mGetUISettingsResponse.ui_properties.getMobile_folder_color();

                                        AccountSettings accountSettings = new AccountSettings(mActivity);
                                        WhiteLabelResponse whiteLabelResponse = new WhiteLabelResponse();
                                        whiteLabelResponse.setItem_Selected_Color(mobileItemEnableColor);
                                        whiteLabelResponse.setItem_Unselected_Color(mobileItemDisableColor);
                                        whiteLabelResponse.setSplash_Screen_Color(splashScreenColor);
                                        whiteLabelResponse.setFolder_Color(folderColor);

                                        accountSettings.InsertWhiteLabelDetails(whiteLabelResponse);
                                    }

                                    Intent mIntent = new Intent(mActivity, FTLUserValidationActivity.class);
                                    mIntent.putExtra(Constants.ACCESSTOKEN, accessToken);
                                    PreferenceUtils.setDocPortalFTLLoggedObj(mActivity, mFTLPINResponse);
                                    mActivity.startActivity(mIntent);
                                    mActivity.finish();


                                }
                            } else {

                            }
                        } else if (apiResponse.status.getCode() instanceof Double) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                                    }
                                }, false);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    // Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void downloadLogoImage(AccountSettingsResponse accountSettingsResponse, String imageUrl)
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

                //    Toast.makeText(mActivity, path, Toast.LENGTH_LONG).show();
                    getTermsConditionsUrlFromService(accountSettingsResponse);

                }

                @Override
                public void fileDownloadFailure() {

                }
            });
            fileDownloadManager.downloadTheFile();
        }
    }

    private void getUiSettings() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final GetUISettingsService getUISettingsService = retrofitAPI.create(GetUISettingsService.class);

            Call call = getUISettingsService.getUISettings(PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<BaseApiResponse<GetUISettingsResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<GetUISettingsResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() instanceof Boolean) {

                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                GetUISettingsResponse mGetUISettingsResponse = response.body().getData();

                                if (mGetUISettingsResponse != null) {

                                    if (mGetUISettingsResponse.ui_properties != null) {

                                        String mobileItemEnableColor = mGetUISettingsResponse.ui_properties.getMobile_item_enable_color();
                                        String mobileItemDisableColor = mGetUISettingsResponse.ui_properties.getMobile_item_disable_color();
                                        String splashScreenColor = mGetUISettingsResponse.ui_properties.getMobile_splash_screen_background_color();
                                        String folderColor = mGetUISettingsResponse.ui_properties.getMobile_folder_color();

                                        AccountSettings accountSettings = new AccountSettings(mActivity);
                                        WhiteLabelResponse whiteLabelResponse = new WhiteLabelResponse();
                                        whiteLabelResponse.setItem_Selected_Color(mobileItemEnableColor);
                                        whiteLabelResponse.setItem_Unselected_Color(mobileItemDisableColor);
                                        whiteLabelResponse.setSplash_Screen_Color(splashScreenColor);
                                        whiteLabelResponse.setFolder_Color(folderColor);

                                        accountSettings.InsertWhiteLabelDetails(whiteLabelResponse);
                                    }

                                    getUserPreferences();

                                }
                            } else {

                            }
                        } else if (apiResponse.status.getCode() instanceof Double) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                                    }
                                }, false);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    // Toast.makeText(mActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void verifyPINInLoginProcess() {

        String pinNo = inputPIN.getText().toString().trim();

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final VerifyPinService verifyPinService = retrofitAPI.create(VerifyPinService.class);

            final VerifyPinRequest mVerifyPinRequest = new VerifyPinRequest(Integer.parseInt(pinNo));

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            String request = new Gson().toJson(mVerifyPinRequest);
            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = verifyPinService.getVerifyPin(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<BaseApiResponse<LoginResponse>>() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onResponse(Response<BaseApiResponse<LoginResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                getUiSettings();

                            } else {
                                String mMessage = apiResponse.status.getMessage().toString();
                                mActivity.showMessagebox(mActivity, mMessage, null, false);
                                transparentProgressDialog.dismiss();
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                                    }
                                }, false);
                            }
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

                        if (apiResponse.status.getCode() instanceof Boolean) {

                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                GetUserPreferencesResponse mGetUserPreferencesResponse = response.body().getData();
                                if (mGetUserPreferencesResponse != null) {
                                    String assistance_popup = mGetUserPreferencesResponse.getAssistance_popup();

                                    Gson gson = new Gson();
                                    mLoggedInObj = gson.fromJson(PreferenceUtils.getDocPortalLoggedInObj(mActivity), LoginResponse.class);

                                    AccountSettingsResponse accountSettingsResponse = new AccountSettingsResponse();
                                    accountSettingsResponse.setUser_Id(mLoggedInObj.getUserId());
                                    accountSettingsResponse.setUser_Name(mLoggedInObj.getUserName());
                                    accountSettingsResponse.setAccess_Token(mLoggedInObj.getAccessToken());
                                    accountSettingsResponse.setCompany_Name(mLoggedInObj.getCompany_name());
                                    accountSettingsResponse.setIs_Terms_Accepted(mLoggedInObj.getTerms_accept());
                                    accountSettingsResponse.setIs_Help_Accepted(assistance_popup);
                                    accountSettingsResponse.setLogin_Complete_Status(String.valueOf(Constants.Login_Completed));
                                    accountSettingsResponse.setIs_Local_Auth_Enabled("0");
                                    accountSettingsResponse.setIs_Push_Notification_Enabled("0");

                                    if(accountSettingsResponse.getCompany_Name() != null && !accountSettingsResponse.getCompany_Name().isEmpty())
                                    {
                                        downloadLogoImage(accountSettingsResponse, Constants.LOGO_IMAGE_BASE_URL+Constants.Logo_ImagePath+accountSettingsResponse.getCompany_Name()+Constants.Logo_Image_Name);
                                    }

                                }

                            } else {

                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                                    }
                                }, false);
                            }
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            checkSecurity();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("PINVerErr", t.getMessage());
                }
            });
        }
    }

    private void getTermsConditionsUrlFromService(AccountSettingsResponse accountSettingsResponse)
    {
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

                                   String mTermsURL = mGetTermsPageContentResponse.getTerms_url();

                                    accountSettingsResponse.setTerms_URL(mTermsURL);

                                    getHelpGuideUrl(accountSettingsResponse);

                                }
                            } else {

                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {

                            String mMessage = apiResponse.status.getMessage().toString();
                            mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
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

    private void getHelpGuideUrl(AccountSettingsResponse accountSettingsResponse)
    {
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

                                    String mHelpGuideURL = mGetAssistancePopupContentResponse.getHelp_guide_url();

                                    accountSettingsResponse.setHelp_Guide_URL(mHelpGuideURL);

                                    AccountSettings accountSettings = new AccountSettings(mActivity);
                                    accountSettings.InsertAccountSettings(accountSettingsResponse);


                                }

                            } else {

                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {

                            String mMessage = apiResponse.status.getMessage().toString();
                            mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
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

    private void sendFTLPin() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final SendFTLPINService sendFTLPINService = retrofitAPI.create(SendFTLPINService.class);

            VerifyFTLRequest mVerifyFTLRequest = null;
            if (!TextUtils.isEmpty(mMobile)) {
                mVerifyFTLRequest = new VerifyFTLRequest(mEmail, mMobile);
            } else {
                mVerifyFTLRequest = new VerifyFTLRequest(mEmail, null);
            }

            String request = new Gson().toJson(mVerifyFTLRequest);
            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = sendFTLPINService.getFTLPIN(params);

            call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() instanceof Boolean) {

                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                String mMessage = apiResponse.status.getMessage().toString();
                                mActivity.showMessagebox(mActivity, "Pin sent successfully", null, false);
                            } else {
                                transparentProgressDialog.dismiss();
                                String mMessage = apiResponse.status.getMessage().toString();
                                mActivity.showMessagebox(mActivity, "Pin sent successfully", null, false);
                                // Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                                    }
                                }, false);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    Log.d("PINVerErr", t.getMessage());
                }
            });
        }
    }

    private void sendPin() {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final SendPinService sendPinService = retrofitAPI.create(SendPinService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            SendPinRequest sendPinRequest = new SendPinRequest(PreferenceUtils.getUserPinDeviceId(mActivity));

            String request = new Gson().toJson(sendPinRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = sendPinService.getSendPin(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<BaseApiResponse>() {
                @Override
                public void onResponse(Response<BaseApiResponse> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() instanceof Boolean) {

                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                Toast.makeText(mActivity, "Pin Resent Successfully", Toast.LENGTH_SHORT).show();
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                                    }
                                }, false);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    // Toast.makeText(pinActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkSecurity() {
        KeyguardManager keyguardManager = (KeyguardManager) mActivity.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure() == true) {
            Intent intent = new Intent(mActivity, Touchid.class);
            mActivity.startActivity(intent);
            mActivity.finish();
        } else {
            Intent intent = new Intent(mActivity, Notifiy.class);
            mActivity.startActivity(intent);
            mActivity.finish();
        }
    }
}
