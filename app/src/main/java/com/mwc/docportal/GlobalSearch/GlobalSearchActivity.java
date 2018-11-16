package com.mwc.docportal.GlobalSearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.GlobalSearchModel.GlobalSearchDataRequestModel;
import com.mwc.docportal.API.Model.GlobalSearchModel.GlobalSearchDataResponseModel;
import com.mwc.docportal.API.Model.GlobalSearchModel.GlobalSearchRequestModel;
import com.mwc.docportal.API.Model.GlobalSearchModel.GlobalSearchResponseModel;
import com.mwc.docportal.API.Service.EndUserGlobalSearchService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Common.SimpleDividerItemDecoration;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.RootActivity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.mwc.docportal.DMS.Tab_Activity.isFromShared;

public class GlobalSearchActivity extends RootActivity implements SearchView.OnQueryTextListener{


    Toolbar toolbar;
    SearchView searchView;
    Context context = this;
    AlertDialog mAlertDialog;
    String search_id;
    String sequence_id;
    List<GetCategoryDocumentsResponse> globalSearchDocumentList = new ArrayList<>();

    GlobalSearchAdapter mAdapterList;
    RecyclerView mRecyclerView;
  //  NestedScrollView scrollView;
    LinearLayoutManager linearLayoutManager;
    int remainingDataStatus;
    Handler handler = new Handler();
    boolean apiCallInProgress = false;
    RelativeLayout search_completed_layout;
   LinearLayout empty_view;
   TextView no_search_results_txt;
   public static String searchingData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_search);

        initializeViews();
        searchView.setOnQueryTextListener(this);

        if(GlobalVariables.searchKey != null && !GlobalVariables.searchKey.isEmpty())
        {
            searchView.setQuery(GlobalVariables.searchKey, false);
            globalSearchDocumentList = GlobalVariables.globalSearchDocumentList;
            if(GlobalVariables.isGlobalSearchCompleted)
            {
                search_completed_layout.setVisibility(View.VISIBLE);
            }
            else
            {
                search_completed_layout.setVisibility(View.GONE);
            }

            loadAdapterData(true);

        }

        /*scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView != null) {
                    if (scrollView.getChildAt(0).getBottom() == (scrollView.getHeight() + scrollView.getScrollY())) {
                        if(remainingDataStatus == 1 && apiCallInProgress == false) {
                            getEndUserGlobalSearchData();
                        }

                    }
                    else {
                        //scroll view is not at bottom
                    }
                }
            }
        });
*/

        mRecyclerView.addOnScrollListener(mScrollListener);





    }

    private void initializeViews()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back));
        searchView = (SearchView) findViewById(R.id.document_search);

        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.black));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        // change close icon color
        ImageView iconClose = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        iconClose.setColorFilter(getResources().getColor(R.color.black));

        //change search icon color
        /*ImageView iconSearch =(ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        iconSearch.setColorFilter(getResources().getColor(R.color.black));*/

        ImageView icon = searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        icon.setColorFilter(Color.BLACK);



        mRecyclerView = (RecyclerView) findViewById(R.id.globalSearch_recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
      //  scrollView = (NestedScrollView) findViewById(R.id.mNestedScrollViewView);
        search_completed_layout = (RelativeLayout) findViewById(R.id.search_completed_layout);
        empty_view = (LinearLayout) findViewById(R.id.empty_view);
        no_search_results_txt = (TextView)findViewById(R.id.no_search_results_txt);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.global_search_item, menu);
        MenuItem cancelItem = menu.findItem(R.id.cancel_item);

       /* int positionOfMenuItem = 0;
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("CANCEL");
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        item.setTitle(s);*/


        return true;
    }





    @Override
    public boolean onQueryTextSubmit(String searchData) {

        globalSearchDocumentList.clear();
        sequence_id = "0";
        search_id = "";
        apiCallInProgress = false;

        if(handler != null)
        {
            handler.removeCallbacksAndMessages(null);
        }

        searchingData = searchData.trim();

        getEndUserGlobalSearch(searchData.trim());

        return false;
    }

    private void getEndUserGlobalSearch(String searchData)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            GlobalSearchRequestModel searchRequestModel = new GlobalSearchRequestModel(searchData);
            String request = new Gson().toJson(searchRequestModel);

            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final EndUserGlobalSearchService mGetCategoryDocumentsService = retrofitAPI.create(EndUserGlobalSearchService.class);
            Call call = mGetCategoryDocumentsService.endUserGlobalSearch(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<GlobalSearchResponseModel>() {
                @Override
                public void onResponse(Response<GlobalSearchResponseModel> response, Retrofit retrofit) {
                    GlobalSearchResponseModel apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        String message = "";
                        if(apiResponse.getStatus().getMessage() != null)
                        {
                            message = apiResponse.getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(GlobalSearchActivity.this, message, apiResponse.getStatus().getCode()))
                        {
                            search_id = apiResponse.getData().getSearchId();
                            sequence_id = "0";

                            getEndUserGlobalSearchData();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeoutAlert(context);
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void getEndUserGlobalSearchData()
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);

            if(!((Activity) context).isFinishing())
            {
                //show dialog
                transparentProgressDialog.show();
            }


            GlobalSearchDataRequestModel globalSearchDataRequestModel = new GlobalSearchDataRequestModel(search_id, sequence_id,15);
            String request = new Gson().toJson(globalSearchDataRequestModel);

            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final EndUserGlobalSearchService mGetCategoryDocumentsService = retrofitAPI.create(EndUserGlobalSearchService.class);

            apiCallInProgress = true;

            Call call = mGetCategoryDocumentsService.endUserGlobalSearchData(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<GlobalSearchDataResponseModel>() {
                @Override
                public void onResponse(Response<GlobalSearchDataResponseModel> response, Retrofit retrofit) {
                    GlobalSearchDataResponseModel apiResponse = response.body();
                    apiCallInProgress = false;
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        String message = "";
                        if(apiResponse.getStatus().getMessage() != null)
                        {
                            message = apiResponse.getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(GlobalSearchActivity.this, message, apiResponse.getStatus().getCode()))
                        {

                            sequence_id = apiResponse.getData().getSequenceId();
                            remainingDataStatus = apiResponse.getData().getStatus();

                            if(remainingDataStatus == 1)
                            {
                                GlobalVariables.isGlobalSearchCompleted = false;
                                search_completed_layout.setVisibility(View.GONE);
                            }
                            else
                            {
                                GlobalVariables.isGlobalSearchCompleted = true;
                                search_completed_layout.setVisibility(View.VISIBLE);
                            }

                            List<GlobalSearchDataResponseModel.Result> documentListData = apiResponse.getData().getDataResults().get(0).getResults();


                            if(documentListData != null && documentListData.size() > 0)
                            {
                                empty_view.setVisibility(View.GONE);
                                for(GlobalSearchDataResponseModel.Result resultModel : documentListData)
                                {
                                    GetCategoryDocumentsResponse categoryDocumentsResponse = new GetCategoryDocumentsResponse();
                                    categoryDocumentsResponse.setObject_id(resultModel.getDocumentId());
                                    categoryDocumentsResponse.setDocument_version_id(resultModel.getDocumentVersionId());
                                    categoryDocumentsResponse.setName(resultModel.getSubject());
                                    categoryDocumentsResponse.setCreated_date(resultModel.getUploadedDate());
                                    categoryDocumentsResponse.setFiletype(resultModel.getFiletype());
                                    categoryDocumentsResponse.setFilesize(resultModel.getDocSize());
                                    categoryDocumentsResponse.setType("document");
                                    categoryDocumentsResponse.setVersion_count(resultModel.getDocVersionCount());
                                    categoryDocumentsResponse.setIs_shared(resultModel.getDocIsShared());
                                    categoryDocumentsResponse.setCategory_id(resultModel.getDocCategoryId());
                                    categoryDocumentsResponse.setFile_path(resultModel.getFilePath());
                                    categoryDocumentsResponse.setDoc_status(resultModel.getDocStatus());

                                    globalSearchDocumentList.add(categoryDocumentsResponse);

                                    GlobalVariables.globalSearchDocumentList = globalSearchDocumentList;
                                }
                            }
                            else
                            {
                                apiCallInProgress = true;
                                empty_view.setVisibility(View.VISIBLE);
                                no_search_results_txt.setText("No search results found.");
                                handler.removeCallbacksAndMessages(null);
                            }

                            loadAdapterData(false);


                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    if(remainingDataStatus == 1 && apiCallInProgress == false)
                                    {
                                        getEndUserGlobalSearchData();
                                    }
                                    else
                                    {
                                        if (remainingDataStatus != 1) {
                                            handler.removeCallbacksAndMessages(null);
                                        }
                                    }
                                }
                            }, 5000);   // 5 seconds


                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeoutAlert(context);
                }
            });
        }

    }

    private void loadAdapterData(boolean isFromMoveOrOther)
    {
        if(isFromMoveOrOther)
        {
            setListAdapterToView(globalSearchDocumentList);
            mAdapterList.notifyDataSetChanged();
        }
        else
        {
            if(globalSearchDocumentList != null && globalSearchDocumentList.size() > 16)
            {
                mAdapterList.notifyDataSetChanged();
            }
            else
            {
                setListAdapterToView(globalSearchDocumentList);
                mAdapterList.notifyDataSetChanged();
            }
        }
    }

    private void setListAdapterToView(List<GetCategoryDocumentsResponse> globalSearchDocumentList)
    {
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        mAdapterList = new GlobalSearchAdapter(globalSearchDocumentList, GlobalSearchActivity.this);
        mRecyclerView.setAdapter(mAdapterList);
    }


    private void showSessionExpiryAlert(String mMessage)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        sendPinButton.setText("Ok");

        sendPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                AccountSettings accountSettings = new AccountSettings(context);
                accountSettings.deleteAll();
                startActivity(new Intent(context, LoginActivity.class));
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.cancel_item:
                cancelItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelItem()
    {
        GlobalVariables.searchKey ="";
        GlobalVariables.globalSearchDocumentList.clear();
        GlobalVariables.isGlobalSearchCompleted = false;
        searchView.setQuery("", false);
        handler.removeCallbacksAndMessages(null);
        if(mAdapterList != null)
        {
            mAdapterList.notifyDataSetChanged();
        }
        search_completed_layout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        clearAllItems();
    }

    public void clearAllItems()
    {
        GlobalVariables.searchKey ="";
        GlobalVariables.globalSearchDocumentList.clear();
        GlobalVariables.isGlobalSearchCompleted = false;
        finish();
    }



    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            if (!recyclerView.canScrollVertically(1) && dy > 0) {
                if(remainingDataStatus == 1 && apiCallInProgress == false) {
                    getEndUserGlobalSearchData();
                }

            }
            /*int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
            if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                //End of list
            }*/
        }
    };


}
