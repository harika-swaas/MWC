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
import android.widget.Toast;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.ApiResponse;
import com.swaas.mwc.API.Model.CopyDocumentRequest;
import com.swaas.mwc.API.Model.GetEndUserCategoriesRequest;
import com.swaas.mwc.API.Model.GetEndUserCategoriesResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Service.CopyDocumentService;
import com.swaas.mwc.API.Service.GetEndUserCategoriesService;
import com.swaas.mwc.Common.SimpleDividerItemDecoration;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;

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

public class MyFolderCopyActivity extends MyFoldersDMSActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView mRecyclerView;
    CopyDmsAdapter mAdapterList;
    TextView shareButton,cancelButton;
    String obj="0";
    List<GetEndUserCategoriesResponse> mGetCategoryDocumentsResponses;
    List<GetEndUserCategoriesResponse> listGetCategoryDocuments = new ArrayList<>();
    List<GetEndUserCategoriesResponse> mSelectedDocumentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_dms);

        intializeViews();
        // getIntentData();
        // getEndUserParentSharedFolders();
        addListenersToViews();
        if(getIntent()!= null)
        {
            obj = getIntent().getStringExtra("abc");

        }
        if(obj==null){
            obj="0";
            getCategoryDocumentsNext(obj);
        }
        else
        {
            getCategoryDocumentsNext(obj);
        }


    }
/*
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent



        }
    };
*/

    private void intializeViews() {

        shareButton = (TextView) findViewById(R.id.share);
        shareButton.setText("COPY");
        TextView text =(TextView)findViewById(R.id.textviewshared);
        text.setText("My Folders");
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

 /*   private void getIntentData() {


       obj=getIntent().getExtras().getString("abc");
    }*/

    public void getCategoryDocumentsNext(String object)
    {

        if (NetworkUtils.isNetworkAvailable(MyFolderCopyActivity.this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(this);
            transparentProgressDialog.show();

            final GetEndUserCategoriesRequest getEndUserCategoriesRequest;

            getEndUserCategoriesRequest = new GetEndUserCategoriesRequest(object);

            String request = new Gson().toJson(getEndUserCategoriesRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetEndUserCategoriesService getCategoryDocumentsService = retrofitAPI.create(GetEndUserCategoriesService.class);

            Call call = getCategoryDocumentsService.getEndUsercategory(params, PreferenceUtils.getAccessToken(this));

            call.enqueue(new Callback<ListPinDevicesResponse<GetEndUserCategoriesResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetEndUserCategoriesResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();

                                listGetCategoryDocuments = response.body().getData();
                                //mGetCategoryDocumentsResponses = response.body().getData();
/*
                                 if(Integer.parseInt(pageCount) > 1)
                                {
                                    paginationList = response.body().getData();
                                    mGetCategoryDocumentsResponses.addAll(paginationList);

                               }
*/
                                setAdapterToView(listGetCategoryDocuments);

                                //     paginationList.clear();



                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MyFolderCopyActivity.this);
                                LayoutInflater inflater = (LayoutInflater) MyFolderCopyActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(MyFolderCopyActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(MyFolderCopyActivity.this, LoginActivity.class));
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

    private void setAdapterToView(List<GetEndUserCategoriesResponse> getEndUserCategoriesResponses) {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyFolderCopyActivity.this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mAdapterList = new CopyDmsAdapter(getEndUserCategoriesResponses, mSelectedDocumentList, MyFolderCopyActivity.this);
        mRecyclerView.setAdapter(mAdapterList);
        mAdapterList.setClickListener(new CopyDmsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapterList.toggleSelection(position);
                GetEndUserCategoriesResponse documentsResponseObj = mGetCategoryDocumentsResponses.get(position);
                mSelectedDocumentList.add(documentsResponseObj);
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                mAdapterList.toggleSelection(position);
                GetEndUserCategoriesResponse documentsResponseObj = mGetCategoryDocumentsResponses.get(position);
                mSelectedDocumentList.add(documentsResponseObj);

                return true;
            }
        });
    }

    private void addListenersToViews() {
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(MyFolderCopyActivity.this)){

                    Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                    final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(MyFolderCopyActivity.this);
                    transparentProgressDialog.show();

                /*    String[] document_ids = new String[0];
                    String[] category_ids = new String[0];
                    if(mSelectedDocumentList != null){
                        for(GetEndUserCategoriesResponse categoryDocumentsResponse : mSelectedDocumentList){
                            List<String> getCategoryDocumentsResponseList = new ArrayList<String>();
                           // getCategoryDocumentsResponseList.add(categoryDocumentsResponse.getDocument_version_id());
                            document_ids = getCategoryDocumentsResponseList.toArray(new String[getCategoryDocumentsResponseList.size()]);
                            category_ids = getCategoryDocumentsResponseList.toArray(new String[getCategoryDocumentsResponseList.size()]);
                        }
                    }
*/                  String document_ids[]= PreferenceUtils.getArrayList(context, "Key").toArray(new String[0]);

                    final CopyDocumentRequest copyDocumentRequest = new CopyDocumentRequest(document_ids,obj);

                    String request = new Gson().toJson(copyDocumentRequest);

                    //Here the json data is add to a hash map with key data
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("data", request);

                    final CopyDocumentService copyDocumentService = retrofitAPI.create(CopyDocumentService.class);
                    Call call = copyDocumentService.getCopyDocuemnt(params,PreferenceUtils.getAccessToken(MyFolderCopyActivity.this));

                    call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
                        @Override
                        public void onResponse(Response<ApiResponse<LoginResponse>> response, Retrofit retrofit) {
                            ApiResponse apiResponse = response.body();
                            if (apiResponse != null) {

                                transparentProgressDialog.dismiss();


                                    if (apiResponse.status.getCode() == Boolean.FALSE) {
                                        transparentProgressDialog.dismiss();
                                        String mMessage = apiResponse.status.getMessage().toString();
                                        Toast.makeText(MyFolderCopyActivity.this,mMessage,Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MyFolderCopyActivity.this,MyFoldersDMSActivity.class));
                                        finish();
                                    }

                                 else  {
                                    transparentProgressDialog.dismiss();
                                    String mMessage = apiResponse.status.getMessage().toString();

                                    Object obj = 401.0;
                                    if(obj.equals(401.0)) {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(MyFolderCopyActivity.this);
                                        LayoutInflater inflater = (LayoutInflater)MyFolderCopyActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                                AccountSettings accountSettings = new AccountSettings(MyFolderCopyActivity.this);
                                                accountSettings.deleteAll();
                                                startActivity(new Intent(MyFolderCopyActivity.this, LoginActivity.class));
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
                            Intent intent = new Intent(MyFolderCopyActivity.this,MyFoldersDMSActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyFolderCopyActivity.this,MyFoldersDMSActivity.class));
                finish();
            }
        });
    }
}


