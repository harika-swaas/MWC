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
import com.swaas.mwc.API.Model.GetEndUserAllowedSharedFoldersRequest;
import com.swaas.mwc.API.Model.GetEndUserSharedParentFoldersResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Service.GetEndUserAllowedSharedFoldersService;
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

public class MyFolderEndUserAllowedSharedFoldersActivity extends RootActivity{

    RecyclerView mRecyclerView;
    SharedDMSAdapter mAdapterList;
    CollapsingToolbarLayout collapsingToolbarLayout;
    GetEndUserSharedParentFoldersResponse getEndUserResponseObj;
    List<GetEndUserSharedParentFoldersResponse> mGetEndUserSharedParentFoldersResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_dms);

        intializeViews();
        getIntentData();

        getEndUserParentSharedFolders(getEndUserResponseObj.getWorkspace_id(),getEndUserResponseObj.getCategory_id());
    }

    private void intializeViews() {
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
        getEndUserResponseObj = (GetEndUserSharedParentFoldersResponse) getIntent().getSerializableExtra(Constants.OBJ);
    }

    private void getEndUserParentSharedFolders(String workspace_id, String category_id) {

        if (NetworkUtils.isNetworkAvailable(this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(this);
            transparentProgressDialog.show();

            final GetEndUserAllowedSharedFoldersRequest mGetEndUserAllowedSharedFoldersRequest = new GetEndUserAllowedSharedFoldersRequest(Integer.parseInt(workspace_id),Integer.parseInt(category_id));

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
                                mGetEndUserSharedParentFoldersResponse = response.body().getData();
                                setAdapterToView(mGetEndUserSharedParentFoldersResponse);
                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(MyFolderEndUserAllowedSharedFoldersActivity.this);
                            LayoutInflater inflater = (LayoutInflater) MyFolderEndUserAllowedSharedFoldersActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                    MyFolderEndUserAllowedSharedFoldersActivity.this.startActivity(new Intent(MyFolderEndUserAllowedSharedFoldersActivity.this, LoginActivity.class));
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

    private void setAdapterToView(List<GetEndUserSharedParentFoldersResponse> mGetEndUserSharedParentFoldersResponse) {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyFolderEndUserAllowedSharedFoldersActivity.this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mAdapterList = new SharedDMSAdapter(mGetEndUserSharedParentFoldersResponse,MyFolderEndUserAllowedSharedFoldersActivity.this);
        mRecyclerView.setAdapter(mAdapterList);
    }

}
