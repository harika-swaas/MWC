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
import com.swaas.mwc.API.Model.GetEndUserCategoriesRequest;
import com.swaas.mwc.API.Model.GetEndUserCategoriesResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.MoveCategoryRequest;
import com.swaas.mwc.API.Service.GetEndUserCategoriesService;
import com.swaas.mwc.API.Service.MoveDocumentService;
import com.swaas.mwc.Common.SimpleDividerItemDecoration;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.RootActivity;

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

public class MyFolderCategoryActivity extends RootActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView mRecyclerView;
    MoveCategoryDmsAdapter mAdapterList;
    TextView shareButton,cancelButton;
    String obj="0";
    List<GetEndUserCategoriesResponse> mGetCategoryDocumentsResponses;
    List<GetEndUserCategoriesResponse> listGetCategoryDocuments = new ArrayList<>();
    List<GetEndUserCategoriesResponse> mSelectedDocumentList = new ArrayList<>();

    Context context = this;

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
        shareButton.setText("MOVE");
        cancelButton = (TextView) findViewById(R.id.cancel);
      //  TextView text =(TextView)findViewById(R.id.textviewshared);
      //  text.setText("My Folders");
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

        if (NetworkUtils.isNetworkAvailable(MyFolderCategoryActivity.this)) {

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
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MyFolderCategoryActivity.this);
                                LayoutInflater inflater = (LayoutInflater) MyFolderCategoryActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(MyFolderCategoryActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(MyFolderCategoryActivity.this, LoginActivity.class));
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyFolderCategoryActivity.this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mAdapterList = new MoveCategoryDmsAdapter(getEndUserCategoriesResponses, mSelectedDocumentList, MyFolderCategoryActivity.this);
        mRecyclerView.setAdapter(mAdapterList);
        mAdapterList.setClickListener(new MoveCategoryDmsAdapter.ItemClickListener() {
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
                if (NetworkUtils.isNetworkAvailable(MyFolderCategoryActivity.this)){

                    Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                    final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(MyFolderCategoryActivity.this);
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

*/                  String document_ids[]= null;
                    String categoryobj[]= PreferenceUtils.getArrayList(context, "Key").toArray(new String[0]);
                    String is_bu="0";
                    final MoveCategoryRequest moveCategoryRequest = new MoveCategoryRequest(document_ids,obj,categoryobj,is_bu);

                    String request = new Gson().toJson(moveCategoryRequest);

                    //Here the json data is add to a hash map with key data
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("data", request);

                    final MoveDocumentService moveDocumentService = retrofitAPI.create(MoveDocumentService.class);
                    Call call = moveDocumentService.getMoveDocuemnt(params, PreferenceUtils.getAccessToken(MyFolderCategoryActivity.this));

                    call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                        @Override
                        public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                            ListPinDevicesResponse apiResponse = response.body();
                            if (apiResponse != null) {

                                transparentProgressDialog.dismiss();

                                if (apiResponse.status.getCode() instanceof Boolean) {
                                    if (apiResponse.status.getCode() == Boolean.FALSE) {
                                        transparentProgressDialog.dismiss();
                                        String mMessage = apiResponse.status.getMessage().toString();
                                        Toast.makeText(MyFolderCategoryActivity.this,mMessage, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MyFolderCategoryActivity.this,MyFoldersDMSActivity.class));
                                        finish();
                                    } else {
                                        String mMessage = apiResponse.status.getMessage().toString();
                                        Toast.makeText(MyFolderCategoryActivity.this,mMessage, Toast.LENGTH_SHORT).show();
                                    }

                                } else if (apiResponse.status.getCode() instanceof Double) {
                                    transparentProgressDialog.dismiss();
                                    String mMessage = apiResponse.status.getMessage().toString();

                                    Object obj = 401.0;
                                    if(obj.equals(401.0)) {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(MyFolderCategoryActivity.this);
                                        LayoutInflater inflater = (LayoutInflater)MyFolderCategoryActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                                AccountSettings accountSettings = new AccountSettings(MyFolderCategoryActivity.this);
                                                accountSettings.deleteAll();
                                                startActivity(new Intent(MyFolderCategoryActivity.this, LoginActivity.class));
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyFolderCategoryActivity.this,MyFoldersDMSActivity.class));
                finish();
            }
        });
    }
}


