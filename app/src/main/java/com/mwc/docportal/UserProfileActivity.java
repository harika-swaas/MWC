package com.mwc.docportal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    TextView user_name_data_txt, title_data_txt, first_name_data_txt, surname_data_txt, email_data_txt, workPhone_data_txt, extension_data_txt,
            mobilePhone_data_txt, addressline1_data_txt, addressline2_data_txt, town_data_txt, postCode_data_txt, country_data_txt;

    Context context = this;
    AlertDialog mAlertDialog;
    MenuItem editItem;
    UserProfileModel.Data userProfileModel;
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
                    country_data_txt.setText(dataModel.getName());
                }
            }
        }
    }

    private void showUserProfileData(UserProfileModel.Data userProfileModel)
    {
        if(userProfileModel.getUsername() != null && !userProfileModel.getUsername().isEmpty())
        {
            user_name_data_txt.setText(userProfileModel.getUsername());
        }
        else
        {
            user_name_data_txt.setText("");
        }

        if(userProfileModel.getTitle() != null && !userProfileModel.getTitle().isEmpty())
        {
            title_data_txt.setText(userProfileModel.getTitle());
        }
        else
        {
            title_data_txt.setText("");
        }

        if(userProfileModel.getFirstname() != null && !userProfileModel.getFirstname().isEmpty())
        {
            first_name_data_txt.setText(userProfileModel.getFirstname());
        }
        else
        {
            first_name_data_txt.setText("");
        }

        if(userProfileModel.getSurname() != null && !userProfileModel.getSurname().isEmpty())
        {
            surname_data_txt.setText(userProfileModel.getSurname());
        }
        else
        {
            surname_data_txt.setText("");
        }

        if(userProfileModel.getEmail() != null && !userProfileModel.getEmail().isEmpty())
        {
            email_data_txt.setText(userProfileModel.getEmail());
        }
        else
        {
            email_data_txt.setText("");
        }

        if(userProfileModel.getWorkphone() != null && !userProfileModel.getWorkphone().isEmpty())
        {
            workPhone_data_txt.setText(userProfileModel.getWorkphone());
        }
        else
        {
            workPhone_data_txt.setText("");
        }

        if(userProfileModel.getWorkphoneExtension() != null && !userProfileModel.getWorkphoneExtension().isEmpty())
        {
            extension_data_txt.setText(userProfileModel.getWorkphoneExtension());
        }
        else
        {
            extension_data_txt.setText("");
        }

        if(userProfileModel.getMobilephone() != null && !userProfileModel.getMobilephone().isEmpty())
        {
            mobilePhone_data_txt.setText(userProfileModel.getMobilephone());
        }
        else
        {
            mobilePhone_data_txt.setText("");
        }


        if(userProfileModel.getAddressLine1() != null && !userProfileModel.getAddressLine1().isEmpty())
        {
            addressline1_data_txt.setText(userProfileModel.getAddressLine1());
        }
        else
        {
            addressline1_data_txt.setText("");
        }

        if(userProfileModel.getAddressLine2() != null && !userProfileModel.getAddressLine2().isEmpty())
        {
            addressline2_data_txt.setText(userProfileModel.getAddressLine2());
        }
        else
        {
            addressline2_data_txt.setText("");
        }

        if(userProfileModel.getTown() != null && !userProfileModel.getTown().isEmpty())
        {
            town_data_txt.setText(userProfileModel.getTown());
        }
        else
        {
            town_data_txt.setText("");
        }

        if(userProfileModel.getPostcode() != null && !userProfileModel.getPostcode().isEmpty())
        {
            postCode_data_txt.setText(userProfileModel.getPostcode());
        }
        else
        {
            postCode_data_txt.setText("");
        }

        /*if(userProfileModel.getCountry() != null && !userProfileModel.getCountry().isEmpty())
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
