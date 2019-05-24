package com.mwc.docportal;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;

import com.mwc.docportal.API.Model.UserProfileModel;

import com.mwc.docportal.API.Model.UserProfileUpdateModel.UserProfileCountryModel;
import com.mwc.docportal.API.Model.UserProfileUpdateModel.UserProfileRequestDataModel;
import com.mwc.docportal.API.Model.UserProfileUpdateModel.UserProfileResponseData;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.CountryListService;
import com.mwc.docportal.API.Service.ListPinDevicesService;
import com.mwc.docportal.Adapters.CountryCustomAdapter;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Login.Notifiy;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;



public class User_Profile_Update_Activity extends RootActivity {

    Toolbar toolbar;
    EditText userName_edttext, title_edittext, firstName_edittext, surname_edittext, email_edittext, workPhone_edittext, extension_edittext, mobilePhone_edittext,
            address1_edittext, address2_edittext, town_edittext, postCode_edittext;
    TextInputLayout input_layout_userName, input_layout_title, input_layout_firstName, input_layout_surName, input_layout_email, input_layout_workPhone, input_layout_extension,
            input_layout_mobilePhone, input_layout_address1, input_layout_address2, input_layout_town, input_layout_postCode;
    Spinner country_spinner;
    Button cacel_btn, update_btn;
    UserProfileModel.Data profileData;
    Context context = this;
    CountryCustomAdapter customAdapter;
    UserProfileCountryModel.Data countryData;
    List<WhiteLabelResponse> mWhiteLabelResponses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update_layout);

        mWhiteLabelResponses = new ArrayList<>();
        initializeViews();
        getIntentData();
        getCountriesList();
        setUpToolbar();
        OnclickMethods();
      //  setBackgroundColor();

    }

    private void setBackgroundColor()
    {

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

                update_btn.setBackgroundDrawable(shape);
            }

            if (mobileItemDisableColor != null) {
                // Initialize a new GradientDrawable
                GradientDrawable shape = new GradientDrawable();

                // Specify the shape of drawable
                shape.setShape(GradientDrawable.RECTANGLE);

                // Make the border rounded
                shape.setCornerRadius(50f);

                // Set the fill color of drawable
                shape.setColor(itemDisableColor);

                cacel_btn.setBackgroundDrawable(shape);
            }
        } else {
            cacel_btn.setBackgroundResource(R.drawable.next);
            update_btn.setBackgroundResource(R.drawable.next);
        }
    }


    private void getIntentData()
    {
        profileData = (UserProfileModel.Data)getIntent().getSerializableExtra("UserProfileData");
        if(profileData.getUsername() != null && !profileData.getUsername().isEmpty())
        {
            userName_edttext.setText(profileData.getUsername());
        }

        if(profileData.getTitle() != null && !profileData.getTitle().isEmpty())
        {
            title_edittext.setText(profileData.getTitle());
        }

        if(profileData.getFirstname() != null && !profileData.getFirstname().isEmpty())
        {
            firstName_edittext.setText(profileData.getFirstname());
        }

        if(profileData.getSurname() != null && !profileData.getSurname().isEmpty())
        {
            surname_edittext.setText(profileData.getSurname());
        }

        if(profileData.getEmail() != null && !profileData.getEmail().isEmpty())
        {
            email_edittext.setText(profileData.getEmail());
        }

        if(profileData.getWorkphone() != null && !profileData.getWorkphone().isEmpty())
        {
            workPhone_edittext.setText(profileData.getWorkphone());
        }

        if(profileData.getWorkphoneExtension() != null && !profileData.getWorkphoneExtension().isEmpty())
        {
            extension_edittext.setText(profileData.getWorkphoneExtension());
        }

        if(profileData.getMobilephone() != null && !profileData.getMobilephone().isEmpty())
        {
            mobilePhone_edittext.setText(profileData.getMobilephone());
        }


        if(profileData.getAddressLine1() != null && !profileData.getAddressLine1().isEmpty())
        {
            address1_edittext.setText(profileData.getAddressLine1());
        }

        if(profileData.getAddressLine2() != null && !profileData.getAddressLine2().isEmpty())
        {
            address2_edittext.setText(profileData.getAddressLine2());
        }

        if(profileData.getTown() != null && !profileData.getTown().isEmpty())
        {
            town_edittext.setText(profileData.getTown());
        }

        if(profileData.getPostcode() != null && !profileData.getPostcode().isEmpty())
        {
            postCode_edittext.setText(profileData.getPostcode());
        }



    }

    private void OnclickMethods()
    {
        cacel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                finish();
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = userName_edttext.getText().toString().trim();
                String title = title_edittext.getText().toString().trim();
                String firstName = firstName_edittext.getText().toString().trim();
                String surName = surname_edittext.getText().toString().trim();
                String email = email_edittext.getText().toString().trim();
                String workPhone = workPhone_edittext.getText().toString().trim();
                String extension = extension_edittext.getText().toString().trim();
                String mobilePhone = mobilePhone_edittext.getText().toString().trim();
                String address1 = address1_edittext.getText().toString().trim();
                String address2 = address2_edittext.getText().toString().trim();
                String town = town_edittext.getText().toString().trim();
                String postCode = postCode_edittext.getText().toString().trim();

                String country = "";
                if(countryData == null)
                {
                    country = profileData.getCountry();
                }
                else
                {
                    country = countryData.getCode();
                }

                input_layout_userName.setErrorEnabled(false);
                input_layout_firstName.setErrorEnabled(false);
                input_layout_surName.setErrorEnabled(false);
                input_layout_email.setErrorEnabled(false);
                input_layout_workPhone.setErrorEnabled(false);
                input_layout_extension.setErrorEnabled(false);
                input_layout_mobilePhone.setErrorEnabled(false);

                if(userName == null || userName.isEmpty())
                {
                    input_layout_userName.setErrorEnabled(true);
                    input_layout_userName.setError(getString(R.string.err_userName));
                    requestFocus(input_layout_userName);
                }
                else if(firstName == null || firstName.isEmpty())
                {
                    input_layout_firstName.setErrorEnabled(true);
                    input_layout_firstName.setError(getString(R.string.err_firstName));
                    requestFocus(input_layout_firstName);
                }
                else if(surName == null || surName.isEmpty())
                {
                    input_layout_surName.setErrorEnabled(true);
                    input_layout_surName.setError(getString(R.string.err_surName));
                    requestFocus(input_layout_surName);
                }
                else if(email == null || !isValidEmail(email))
                {
                    input_layout_email.setErrorEnabled(true);
                    input_layout_email.setError(getString(R.string.err_msg_email));
                    requestFocus(input_layout_email);
                }
                else if(workPhone.length()> 0 && workPhone.length()<10 || workPhone.length()>14)
                {
                    input_layout_workPhone.setErrorEnabled(true);
                    input_layout_workPhone.setError(getString(R.string.err_workPhone));
                    requestFocus(input_layout_workPhone);
                }
                else if(extension.length()> 0 && extension.length()<3 || extension.length()>5)
                {
                    input_layout_extension.setErrorEnabled(true);
                    input_layout_extension.setError(getString(R.string.err_extension));
                    requestFocus(input_layout_extension);
                }
                else if(mobilePhone == null || mobilePhone.isEmpty())
                {
                    input_layout_mobilePhone.setErrorEnabled(true);
                    input_layout_mobilePhone.setError(getString(R.string.err_mobilePhone));
                    requestFocus(input_layout_mobilePhone);
                }
                else if(mobilePhone.length()> 0 && mobilePhone.length()<10 || mobilePhone.length()>14)
                {
                    input_layout_mobilePhone.setErrorEnabled(true);
                    input_layout_mobilePhone.setError(getString(R.string.err_workPhone));
                    requestFocus(input_layout_mobilePhone);
                }
                else {
                    updateProfileDetails(userName, title, firstName, surName, email, workPhone, extension, mobilePhone, address1, address2, town, postCode, country);
                }


            }
        });


        country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryData = (UserProfileCountryModel.Data) country_spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        userName_edttext.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start, int end,
                                               Spanned spanned, int dStart, int dEnd) {

                        if (cs.equals("")) {
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z0-9-_@. ]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });

    }

    private void updateProfileDetails(String userName, String title, String firstName, String surName, String email,
                                      String workPhone, String extension, String mobilePhone, String address1,
                                      String address2, String town, String postCode, String country)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final CountryListService countryListService = retrofitAPI.create(CountryListService.class);

            UserProfileRequestDataModel userProfileRequestDataModel = new UserProfileRequestDataModel(userName, title, firstName, surName, email, workPhone,
                    extension, mobilePhone, address1, address2, town, postCode, country);
            String request = new Gson().toJson(userProfileRequestDataModel);
            //Here the json data is add to a hash map with gkey data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = countryListService.getUserProfileUpdateDetails(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<UserProfileResponseData>() {
                @Override
                public void onResponse(Response<UserProfileResponseData> response, Retrofit retrofit) {
                    UserProfileResponseData apiResponse = response.body();
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.getStatus().getMessage() != null)
                        {
                            message = apiResponse.getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(User_Profile_Update_Activity.this, message, apiResponse.getStatus().getCode())) {
                           finish();
                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                }
            });
        }

    }

    private void initializeViews()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        userName_edttext = (EditText) findViewById(R.id.userName_edttext);
        title_edittext = (EditText) findViewById(R.id.title_edittext);
        firstName_edittext = (EditText) findViewById(R.id.firstName_edittext);
        surname_edittext = (EditText) findViewById(R.id.surname_edittext);
        email_edittext = (EditText) findViewById(R.id.email_edittext);
        workPhone_edittext = (EditText) findViewById(R.id.workPhone_edittext);
        extension_edittext = (EditText) findViewById(R.id.extension_edittext);
        mobilePhone_edittext = (EditText) findViewById(R.id.mobilePhone_edittext);
        address1_edittext = (EditText) findViewById(R.id.address1_edittext);
        address2_edittext = (EditText) findViewById(R.id.address2_edittext);
        town_edittext = (EditText) findViewById(R.id.town_edittext);
        postCode_edittext = (EditText) findViewById(R.id.postCode_edittext);



        input_layout_userName = (TextInputLayout) findViewById(R.id.input_layout_userName);
        input_layout_title = (TextInputLayout) findViewById(R.id.input_layout_title);
        input_layout_firstName = (TextInputLayout) findViewById(R.id.input_layout_firstName);
        input_layout_surName = (TextInputLayout) findViewById(R.id.input_layout_surName);
        input_layout_email = (TextInputLayout) findViewById(R.id.input_layout_email);
        input_layout_workPhone = (TextInputLayout) findViewById(R.id.input_layout_workPhone);
        input_layout_extension = (TextInputLayout) findViewById(R.id.input_layout_extension);
        input_layout_mobilePhone = (TextInputLayout) findViewById(R.id.input_layout_mobilePhone);
        input_layout_address1 = (TextInputLayout) findViewById(R.id.input_layout_address1);
        input_layout_address2 = (TextInputLayout) findViewById(R.id.input_layout_address2);
        input_layout_town = (TextInputLayout) findViewById(R.id.input_layout_town);
        input_layout_postCode = (TextInputLayout) findViewById(R.id.input_layout_postCode);


        cacel_btn = (Button) findViewById(R.id.cancel_button);
        update_btn = (Button) findViewById(R.id.update_button);
        country_spinner = (Spinner) findViewById(R.id.country_spinner);

    }

    private void setUpToolbar()
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back));
        getSupportActionBar().setTitle("Profile Details");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCountriesList()
    {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final ListPinDevicesService listPinDevicesService = retrofitAPI.create(ListPinDevicesService.class);
            Call call = listPinDevicesService.getCountriesList(PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<UserProfileCountryModel>() {
                @Override
                public void onResponse(Response<UserProfileCountryModel> response, Retrofit retrofit) {
                    UserProfileCountryModel apiResponse = response.body();
                    transparentProgressDialog.dismiss();
                    if (response.body() != null) {

                        String message = "";
                        if(apiResponse.getStatus().getMessage()!= null)
                        {
                            message = response.body().getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(User_Profile_Update_Activity.this, message, response.body().getStatus().getCode()))
                        {
                            List<UserProfileCountryModel.Data> countriesList = response.body().getData();

                            /*List<String> countryNameList = new ArrayList<>();
                            if(countriesList != null && countriesList.size() > 0)
                            {
                                for(UserProfileCountryModel.Data countryName : countriesList)
                                {
                                    countryNameList.add(countryName.getName());
                                }
                            }*/
                            setUPAdapterData(countriesList);
                            preFillCountryData(countriesList);

                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    Log.d("UserProfile Message", t.getMessage());
                    CommonFunctions.showTimeOutError(context, t);
                }
            });

            Log.d("Message", "Errpr");
        }

    }

    private void preFillCountryData(List<UserProfileCountryModel.Data> countriesList)
    {
        if(profileData.getCountry() != null && !profileData.getCountry().isEmpty())
        {
            for(UserProfileCountryModel.Data dataModel : countriesList)
            {
                if(dataModel.getCode().equalsIgnoreCase(profileData.getCountry()))
                {
                    int positionMode = countriesList.indexOf(dataModel);
                    country_spinner.setSelection(positionMode);
                }
            }
        }
        else
        {
            UserProfileCountryModel.Data emptyData = new UserProfileCountryModel.Data();
            emptyData.setCode("");
            emptyData.setName("--Choose Country--");
            countriesList.add(0, emptyData);
        }
    }

    private void setUPAdapterData(List<UserProfileCountryModel.Data> countriesList)
    {
        customAdapter=new CountryCustomAdapter(context,countriesList);
        country_spinner.setAdapter(customAdapter);
       /* ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countriesList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country_spinner.setAdapter(dataAdapter);
*/
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void getWhiteLabelProperities() {

        AccountSettings accountSettings = new AccountSettings(User_Profile_Update_Activity.this);
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
}
