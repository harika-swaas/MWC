package com.mwc.docportal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.mwc.docportal.API.Model.UserProfileModel;
import com.mwc.docportal.API.Service.GetTermsPageContentService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.SplashScreen;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class UserProfileActivity extends AppCompatActivity {


    Toolbar toolbar;
    TextView user_name_data_txt, title_data_txt, first_name_data_txt, surname_data_txt, email_data_txt, workPhone_data_txt, extension_data_txt,
            mobilePhone_data_txt, addressline1_data_txt, addressline2_data_txt, town_data_txt, postCode_data_txt, country_data_txt;

    Context context = this;
    AlertDialog mAlertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

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
        user_name_data_txt =  (TextView)findViewById(R.id.user_name_data_txt);
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
        country_data_txt =  (TextView)findViewById(R.id.country_data_txt);

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

                    if (response != null) {
                        transparentProgressDialog.dismiss();


                        String message = "";
                        if(response.body().getStatus().getMessage() != null)
                        {
                            message = response.body().getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(UserProfileActivity.this, message, response.body().getStatus().getCode()))
                        {
                            UserProfileModel.Data userProfileModel = response.body().getData();

                            if(userProfileModel != null)
                            {
                                showUserProfileData(userProfileModel);
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("PINVerErr", t.getMessage());
                    CommonFunctions.showTimeoutAlert(context);
                }
            });
        }

    }

    private void showUserProfileData(UserProfileModel.Data userProfileModel)
    {
        if(userProfileModel.getUsername() != null && !userProfileModel.getUsername().isEmpty())
        {
            user_name_data_txt.setText(userProfileModel.getUsername());
        }

        if(userProfileModel.getTitle() != null && !userProfileModel.getTitle().isEmpty())
        {
            title_data_txt.setText(userProfileModel.getTitle());
        }

        if(userProfileModel.getFirstname() != null && !userProfileModel.getFirstname().isEmpty())
        {
            first_name_data_txt.setText(userProfileModel.getFirstname());
        }

        if(userProfileModel.getSurname() != null && !userProfileModel.getSurname().isEmpty())
        {
            surname_data_txt.setText(userProfileModel.getSurname());
        }

        if(userProfileModel.getEmail() != null && !userProfileModel.getEmail().isEmpty())
        {
            email_data_txt.setText(userProfileModel.getEmail());
        }

        if(userProfileModel.getWorkphone() != null && !userProfileModel.getWorkphone().isEmpty())
        {
            workPhone_data_txt.setText(userProfileModel.getWorkphone());
        }

        if(userProfileModel.getWorkphoneExtension() != null && !userProfileModel.getWorkphoneExtension().isEmpty())
        {
            extension_data_txt.setText(userProfileModel.getWorkphoneExtension());
        }

        if(userProfileModel.getMobilephone() != null && !userProfileModel.getMobilephone().isEmpty())
        {
            mobilePhone_data_txt.setText(userProfileModel.getMobilephone());
        }


        if(userProfileModel.getAddressLine1() != null && !userProfileModel.getAddressLine1().isEmpty())
        {
            addressline1_data_txt.setText(userProfileModel.getAddressLine1());
        }

        if(userProfileModel.getAddressLine2() != null && !userProfileModel.getAddressLine2().isEmpty())
        {
            addressline2_data_txt.setText(userProfileModel.getAddressLine2());
        }

        if(userProfileModel.getTown() != null && !userProfileModel.getTown().isEmpty())
        {
            town_data_txt.setText(userProfileModel.getTown());
        }

        if(userProfileModel.getPostcode() != null && !userProfileModel.getPostcode().isEmpty())
        {
            postCode_data_txt.setText(userProfileModel.getPostcode());
        }

        if(userProfileModel.getCountry() != null && !userProfileModel.getCountry().isEmpty())
        {
            country_data_txt.setText(userProfileModel.getCountry());
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
