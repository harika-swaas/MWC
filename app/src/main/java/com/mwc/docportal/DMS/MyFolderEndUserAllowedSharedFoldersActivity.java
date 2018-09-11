package com.mwc.docportal.DMS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.GetEndUserAllowedSharedFoldersRequest;
import com.mwc.docportal.API.Model.GetEndUserSharedParentFoldersResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.ShareEndUserDocumentsRequest;
import com.mwc.docportal.API.Service.GetEndUserAllowedSharedFoldersService;
import com.mwc.docportal.API.Service.ShareEndUserDocumentsService;
import com.mwc.docportal.Common.SimpleDividerItemDecoration;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
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
 * Created by barath on 7/26/2018.
 */

public class MyFolderEndUserAllowedSharedFoldersActivity extends AppCompatActivity{

    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView mRecyclerView;
    SharedDMSEndUserParentFolderAdapter mAdapterList;
    TextView shareButton,cancelButton;
    private List<GetEndUserSharedParentFoldersResponse> getEndUserSharedParentFoldersResponses;
    GetEndUserSharedParentFoldersResponse documentsResponseObj;
    List<GetCategoryDocumentsResponse> mSelectedDocumentList;
    GetEndUserSharedParentFoldersResponse endUserSharedParentFoldersResponse;
    AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_dms);

        intializeViews();
        // getIntentData();
        getEndUserParentSharedFolders();
        addListenersToViews();
    }

    private void intializeViews() {

        shareButton = (TextView) findViewById(R.id.share);
        cancelButton = (TextView) findViewById(R.id.cancel);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_shared_dms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.shared));

        toolbarTextAppernce();
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

    private void getIntentData() {

        endUserSharedParentFoldersResponse = (GetEndUserSharedParentFoldersResponse) getIntent().getSerializableExtra(Constants.SHAREDMSOBJ);
        mSelectedDocumentList = (List<GetCategoryDocumentsResponse>) getIntent().getSerializableExtra(Constants.OBJ);
    }

    private void getEndUserParentSharedFolders() {

        if (NetworkUtils.isNetworkAvailable(MyFolderEndUserAllowedSharedFoldersActivity.this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(MyFolderEndUserAllowedSharedFoldersActivity.this);
            transparentProgressDialog.show();

            final GetEndUserAllowedSharedFoldersRequest mGetEndUserAllowedSharedFoldersRequest = new GetEndUserAllowedSharedFoldersRequest(PreferenceUtils.getWorkspaceId(MyFolderEndUserAllowedSharedFoldersActivity.this),PreferenceUtils.getCategoryId(MyFolderEndUserAllowedSharedFoldersActivity.this));

            String request = new Gson().toJson(mGetEndUserAllowedSharedFoldersRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetEndUserAllowedSharedFoldersService mGetEndUserAllowedSharedFoldersService = retrofitAPI.create(GetEndUserAllowedSharedFoldersService.class);

            Call call = mGetEndUserAllowedSharedFoldersService.getEndUserAllowedSharedFolders(params, PreferenceUtils.getAccessToken(this));

            call.enqueue(new Callback<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                getEndUserSharedParentFoldersResponses = response.body().getData();
                                setAdapterToView(getEndUserSharedParentFoldersResponses);
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MyFolderEndUserAllowedSharedFoldersActivity.this);
                                LayoutInflater inflater = (LayoutInflater) MyFolderEndUserAllowedSharedFoldersActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                builder.setView(view);
                                builder.setCancelable(false);

                                TextView title = (TextView) view.findViewById(R.id.title);
                                title.setText("Alert");

                                TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                                txtMessage.setText(mMessage);

                                Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                cancelButton.setVisibility(View.GONE);

                                sendPinButton.setText("OK");

                                sendPinButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mAlertDialog.dismiss();
                                        AccountSettings accountSettings = new AccountSettings(MyFolderEndUserAllowedSharedFoldersActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(MyFolderEndUserAllowedSharedFoldersActivity.this, LoginActivity.class));
                                    }
                                });

                                mAlertDialog = builder.create();
                                mAlertDialog.show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }

    private void setAdapterToView(List<GetEndUserSharedParentFoldersResponse> getEndUserSharedParentFoldersResponses) {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyFolderEndUserAllowedSharedFoldersActivity.this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mAdapterList = new SharedDMSEndUserParentFolderAdapter(getEndUserSharedParentFoldersResponses,MyFolderEndUserAllowedSharedFoldersActivity.this);
        mRecyclerView.setAdapter(mAdapterList);
        mAdapterList.setClickListener(new SharedDMSAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapterList.toggleSelection(position);

                shareDMSDocuments();
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                mAdapterList.toggleSelection(position);

                return true;
            }
        });
    }

    public void shareDMSDocuments() {

        if (NetworkUtils.isNetworkAvailable(MyFolderEndUserAllowedSharedFoldersActivity.this)){

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(MyFolderEndUserAllowedSharedFoldersActivity.this);
            transparentProgressDialog.show();

           /* String[] document_ids = new String[0];

            if(mSelectedDocumentList != null){
                for(GetCategoryDocumentsResponse categoryDocumentsResponse : mSelectedDocumentList){
                    List<String> getCategoryDocumentsResponseList = new ArrayList<String>();
                    getCategoryDocumentsResponseList.add(categoryDocumentsResponse.getDocument_version_id());
                    document_ids = getCategoryDocumentsResponseList.toArray(new String[getCategoryDocumentsResponseList.size()]);
                }
            }*/

           ArrayList<String> documentIds = new ArrayList<>();
            final ShareEndUserDocumentsRequest mShareEndUserDocumentsRequest = new ShareEndUserDocumentsRequest(documentIds,PreferenceUtils.getWorkspaceId(MyFolderEndUserAllowedSharedFoldersActivity.this), PreferenceUtils.getCategoryId(MyFolderEndUserAllowedSharedFoldersActivity.this));

            String request = new Gson().toJson(mShareEndUserDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final ShareEndUserDocumentsService mShareEndUserDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);
            Call call = mShareEndUserDocumentsService.getSharedEndUserDocuments(params,PreferenceUtils.getAccessToken(MyFolderEndUserAllowedSharedFoldersActivity.this));

            call.enqueue(new Callback<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                String mMessage = apiResponse.status.getMessage().toString();
                                Toast.makeText(MyFolderEndUserAllowedSharedFoldersActivity.this,mMessage,Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MyFolderEndUserAllowedSharedFoldersActivity.this,MyFoldersDMSActivity.class));
                                finish();
                            } else {
                                String mMessage = apiResponse.status.getMessage().toString();
                                Toast.makeText(MyFolderEndUserAllowedSharedFoldersActivity.this,mMessage,Toast.LENGTH_SHORT).show();
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MyFolderEndUserAllowedSharedFoldersActivity.this);
                                LayoutInflater inflater = (LayoutInflater) MyFolderEndUserAllowedSharedFoldersActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                builder.setView(view);
                                builder.setCancelable(false);

                                TextView title = (TextView) view.findViewById(R.id.title);
                                title.setText("Alert");

                                TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                                txtMessage.setText(mMessage);

                                Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                                Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                                cancelButton.setVisibility(View.GONE);

                                sendPinButton.setText("OK");

                                sendPinButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mAlertDialog.dismiss();
                                        AccountSettings accountSettings = new AccountSettings(MyFolderEndUserAllowedSharedFoldersActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(MyFolderEndUserAllowedSharedFoldersActivity.this, LoginActivity.class));
                                    }
                                });

                                mAlertDialog = builder.create();
                                mAlertDialog.show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }

    private void addListenersToViews() {
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
