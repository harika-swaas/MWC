package com.mwc.docportal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.mwc.docportal.API.Model.OfflineFiles;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.Adapters.DmsAdapterList;
import com.mwc.docportal.Adapters.OffLineFilesListAdapter;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Common.SimpleDividerItemDecoration;
import com.mwc.docportal.DMS.MyFoldersDMSActivity;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.OffLine_Files_Repository;


import java.util.ArrayList;
import java.util.List;

public class OffLine_Files_List extends RootActivity {

    RecyclerView mRecyclerView;
    OffLineFilesListAdapter offlineAdapter;
    OffLine_Files_Repository offLine_files_repository;
    Context context = this;
    List<OfflineFiles> offlineFilesList;
    LinearLayoutManager linearLayoutManager;
    Toolbar toolbar;
    LinearLayout empty_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_line_files_list);

        offLine_files_repository = new OffLine_Files_Repository(context);
        initializeViews();
        getOffLineDocumentList();

    }

    private void getOffLineDocumentList()
    {
        offLine_files_repository.setForm1MasterAPIListenerCB(new OffLine_Files_Repository.GetOfflineFilesListenerCB() {
            @Override
            public void getOfflineFilesDataSuccessCB(List<OfflineFiles> offlineList) {
                if (offlineList != null && offlineList.size() > 0) {
                    empty_view.setVisibility(View.GONE);
                    offlineFilesList = new ArrayList<>(offlineList);
                    setListAdapterToView(offlineFilesList);
                }
                else
                {
                    empty_view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void getOfflineFilesDataFailureCB(String message) {

            }
        });

        offLine_files_repository.getOfflineDocumentList();
    }

    private void setListAdapterToView(List<OfflineFiles> offlineFilesList)
    {
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        offlineAdapter = new OffLineFilesListAdapter(offlineFilesList, OffLine_Files_List.this);
        mRecyclerView.setAdapter(offlineAdapter);

    }

    private void initializeViews()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.offline_recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back));
        getSupportActionBar().setTitle("Offline");
        empty_view = (LinearLayout)findViewById(R.id.empty_view);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
               finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



}
