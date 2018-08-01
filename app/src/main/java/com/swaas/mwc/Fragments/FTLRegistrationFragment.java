package com.swaas.mwc.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.VerifyFTLRequest;
import com.swaas.mwc.API.Model.VerifyFTLRequestWithEMail;
import com.swaas.mwc.API.Model.VerifyFTLResponse;
import com.swaas.mwc.API.Service.SendFTLPINService;
import com.swaas.mwc.API.Service.VerifyFTLDetailsService;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.FTL.FTLPinVerificationActivity;
import com.swaas.mwc.FTL.FTLRegistrationActivity;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by harika on 21-06-2018.
 */

public class FTLRegistrationFragment extends Fragment {

    FTLRegistrationActivity mActivity;
    View mView;
    Button mNext;
    EditText inputEmail;
    EditText inputMobile;
    ImageView mBackIv;
    TextInputLayout inputLayoutEmail, inputLayoutMobile;
    AlertDialog mAlertDialog;
    AlertDialog mCustomAlertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (FTLRegistrationActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ftl_registration_fragment, container, false);

        intializeViews();
        addListenersToViews();
        return mView;
    }

    private void intializeViews() {

        mNext = (Button) mView.findViewById(R.id.next_button);
        inputEmail = (EditText) mView.findViewById(R.id.input_email);
        inputMobile = (EditText) mView.findViewById(R.id.input_mobile);
        inputLayoutEmail = (TextInputLayout) mView.findViewById(R.id.input_layout_email);
        inputLayoutMobile = (TextInputLayout) mView.findViewById(R.id.input_layout_mobile);
        mBackIv = (ImageView) mView.findViewById(R.id.back_image_view);
    }

    private void addListenersToViews() {

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
                inputMobile.addTextChangedListener(new MyTextWatcher(inputMobile));

                verifyFTLDetails();
                if(inputLayoutEmail.getVisibility() == View.VISIBLE && inputLayoutMobile.getVisibility() == View.GONE) {
                    verifyFTLDetailsWithEmail();
                } else if(inputLayoutEmail.getVisibility() == View.VISIBLE && inputLayoutMobile.getVisibility() == View.VISIBLE){
                    verifyFTLDetailsWithMobile();
                }
            }
        });

        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });

        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        inputMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

    private void verifyFTLDetails() {

        if (!validateEmail()) {
            return;
        }

        if (!validateMobile()) {
            return;
        }
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMobile() {
        String mobile = inputMobile.getText().toString().trim();

        if (mobile.isEmpty() || mobile.length() > 11) {
            inputLayoutMobile.setError(getString(R.string.err_msg_mobile));
            requestFocus(inputMobile);
            return false;
        } else {
            inputLayoutMobile.setErrorEnabled(false);
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
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_mobile:
                    validateMobile();
                    break;
            }
        }
    }

    private void verifyFTLDetailsWithEmail() {

        final String email = inputEmail.getText().toString().trim();
        if (validateEmail()) {
            if (NetworkUtils.isNetworkAvailable(mActivity)) {

                /*final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setLayout(600, 400);
                dialog.show();*/

                final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
                transparentProgressDialog.show();

                Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                final VerifyFTLDetailsService verifyFTLDetailsService = retrofitAPI.create(VerifyFTLDetailsService.class);

                VerifyFTLRequestWithEMail mVerifyFTLRequest = new VerifyFTLRequestWithEMail(email);
                String request = new Gson().toJson(mVerifyFTLRequest);
                //Here the json data is add to a hash map with key data
                Map<String, String> params = new HashMap<String, String>();
                params.put("data", request);

                Call call = verifyFTLDetailsService.getVerifyFTLDetailsWithEmail(params);

                call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                    @Override
                    public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                        BaseApiResponse apiResponse = response.body();
                        if (apiResponse != null) {

                            if (apiResponse.status.getCode() instanceof Boolean) {

                                if (apiResponse.status.getCode() == Boolean.FALSE) {
                                    VerifyFTLResponse mVerifyFTLResponse = response.body().getData();

                                    transparentProgressDialog.dismiss();

                                    if (mVerifyFTLResponse != null) {
                                        if (mVerifyFTLResponse.isCheck_mobile() == true) {
                                            String mMessage = apiResponse.status.getMessage().toString();
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                            View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                            builder.setView(view);
                                            builder.setCancelable(false);

                                            TextView txtTitle = (TextView) view.findViewById(R.id.title);
                                            txtTitle.setText("Alert");

                                            TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);
                                            txtMessage.setText(mMessage);

                                            Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                            Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                            cancelButton.setVisibility(View.GONE);
                                            sendPinButton.setText("OK");

                                            sendPinButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    mCustomAlertDialog.dismiss();
                                                    inputLayoutMobile.setVisibility(View.VISIBLE);
                                                   // verifyFTLDetailsWithMobile();
                                                }
                                            });

                                            mCustomAlertDialog = builder.create();
                                            mCustomAlertDialog.show();

                                        } else {
                                            inputLayoutMobile.setVisibility(View.GONE);
                                        }
                                    } else {

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                        builder.setView(view);
                                        builder.setCancelable(false);

                                        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                                        txtMessage.setText(getString(R.string.pin_verification_dialog_msg));

                                        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                        sendPinButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mAlertDialog.dismiss();
                                                sendFTLPin(email);
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
                                    }
                                } else {
                                    String mMessage = apiResponse.status.getMessage().toString();
                                    transparentProgressDialog.dismiss();
                                    mActivity.showMessagebox(mActivity, mMessage, null, false);
                                }

                            }else if (apiResponse.status.getCode() instanceof Double) {
                                String mMessage = apiResponse.status.getMessage().toString();
                                Object obj = 401.0;
                                if(obj.equals(401.0)) {
                                    mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(mActivity, LoginActivity.class));
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
    }

    private void verifyFTLDetailsWithMobile() {

        final String email = inputEmail.getText().toString().trim();
        final String mobile = inputMobile.getText().toString().trim();

        if (validateEmail() && validateMobile()) {
            if (NetworkUtils.isNetworkAvailable(mActivity)) {

                /*final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();*/

                final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
                transparentProgressDialog.show();

                Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                final VerifyFTLDetailsService verifyFTLDetailsService = retrofitAPI.create(VerifyFTLDetailsService.class);

                VerifyFTLRequest mVerifyFTLRequest = new VerifyFTLRequest(email, mobile);
                String request = new Gson().toJson(mVerifyFTLRequest);

                //Here the json data is add to a hash map with key data
                Map<String, String> params = new HashMap<String, String>();
                params.put("data", request);

                Call call = verifyFTLDetailsService.getVerifyFTLDetails(params);

                call.enqueue(new Callback<BaseApiResponse<VerifyFTLResponse>>() {
                    @Override
                    public void onResponse(Response<BaseApiResponse<VerifyFTLResponse>> response, Retrofit retrofit) {
                        BaseApiResponse apiResponse = response.body();

                        if (apiResponse != null) {

                            transparentProgressDialog.dismiss();

                            if (apiResponse.status.getCode() instanceof Boolean) {

                                if (apiResponse.status.getCode() == Boolean.FALSE) {

                                    transparentProgressDialog.dismiss();

                                    VerifyFTLResponse mVerifyFTLResponse = response.body().getData();

                                    if (mVerifyFTLResponse != null) {
                                        /*MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(mActivity)
                                                .setTitle("Pin verification")
                                                .setDescription(getString(R.string.pin_verification_dialog_msg))
                                                // .setStyle(Style.HEADER_WITH_ICON)
                                                .setStyle(Style.HEADER_WITH_TITLE)
                                                .setPositiveText(R.string.send_pin)
                                                .setNegativeText(R.string.cancel)
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                        sendFTLPinWithMobile(email, mMobile);
                                                    }
                                                })
                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .build();
                                        dialog.show();*/

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                        builder.setView(view);
                                        builder.setCancelable(false);

                                        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                                        txtMessage.setText(getString(R.string.pin_verification_dialog_msg));

                                        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                        sendPinButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mAlertDialog.dismiss();
                                                sendFTLPinWithMobile(email, mobile);
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

                                    } else {
                                        /*MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(mActivity)
                                                .setTitle("Pin verification")
                                                .setDescription(getString(R.string.pin_verification_dialog_msg))
                                                // .setStyle(Style.HEADER_WITH_ICON)
                                                .setStyle(Style.HEADER_WITH_TITLE)
                                                .setPositiveText(R.string.send_pin)
                                                .setNegativeText(R.string.cancel)
                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                        sendFTLPinWithMobile(email, mMobile);
                                                    }
                                                })
                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .build();
                                        dialog.show();*/

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                        builder.setView(view);
                                        builder.setCancelable(false);

                                        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                                        txtMessage.setText(getString(R.string.pin_verification_dialog_msg));

                                        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                        sendPinButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mAlertDialog.dismiss();
                                                sendFTLPinWithMobile(email, mobile);
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
                                    }
                                } else {
                                    String mMessage = apiResponse.status.getMessage().toString();
                                    transparentProgressDialog.dismiss();
                                    mActivity.showMessagebox(mActivity, mMessage, null, false);
                                }

                            } else if (apiResponse.status.getCode() instanceof Double) {
                                String mMessage = apiResponse.status.getMessage().toString();
                                Object obj = 401.0;
                                if(obj.equals(401.0)) {
                                    mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(mActivity, LoginActivity.class));
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
    }

    private void sendFTLPin(final String email) {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            /*final AlertDialog dialog = new SpotsDialog(mActivity, R.style.Custom);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();*/

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final SendFTLPINService sendFTLPINService = retrofitAPI.create(SendFTLPINService.class);

            VerifyFTLRequest mVerifyFTLRequest = new VerifyFTLRequest(email, null);

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
                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {

                                Intent mIntent = new Intent(mActivity, FTLPinVerificationActivity.class);
                                mIntent.putExtra(Constants.EMAIL, email);
                                startActivity(mIntent);
                                mActivity.finish();
                            } else {
                                String mMessage = apiResponse.status.getMessage().toString();
                                // Toast.makeText(mActivity, mMessage, Toast.LENGTH_SHORT).show();
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(mActivity, LoginActivity.class));
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

    private void sendFTLPinWithMobile(final String email, final String mobile) {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final SendFTLPINService sendFTLPINService = retrofitAPI.create(SendFTLPINService.class);

            VerifyFTLRequest mVerifyFTLRequest = new VerifyFTLRequest(email, mobile);

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
                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {

                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                Intent mIntent = new Intent(mActivity, FTLPinVerificationActivity.class);
                                mIntent.putExtra(Constants.EMAIL, email);
                                mIntent.putExtra(Constants.MOBILE, mobile);
                                startActivity(mIntent);
                                mActivity.finish();
                            } else {
                                String mMessage = apiResponse.status.getMessage().toString();
                                mActivity.showMessagebox(mActivity, mMessage, null, false);
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(mActivity, LoginActivity.class));
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


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
