package com.mwc.docportal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mwc.docportal.API.Model.UserProfileModel;
import com.mwc.docportal.API.Model.UserProfileUpdateModel.UserProfileCountryModel;
import com.mwc.docportal.API.Service.GetTermsPageContentService;
import com.mwc.docportal.API.Service.ListPinDevicesService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;


import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.mwc.docportal.DMS.Tab_Activity.isFromShared;

public class UserProfileActivity extends RootActivity {


    Toolbar toolbar;
    /*TextView user_name_data_txt, title_data_txt, first_name_data_txt, surname_data_txt, email_data_txt, workPhone_data_txt, extension_data_txt,
            mobilePhone_data_txt, addressline1_data_txt, addressline2_data_txt, town_data_txt, postCode_data_txt, country_data_txt;*/

    EditText userName_edttext, title_edittext, firstName_edittext, surname_edittext, email_edittext, workPhone_edittext, extension_edittext, mobilePhone_edittext,
            address1_edittext, address2_edittext, town_edittext, postCode_edittext, country_edittext;
    TextInputLayout input_layout_userName, input_layout_title, input_layout_firstName, input_layout_surName, input_layout_email, input_layout_workPhone, input_layout_extension,
            input_layout_mobilePhone, input_layout_address1, input_layout_address2, input_layout_town, input_layout_postCode, input_layout_country;
  //  Spinner country_spinner;

    Context context = this;
    MenuItem editItem;
    UserProfileModel.Data userProfileModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_profile_details_layout);

        initializeViews();
        setUpToolbar();


    }

    private void setUpToolbar()
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back));
        getSupportActionBar().setTitle("Account");
    }

    private void initializeViews()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        /*user_name_data_txt =  (TextView)findViewById(R.id.user_name_data_txt);
        title_data_txt =  (TextView)findViewById(R.id.title_data_txt);
        first_name_data_txt =  (TextView)findViewById(R.id.first_name_data_txt);
        surname_data_txt =  (TextView)findViewById(R.id.surname_data_txt);
        email_data_txt =  (TextView)findViewById(R.id.email_data_txt);
        workPhone_data_txt =  (TextView)findViewById(R.id.workPhone_data_txt);
        mobilePhone_data_txt =  (TextView)findViewById(R.id.mobilePhone_data_txt);
        extension_data_txt =  (TextView)findViewById(R.id.extension_data_txt);
        addressline1_data_txt =  (TextView)findViewById(R.id.addressline1_data_txt);
        addressline2_data_txt =  (TextView)findViewById(R.id.addressline2_data_txt);
        town_data_txt =  (TextView)findViewById(R.id.town_data_txt);
        postCode_data_txt =  (TextView)findViewById(R.id.postCode_data_txt);
        country_data_txt =  (TextView)findViewById(R.id.country_data_txt);*/

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
        country_edittext = (EditText) findViewById(R.id.country_edittext);



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
        input_layout_country = (TextInputLayout) findViewById(R.id.input_layout_country);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getProfileDetailsFromAPI();
    }

    private void getProfileDetailsFromAPI()
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();


            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();


            final GetTermsPageContentService getUserPreferencesService = retrofitAPI.create(GetTermsPageContentService.class);

            Call call = getUserPreferencesService.getUserProfileDetails(PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<UserProfileModel>() {
                @Override
                public void onResponse(Response<UserProfileModel> response, Retrofit retrofit) {
                    transparentProgressDialog.dismiss();
                    if (response.body() != null) {

                        String message = "";
                        if(response.body().getStatus().getMessage() != null)
                        {
                            message = response.body().getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(UserProfileActivity.this, message, response.body().getStatus().getCode()))
                        {
                             userProfileModel = response.body().getData();

                            if(userProfileModel != null)
                            {
                                showUserProfileData(userProfileModel);
                                editItem.setVisible(true);
                                getCountriesList();
                            }
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
        }

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

                        if(CommonFunctions.isApiSuccess(UserProfileActivity.this, message, response.body().getStatus().getCode()))
                        {
                            List<UserProfileCountryModel.Data> countriesList = response.body().getData();
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
        if(userProfileModel.getCountry() != null && !userProfileModel.getCountry().isEmpty())
        {
            for(UserProfileCountryModel.Data dataModel : countriesList)
            {
                if(dataModel.getCode().equalsIgnoreCase(userProfileModel.getCountry()))
                {
                    country_edittext.setText(dataModel.getName());
                }
            }
        }
        disableAllEditTextData();
    }

    private void disableAllEditTextData()
    {
        userName_edttext.setEnabled(false);
        title_edittext.setEnabled(false);
        firstName_edittext.setEnabled(false);
        surname_edittext.setEnabled(false);
        email_edittext.setEnabled(false);
        workPhone_edittext.setEnabled(false);
        extension_edittext.setEnabled(false);
        address1_edittext.setEnabled(false);
        mobilePhone_edittext.setEnabled(false);
        address2_edittext.setEnabled(false);
        town_edittext.setEnabled(false);
        postCode_edittext.setEnabled(false);
        country_edittext.setEnabled(false);

    }

    private void showUserProfileData(UserProfileModel.Data userProfileModel)
    {
        if(userProfileModel.getUsername() != null && !userProfileModel.getUsername().isEmpty())
        {
            userName_edttext.setText(userProfileModel.getUsername());
        }

        if(userProfileModel.getTitle() != null && !userProfileModel.getTitle().isEmpty())
        {
            title_edittext.setText(userProfileModel.getTitle());
        }


        if(userProfileModel.getFirstname() != null && !userProfileModel.getFirstname().isEmpty())
        {
            firstName_edittext.setText(userProfileModel.getFirstname());
        }

        if(userProfileModel.getSurname() != null && !userProfileModel.getSurname().isEmpty())
        {
            surname_edittext.setText(userProfileModel.getSurname());
        }


        if(userProfileModel.getEmail() != null && !userProfileModel.getEmail().isEmpty())
        {
            email_edittext.setText(userProfileModel.getEmail());
        }

        if(userProfileModel.getWorkphone() != null && !userProfileModel.getWorkphone().isEmpty())
        {
            workPhone_edittext.setText(userProfileModel.getWorkphone());
        }


        if(userProfileModel.getWorkphoneExtension() != null && !userProfileModel.getWorkphoneExtension().isEmpty())
        {
            extension_edittext.setText(userProfileModel.getWorkphoneExtension());
        }


        if(userProfileModel.getMobilephone() != null && !userProfileModel.getMobilephone().isEmpty())
        {
            mobilePhone_edittext.setText(userProfileModel.getMobilephone());
        }



        if(userProfileModel.getAddressLine1() != null && !userProfileModel.getAddressLine1().isEmpty())
        {
            address1_edittext.setText(userProfileModel.getAddressLine1());
        }


        if(userProfileModel.getAddressLine2() != null && !userProfileModel.getAddressLine2().isEmpty())
        {
            address2_edittext.setText(userProfileModel.getAddressLine2());
        }


        if(userProfileModel.getTown() != null && !userProfileModel.getTown().isEmpty())
        {
            town_edittext.setText(userProfileModel.getTown());
        }

        if(userProfileModel.getPostcode() != null && !userProfileModel.getPostcode().isEmpty())
        {
            postCode_edittext.setText(userProfileModel.getPostcode());
        }


      /*  if(userProfileModel.getCountry() != null && !userProfileModel.getCountry().isEmpty())
        {
            country_data_txt.setText(userProfileModel.getCountry());
        }*/
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
                break;
            case R.id.update_profile:
                gotoProfileUpdateActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoProfileUpdateActivity()
    {
        Intent intent = new Intent(UserProfileActivity.this, User_Profile_Update_Activity.class);
        intent.putExtra("UserProfileData", (UserProfileModel.Data)userProfileModel);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_edit_menu, menu);

        int positionOfMenuItem = 0;
        editItem = menu.getItem(positionOfMenuItem);
        editItem.setVisible(false);
        SpannableString s = new SpannableString("Edit");
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        editItem.setTitle(s);

        return true;
    }

}
