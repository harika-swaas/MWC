package com.swaas.mwc.DMS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.GetCategoryDocumentsRequest;
import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;
import com.swaas.mwc.API.Model.GetEndUserSharedParentFoldersResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.ShareEndUserDocumentsRequest;
import com.swaas.mwc.API.Service.GetEndUserParentSHaredFoldersService;
import com.swaas.mwc.API.Service.ShareEndUserDocumentsService;
import com.swaas.mwc.Adapters.SharedDMSAdapter;
import com.swaas.mwc.Common.SimpleDividerItemDecoration;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.RootActivity;
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
 * Created by harika on 19-07-2018.
 */

public class MyFolderSharedDocuments extends RootActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView mRecyclerView;
    SharedDMSAdapter mAdapterList;
    Button shareButton,cancelButton;
    List<GetEndUserSharedParentFoldersResponse> mGetEndUserSharedParentFoldersResponse;
    GetEndUserSharedParentFoldersResponse documentsResponseObj;
    List<GetCategoryDocumentsResponse> mSelectedDocumentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_dms);

        intializeViews();
        getIntentData();
        getEndUserParentSharedFolders();
        addListenersToViews();
    }

    private void intializeViews() {

        shareButton = (Button) findViewById(R.id.share);
        cancelButton = (Button) findViewById(R.id.cancel);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_dms);
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

        mSelectedDocumentList = (List<GetCategoryDocumentsResponse>) getIntent().getSerializableExtra(Constants.OBJ);
    }

    private void getEndUserParentSharedFolders() {

        if (NetworkUtils.isNetworkAvailable(this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(this);
            transparentProgressDialog.show();

            final GetEndUserParentSHaredFoldersService mGetEndUserParentSHaredFoldersService = retrofitAPI.create(GetEndUserParentSHaredFoldersService.class);

            Call call = mGetEndUserParentSHaredFoldersService.getEndUserParentSharedFolders(PreferenceUtils.getAccessToken(this));

            call.enqueue(new Callback<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                mGetEndUserSharedParentFoldersResponse = response.body().getData();
                                setAdapterToView(mGetEndUserSharedParentFoldersResponse);
                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(MyFolderSharedDocuments.this);
                            LayoutInflater inflater = (LayoutInflater) MyFolderSharedDocuments.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                            builder.setView(view);
                            builder.setCancelable(false);

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
                                    startActivity(new Intent(MyFolderSharedDocuments.this, LoginActivity.class));
                                    finish();
                                }
                            });

                            mAlertDialog = builder.create();
                            mAlertDialog.show();
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

    private void setAdapterToView(final List<GetEndUserSharedParentFoldersResponse> mGetEndUserSharedParentFoldersResponse) {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyFolderSharedDocuments.this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mAdapterList = new SharedDMSAdapter(mGetEndUserSharedParentFoldersResponse,MyFolderSharedDocuments.this);
        mRecyclerView.setAdapter(mAdapterList);
        mAdapterList.setClickListener(new SharedDMSAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapterList.toggleSelection(position);
                documentsResponseObj = mGetEndUserSharedParentFoldersResponse.get(position);
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                mAdapterList.toggleSelection(position);
                documentsResponseObj = mGetEndUserSharedParentFoldersResponse.get(position);

                return true;
            }
        });
    }

    private void addListenersToViews() {
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(MyFolderSharedDocuments.this)){

                    Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                    final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(MyFolderSharedDocuments.this);
                    transparentProgressDialog.show();

                    String[] document_ids = new String[0];

                    if(mSelectedDocumentList != null){
                        for(GetCategoryDocumentsResponse categoryDocumentsResponse : mSelectedDocumentList){
                            List<String> getCategoryDocumentsResponseList = new ArrayList<String>();
                            getCategoryDocumentsResponseList.add(categoryDocumentsResponse.getDocument_version_id());
                            document_ids = getCategoryDocumentsResponseList.toArray(new String[getCategoryDocumentsResponseList.size()]);
                        }
                    }
                    
                    final ShareEndUserDocumentsRequest mShareEndUserDocumentsRequest = new ShareEndUserDocumentsRequest(document_ids,Integer.parseInt(documentsResponseObj.getWorkspace_id()), Integer.parseInt(documentsResponseObj.getCategory_id()));

                    String request = new Gson().toJson(mShareEndUserDocumentsRequest);

                    //Here the json data is add to a hash map with key data
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("data", request);

                    final ShareEndUserDocumentsService mShareEndUserDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);
                    Call call = mShareEndUserDocumentsService.getSharedEndUserDocuments(params,PreferenceUtils.getAccessToken(MyFolderSharedDocuments.this));

                    call.enqueue(new Callback<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>>() {
                        @Override
                        public void onResponse(Response<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>> response, Retrofit retrofit) {
                            ListPinDevicesResponse apiResponse = response.body();
                            if (apiResponse != null) {

                                transparentProgressDialog.dismiss();

                                if (apiResponse.status.getCode() instanceof Boolean) {
                                    if (apiResponse.status.getCode() == Boolean.FALSE) {
                                        transparentProgressDialog.dismiss();
                                        mGetEndUserSharedParentFoldersResponse = response.body().getData();
                                    }

                                } else if (apiResponse.status.getCode() instanceof Integer) {
                                    transparentProgressDialog.dismiss();
                                    String mMessage = apiResponse.status.getMessage().toString();

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(MyFolderSharedDocuments.this);
                                    LayoutInflater inflater = (LayoutInflater) MyFolderSharedDocuments.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                    builder.setView(view);
                                    builder.setCancelable(false);

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
                                            startActivity(new Intent(MyFolderSharedDocuments.this, LoginActivity.class));
                                            finish();
                                        }
                                    });

                                    mAlertDialog = builder.create();
                                    mAlertDialog.show();
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
        });
    }
}
