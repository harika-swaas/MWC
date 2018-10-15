package com.mwc.docportal.DMS;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.APIResponseModel;
import com.mwc.docportal.API.Model.ApiResponse;
import com.mwc.docportal.API.Model.ColorCodeModel;
import com.mwc.docportal.API.Model.DownloadDocumentRequest;
import com.mwc.docportal.API.Model.DownloadDocumentResponse;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.GetEndUserAllowedSharedFoldersRequest;
import com.mwc.docportal.API.Model.GetEndUserCategoriesResponse;
import com.mwc.docportal.API.Model.GetEndUserSharedParentFoldersResponse;
import com.mwc.docportal.API.Model.GetSharedCategoryDocumentsRequest;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.OfflineFiles;
import com.mwc.docportal.API.Model.ShareEndUserDocumentsRequest;
import com.mwc.docportal.API.Model.SharedFolderModel.SharedDocumentRequestModel;
import com.mwc.docportal.API.Model.SharedFolderModel.SharedDocumentResponseModel;
import com.mwc.docportal.API.Model.StopSharingRequestModel;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.DownloadDocumentService;
import com.mwc.docportal.API.Service.GetCategoryDocumentsService;
import com.mwc.docportal.API.Service.GetEndUserAllowedSharedFoldersService;
import com.mwc.docportal.API.Service.GetEndUserParentSHaredFoldersService;
import com.mwc.docportal.API.Service.ShareEndUserDocumentsService;
import com.mwc.docportal.Adapters.DmsAdapter;
import com.mwc.docportal.Adapters.DmsAdapterList;
import com.mwc.docportal.Adapters.SharedFolderAdapter;
import com.mwc.docportal.Adapters.SharedFolderAdapterList;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.FileDownloadManager;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Common.SimpleDividerItemDecoration;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.OffLine_Files_Repository;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.GlobalSearch.GlobalSearchActivity;
import com.mwc.docportal.GridAutofitLayoutManager;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;
import com.mwc.docportal.Utils.DateHelper;
import com.mwc.docportal.Utils.SplashScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class NavigationSharedActivity extends BaseActivity {

    Context context = this;
    AlertDialog mAlertDialog;
    public String ObjectId = "0";
    List<GetCategoryDocumentsResponse> documentsCategoryList = new ArrayList<>();
    RecyclerView mRecyclerView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    RelativeLayout toggleView;
    ImageView toggle, sort_image;
    LinearLayout sortingView;
    TextView sort;
    LinearLayout empty_view;
    TextView no_documents_txt;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList();

    boolean isFromList;
    LinearLayout sorting_layout;
    boolean sortByName = false;
    boolean sortByNewest = false;
    boolean sortBySize = false;
    boolean sortByDate = false;

    SharedFolderAdapter mAdapter;
    SharedFolderAdapterList mAdapterList;
    LinearLayoutManager linearLayoutManager;
    String categoryName, workSpaceId;
    int backButtonCount = 0;
    MenuItem menuItemSearch, menuItemDelete, menuItemShare, menuItemMove, menuItemMore;
    List<GetCategoryDocumentsResponse> mSelectedDocumentList = new ArrayList<>();
    static Boolean isTouched = false;
    List<GetCategoryDocumentsResponse> downloadingUrlDataList = new ArrayList<>();
    int index=0;
    int downloadIndex = 0;
    boolean isSecondLevel = false;
    List<GetCategoryDocumentsResponse> downloadingItemsList = new ArrayList<>();
    LoadingProgressDialog transparentProgressDialog;
    RelativeLayout move_layout;
    BottomNavigationView bottomNavigationLayout;
    TextView cancel_textview;
    LinearLayout shared_bottom_linearlayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViews();
        OnClickListeners();
        getWhiteLabelProperities();
        no_documents_txt.setText("");
        mRecyclerView.setNestedScrollingEnabled(false);

        transparentProgressDialog = new LoadingProgressDialog(context);

        if (!GlobalVariables.isMoveInitiated) {
            hideBottomView();
        }
        else{
            showBottomView();
        }

        getIntentData();
        getDocuments();
        toggleAddAndBackButton();


    }

    private void hideBottomView()
    {
        bottomNavigationLayout.setVisibility(View.VISIBLE);
        sorting_layout.setVisibility(View.VISIBLE);
        move_layout.setVisibility(View.GONE);

        setMargins(shared_bottom_linearlayout,0,0,0,60);

    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            final float scale = getBaseContext().getResources().getDisplayMetrics().density;
            // convert the DP into pixel
            int l =  (int)(left * scale + 0.5f);
            int r =  (int)(right * scale + 0.5f);
            int t =  (int)(top * scale + 0.5f);
            int b =  (int)(bottom * scale + 0.5f);

            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }


    private void showBottomView()
    {
        bottomNavigationLayout.setVisibility(View.GONE);
        sorting_layout.setVisibility(View.GONE);
        move_layout.setVisibility(View.VISIBLE);
        GlobalVariables.sharedDocsSortType = "type";
        setMargins(shared_bottom_linearlayout,0,0,0,110);
    }


    private void getDocuments()
    {
        if(ObjectId.equals("0"))
        {
            if(!GlobalVariables.isMoveInitiated && !GlobalVariables.selectedActionName.equalsIgnoreCase("share"))
            {
                getWorkSpaceCategoriesDocument();
            }
            else
            {
                getShareParentFolders();
            }
        }
        else if(isSecondLevel == true)
        {
            if(!GlobalVariables.isMoveInitiated && !GlobalVariables.selectedActionName.equalsIgnoreCase("share"))
            {
                getSharedDocs();
            }
            else
            {
                getAllowedSharedFolders();
            }

        }
        else if(isSecondLevel == false)
        {
            getSharedDocumentsFromLocal(ObjectId);
        }
    }

    private void getAllowedSharedFolders()
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetEndUserAllowedSharedFoldersRequest mGetEndUserAllowedSharedFoldersRequest = new GetEndUserAllowedSharedFoldersRequest(workSpaceId, ObjectId);

            String request = new Gson().toJson(mGetEndUserAllowedSharedFoldersRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetEndUserAllowedSharedFoldersService mGetEndUserAllowedSharedFoldersService = retrofitAPI.create(GetEndUserAllowedSharedFoldersService.class);

            Call call = mGetEndUserAllowedSharedFoldersService.getEndUserAllowedSharedFolders(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();
                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationSharedActivity.this, message, apiResponse.status.getCode()))
                        {
                            List<GetEndUserSharedParentFoldersResponse> sharedParentAllowedList = response.body().getData();

                            if(sharedParentAllowedList != null && sharedParentAllowedList.size() > 0)
                            {
                                for(GetEndUserSharedParentFoldersResponse endUserSharedParentFoldersResponse : sharedParentAllowedList)
                                {
                                    GetCategoryDocumentsResponse categoryDocumentsResponse = new GetCategoryDocumentsResponse();
                                    categoryDocumentsResponse.setObject_id(endUserSharedParentFoldersResponse.getCategory_id());
                                    categoryDocumentsResponse.setName(endUserSharedParentFoldersResponse.getCategory_name());
                                    categoryDocumentsResponse.setCreated_date("");
                                    categoryDocumentsResponse.setShared_date("");
                                    categoryDocumentsResponse.setCategory_id(endUserSharedParentFoldersResponse.getWorkspace_id());
                                    categoryDocumentsResponse.setFilesize("0");
                                    categoryDocumentsResponse.setType("category");

                                    documentsCategoryList.add(categoryDocumentsResponse);

                                }
                            }

                            reloadAdapter();
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

    private void showAccessDeniedAlert(String mMessage)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

        sendPinButton.setText("OK");

        sendPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void getShareParentFolders()
    {
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
                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationSharedActivity.this, message, apiResponse.status.getCode()))
                        {
                            List<GetEndUserSharedParentFoldersResponse> sharedParentList = response.body().getData();

                            if(sharedParentList != null && sharedParentList.size() > 0)
                            {
                                for(GetEndUserSharedParentFoldersResponse endUserSharedParentFoldersResponse : sharedParentList)
                                {
                                    GetCategoryDocumentsResponse categoryDocumentsResponse = new GetCategoryDocumentsResponse();
                                    categoryDocumentsResponse.setObject_id(endUserSharedParentFoldersResponse.getCategory_id());
                                    categoryDocumentsResponse.setName(endUserSharedParentFoldersResponse.getCategory_name());
                                    categoryDocumentsResponse.setCreated_date("");
                                    categoryDocumentsResponse.setShared_date("");
                                    categoryDocumentsResponse.setCategory_id(endUserSharedParentFoldersResponse.getWorkspace_id());
                                    categoryDocumentsResponse.setFilesize("0");
                                    categoryDocumentsResponse.setType("category");

                                    documentsCategoryList.add(categoryDocumentsResponse);

                                }
                            }

                            reloadAdapter();

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

    private void getIntentData()
    {
        if(getIntent().getStringExtra("ObjectId") != null)
        {
            ObjectId = getIntent().getStringExtra("ObjectId");
        }
        else
        {
            ObjectId = "0";
        }

        if(getIntent().getStringExtra("CategoryName") != null)
        {
            categoryName = getIntent().getStringExtra("CategoryName");
        }
        else
        {
            categoryName = "Shared";
        }

        if(getIntent().getStringExtra("WorkSpaceId") != null)
        {
            workSpaceId = getIntent().getStringExtra("WorkSpaceId");
        }
        else
        {
            workSpaceId = "";
        }

        isSecondLevel = getIntent().getBooleanExtra("isSecondLevel", false);

    }

    private void OnClickListeners()
    {
        toggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.isSharedTileView = !GlobalVariables.isSharedTileView;
                reloadAdapter();
            }
        });

        sortingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });

        cancel_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalVariables.isMoveInitiated = false;
                GlobalVariables.selectedActionName = "";
                GlobalVariables.selectedDocumentsList.clear();

                Intent intent = new Intent(context, NavigationMyFolderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();


            }
        });

    }

    private void initializeViews()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_dms);
        toggleView = (RelativeLayout) findViewById(R.id.toggle_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        sortingView = (LinearLayout) findViewById(R.id.sort);
        toggle = (ImageView) findViewById(R.id.toggle);
        sort_image = (ImageView) findViewById(R.id.sort_image);
        sort = (TextView) findViewById(R.id.name_sort);
        empty_view = (LinearLayout)findViewById(R.id.empty_view);
        no_documents_txt = (TextView)findViewById(R.id.no_documents_txt);
        sorting_layout = (LinearLayout) findViewById(R.id.sorting_layout);
        move_layout = (RelativeLayout) findViewById(R.id.move_layout);
        bottomNavigationLayout = (BottomNavigationView) findViewById(R.id.navigation);
        cancel_textview = (TextView) findViewById(R.id.cancel_textview);
        shared_bottom_linearlayout = (LinearLayout)findViewById(R.id.shared_bottom_linearlayout);


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Shared");
        toolbarTextAppernce();

    }


    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_navigation_shared;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_shared;
    }

    public void getSharedDocs()
    {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            GetSharedCategoryDocumentsRequest sharedDocumentRequestModel = new GetSharedCategoryDocumentsRequest(ObjectId);

            String request = new Gson().toJson(sharedDocumentRequestModel);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);
            Call call = mGetCategoryDocumentsService.getSharedDocumentAndCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<SharedDocumentResponseModel>() {
                @Override
                public void onResponse(Response<SharedDocumentResponseModel> response, Retrofit retrofit) {
                    SharedDocumentResponseModel apiResponse = response.body();
                    GlobalVariables.sharedDocumentList.clear();


                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        String message = "";
                        if(apiResponse.getStatus().getMessage() != null)
                        {
                            message = apiResponse.getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationSharedActivity.this, message, apiResponse.getStatus().getCode()))
                        {
                            List<Object> listOfObjects = apiResponse.getData().getCategories();

                            JSONArray categoryArray = new JSONArray(listOfObjects);
                            JSONArray documentArray = new JSONArray();
                            JSONObject obj= new JSONObject();
                            try {
                                obj.put("categories",categoryArray);
                                obj.put("documents",documentArray);

                                getFoldersAndDocs(obj, "0");

                             /*   GlobalVariables.sortType = "type";
                                GlobalVariables.sharedDocsIsAscending = false;*/
                                getSharedDocumentsFromLocal("0");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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


    private void getFoldersAndDocs(JSONObject dict, String parentDocumentId) {

        try {



            if(dict.has("documents")){
                JSONArray docArray = dict.getJSONArray("documents");
                if(docArray.length() > 0)
                {
                    for (int i = 0; i < docArray.length(); i++) {
                        JSONObject objString = docArray.getJSONObject(i);
                        GetCategoryDocumentsResponse categoryDocumentsResponse = new GetCategoryDocumentsResponse();
                        categoryDocumentsResponse.setObject_id(objString.getString("document_id"));
                        categoryDocumentsResponse.setDocument_version_id(objString.getString("document_version_id"));
                        categoryDocumentsResponse.setName(objString.getString("name"));
                        categoryDocumentsResponse.setFiletype(objString.getString("filetype"));

                        if(objString.getString("filesize") == null || objString.getString("filesize").isEmpty())
                        {
                            categoryDocumentsResponse.setFilesize(objString.getString("0"));
                        }
                        else {
                            categoryDocumentsResponse.setFilesize(objString.getString("filesize"));
                        }

                        categoryDocumentsResponse.setFilesize(objString.getString("filesize"));
                        categoryDocumentsResponse.setShared_date(objString.getString("shared_date"));
                        categoryDocumentsResponse.setCreated_date(objString.getString("shared_date"));
                        categoryDocumentsResponse.setCategory_id(parentDocumentId);
                        categoryDocumentsResponse.setIs_shared("1");
                        categoryDocumentsResponse.setType("document");

                        GlobalVariables.sharedDocumentList.add(categoryDocumentsResponse);
                    }
                }

            }

            if(dict.has("categories")){

                JSONArray folderArray = dict.getJSONArray("categories");

                if(folderArray.length() > 0)
                {
                    for (int i = 0; i < folderArray.length(); i++) {
                        JSONObject objString = new JSONObject();
                        objString = folderArray.getJSONObject(i);
                        GetCategoryDocumentsResponse categoryDocumentsResponse = new GetCategoryDocumentsResponse();
                        categoryDocumentsResponse.setObject_id(objString.getString("category_id"));
                        categoryDocumentsResponse.setName(objString.getString("category_name"));
                        categoryDocumentsResponse.setCreated_date(objString.getString("created_date"));
                        categoryDocumentsResponse.setShared_date("");
                        categoryDocumentsResponse.setCategory_id(parentDocumentId);
                        categoryDocumentsResponse.setFilesize("0");
                        categoryDocumentsResponse.setType("category");

                        GlobalVariables.sharedDocumentList.add(categoryDocumentsResponse);

                        if(objString.has("nodes")){
                            JSONObject childArray = objString.getJSONObject("nodes");
                            if(childArray.length() > 0)
                            {
                                getFoldersAndDocs(childArray, objString.getString("category_id"));
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSharedDocumentsFromLocal(String parentId)
    {

        Log.d("List Before Filter", DateHelper.getCurrentTime());

        if(GlobalVariables.sharedDocumentList != null && GlobalVariables.sharedDocumentList.size() > 0) {

            for(GetCategoryDocumentsResponse categoryDocumentsResponse : GlobalVariables.sharedDocumentList)
            {
                if(categoryDocumentsResponse.getCategory_id().equalsIgnoreCase(parentId))
                {
                    documentsCategoryList.add(categoryDocumentsResponse);
                }
            }
        }

        Log.d("List After Filter", DateHelper.getCurrentTime());
        doLocalSorting(GlobalVariables.sharedDocsSortType);
        toggleEmptyState();
        reloadAdapter();
    }

    private void reloadAdapter()
    {
        if (GlobalVariables.isSharedTileView && !GlobalVariables.isMoveInitiated)
        {
            toggle.setImageResource(R.mipmap.ic_grid);
            setGridAdapterToView(documentsCategoryList);
            mAdapter.notifyDataSetChanged();
            isFromList = false;
        }
        else
        {
            toggle.setImageResource(R.mipmap.ic_list);
            setListAdapterToView(documentsCategoryList);
            isFromList = true;
            mAdapterList.notifyDataSetChanged();
        }
    }

    public void toggleEmptyState()
    {
        no_documents_txt.setText("");
        if(documentsCategoryList != null && documentsCategoryList.size() > 0)
        {
            empty_view.setVisibility(View.GONE);
            sorting_layout.setVisibility(View.VISIBLE);
        }
        else
        {
            empty_view.setVisibility(View.VISIBLE);
            no_documents_txt.setText("NO DOCUMENTS FOUND.");
            sorting_layout.setVisibility(View.GONE);
        }
    }



    private void setGridAdapterToView(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        toggle.setImageResource(R.mipmap.ic_grid);
        int mNoOfColumns = GridAutofitLayoutManager.calculateNoOfColumns(getApplicationContext());
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, mNoOfColumns));
        mAdapter = new SharedFolderAdapter(getCategoryDocumentsResponses, NavigationSharedActivity.this, ObjectId);
        mRecyclerView.setAdapter(mAdapter);
        Log.d("After Adapter Reload", DateHelper.getCurrentTime());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (GlobalVariables.isSharedTileView && !GlobalVariables.isMoveInitiated)
        {
            int mNoOfColumns = GridAutofitLayoutManager.calculateNoOfColumns(getApplicationContext());
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mNoOfColumns));
        }

    }

    public void setListAdapterToView(final List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        mAdapterList = new SharedFolderAdapterList(getCategoryDocumentsResponses, NavigationSharedActivity.this, ObjectId, isSecondLevel);
        mRecyclerView.setAdapter(mAdapterList);



    }

    private void getWhiteLabelProperities() {

        AccountSettings accountSettings = new AccountSettings(context);
        accountSettings.SetWhiteLabelCB(new AccountSettings.GetWhiteLabelCB() {
            @Override
            public void getWhiteLabelSuccessCB(List<WhiteLabelResponse> whiteLabelResponses) {
                if (whiteLabelResponses != null && whiteLabelResponses.size() > 0) {
                    mWhiteLabelResponses = whiteLabelResponses;
                }
            }

            @Override
            public void getWhiteLabelFailureCB(String message) {

            }
        });

        accountSettings.getWhiteLabelProperties();
    }

    private void toggleAddAndBackButton()
    {
        if (ObjectId.equals("0")){
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        else
        {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (ObjectId.equals("0")){
            collapsingToolbarLayout.setTitle("Shared");
        }
        else
        {
            collapsingToolbarLayout.setTitle(categoryName);
        }
        doLocalSorting(GlobalVariables.sharedDocsSortType);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_more:

                openBottomSheetForMultiSelect();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(ObjectId.equals("0"))
        {
            if (backButtonCount >= 1) {
                backButtonCount = 0;
                moveTaskToBack(true);
            } else {
                Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
                backButtonCount++;
            }
        }
        else
        {
            if (isFromList == true) {
                mAdapterList.clearAll();
            } else {
                mAdapter.clearAll();
            }
            List<GetCategoryDocumentsResponse> dummyList = new ArrayList<>();
            updateToolbarMenuItems(dummyList);
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_multi_select, menu);
        menuItemSearch = menu.findItem(R.id.action_search);
        menuItemDelete = menu.findItem(R.id.action_delete);
        menuItemShare = menu.findItem(R.id.action_share);
        menuItemMore = menu.findItem(R.id.action_more);
        menuItemMove = menu.findItem(R.id.action_move);

        menuItemShare.setVisible(false);
        menuItemDelete.setVisible(false);
        menuItemShare.setVisible(false);
        menuItemMove.setVisible(false);
        menuItemMore.setVisible(false);

        if(mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
            String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            int selectedColor = Color.parseColor(itemSelectedColor);
            menuIconColor(menuItemSearch, selectedColor);
            menuIconColor(menuItemMore, selectedColor);
        }

        menuItemSearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent(NavigationSharedActivity.this, GlobalSearchActivity.class);
                startActivity(intent);

                return false;
            }
        });

        return true;
    }

    public static void menuIconColor(MenuItem menuItem, int color) {
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }


    public void updateToolbarMenuItems(List<GetCategoryDocumentsResponse> selectedDocumentList) {

        mSelectedDocumentList = selectedDocumentList;

        getWhiteLabelProperities();

        if (mSelectedDocumentList != null && mSelectedDocumentList.size() > 0) {
                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                    int selectedColor = Color.parseColor(itemSelectedColor);

                    menuIconColor(menuItemMore,selectedColor);
                }
                menuItemDelete.setVisible(false);
                menuItemMove.setVisible(false);
                menuItemMore.setVisible(true);
                menuItemShare.setVisible(false);
                menuItemSearch.setVisible(false);

        } else {
            if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                int selectedColor = Color.parseColor(itemSelectedColor);

                menuIconColor(menuItemSearch,selectedColor);
            }
            menuItemDelete.setVisible(false);
            menuItemShare.setVisible(false);
            menuItemMore.setVisible(false);
            menuItemMove.setVisible(false);
            menuItemSearch.setVisible(true);
        }
    }


    public void openBottomSheetForMultiSelect() {

        getWhiteLabelProperities();

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_sort, null);
        TextView clearSelection = (TextView) view.findViewById(R.id.clear_selection);
        TextView copy = (TextView) view.findViewById(R.id.copy);
        final TextView move = (TextView) view.findViewById(R.id.move);
        TextView rename = (TextView) view.findViewById(R.id.rename);
        TextView delete = (TextView) view.findViewById(R.id.delete);
        RelativeLayout availableOfflineLayout = (RelativeLayout) view.findViewById(R.id.available_offline_layout);
        RelativeLayout shareLayout = (RelativeLayout) view.findViewById(R.id.share_layout);
        RelativeLayout clearSelectionLayout = (RelativeLayout) view.findViewById(R.id.clear_selection_layout);
        RelativeLayout copyLayout = (RelativeLayout) view.findViewById(R.id.copy_layout);
        RelativeLayout moveLayout = (RelativeLayout) view.findViewById(R.id.move_layout);
        RelativeLayout renameLayout = (RelativeLayout) view.findViewById(R.id.rename_layout);
        SwitchCompat download = (SwitchCompat) view.findViewById(R.id.switchButton_download);
        SwitchCompat switchButton_share = (SwitchCompat) view.findViewById(R.id.switchButton_share);


        RelativeLayout doc_info_layout = (RelativeLayout) view.findViewById(R.id.doc_info_layout);
        ImageView thumbnailCornerIcon = (ImageView) view.findViewById(R.id.thumbnail_corner_image);
        TextView thumbnailText = (TextView) view.findViewById(R.id.thumbnail_text);
        ImageView thumbnailIcon = (ImageView) view.findViewById(R.id.thumbnail_image);
        TextView docText = (TextView) view.findViewById(R.id.doc_text);

        ImageView clearSelectionImage = (ImageView) view.findViewById(R.id.clear_selection_image);
        ImageView copyImage = (ImageView) view.findViewById(R.id.copy_image);
        ImageView moveImage = (ImageView) view.findViewById(R.id.move_image);
        ImageView renameImage = (ImageView) view.findViewById(R.id.rename_image);
        ImageView shareImage = (ImageView) view.findViewById(R.id.share_image);
        ImageView availableOfflineImage = (ImageView) view.findViewById(R.id.available_offline_image);

        RelativeLayout folder_layout = (RelativeLayout) view.findViewById(R.id.folder_layout);
        TextView categoryText = (TextView) view.findViewById(R.id.category_text);
        ImageView categoryImage = (ImageView) view.findViewById(R.id.category_image);
        View line_layout = (View) view.findViewById(R.id.line_layout);

        shareLayout.setVisibility(View.GONE);
        moveLayout.setVisibility(View.GONE);
        renameLayout.setVisibility(View.GONE);
        folder_layout.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);


        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        CommonFunctions.setSelectedItems(mSelectedDocumentList);

        if (mSelectedDocumentList != null && mSelectedDocumentList.size() == 1) {
            if (mSelectedDocumentList.get(0).getType().equalsIgnoreCase("document")) {

                ColorCodeModel colorCodeModel = CommonFunctions.getColorCodesforFileType(mSelectedDocumentList.get(0).getFiletype());
                if (colorCodeModel != null) {

                    thumbnailIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getPrimaryColor()));
                    thumbnailCornerIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getSecondaryColor()));

                    thumbnailText.setText(colorCodeModel.getFileType());
                    docText.setText(mSelectedDocumentList.get(0).getName());
                }

            }
        }



            List<String> temporarydownloadList = new ArrayList<>();
            if (mSelectedDocumentList != null && mSelectedDocumentList.size() > 0) {
                for (GetCategoryDocumentsResponse getcategoryResponseModel : mSelectedDocumentList) {
                    if (getcategoryResponseModel.getType() != null && getcategoryResponseModel.getType().equalsIgnoreCase("document")) {
                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                        if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(getcategoryResponseModel.getDocument_version_id())) {
                            temporarydownloadList.add("Is_Download_Available");
                        }
                    }

                }
            }


            if (temporarydownloadList != null && temporarydownloadList.size() == mSelectedDocumentList.size()) {
                download.setChecked(true);
            } else {
                download.setChecked(false);
            }




            copyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.isMultiSelect = false;
                    mBottomSheetDialog.dismiss();
                    GlobalVariables.isMoveInitiated = true;
                    GlobalVariables.selectedActionName = "copy";
                    clearSelectedListAfterOperation();
                    assigningMoveOriginIndex();
                    Intent intent = new Intent(context, NavigationMyFolderActivity.class);
                    intent.putExtra("ObjectId", "0");
                    context.startActivity(intent);
                }
            });

            doc_info_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalVariables.isMultiSelect = false;
                    mBottomSheetDialog.dismiss();
                    if (mSelectedDocumentList != null && mSelectedDocumentList.size() == 1) {
                        PreferenceUtils.setDocumentVersionId(context, mSelectedDocumentList.get(0).getDocument_version_id());
                        PreferenceUtils.setDocument_Id(context, mSelectedDocumentList.get(0).getObject_id());
                    }

                    Intent intent = new Intent(context, Tab_Activity.class);
                    intent.putExtra("IsFromShared", true);
                    context.startActivity(intent);
                    clearSelectedListAfterOperation();
                }
            });

            clearSelectionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFromList == true) {
                        mAdapterList.clearAll();
                    } else {
                        mAdapter.clearAll();
                    }
                    GlobalVariables.selectedDocumentsList.clear();
                    mBottomSheetDialog.dismiss();
                    List<GetCategoryDocumentsResponse> dummyList = new ArrayList<>();
                    updateToolbarMenuItems(dummyList);
                }
            });


            download.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    isTouched = true;
                    return false;
                }
            });

            download.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isTouched) {
                        GlobalVariables.isMultiSelect = false;
                        isTouched = false;
                        if (isChecked) {

                            mBottomSheetDialog.dismiss();
                            List<GetCategoryDocumentsResponse> downloadedList = new ArrayList<>();
                            if (mSelectedDocumentList != null && mSelectedDocumentList.size() > 0) {

                                for (GetCategoryDocumentsResponse getCategoryDocumentsResponse : mSelectedDocumentList) {
                                    if (getCategoryDocumentsResponse.getType().equalsIgnoreCase("document")) {
                                        downloadedList.add(getCategoryDocumentsResponse);
                                    }
                                }
                            }

                            if (downloadedList != null && downloadedList.size() > 0) {
                                convertingDownloadUrl(downloadedList);
                            }

                        }
                        else
                        {
                            mBottomSheetDialog.dismiss();
                            if (mSelectedDocumentList != null && mSelectedDocumentList.size() > 0) {
                                for(GetCategoryDocumentsResponse categoryDocumentsResponse : mSelectedDocumentList)
                                {
                                    OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                                    String filepath = offLine_files_repository.getFilePathFromLocalTable(categoryDocumentsResponse.getDocument_version_id());

                                    if(filepath != null && !filepath.isEmpty())
                                    {
                                        CommonFunctions.deleteFileFromInternalStorage(filepath);
                                    }
                                    offLine_files_repository.deleteAlreadydownloadedFile(categoryDocumentsResponse.getDocument_version_id());
                                }
                            }

                            if (isFromList == true) {
                                mAdapterList.clearAll();
                            } else {
                                mAdapter.clearAll();
                            }
                            List<GetCategoryDocumentsResponse> dummyList = new ArrayList<>();
                            updateToolbarMenuItems(dummyList);


                        }
                    }
                }
            });


            List<GetCategoryDocumentsResponse> categoryDocumentsResponseFolderList = new ArrayList<>();
            List<GetCategoryDocumentsResponse> categoryDocumentsResponseDocumentList = new ArrayList<>();

            for (GetCategoryDocumentsResponse viewListObj : mSelectedDocumentList) {

                if (viewListObj.getType().equalsIgnoreCase("category")) {
                    categoryDocumentsResponseFolderList.add(viewListObj);
                } else {
                    categoryDocumentsResponseDocumentList.add(viewListObj);
                }
            }



            if (categoryDocumentsResponseDocumentList.size() == 1) {
                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                    int selectedColor = Color.parseColor(itemSelectedColor);

                    copyImage.setColorFilter(selectedColor);
                    moveImage.setColorFilter(selectedColor);
                    renameImage.setColorFilter(selectedColor);
                    shareImage.setColorFilter(selectedColor);
                    availableOfflineImage.setColorFilter(selectedColor);
                }

                clearSelectionLayout.setVisibility(View.GONE);
                availableOfflineLayout.setVisibility(View.VISIBLE);
                copyLayout.setVisibility(View.VISIBLE);
                doc_info_layout.setVisibility(View.VISIBLE);
                line_layout.setVisibility(View.VISIBLE);

            } else if (categoryDocumentsResponseDocumentList.size() >= 1) {
                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                    int selectedColor = Color.parseColor(itemSelectedColor);

                    clearSelectionImage.setColorFilter(selectedColor);
                    copyImage.setColorFilter(selectedColor);
                    moveImage.setColorFilter(selectedColor);
                    shareImage.setColorFilter(selectedColor);
                    availableOfflineImage.setColorFilter(selectedColor);
                }

                clearSelectionLayout.setVisibility(View.VISIBLE);
                availableOfflineLayout.setVisibility(View.VISIBLE);
                doc_info_layout.setVisibility(View.GONE);
                line_layout.setVisibility(View.GONE);
                copyLayout.setVisibility(View.VISIBLE);
            }


        }


    public void convertingDownloadUrl(List<GetCategoryDocumentsResponse> downloadedList)
    {
        downloadingUrlDataList = downloadedList;
        if(downloadingUrlDataList.size()> index) {

            getDownloadurlFromService(downloadingUrlDataList.get(index).getObject_id(),downloadingUrlDataList.get(index).getIs_shared());

        }
    }


    public void getDownloadurlFromService(String document_version_id, String is_Shared)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DownloadDocumentService downloadDocumentService = retrofitAPI.create(DownloadDocumentService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            List<String> strlist = new ArrayList<>();
            strlist.add(document_version_id);
            DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(strlist, is_Shared);
            final String request = new Gson().toJson(downloadDocumentRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = downloadDocumentService.download(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ApiResponse<DownloadDocumentResponse>>() {
                @Override
                public void onResponse(Response<ApiResponse<DownloadDocumentResponse>> response, Retrofit retrofit) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();
                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationSharedActivity.this, message, apiResponse.status.getCode())) {
                            DownloadDocumentResponse downloadDocumentResponse = response.body().getData();

                            String downloaded_url = downloadDocumentResponse.getData();

                            String access_Token = PreferenceUtils.getAccessToken(context);

                            byte[] encodeValue = Base64.encode(access_Token.getBytes(), Base64.DEFAULT);
                            String base64AccessToken = new String(encodeValue);

                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                StrictMode.ThreadPolicy policy =  new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                            }


                            downloadingUrlDataList.get(index).setDownloadUrl(downloaded_url+"&token="+base64AccessToken);


                            index++;
                            if(downloadingUrlDataList.size()> index) {
                                getDownloadurlFromService(downloadingUrlDataList.get(index).getObject_id(), downloadingUrlDataList.get(index).getIs_shared());

                            }
                            else
                            {

                                if (isFromList == true) {
                                    mAdapterList.notifyDataSetChanged();
                                    mAdapterList.clearAll();
                                }
                                else
                                {
                                    mAdapter.notifyDataSetChanged();
                                    mAdapter.clearAll();
                                }

                                getDownloadManagerForDownloading(downloadingUrlDataList);

                            }

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

        sendPinButton.setText("OK");

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


    private void getDownloadManagerForDownloading(List<GetCategoryDocumentsResponse> downloadingUrlDataList)
    {
        if (isFromList == true) {
            mAdapterList.clearAll();
        } else {
            mAdapter.clearAll();
        }
        List<GetCategoryDocumentsResponse> dummyList = new ArrayList<>();
        updateToolbarMenuItems(dummyList);
        index = 0;
        transparentProgressDialog.show();

        downloadingItemsList = downloadingUrlDataList;
        if(downloadingItemsList.size() > downloadIndex) {
            downLoadImageSeparately(downloadingItemsList.get(downloadIndex));
        }


    }

    private void downLoadImageSeparately(GetCategoryDocumentsResponse categoryDocumentsResponse)
    {
        if (!TextUtils.isEmpty(categoryDocumentsResponse.getDownloadUrl())) {
            FileDownloadManager fileDownloadManager = new FileDownloadManager(NavigationSharedActivity.this);
            fileDownloadManager.setFileTitle(categoryDocumentsResponse.getName());
            fileDownloadManager.setDownloadUrl(categoryDocumentsResponse.getDownloadUrl());
            fileDownloadManager.setDigitalAssets(categoryDocumentsResponse);
            fileDownloadManager.setmFileDownloadListener(new FileDownloadManager.FileDownloadListener() {
                @Override
                public void fileDownloadSuccess(String path) {

                    OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                    if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(categoryDocumentsResponse.getDocument_version_id())) {
                        String filepath = offLine_files_repository.getFilePathFromLocalTable(categoryDocumentsResponse.getDocument_version_id());
                        if(filepath != null && !filepath.isEmpty())
                        {
                            CommonFunctions.deleteFileFromInternalStorage(filepath);
                        }
                        offLine_files_repository.deleteAlreadydownloadedFile(categoryDocumentsResponse.getDocument_version_id());
                        insertIntoOffLineFilesTable(categoryDocumentsResponse, path);
                    }
                    else
                    {
                        insertIntoOffLineFilesTable(categoryDocumentsResponse, path);
                    }


                    downloadIndex++;
                    if(downloadingItemsList.size()> downloadIndex) {
                        downLoadImageSeparately(downloadingItemsList.get(downloadIndex));

                    }
                    else
                    {
                        downloadIndex = 0;
                        transparentProgressDialog.dismiss();
                        CommonFunctions.showSuccessfullyDownloaded(context);
                    }


                }

                @Override
                public void fileDownloadFailure() {

                    Toast.makeText(context, "Download Failed", Toast.LENGTH_LONG).show();

                }
            });
            fileDownloadManager.downloadTheFile();
        }
    }

    private void insertIntoOffLineFilesTable(GetCategoryDocumentsResponse digitalAsset, String path)
    {
        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
        OfflineFiles offlineFilesModel = new OfflineFiles();
        offlineFilesModel.setDocumentId(digitalAsset.getObject_id());
        offlineFilesModel.setDocumentName(digitalAsset.getName());
        offlineFilesModel.setDocumentVersionId(digitalAsset.getDocument_version_id());
        offlineFilesModel.setDownloadDate(DateHelper.getCurrentDate());
        offlineFilesModel.setFilename(digitalAsset.getName());
        offlineFilesModel.setFilePath(path);
        offlineFilesModel.setFiletype(digitalAsset.getFiletype());
        offlineFilesModel.setFileSize(digitalAsset.getFilesize());
        offlineFilesModel.setVersionNumber(digitalAsset.getVersion_number());
        offlineFilesModel.setSource("Shared");

        offLine_files_repository.InsertOfflineFilesData(offlineFilesModel);
    }



    public void getDownloadurlFromServiceSingleDocument(GetCategoryDocumentsResponse documentsResponse)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DownloadDocumentService downloadDocumentService = retrofitAPI.create(DownloadDocumentService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            List<String> strlist = new ArrayList<>();
            strlist.add(documentsResponse.getObject_id());
            DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(strlist, documentsResponse.getIs_shared());
            final String request = new Gson().toJson(downloadDocumentRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = downloadDocumentService.download(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ApiResponse<DownloadDocumentResponse>>() {
                @Override
                public void onResponse(Response<ApiResponse<DownloadDocumentResponse>> response, Retrofit retrofit) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();
                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationSharedActivity.this, message, apiResponse.status.getCode())) {
                            DownloadDocumentResponse downloadDocumentResponse = response.body().getData();

                            String downloaded_url = downloadDocumentResponse.getData();

                            String access_Token = PreferenceUtils.getAccessToken(context);

                            byte[] encodeValue = Base64.encode(access_Token.getBytes(), Base64.DEFAULT);
                            String base64AccessToken = new String(encodeValue);

                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                StrictMode.ThreadPolicy policy =  new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                            }

                            documentsResponse.setDownloadUrl(downloaded_url+"&token="+base64AccessToken);
                            List<GetCategoryDocumentsResponse> downloadingList = new ArrayList<>();
                            downloadingList.add(documentsResponse);
                            getDownloadManagerForDownloading(downloadingList);
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




    public void getWorkSpaceCategoriesDocument()
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetSharedCategoryDocumentsRequest mGetSharedDocumentsRequest;

            mGetSharedDocumentsRequest = new GetSharedCategoryDocumentsRequest(ObjectId);

            String request = new Gson().toJson(mGetSharedDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = mGetCategoryDocumentsService.getSharedCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<APIResponseModel>() {
                @Override
                public void onResponse(Response<APIResponseModel> response, Retrofit retrofit) {

                    // BaseApiResponse apiResponse = response.body();
                    if (response != null) {

                        transparentProgressDialog.dismiss();

                        String message = "";
                        if(response.body().getStatus().getMessage() != null)
                        {
                            message = response.body().getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationSharedActivity.this, message, response.body().getStatus().getCode()))
                        {
                            List<APIResponseModel.Category> categoryList = response.body().getData().getCategories();
                            if (categoryList != null && categoryList.size() > 0) {
                                for (APIResponseModel.Category category : categoryList) {
                                    GetCategoryDocumentsResponse getCategoryDocumentsResponse = new GetCategoryDocumentsResponse();
                                    getCategoryDocumentsResponse.setObject_id(category.getCategoryId());
                                    getCategoryDocumentsResponse.setName(category.getCategoryName());
                                    getCategoryDocumentsResponse.setType(category.getType());
                                    getCategoryDocumentsResponse.setCreated_date(category.getCreatedDate());
                                    getCategoryDocumentsResponse.setFilesize("0");
                                    documentsCategoryList.add(getCategoryDocumentsResponse);
                                }
                            }

                            GlobalVariables.sharedDocsIsAscending = true;
                        //    GlobalVariables.sharedDocsSortType = "type";
                            doLocalSorting(GlobalVariables.sharedDocsSortType);
                            toggleEmptyState();
                            reloadAdapter();
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



    private void openBottomSheet() {

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);

        final TextView mSortByName = (TextView) view.findViewById(R.id.sort_by_name);
        TextView mSortByNewest = (TextView) view.findViewById(R.id.sort_by_newest);
        TextView mSortBySize = (TextView) view.findViewById(R.id.sort_by_size);
        TextView mSortByDate = (TextView) view.findViewById(R.id.sort_by_date);
        final ImageView sortNameImage = (ImageView) view.findViewById(R.id.sort_up_image);
        final ImageView sortNewestImage = (ImageView) view.findViewById(R.id.sort_up_newest_image);
        final ImageView sortSizeImage = (ImageView) view.findViewById(R.id.sort_up_size_image);
        final ImageView sortDateImage = (ImageView) view.findViewById(R.id.sort_up_date_image);

        final ImageView sortNameDoneImage = (ImageView) view.findViewById(R.id.done_image);
        final ImageView sortNewestDoneImage = (ImageView) view.findViewById(R.id.done_sort_newest_image);
        final ImageView sortSizeDoneImage = (ImageView) view.findViewById(R.id.done_sort_size_image);
        final ImageView sortDateDoneImage = (ImageView) view.findViewById(R.id.done_sort_date_image);
        RelativeLayout sortBy_date_layout = (RelativeLayout) view.findViewById(R.id.sortBy_date_layout);
        sortBy_date_layout.setVisibility(View.GONE);

        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        if (GlobalVariables.sharedDocsSortType.equalsIgnoreCase("type")) {
            sortNameImage.setVisibility(View.INVISIBLE);
            sortNewestImage.setVisibility(View.VISIBLE);
            sortSizeImage.setVisibility(View.INVISIBLE);
            sortDateImage.setVisibility(View.INVISIBLE);

            sortNameDoneImage.setVisibility(View.INVISIBLE);
            sortNewestDoneImage.setVisibility(View.VISIBLE);
            sortSizeDoneImage.setVisibility(View.INVISIBLE);
            sortDateDoneImage.setVisibility(View.INVISIBLE);

            if (GlobalVariables.sharedDocsIsAscending)
            {
                sortNewestImage.setImageResource(R.mipmap.ic_sort_up);
            }
            else
            {
                sortNewestImage.setImageResource(R.mipmap.ic_sort_down);
            }
        }
        else if (GlobalVariables.sharedDocsSortType.equalsIgnoreCase("name")) {
            sortNameImage.setVisibility(View.VISIBLE);
            sortNewestImage.setVisibility(View.INVISIBLE);
            sortSizeImage.setVisibility(View.INVISIBLE);
            sortDateImage.setVisibility(View.INVISIBLE);

            sortNameDoneImage.setVisibility(View.VISIBLE);
            sortNewestDoneImage.setVisibility(View.INVISIBLE);
            sortSizeDoneImage.setVisibility(View.INVISIBLE);
            sortDateDoneImage.setVisibility(View.INVISIBLE);

            if (GlobalVariables.sharedDocsIsAscending)
            {
                sortNameImage.setImageResource(R.mipmap.ic_sort_up);
            }
            else
            {
                sortNameImage.setImageResource(R.mipmap.ic_sort_down);
            }
        } else if (GlobalVariables.sharedDocsSortType.equalsIgnoreCase("filesize")) {
            sortNameImage.setVisibility(View.INVISIBLE);
            sortNewestImage.setVisibility(View.INVISIBLE);
            sortSizeImage.setVisibility(View.VISIBLE);
            sortDateImage.setVisibility(View.INVISIBLE);

            sortNameDoneImage.setVisibility(View.INVISIBLE);
            sortNewestDoneImage.setVisibility(View.INVISIBLE);
            sortSizeDoneImage.setVisibility(View.VISIBLE);
            sortDateDoneImage.setVisibility(View.INVISIBLE);

            if (GlobalVariables.sharedDocsIsAscending)
            {
                sortSizeImage.setImageResource(R.mipmap.ic_sort_up);
            }
            else
            {
                sortSizeImage.setImageResource(R.mipmap.ic_sort_down);
            }
        } else if (GlobalVariables.sharedDocsSortType.equalsIgnoreCase("unix_date")) {
            sortNameImage.setVisibility(View.INVISIBLE);
            sortNewestImage.setVisibility(View.INVISIBLE);
            sortSizeImage.setVisibility(View.INVISIBLE);
            sortDateImage.setVisibility(View.VISIBLE);

            sortNameDoneImage.setVisibility(View.INVISIBLE);
            sortNewestDoneImage.setVisibility(View.INVISIBLE);
            sortSizeDoneImage.setVisibility(View.INVISIBLE);
            sortDateDoneImage.setVisibility(View.VISIBLE);

            if (GlobalVariables.sharedDocsIsAscending)
            {
                sortDateImage.setImageResource(R.mipmap.ic_sort_up);
            }
            else
            {
                sortDateImage.setImageResource(R.mipmap.ic_sort_down);
            }
        }

        mSortByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByName = true;
                sortByNewest = false;
                sortBySize = false;
                sortByDate = false;

                mBottomSheetDialog.dismiss();
                sortNameImage.setVisibility(View.VISIBLE);
                sortNewestImage.setVisibility(View.INVISIBLE);
                sortSizeImage.setVisibility(View.INVISIBLE);
                sortDateImage.setVisibility(View.INVISIBLE);

                sortNameDoneImage.setVisibility(View.VISIBLE);
                sortNewestDoneImage.setVisibility(View.INVISIBLE);
                sortSizeDoneImage.setVisibility(View.INVISIBLE);
                sortDateDoneImage.setVisibility(View.INVISIBLE);

                if(GlobalVariables.sharedDocsSortType.equalsIgnoreCase("name"))
                {
                    GlobalVariables.sharedDocsIsAscending = !GlobalVariables.sharedDocsIsAscending;
                }
                else
                {
                    GlobalVariables.sharedDocsIsAscending = true;
                }

                doLocalSorting("name");


            }
        });

        mSortByNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByNewest = true;
                sortByName = false;
                sortBySize = false;
                sortByDate = false;

                if(GlobalVariables.sharedDocsSortType.equalsIgnoreCase("type"))
                {
                    GlobalVariables.sharedDocsIsAscending = !GlobalVariables.sharedDocsIsAscending;
                }
                else
                {
                    GlobalVariables.sharedDocsIsAscending = true;
                }

                doLocalSorting("type");
                mBottomSheetDialog.dismiss();
                sortNameImage.setVisibility(View.INVISIBLE);
                sortNewestImage.setVisibility(View.VISIBLE);
                sortSizeImage.setVisibility(View.INVISIBLE);
                sortDateImage.setVisibility(View.INVISIBLE);
                sortNameDoneImage.setVisibility(View.INVISIBLE);
                sortNewestDoneImage.setVisibility(View.VISIBLE);
                sortSizeDoneImage.setVisibility(View.INVISIBLE);
                sortDateDoneImage.setVisibility(View.INVISIBLE);
            }
        });

        mSortBySize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortBySize = true;
                sortByNewest = false;
                sortByName = false;
                sortByDate = false;

                if(GlobalVariables.sharedDocsSortType.equalsIgnoreCase("filesize"))
                {
                    GlobalVariables.sharedDocsIsAscending = !GlobalVariables.sharedDocsIsAscending;
                }
                else
                {
                    GlobalVariables.sharedDocsIsAscending = true;
                }

                doLocalSorting("filesize");
                mBottomSheetDialog.dismiss();
                sortNameImage.setVisibility(View.INVISIBLE);
                sortNewestImage.setVisibility(View.INVISIBLE);
                sortSizeImage.setVisibility(View.VISIBLE);
                sortDateImage.setVisibility(View.INVISIBLE);
                sortNameDoneImage.setVisibility(View.INVISIBLE);
                sortNewestDoneImage.setVisibility(View.INVISIBLE);
                sortSizeDoneImage.setVisibility(View.VISIBLE);
                sortDateDoneImage.setVisibility(View.INVISIBLE);
            }
        });

        mSortByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByDate = true;
                sortBySize = false;
                sortByNewest = false;
                sortByName = false;

                if(GlobalVariables.sharedDocsSortType.equalsIgnoreCase("unix_date"))
                {
                    GlobalVariables.sharedDocsIsAscending = !GlobalVariables.sharedDocsIsAscending;
                }
                else
                {
                    GlobalVariables.sharedDocsIsAscending = true;
                }
                doLocalSorting("unix_date");
                mBottomSheetDialog.dismiss();
                sortNameImage.setVisibility(View.INVISIBLE);
                sortNewestImage.setVisibility(View.INVISIBLE);
                sortSizeImage.setVisibility(View.INVISIBLE);
                sortDateImage.setVisibility(View.VISIBLE);
                sortNameDoneImage.setVisibility(View.INVISIBLE);
                sortNewestDoneImage.setVisibility(View.INVISIBLE);
                sortSizeDoneImage.setVisibility(View.INVISIBLE);
                sortDateDoneImage.setVisibility(View.VISIBLE);
            }
        });
    }



    //Sorting
    private  void doLocalSorting(String sortType)
    {
        GlobalVariables.sharedDocsSortType = sortType;
     //   GlobalVariables.sharedDocsIsAscending = !GlobalVariables.sharedDocsIsAscending;

        if(GlobalVariables.sharedDocsSortType.equalsIgnoreCase("name"))
        {
            sort.setText("Name");
        }
        else if(GlobalVariables.sharedDocsSortType.equalsIgnoreCase("type"))
        {
            sort.setText("Type");
        }
        else if(GlobalVariables.sharedDocsSortType.equalsIgnoreCase("filesize"))
        {
            sort.setText("Size");
        }
        else if(GlobalVariables.sharedDocsSortType.equalsIgnoreCase("unix_date"))
        {
            sort.setText("Date");
        }


        if(GlobalVariables.sharedDocsIsAscending)
        {
            sort_image.setImageResource(R.mipmap.ic_sortup);
        }
        else
        {
            sort_image.setImageResource(R.mipmap.ic_sort);
        }

        if (sortType.equalsIgnoreCase("name"))
        {
            sortByName();
        }
        else if (sortType.equalsIgnoreCase("type"))
        {
            sortByType();
        }
        else if (sortType.equalsIgnoreCase("unix_date"))
        {
            sortByDate();
        }
        else if (sortType.equalsIgnoreCase("filesize"))
        {
            sortBySize();
        }

        reloadAdapter();
    }

    private void sortByName()
    {
        if (GlobalVariables.sharedDocsIsAscending) {
            Collections.sort(documentsCategoryList, new Comparator<GetCategoryDocumentsResponse>() {
                @Override
                public int compare(GetCategoryDocumentsResponse lhs, GetCategoryDocumentsResponse rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });
        }
        else
        {
            Collections.reverse(documentsCategoryList);
        }
    }

    private void sortByType()
    {
            if(documentsCategoryList != null && documentsCategoryList.size() > 0)
            {
                List<GetCategoryDocumentsResponse> folderArrayList = new ArrayList<>();
                List<GetCategoryDocumentsResponse> documentArrayList = new ArrayList<>();

                for(GetCategoryDocumentsResponse categoryDocumentsResponse : documentsCategoryList)
                {
                    if(categoryDocumentsResponse.getType().equalsIgnoreCase("category"))
                    {
                        folderArrayList.add(categoryDocumentsResponse);
                    }
                    else
                    {
                        documentArrayList.add(categoryDocumentsResponse);
                    }
                }
                if (GlobalVariables.sharedDocsIsAscending) {
                    Collections.sort(folderArrayList, new Comparator<GetCategoryDocumentsResponse>() {
                        @Override
                        public int compare(GetCategoryDocumentsResponse lhs, GetCategoryDocumentsResponse rhs) {
                            return lhs.getName().compareToIgnoreCase(rhs.getName());
                        }
                    });


                    Collections.sort(documentArrayList, new Comparator<GetCategoryDocumentsResponse>() {
                        @Override
                        public int compare(GetCategoryDocumentsResponse lhs, GetCategoryDocumentsResponse rhs) {
                            return lhs.getName().compareToIgnoreCase(rhs.getName());
                        }
                    });
                }
                else
                {
                    Collections.sort(folderArrayList, new Comparator<GetCategoryDocumentsResponse>() {
                        @Override
                        public int compare(GetCategoryDocumentsResponse lhs, GetCategoryDocumentsResponse rhs) {
                            return rhs.getName().compareToIgnoreCase(lhs.getName());
                        }
                    });

                    Collections.sort(documentArrayList, new Comparator<GetCategoryDocumentsResponse>() {
                        @Override
                        public int compare(GetCategoryDocumentsResponse lhs, GetCategoryDocumentsResponse rhs) {
                            return rhs.getName().compareToIgnoreCase(lhs.getName());
                        }
                    });


                }

                documentsCategoryList.clear();

                if (GlobalVariables.sharedDocsIsAscending) {
                    if(folderArrayList != null && folderArrayList.size() > 0)
                    {
                        for(GetCategoryDocumentsResponse categoryDocumentsResponse : folderArrayList)
                        {
                            documentsCategoryList.add(categoryDocumentsResponse);
                        }
                    }

                    if(documentArrayList != null && documentArrayList.size() > 0)
                    {
                        for(GetCategoryDocumentsResponse categoryDocumentsResponse : documentArrayList)
                        {
                            documentsCategoryList.add(categoryDocumentsResponse);
                        }
                    }

                }
                else
                {

                    if(documentArrayList != null && documentArrayList.size() > 0)
                    {
                        for(GetCategoryDocumentsResponse categoryDocumentsResponse : documentArrayList)
                        {
                            documentsCategoryList.add(categoryDocumentsResponse);
                        }
                    }
                    if(folderArrayList != null && folderArrayList.size() > 0)
                    {
                        for(GetCategoryDocumentsResponse categoryDocumentsResponse : folderArrayList)
                        {
                            documentsCategoryList.add(categoryDocumentsResponse);
                        }
                    }



                }

            }



    }

    private void sortByDate()
    {
        if (GlobalVariables.sharedDocsIsAscending) {
            Collections.sort(documentsCategoryList, new Comparator<GetCategoryDocumentsResponse>() {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                @Override
                public int compare(GetCategoryDocumentsResponse categoryDocumentsResponse, GetCategoryDocumentsResponse categoryDocumentsResponse1) {
                    try {
                        return dateFormat.parse(categoryDocumentsResponse.getShared_date()).compareTo(dateFormat.parse(categoryDocumentsResponse1.getShared_date()));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
        }
        else
        {
            Collections.reverse(documentsCategoryList);
        }

    }

    private void sortBySize()
    {
        if (GlobalVariables.sharedDocsIsAscending) {
            Collections.sort(documentsCategoryList, new Comparator<GetCategoryDocumentsResponse>() {
                @Override
                public int compare(GetCategoryDocumentsResponse lhs, GetCategoryDocumentsResponse rhs) {
                    return Float.compare(Float.parseFloat(lhs.getFilesize()), Float.parseFloat(rhs.getFilesize()));
                }
            });
        }
        else
        {
            Collections.reverse(documentsCategoryList);
        }

    }

    public void shareEndUserDocuments(String categoryId, String shareWorkSpaceId)
    {
        if (NetworkUtils.isNetworkAvailable(context)){

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

           ArrayList<String> document_ids = new ArrayList<>();
            if(GlobalVariables.selectedDocumentsList.size() > 0)
            {
                for(GetCategoryDocumentsResponse categoryDocumentsResponse : GlobalVariables.selectedDocumentsList)
                {
                    document_ids.add(categoryDocumentsResponse.getObject_id());
                }
            }

            ShareEndUserDocumentsRequest mShareEndUserDocumentsRequest = new ShareEndUserDocumentsRequest(document_ids,shareWorkSpaceId, categoryId);

            String request = new Gson().toJson(mShareEndUserDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final ShareEndUserDocumentsService mShareEndUserDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);
            Call call = mShareEndUserDocumentsService.getSharedEndUserDocuments(params,PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationSharedActivity.this, message, apiResponse.status.getCode()))
                        {
                            String mMessage = apiResponse.status.getMessage().toString();
                            Toast.makeText(context,mMessage,Toast.LENGTH_SHORT).show();
                            GlobalVariables.isMoveInitiated = false;
                            GlobalVariables.selectedActionName = "";
                            GlobalVariables.selectedDocumentsList.clear();

                            Intent intent = new Intent(context, NavigationSharedActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("ObjectId", "0");
                            startActivity(intent);
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

    private void clearSelectedListAfterOperation()
    {
        if (isFromList == true) {
            mAdapterList.clearAll();
        } else {
            mAdapter.clearAll();
        }
        List<GetCategoryDocumentsResponse> dummyList = new ArrayList<>();
        updateToolbarMenuItems(dummyList);

    }

    public void assigningMoveOriginIndex()
    {
        GlobalVariables.moveOriginIndex = GlobalVariables.activityCount;
    }


}
