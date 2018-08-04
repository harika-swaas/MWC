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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.GetCategoryDocumentsRequest;
import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Service.GetCategoryDocumentsService;
import com.swaas.mwc.Common.SimpleDividerItemDecoration;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.LoginActivity;
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
 * Created by barath on 8/4/2018.
 */

public class MyFolderActivity extends MyFoldersDMSActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView mRecyclerView;
    MoveDmsAdapter mAdapterList;
    TextView shareButton,cancelButton;
    int pageNumber = 1;
    int totalPages=1;
    String obj="0";
    List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    List<GetCategoryDocumentsResponse> listGetCategoryDocuments = new ArrayList<>();
    List<GetCategoryDocumentsResponse> mSelectedDocumentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_dms);

        intializeViews();
        getIntentData();
       // getEndUserParentSharedFolders();
        addListenersToViews();
        getCategoryDocumentsNext(obj,String.valueOf(pageNumber));


        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView != null) {
                    if (scrollView.getChildAt(0).getBottom() == (scrollView.getHeight() + scrollView.getScrollY())) {
                        //scroll view is at bottom
                        String object= PreferenceUtils.getObjectId(MyFolderActivity.this);
                        obj=object;

                        Toast.makeText(MyFolderActivity.this, "end position", Toast.LENGTH_SHORT).show();

                        if(pageNumber < totalPages) {
                            pageNumber=pageNumber+1;
                            getCategoryDocumentsNext(obj, String.valueOf(pageNumber));

                        }
                    }
                    else {
                        //scroll view is not at bottom
                    }
                }
            }
        });
    }

    private void intializeViews() {

        shareButton = (TextView) findViewById(R.id.share);
        shareButton.setText("MOVE");
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

        mSelectedDocumentList = (List<GetCategoryDocumentsResponse>) getIntent().getSerializableExtra(Constants.OBJ);
    }

    public void getCategoryDocumentsNext(String obj, String page)
    {

        if (NetworkUtils.isNetworkAvailable(MyFolderActivity.this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(this);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(Integer.parseInt(obj), "list", "category", "1", "0");

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = mGetCategoryDocumentsService.getCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(this),page);

            call.enqueue(new Callback<ListPinDevicesResponse<GetCategoryDocumentsResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetCategoryDocumentsResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();

                                listGetCategoryDocuments = response.body().getData();
                                //mGetCategoryDocumentsResponses = response.body().getData();

                                mGetCategoryDocumentsResponses.addAll(listGetCategoryDocuments);
                                totalPages   = Integer.parseInt(response.headers().get("X-Pagination-Page-Count"));
                                pageNumber = Integer.parseInt(response.headers().get("X-Pagination-Current-Page"));

/*
                                 if(Integer.parseInt(pageCount) > 1)
                                {
                                    paginationList = response.body().getData();
                                    mGetCategoryDocumentsResponses.addAll(paginationList);

                                }
*/
                                setAdapterToView(mGetCategoryDocumentsResponses);

                                //     paginationList.clear();



                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MyFolderActivity.this);
                                LayoutInflater inflater = (LayoutInflater) MyFolderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(MyFolderActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(MyFolderActivity.this, LoginActivity.class));
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

    private void setAdapterToView(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyFolderActivity.this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mAdapterList = new MoveDmsAdapter(getCategoryDocumentsResponses, mSelectedDocumentList, MyFolderActivity.this);
        mRecyclerView.setAdapter(mAdapterList);
        mAdapterList.setClickListener(new MoveDmsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapterList.toggleSelection(position);
                GetCategoryDocumentsResponse documentsResponseObj = mGetCategoryDocumentsResponses.get(position);
                mSelectedDocumentList.add(documentsResponseObj);
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                mAdapterList.toggleSelection(position);
                GetCategoryDocumentsResponse documentsResponseObj = mGetCategoryDocumentsResponses.get(position);
                mSelectedDocumentList.add(documentsResponseObj);

                return true;
            }
        });
    }

    private void addListenersToViews() {
/*
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(MyFolderActivity.this)){

                    Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                    final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(MyFolderActivity.this);
                    transparentProgressDialog.show();

                    String[] document_ids = new String[0];

                    if(mSelectedDocumentList != null){
                        for(GetCategoryDocumentsResponse categoryDocumentsResponse : mSelectedDocumentList){
                            List<String> getCategoryDocumentsResponseList = new ArrayList<String>();
                            getCategoryDocumentsResponseList.add(categoryDocumentsResponse.getDocument_version_id());
                            document_ids = getCategoryDocumentsResponseList.toArray(new String[getCategoryDocumentsResponseList.size()]);
                        }
                    }

                    final ShareEndUserDocumentsRequest mShareEndUserDocumentsRequest = new ShareEndUserDocumentsRequest(document_ids,PreferenceUtils.getWorkspaceId(MyFolderActivity.this), PreferenceUtils.getCategoryId(MyFolderActivity.this));

                    String request = new Gson().toJson(mShareEndUserDocumentsRequest);

                    //Here the json data is add to a hash map with key data
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("data", request);

                    final ShareEndUserDocumentsService mShareEndUserDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);
                    Call call = mShareEndUserDocumentsService.getSharedEndUserDocuments(params,PreferenceUtils.getAccessToken(MyFolderActivity.this));

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
                                        Toast.makeText(MyFolderActivity.this,mMessage,Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MyFolderActivity.this,MyFoldersDMSActivity.class));
                                        finish();
                                    } else {
                                        String mMessage = apiResponse.status.getMessage().toString();
                                        Toast.makeText(MyFolderActivity.this,mMessage,Toast.LENGTH_SHORT).show();
                                    }

                                } else if (apiResponse.status.getCode() instanceof Double) {
                                    transparentProgressDialog.dismiss();
                                    String mMessage = apiResponse.status.getMessage().toString();

                                    Object obj = 401.0;
                                    if(obj.equals(401.0)) {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(MyFolderActivity.this);
                                        LayoutInflater inflater = (LayoutInflater)MyFolderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                                AccountSettings accountSettings = new AccountSettings(MyFolderActivity.this);
                                                accountSettings.deleteAll();
                                                startActivity(new Intent(MyFolderActivity.this, LoginActivity.class));
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
        });
*/

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyFolderActivity.this,MyFoldersDMSActivity.class));
                finish();
            }
        });
    }
}


