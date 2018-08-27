package com.swaas.mwc.DMS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.swaas.mwc.API.Model.ApiResponse;
import com.swaas.mwc.API.Model.ColorCodeModel;
import com.swaas.mwc.API.Model.CopyDocumentRequest;
import com.swaas.mwc.API.Model.DeleteDocumentRequest;
import com.swaas.mwc.API.Model.DeleteDocumentResponseModel;
import com.swaas.mwc.API.Model.DeleteEndUserFolderMoveRequest;
import com.swaas.mwc.API.Model.DeleteEndUserFolderRequest;
import com.swaas.mwc.API.Model.DownloadDocumentRequest;
import com.swaas.mwc.API.Model.DownloadDocumentResponse;
import com.swaas.mwc.API.Model.EditDocumentPropertiesRequest;
import com.swaas.mwc.API.Model.EndUserRenameRequest;
import com.swaas.mwc.API.Model.GetCategoryDocumentsRequest;
import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;
import com.swaas.mwc.API.Model.GetEndUserCategoriesRequest;
import com.swaas.mwc.API.Model.GetEndUserCategoriesResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.MoveDocumentRequest;
import com.swaas.mwc.API.Model.OfflineFiles;
import com.swaas.mwc.API.Model.SharedDocumentResponseModel;
import com.swaas.mwc.API.Model.StopSharingRequestModel;
import com.swaas.mwc.API.Model.UploadNewFolderRequest;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.API.Service.CopyDocumentService;
import com.swaas.mwc.API.Service.DeleteDocumentService;
import com.swaas.mwc.API.Service.DeleteEndUserFolderService;
import com.swaas.mwc.API.Service.DeleteEndUserMoveService;
import com.swaas.mwc.API.Service.DownloadDocumentService;
import com.swaas.mwc.API.Service.EditDocumentPropertiesService;
import com.swaas.mwc.API.Service.EndUserRenameService;
import com.swaas.mwc.API.Service.GetCategoryDocumentsService;
import com.swaas.mwc.API.Service.GetEndUserCategoriesService;
import com.swaas.mwc.API.Service.GetEndUserParentSHaredFoldersService;
import com.swaas.mwc.API.Service.MoveDocumentService;
import com.swaas.mwc.API.Service.UploadNewFolderService;
import com.swaas.mwc.Adapters.DmsAdapter;
import com.swaas.mwc.Adapters.DmsAdapterList;
import com.swaas.mwc.Common.CameraUtils;
import com.swaas.mwc.Common.CommonFunctions;
import com.swaas.mwc.Common.FileDownloadManager;
import com.swaas.mwc.Common.GlobalVariables;
import com.swaas.mwc.Common.ServerConfig;
import com.swaas.mwc.Common.SimpleDividerItemDecoration;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Database.OffLine_Files_Repository;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;
import com.swaas.mwc.Utils.DateHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class NavigationMyFolderActivity extends BaseActivity {

    DmsAdapter mAdapter;
    DmsAdapterList mAdapterList;
    RecyclerView mRecyclerView;
    NestedScrollView scrollView;
    AlertDialog mCustomAlertDialog;
    AlertDialog mAlertDialog;
    MenuItem menuItemSearch, menuItemDelete, menuItemShare, menuItemMove, menuItemMore;
    boolean sortByNameAsc = true;
    boolean sortByTypeAsc = true;
    boolean sortBySizeAsc = true;
    boolean sortByDateAsc = true;

    boolean isFromList;
    LinearLayoutManager linearLayoutManager;
    boolean sortByName = false;
    boolean sortByNewest = false;
    boolean sortBySize = false;
    boolean sortByDate = false;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int REQUEST_GALLERY_CODE = 200;
    public static final int REQUEST_CAPTURE_IMAGE_CODE = 300;
    public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 400;
    ArrayList<String> list_upload = new ArrayList<>();
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    RelativeLayout toggleView;
    ImageView toggle, sort_image;
    public static TextView sort;
    LinearLayout sortingView;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton actionUpload, actionCamera, actionNewFolder, actionVideo;

    Context context = this;
    Uri fileUri;
    List<GetCategoryDocumentsResponse> downloadingUrlDataList = new ArrayList<>();
    int index=0;
    private  String imageStoragePath;
    List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses = new ArrayList<>();
    List<GetCategoryDocumentsResponse> mSelectedDocumentList = new ArrayList<>();
    Uri uri = null;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList();
    List<GetCategoryDocumentsResponse> listGetCategoryDocuments = new ArrayList<>();
    List<GetEndUserCategoriesResponse> listGetMoveCategoryDocuments = new ArrayList<>();
    int pageNumber = 0;
    int totalPages=1;
    static Boolean isTouched = false;
    String objectId;
    int pageSize = 20;
    int backButtonCount = 0;
    RelativeLayout move_layout;
    TextView move_textview, cancel_textview;
    LinearLayout sorting_layout;
    BottomNavigationView bottomNavigationLayout;
    public static int mode = 0;
    AlertDialog mBackDialog;
    boolean permissionAccess = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intiaizeViews();
        OnClickListeners();
        getWhiteLabelProperities();

        mRecyclerView.setNestedScrollingEnabled(false);

        if(getIntent().getStringExtra("ObjectId") != null)
        {
            objectId = getIntent().getStringExtra("ObjectId");
        }
        else
        {
            objectId = "0";
        }

        toggleAddAndBackButton();
        getCategoryDocuments();

        if (!GlobalVariables.isMoveInitiated) {
            hideBottomView();
        }
        else{
            showBottomView();
        }


        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.CAPTURE_VIDEO_OUTPUT,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) // ask single or multiple permission once
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        // I can control the camera now
                        permissionAccess = false;
                    //    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                    } else {
                        // Oups permission denied
                   //     Toast.makeText(context, "Permission Required", Toast.LENGTH_SHORT).show();
                        permissionAccess = true;
                    }
                });




    }



    private void showBottomView()
    {
        bottomNavigationLayout.setVisibility(View.GONE);
        sorting_layout.setVisibility(View.GONE);
        move_layout.setVisibility(View.VISIBLE);
        floatingActionMenu.setVisibility(View.GONE);
        GlobalVariables.sortType = "type";

        if (GlobalVariables.selectedActionName.equalsIgnoreCase("move") || GlobalVariables.selectedActionName.equalsIgnoreCase("delete"))
        {
            move_textview.setText("MOVE");
        }
        else if(GlobalVariables.selectedActionName.equalsIgnoreCase("copy"))
        {
            move_textview.setText("COPY");
        }
    }

    private void hideBottomView()
    {
        bottomNavigationLayout.setVisibility(View.VISIBLE);
        sorting_layout.setVisibility(View.VISIBLE);
        move_layout.setVisibility(View.GONE);
        floatingActionMenu.setVisibility(View.VISIBLE);

    }

    private void getCategoryMoveDocuments()
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

         /*   LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);

            if (!isFinishing()) {
                transparentProgressDialog.show();
            }*/


            final GetEndUserCategoriesRequest getEndUserCategoriesRequest;

            getEndUserCategoriesRequest = new GetEndUserCategoriesRequest(objectId);

            String request = new Gson().toJson(getEndUserCategoriesRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

             GetEndUserCategoriesService getCategoryDocumentsService = retrofitAPI.create(GetEndUserCategoriesService.class);

            Call call = getCategoryDocumentsService.getEndUsercategory(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ListPinDevicesResponse<GetEndUserCategoriesResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetEndUserCategoriesResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                      //  transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                             //   transparentProgressDialog.dismiss();



                                listGetMoveCategoryDocuments = response.body().getData();

                                convertToCategoryDocumentResponse(listGetMoveCategoryDocuments);
                                reloadAdapterData();

                           //     setAdapterToView(listGetMoveCategoryDocuments);

                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                          //  transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
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
                                        AccountSettings accountSettings = new AccountSettings(NavigationMyFolderActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(NavigationMyFolderActivity.this, LoginActivity.class));
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
                //    transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }

    private void convertToCategoryDocumentResponse(List<GetEndUserCategoriesResponse> listGetMoveCategoryDocuments)
    {
        listGetCategoryDocuments.clear();
        if(listGetMoveCategoryDocuments != null && listGetMoveCategoryDocuments.size() > 0)
        {
            for(GetEndUserCategoriesResponse endUserCategoriesResponse : listGetMoveCategoryDocuments)
            {
                GetCategoryDocumentsResponse getCategoryDocumentsResponse = new GetCategoryDocumentsResponse();
                getCategoryDocumentsResponse.setName(endUserCategoriesResponse.getCategory_name());
                getCategoryDocumentsResponse.setCategory_id(endUserCategoriesResponse.getCategory_id());
                getCategoryDocumentsResponse.setType("category");
                getCategoryDocumentsResponse.setObject_id(endUserCategoriesResponse.getCategory_id());

                listGetCategoryDocuments.add(getCategoryDocumentsResponse);

            }
        }
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_navigation_my_folder;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_folder;
    }

    public void getCategoryDocuments() {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(objectId, "list", "category", "1", "0");

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            String sorting = GlobalVariables.sortType;
            if(GlobalVariables.isAscending == false)
            {
                sorting = "-"+GlobalVariables.sortType;
            }

         //   Call call = mGetCategoryDocumentsService.getCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(context),page);
            Call call = mGetCategoryDocumentsService.getCategoryDocuments(params, PreferenceUtils.getAccessToken(context),String.valueOf(pageNumber+1), String.valueOf(pageSize),sorting );

            call.enqueue(new Callback<ListPinDevicesResponse<GetCategoryDocumentsResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetCategoryDocumentsResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();

                                pageNumber++;

                                List<GetCategoryDocumentsResponse> filteredArray = new ArrayList<>();
                                /*List<GetCategoryDocumentsResponse> tempList = new ArrayList<>();
                                tempList =  response.body().getData();

                                if(GlobalVariables.isMoveInitiated)
                                {
                                    for(GetCategoryDocumentsResponse getCategory : tempList)
                                    {
                                        if(getCategory.getType().equalsIgnoreCase("category"))
                                        {
                                            filteredArray.add(getCategory);
                                        }
                                    }
                                }
                                else
                                {
                                    filteredArray = tempList;
                                }*/

                                filteredArray = response.body().getData();

                                if(pageNumber == 1)
                                {
                                    mGetCategoryDocumentsResponses = filteredArray;
                                }
                                else
                                {
                                    listGetCategoryDocuments = filteredArray;
                                    mGetCategoryDocumentsResponses.addAll(filteredArray);
                                }


                                totalPages  = Integer.parseInt(response.headers().get("X-Pagination-Page-Count"));
                            //    pageNumber = Integer.parseInt(response.headers().get("X-Pagination-Current-Page"));

                                setSortTitle();

                                reloadAdapterData();
                                List<GetCategoryDocumentsResponse> dummyList = new ArrayList<>();
                                updateToolbarMenuItems(dummyList);


                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
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

    private void setSortTitle()
    {
        if(GlobalVariables.sortType.equalsIgnoreCase("name"))
        {
            sort.setText("Name");

        }
        else if(GlobalVariables.sortType.equalsIgnoreCase("type"))
        {
            sort.setText("Type");
        }
        else if(GlobalVariables.sortType.equalsIgnoreCase("filesize"))
        {
            sort.setText("Size");
        }
        else if(GlobalVariables.sortType.equalsIgnoreCase("unix_date"))
        {
            sort.setText("Date");
        }

        if(GlobalVariables.isAscending)
        {
            sort_image.setImageResource(R.mipmap.ic_sortup);
        }
        else
        {
            sort_image.setImageResource(R.mipmap.ic_sort);
        }

    }

    private void toggleAddAndBackButton()
    {
        if (objectId.equals("0")){
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionCamera.setVisibility(View.GONE);
            actionUpload.setVisibility(View.GONE);
            actionVideo.setVisibility(View.GONE);
        }
        else
        {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionCamera.setVisibility(View.VISIBLE);
            actionUpload.setVisibility(View.VISIBLE);
            actionVideo.setVisibility(View.VISIBLE);
        }
    }


    private void OnClickListeners()
    {

        move_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GlobalVariables.selectedActionName.equalsIgnoreCase("move"))
                {
                    moveDocuments();
                }
                else if(GlobalVariables.selectedActionName.equalsIgnoreCase("copy"))
                {
                    copyDocuments();
                }
                else if(GlobalVariables.selectedActionName.equalsIgnoreCase("delete"))
                {
                    if (mode == 1)
                    {
                        delete_Folder_Move();
                    }


                }


            }
        });

        cancel_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalVariables.isMoveInitiated = false;
                GlobalVariables.selectedDocumentsList.clear();
                mAdapterList.clearAll();
                hideBottomView();
                reloadAdapterData();


            }
        });

        toggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.isTileView = !GlobalVariables.isTileView;
                reloadAdapterData();
            }
        });

        sortingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });




        actionUpload.setOnClickListener(v -> {
            floatingActionMenu.close(true);
            if(Build.VERSION.SDK_INT >= 24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            pickFile();

        });

        actionCamera.setOnClickListener(v -> {
            floatingActionMenu.close(true);
            //   requestCameraPermission();
            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileUri = getOutputMediaFileUri(1);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE_CODE);
        });

        actionVideo.setOnClickListener(v -> {
            floatingActionMenu.close(true);
            //  requestCameraPermission();
            /*Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            fileUri = Uri.fromFile(mediaFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);*/
            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_VIDEO);
            if (file != null) {
                imageStoragePath = file.getAbsolutePath();
            }

            Uri fileUri = CameraUtils.getOutputMediaFileUri(context, file);

            // set video quality
            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takeVideoIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
        });



        actionNewFolder.setOnClickListener(v -> {
            floatingActionMenu.close(true);

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view1 = inflater.inflate(R.layout.newfolder, null);
            builder.setView(view1);
            builder.setCancelable(false);

            Button cancel = (Button) view1.findViewById(R.id.cancel_b);
            Button allow = (Button) view1.findViewById(R.id.allow);
            final EditText namer = (EditText) view1.findViewById(R.id.edit_username1);
            allow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String folder_name = namer.getText().toString().trim();

                    if(folder_name == null || folder_name.isEmpty())
                    {
                        Toast.makeText(context, "Please enter category name", Toast.LENGTH_SHORT).show();
                        return;
                    }



                    if (NetworkUtils.isNetworkAvailable(context)) {

                        Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                        final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
                        transparentProgressDialog.show();

                        final UploadNewFolderRequest uploadNewFolderRequest = new UploadNewFolderRequest(objectId, folder_name);

                        String request = new Gson().toJson(uploadNewFolderRequest);

                        //Here the json data is add to a hash map with key data
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("data", request);

                        final UploadNewFolderService uploadNewFolderService = retrofitAPI.create(UploadNewFolderService.class);

                        Call call = uploadNewFolderService.getNewFolder(params, PreferenceUtils.getAccessToken(context));

                        call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                            @Override
                            public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                                ListPinDevicesResponse apiResponse = response.body();
                                if (apiResponse != null) {

                                    transparentProgressDialog.dismiss();

                                    if (apiResponse.status.getCode() instanceof Boolean) {
                                        if (apiResponse.status.getCode() == Boolean.FALSE) {

                                            resetPageNumber();
                                            getCategoryDocuments();


                                        }

                                    } else if (apiResponse.status.getCode() instanceof Integer) {

                                        String mMessage = apiResponse.status.getMessage().toString();

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View view1 = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                                        builder.setView(view1);
                                        builder.setCancelable(false);

                                        TextView txtMessage = (TextView) view1.findViewById(R.id.txt_message);

                                        txtMessage.setText(mMessage);

                                        Button sendPinButton = (Button) view1.findViewById(R.id.send_pin_button);
                                        Button cancelButton = (Button) view1.findViewById(R.id.cancel_button);

                                        cancelButton.setVisibility(View.GONE);

                                        sendPinButton.setText("OK");

                                        sendPinButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mAlertDialog.dismiss();
                                                context.startActivity(new Intent(context, LoginActivity.class));
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


                    mAlertDialog.dismiss();

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();

                }
            });

            mAlertDialog = builder.create();
            mAlertDialog.show();


        });


        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView != null) {
                    if (scrollView.getChildAt(0).getBottom() == (scrollView.getHeight() + scrollView.getScrollY())) {

                        String object= PreferenceUtils.getObjectId(context);

                        if(pageNumber + 1 <= totalPages) {
                        //    pageNumber=pageNumber+1;
                         //   getCategoryDocumentsNext(object, String.valueOf(pageNumber));
                            getCategoryDocuments();

                        }
                    }
                    else {
                        //scroll view is not at bottom
                    }
                }
            }
        });




    }

    public void delete_Folder_DeleteDocuments() {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            ArrayList<String> documentIds = new ArrayList<>();

            for(GetCategoryDocumentsResponse cate : GlobalVariables.selectedDocumentsList)
            {
                documentIds.add(cate.getObject_id());
            }

            final DeleteEndUserFolderRequest deleteEndUserFolderRequest = new DeleteEndUserFolderRequest(documentIds,"0");

            String request = new Gson().toJson(deleteEndUserFolderRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final DeleteEndUserFolderService deleteEndUserFolderService = retrofitAPI.create(DeleteEndUserFolderService.class);

            Call call = deleteEndUserFolderService.delete_eu_folder(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<ApiResponse<LoginResponse>> response, Retrofit retrofit) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();
                        GlobalVariables.isMoveInitiated = false;


                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                            hideBottomView();
                            resetPageNumber();
                            getCategoryDocuments();
                        }
                        else {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(NavigationMyFolderActivity.this);
                                LayoutInflater inflater = (LayoutInflater)NavigationMyFolderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(NavigationMyFolderActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(NavigationMyFolderActivity.this, LoginActivity.class));
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

    private void delete_Folder_Move()
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            ArrayList<String> documentIds = new ArrayList<>();

            for(GetCategoryDocumentsResponse cate : GlobalVariables.selectedDocumentsList)
            {
                documentIds.add(cate.getObject_id());
            }


            final DeleteEndUserFolderMoveRequest deleteEndUserFolderMoveRequest = new DeleteEndUserFolderMoveRequest(documentIds,"1",objectId);

            String request = new Gson().toJson(deleteEndUserFolderMoveRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final DeleteEndUserMoveService deleteEndUserMoveService = retrofitAPI.create(DeleteEndUserMoveService.class);

            Call call = deleteEndUserMoveService.delete_eu_move(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<ApiResponse<LoginResponse>> response, Retrofit retrofit) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();
                        GlobalVariables.isMoveInitiated = false;

                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                            hideBottomView();
                            resetPageNumber();
                            getCategoryDocuments();
                        }
                        else  {
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                    context.startActivity(new Intent(context, LoginActivity.class));
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

    private void copyDocuments()
    {
        if (NetworkUtils.isNetworkAvailable(context)){

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(NavigationMyFolderActivity.this);
            transparentProgressDialog.show();


            ArrayList<String> documentIds = new ArrayList<>();

            for(GetCategoryDocumentsResponse cate : GlobalVariables.selectedDocumentsList)
            {
                documentIds.add(cate.getObject_id());
            }


            final CopyDocumentRequest copyDocumentRequest = new CopyDocumentRequest(documentIds,objectId);

            String request = new Gson().toJson(copyDocumentRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final CopyDocumentService copyDocumentService = retrofitAPI.create(CopyDocumentService.class);
            Call call = copyDocumentService.getCopyDocuemnt(params, PreferenceUtils.getAccessToken(NavigationMyFolderActivity.this));

            call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<ApiResponse<LoginResponse>> response, Retrofit retrofit) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();
                        GlobalVariables.isMoveInitiated = false;



                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                String mMessage = apiResponse.status.getMessage().toString();
                                //    Toast.makeText(NavigationMyFolderActivity.this,mMessage,Toast.LENGTH_SHORT).show();
                                hideBottomView();
                                resetPageNumber();
                                getCategoryDocuments();


                            }

                            else {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(NavigationMyFolderActivity.this);
                                LayoutInflater inflater = (LayoutInflater)NavigationMyFolderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(NavigationMyFolderActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(NavigationMyFolderActivity.this, LoginActivity.class));
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

    private void moveDocuments()
    {
        if (NetworkUtils.isNetworkAvailable(context)){

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(NavigationMyFolderActivity.this);
            transparentProgressDialog.show();


            ArrayList<String> documentIds = new ArrayList<>();

            for(GetCategoryDocumentsResponse cate : GlobalVariables.selectedDocumentsList)
            {
                documentIds.add(cate.getObject_id());
            }

            final MoveDocumentRequest moveDocumentRequest = new MoveDocumentRequest(documentIds, objectId);

            String request = new Gson().toJson(moveDocumentRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final MoveDocumentService moveDocumentService = retrofitAPI.create(MoveDocumentService.class);
            Call call = moveDocumentService.getMoveDocuemnt(params,PreferenceUtils.getAccessToken(NavigationMyFolderActivity.this));

            call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();
                        GlobalVariables.isMoveInitiated = false;


                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                String mMessage = apiResponse.status.getMessage().toString();
                                hideBottomView();
                                resetPageNumber();
                                getCategoryDocuments();



                            } else {
                                String mMessage = apiResponse.status.getMessage().toString();
                                Toast.makeText(NavigationMyFolderActivity.this,mMessage,Toast.LENGTH_SHORT).show();
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(NavigationMyFolderActivity.this);
                                LayoutInflater inflater = (LayoutInflater)NavigationMyFolderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(NavigationMyFolderActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(NavigationMyFolderActivity.this, LoginActivity.class));
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

    private void reloadAdapterData()
    {
        if (GlobalVariables.isTileView && !GlobalVariables.isMoveInitiated)
        {
            toggle.setImageResource(R.mipmap.ic_grid);
            setGridAdapterToView(mGetCategoryDocumentsResponses);
            mAdapter.notifyDataSetChanged();
            isFromList = false;

        }
        else
        {
            toggle.setImageResource(R.mipmap.ic_list);
            setListAdapterToView(mGetCategoryDocumentsResponses);
            isFromList = true;
            mAdapterList.notifyDataSetChanged();

        }

        if(pageNumber == 1)
        {
            scrollView.fullScroll(ScrollView.FOCUS_UP);
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                ServerConfig.IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("", "Oops! Failed create " + ServerConfig.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void intiaizeViews()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_dms);
        scrollView = (NestedScrollView) findViewById(R.id.nest_scrollview);
        sort = (TextView) findViewById(R.id.name_sort);
        toggleView = (RelativeLayout) findViewById(R.id.toggle_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        sortingView = (LinearLayout) findViewById(R.id.sort);
        toggle = (ImageView) findViewById(R.id.toggle);
        sort_image = (ImageView) findViewById(R.id.sort_image);
        move_layout = (RelativeLayout) findViewById(R.id.move_layout);
        move_textview = (TextView) findViewById(R.id.move_textview);
        cancel_textview = (TextView) findViewById(R.id.cancel_textview);
        sorting_layout = (LinearLayout) findViewById(R.id.sorting_layout);
        bottomNavigationLayout = (BottomNavigationView) findViewById(R.id.navigation);
        floatingActionMenu = (FloatingActionMenu)findViewById(R.id.floating_action_menu);

        actionUpload = (FloatingActionButton) findViewById(R.id.menu_upload_item);
        actionCamera = (FloatingActionButton)findViewById(R.id.menu_camera_item);
        actionNewFolder = (FloatingActionButton) findViewById(R.id.menu_new_folder_item);
        actionVideo = (FloatingActionButton)findViewById(R.id.menu_camera_video_item);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.my_folder));
        toolbarTextAppernce();
    }


    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }



    public void pickFile() {
        DialogProperties properties = new DialogProperties();
        properties.extensions = new String[]{".pdf", ".doc", ".docx", ".xlsx", ".txt", ".jpg", ".png", ".bmp", ".gif", ".tiff", ".jpeg", ".xls" ,".mp4",".mp3",".wav",".mov",".avi",".m4a",".jpeg",".mkv",".ppt",".pptx"};
        properties.selection_mode = DialogConfigs.MULTI_MODE;
        FilePickerDialog dialog = new FilePickerDialog(context, properties);
        dialog.setTitle("Select a File");
        list_upload = PreferenceUtils.getupload(context,"key");
        if(list_upload==null){
            list_upload= new ArrayList<>();
        }
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (list_upload.size() + files.length <= 10) {
                    int fileCount = files.length;
                    for (int i = 0; i <= fileCount - 1; i++) {
                        list_upload.add(String.valueOf(new File(files[i])));
                        PreferenceUtils.setupload(context,list_upload,"key");
                    }

                }
                else
                {
                    Toast.makeText(context,"Please select less then 10 documents ",Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(context,UploadListActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
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

        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

         if (GlobalVariables.sortType.equalsIgnoreCase("type")) {
            sortNameImage.setVisibility(View.INVISIBLE);
            sortNewestImage.setVisibility(View.VISIBLE);
            sortSizeImage.setVisibility(View.INVISIBLE);
            sortDateImage.setVisibility(View.INVISIBLE);

            sortNameDoneImage.setVisibility(View.INVISIBLE);
            sortNewestDoneImage.setVisibility(View.VISIBLE);
            sortSizeDoneImage.setVisibility(View.INVISIBLE);
            sortDateDoneImage.setVisibility(View.INVISIBLE);

            if (GlobalVariables.isAscending)
            {
                sortNewestImage.setImageResource(R.mipmap.ic_sort_up);
            }
            else
            {
                sortNewestImage.setImageResource(R.mipmap.ic_sort_down);
            }
        }
        else if (GlobalVariables.sortType.equalsIgnoreCase("name")) {
            sortNameImage.setVisibility(View.VISIBLE);
            sortNewestImage.setVisibility(View.INVISIBLE);
            sortSizeImage.setVisibility(View.INVISIBLE);
            sortDateImage.setVisibility(View.INVISIBLE);

            sortNameDoneImage.setVisibility(View.VISIBLE);
            sortNewestDoneImage.setVisibility(View.INVISIBLE);
            sortSizeDoneImage.setVisibility(View.INVISIBLE);
            sortDateDoneImage.setVisibility(View.INVISIBLE);

             if (GlobalVariables.isAscending)
             {
                 sortNameImage.setImageResource(R.mipmap.ic_sort_up);
             }
             else
             {
                 sortNameImage.setImageResource(R.mipmap.ic_sort_down);
             }
        } else if (GlobalVariables.sortType.equalsIgnoreCase("filesize")) {
            sortNameImage.setVisibility(View.INVISIBLE);
            sortNewestImage.setVisibility(View.INVISIBLE);
            sortSizeImage.setVisibility(View.VISIBLE);
            sortDateImage.setVisibility(View.INVISIBLE);

            sortNameDoneImage.setVisibility(View.INVISIBLE);
            sortNewestDoneImage.setVisibility(View.INVISIBLE);
            sortSizeDoneImage.setVisibility(View.VISIBLE);
            sortDateDoneImage.setVisibility(View.INVISIBLE);

             if (GlobalVariables.isAscending)
             {
                 sortSizeImage.setImageResource(R.mipmap.ic_sort_up);
             }
             else
             {
                 sortSizeImage.setImageResource(R.mipmap.ic_sort_down);
             }
        } else if (GlobalVariables.sortType.equalsIgnoreCase("unix_date")) {
            sortNameImage.setVisibility(View.INVISIBLE);
            sortNewestImage.setVisibility(View.INVISIBLE);
            sortSizeImage.setVisibility(View.INVISIBLE);
            sortDateImage.setVisibility(View.VISIBLE);

            sortNameDoneImage.setVisibility(View.INVISIBLE);
            sortNewestDoneImage.setVisibility(View.INVISIBLE);
            sortSizeDoneImage.setVisibility(View.INVISIBLE);
            sortDateDoneImage.setVisibility(View.VISIBLE);

             if (GlobalVariables.isAscending)
             {
                 sortDateImage.setImageResource(R.mipmap.ic_sort_up);
             }
             else
             {
                 sortDateImage.setImageResource(R.mipmap.ic_sort_down);
             }
        }
/*

        if(GlobalVariables.isAscending == false && GlobalVariables.sortType.equalsIgnoreCase("name"))
        {
            sortNameImage.setImageResource(R.mipmap.ic_sort_down);
        }
        else if(GlobalVariables.isAscending == false && GlobalVariables.sortType.equalsIgnoreCase("type"))
        {
            sortNewestImage.setImageResource(R.mipmap.ic_sort_down);
        }
        else if(GlobalVariables.isAscending == false && GlobalVariables.sortType.equalsIgnoreCase("filesize"))
        {
            sortSizeImage.setImageResource(R.mipmap.ic_sort_down);
        }
        else if(GlobalVariables.isAscending == false && GlobalVariables.sortType.equalsIgnoreCase("unix_date"))
        {
            sortDateImage.setImageResource(R.mipmap.ic_sort_down);
        }
*/



        mSortByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByName = true;
                sortByNewest = false;
                sortBySize = false;
                sortByDate = false;

                setSortType("name");




                mBottomSheetDialog.dismiss();
                sortNameImage.setVisibility(View.VISIBLE);
                sortNewestImage.setVisibility(View.INVISIBLE);
                sortSizeImage.setVisibility(View.INVISIBLE);
                sortDateImage.setVisibility(View.INVISIBLE);

                sortNameDoneImage.setVisibility(View.VISIBLE);
                sortNewestDoneImage.setVisibility(View.INVISIBLE);
                sortSizeDoneImage.setVisibility(View.INVISIBLE);
                sortDateDoneImage.setVisibility(View.INVISIBLE);
                //    pageNumber=0;
                totalPages=1;
                mGetCategoryDocumentsResponses.clear();
                listGetCategoryDocuments.clear();

              //  getCategoryDocumentsSortByName(objectId,"1");
                getCategoryDocuments();

                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        mSortByNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByNewest = true;
                sortByName = false;
                sortBySize = false;
                sortByDate = false;

                setSortType("type");

                mBottomSheetDialog.dismiss();
                sortNameImage.setVisibility(View.INVISIBLE);
                sortNewestImage.setVisibility(View.VISIBLE);
                sortSizeImage.setVisibility(View.INVISIBLE);
                sortDateImage.setVisibility(View.INVISIBLE);
                // indicatorParentView.setVisibility(View.INVISIBLE);
                sortNameDoneImage.setVisibility(View.INVISIBLE);
                sortNewestDoneImage.setVisibility(View.VISIBLE);
                sortSizeDoneImage.setVisibility(View.INVISIBLE);
                sortDateDoneImage.setVisibility(View.INVISIBLE);
                //   pageNumber=0;
                totalPages=1;
                mGetCategoryDocumentsResponses.clear();
                listGetCategoryDocuments.clear();
               // getCategoryDocumentsSortByNewest("1");
                getCategoryDocuments();
                scrollView.fullScroll(ScrollView.FOCUS_UP);

            }
        });

        mSortBySize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortBySize = true;
                sortByNewest = false;
                sortByName = false;
                sortByDate = false;

                setSortType("filesize");


                mBottomSheetDialog.dismiss();
                sortNameImage.setVisibility(View.INVISIBLE);
                sortNewestImage.setVisibility(View.INVISIBLE);
                sortSizeImage.setVisibility(View.VISIBLE);
                sortDateImage.setVisibility(View.INVISIBLE);
                // indicatorParentView.setVisibility(View.INVISIBLE);
                sortNameDoneImage.setVisibility(View.INVISIBLE);
                sortNewestDoneImage.setVisibility(View.INVISIBLE);
                sortSizeDoneImage.setVisibility(View.VISIBLE);
                sortDateDoneImage.setVisibility(View.INVISIBLE);
                //    pageNumber=0;
                totalPages=1;
                mGetCategoryDocumentsResponses.clear();
                listGetCategoryDocuments.clear();
              //  getCategoryDocumentsSortBySize("1");
                getCategoryDocuments();
                scrollView.fullScroll(ScrollView.FOCUS_UP);

            }
        });

        mSortByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByDate = true;
                sortBySize = false;
                sortByNewest = false;
                sortByName = false;

                setSortType("unix_date");


                mBottomSheetDialog.dismiss();
                sortNameImage.setVisibility(View.INVISIBLE);
                sortNewestImage.setVisibility(View.INVISIBLE);
                sortSizeImage.setVisibility(View.INVISIBLE);
                sortDateImage.setVisibility(View.VISIBLE);
                // indicatorParentView.setVisibility(View.INVISIBLE);
                sortNameDoneImage.setVisibility(View.INVISIBLE);
                sortNewestDoneImage.setVisibility(View.INVISIBLE);
                sortSizeDoneImage.setVisibility(View.INVISIBLE);
                sortDateDoneImage.setVisibility(View.VISIBLE);
                //      pageNumber=0;
                totalPages=1;
                mGetCategoryDocumentsResponses.clear();
                listGetCategoryDocuments.clear();
              //  getCategoryDocumentsSortByDate("1");
                getCategoryDocuments();
                scrollView.fullScroll(ScrollView.FOCUS_UP);


            }
        });
    }

    private void setSortType(String sorttype)
    {
        if(GlobalVariables.sortType.equalsIgnoreCase(sorttype))
        {
            GlobalVariables.isAscending = !GlobalVariables.isAscending;
       }
        else
        {
            GlobalVariables.isAscending = true;
        }

        GlobalVariables.sortType = sorttype;
        if(GlobalVariables.isAscending)
        {
            sort_image.setImageResource(R.mipmap.ic_sortup);
        }
        else {
            sort_image.setImageResource(R.mipmap.ic_sort);
        }

        resetPageNumber();
    }

    public void resetPageNumber()
    {
        pageNumber = 0;
        totalPages = 1;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_GALLERY_CODE && resultCode == RESULT_OK) {
            fileUri = data.getData();


            list_upload = new ArrayList<>();

            if (null != data) { // checking empty selection
                if (null != data.getClipData()) { // checking multiple selection or not
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        ArrayList<Uri> uris = new ArrayList<>();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            uri = item.getUri();
                            uris.add(uri);

                            String filePath = getRealPathFromURIPath(uri, context);

                            //  File file = new File(filePath);



                            list_upload.add(String.valueOf(filePath));
                            PreferenceUtils.setupload(context,list_upload,"key");
                            //   PreferenceUtils.setupload(getContext(),list_upload,"key");


                        }

                    } else {
                        uri = data.getData();

                        String filePath = getRealPathFromURIPath(uri, context);
                        //File file = new File(filePath);

                      /*  ArrayList<String> filePathList = new ArrayList<String>();
                        filePathList.add(filePath);*/
                        list_upload = PreferenceUtils.getupload(context,"key");
                        list_upload.add(String.valueOf(filePath));
                        PreferenceUtils.setupload(context,list_upload,"key");

                        //   uploadGalleryImage(file, filePathList);
                    }
                }
                if((list_upload == null )||(list_upload.size()==0))
                {
                    String filepath = getRealPathFromURIPath(fileUri, context);
                    list_upload.add(filepath);
                    PreferenceUtils.setupload(context,list_upload,"key");
                }
            }
            Intent intent = new Intent (context,UploadListActivity.class);
            //  PreferenceUtils.setupload(MyFoldersDMSActivity.this,list_upload,"key");
            // list_upload.clear();

            startActivity(intent);

        }
        else if (requestCode == REQUEST_CAPTURE_IMAGE_CODE && resultCode == RESULT_OK) {
            list_upload = new ArrayList<>();
            if (resultCode == RESULT_OK) {
                // uploadImage(fileUri.getPath());



                String filePath = fileUri.getPath();
/*
                File file = new File(filePath);
*/

                ArrayList<String> filePathList = new ArrayList<String>();
                filePathList.add(filePath);
                //  list_upload = PreferenceUtils.getupload(MyFoldersDMSActivity.this,"key");
                list_upload.add(String.valueOf(filePath));
                PreferenceUtils.setupload(context,list_upload,"key");
                // list_upload.clear();

                Intent intent = new Intent (context,UploadListActivity.class);
                startActivity(intent);


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(context, "User cancelled image capture", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "Error capturing image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            // uriVideo = data.getData();
            // uploadVideo(imageStoragePath);



            String filePath =imageStoragePath;


            ArrayList<String> filePathList = new ArrayList<String>();
            filePathList.add(filePath);
            list_upload = PreferenceUtils.getupload(context,"key");
            list_upload.add(String.valueOf(filePath));
            PreferenceUtils.setupload(context,list_upload,"key");
            list_upload.clear();
            Intent intent = new Intent (context,UploadListActivity.class);
            startActivity(intent);

        }
    }


    private String getRealPathFromURIPath(Uri uri, Context context) {

        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] projection = {MediaStore.Images.Media.DATA};
            final Cursor cursor = context.getContentResolver().query(uri,
                    projection, null, null, null);
            //  final Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        list_upload = PreferenceUtils.getArrayList(NavigationMyFolderActivity.this,"key");
        if ((list_upload!=null)&&(list_upload.size()>0)){
            final AlertDialog.Builder builder = new AlertDialog.Builder(NavigationMyFolderActivity.this);
            LayoutInflater inflater = (LayoutInflater) NavigationMyFolderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.custom_dialog, null);
            builder.setView(view);
            builder.setCancelable(false);

            final Button BtnAllow = (Button) view.findViewById(R.id.allow_button);
            BtnAllow.setText("RETRY");
            final Button BtnCancel = (Button) view.findViewById(R.id.cancel_button);
            mCustomAlertDialog = builder.create();
            mCustomAlertDialog.show();

            BtnAllow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCustomAlertDialog.dismiss();
                    Intent intent = new Intent (NavigationMyFolderActivity.this,UploadListActivity.class);
                    startActivity(intent);
                }
            });

            BtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCustomAlertDialog.dismiss();
                }
            });


        }
    }


    private void getCategoryDocumentsSortByName(String objectId, String page) {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

           /* if (PreferenceUtils.getObjectId(context).equalsIgnoreCase("")) {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest("0", "list", "category", "1", "0");
            } else {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(PreferenceUtils.getObjectId(context), "list", "category", "1", "0");
            }*/
            mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(objectId, "list", "category", "1", "0");

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;
            String perPage = "20";

            call = mGetCategoryDocumentsService.getCategoryDocuments(params, PreferenceUtils.getAccessToken(context),page,perPage, "name" );

           /* if(sortByNameAsc == true) {

                sortByNameAsc = false;
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByName(params, PreferenceUtils.getAccessToken(context),page,perPage, "-name" );
                sortByNameAsc = true;
            }*/

            call.enqueue(new Callback<ListPinDevicesResponse<GetCategoryDocumentsResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetCategoryDocumentsResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                mGetCategoryDocumentsResponses = response.body().getData();

                                if (isFromList == true) {
                                    setListAdapterToView(mGetCategoryDocumentsResponses);
                                } else {
                                    setGridAdapterToView(mGetCategoryDocumentsResponses);
                                }

                                totalPages = Integer.parseInt(response.headers().get("X-Pagination-Page-Count"));
                                pageNumber = Integer.parseInt(response.headers().get("X-Pagination-Current-Page"));
                            }
                         //   sort.setText("Name");




                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {

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


    private void getCategoryDocumentsSortByNewest(String page) {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            if (PreferenceUtils.getObjectId(context).equalsIgnoreCase("")) {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest("0", "list", "category", "1", "0");
            } else {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(PreferenceUtils.getObjectId(context), "list", "category", "1", "0");
            }

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(sortByTypeAsc == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByType(params, PreferenceUtils.getAccessToken(context),page);
                sortByTypeAsc = false;
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByTypeDesc(params, PreferenceUtils.getAccessToken(context),page);
                sortByTypeAsc = true;
            }

            call.enqueue(new Callback<ListPinDevicesResponse<GetCategoryDocumentsResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetCategoryDocumentsResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                mGetCategoryDocumentsResponses = response.body().getData();
                                if (isFromList == true) {
                                    setListAdapterToView(mGetCategoryDocumentsResponses);
                                } else {
                                    setGridAdapterToView(mGetCategoryDocumentsResponses);
                                }
                                totalPages = Integer.parseInt(response.headers().get("X-Pagination-Page-Count"));
                                pageNumber = Integer.parseInt(response.headers().get("X-Pagination-Current-Page"));
                            }

                            sort.setText("Type");

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
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


    private void getCategoryDocumentsSortBySize(String page) {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            if (PreferenceUtils.getObjectId(context).equalsIgnoreCase("")) {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest("0", "list", "category", "1", "0");
            } else {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(PreferenceUtils.getObjectId(context), "list", "category", "1", "0");
            }

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(sortBySizeAsc == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortBySize(params, PreferenceUtils.getAccessToken(context),page);
                sortBySizeAsc = false;
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortBySizeDesc(params, PreferenceUtils.getAccessToken(context),page);
                sortBySizeAsc = true;
            }

            call.enqueue(new Callback<ListPinDevicesResponse<GetCategoryDocumentsResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetCategoryDocumentsResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                mGetCategoryDocumentsResponses = response.body().getData();
                                if (isFromList == true) {
                                    setListAdapterToView(mGetCategoryDocumentsResponses);
                                } else {
                                    setGridAdapterToView(mGetCategoryDocumentsResponses);
                                }

                                totalPages = Integer.parseInt(response.headers().get("X-Pagination-Page-Count"));
                                pageNumber = Integer.parseInt(response.headers().get("X-Pagination-Current-Page"));
                            }

                            sort.setText("Size");

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
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


    private void getCategoryDocumentsSortByDate(String page) {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            if (PreferenceUtils.getObjectId(context).equalsIgnoreCase("")) {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest("0", "list", "category", "1", "0");
            } else {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(PreferenceUtils.getObjectId(context), "list", "category", "1", "0");
            }

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(sortByDateAsc == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByDate(params, PreferenceUtils.getAccessToken(context),page);
                sortByDateAsc = false;
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByDateDesc(params, PreferenceUtils.getAccessToken(context),page);
                sortByDateAsc = true;
            }

            call.enqueue(new Callback<ListPinDevicesResponse<GetCategoryDocumentsResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetCategoryDocumentsResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();

                                mGetCategoryDocumentsResponses = response.body().getData();
                                if (isFromList == true) {
                                    setListAdapterToView(mGetCategoryDocumentsResponses);
                                } else {
                                    setGridAdapterToView(mGetCategoryDocumentsResponses);
                                }

                                totalPages = Integer.parseInt(response.headers().get("X-Pagination-Page-Count"));
                                pageNumber= Integer.parseInt(response.headers().get("X-Pagination-Current-Page"));
                            }

                            sort.setText("Date");

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
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


    public void setListAdapterToView(final List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        mAdapterList = new DmsAdapterList(getCategoryDocumentsResponses, NavigationMyFolderActivity.this);
        mRecyclerView.setAdapter(mAdapterList);


    }

    private void setGridAdapterToView(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        toggle.setImageResource(R.mipmap.ic_grid);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context,3));
        mAdapter = new DmsAdapter(getCategoryDocumentsResponses, NavigationMyFolderActivity.this);
        mRecyclerView.setAdapter(mAdapter);

    }


      @Override
      public boolean onCreateOptionsMenu(Menu menu) {

          getMenuInflater().inflate(R.menu.menu_multi_select, menu);
          menuItemSearch = menu.findItem(R.id.action_search);
          menuItemDelete = menu.findItem(R.id.action_delete);
          menuItemShare = menu.findItem(R.id.action_share);
          menuItemMore = menu.findItem(R.id.action_more);
          menuItemMove = menu.findItem(R.id.action_move);

          String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
          int selectedColor = Color.parseColor(itemSelectedColor);

          menuIconColor(menuItemSearch,selectedColor);

          return true;
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
                  return true;

              case R.id.action_delete:

                          final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                          LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                          View view = inflater.inflate(R.layout.delete_alert, null);
                          builder.setView(view);
                          builder.setCancelable(false);

                          Button cancel = (Button) view.findViewById(R.id.canceldel);
                          Button delete = (Button) view.findViewById(R.id.delete);
                          Button move = (Button) view.findViewById(R.id.movefolder);

                          delete.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  mAlertDialog.dismiss();
                                  mode = 0;
                                  delete_Folder_DeleteDocuments();
                              }
                          });
                          move.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                 mAlertDialog.dismiss();
                                 mode = 1;
                                 initiateMoveAction("delete");

                              }
                          });

                          cancel.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  mAlertDialog.dismiss();

                              }
                          });

                          mAlertDialog = builder.create();
                          mAlertDialog.show();

                  break;
              case R.id.action_move:

                  CommonFunctions.setSelectedItems(mSelectedDocumentList);
                  initiateMoveAction("move");

                      break;
          }
          return super.onOptionsItemSelected(item);
      }

    @Override
    public void onBackPressed() {
        if(objectId.equals("0"))
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



    public void openBottomSheetForMultiSelect(){

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




        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        CommonFunctions.setSelectedItems(mSelectedDocumentList);

        if(mSelectedDocumentList != null && mSelectedDocumentList.size() == 1)
        {
            if(mSelectedDocumentList.get(0).getType().equalsIgnoreCase("category"))
            {
                categoryText.setText(mSelectedDocumentList.get(0).getName());

            }
            else {


                ColorCodeModel colorCodeModel = CommonFunctions.getColorCodesforFileType(mSelectedDocumentList.get(0).getFiletype());
                if(colorCodeModel != null)
                {

                    thumbnailIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getPrimaryColor()));
                    thumbnailCornerIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getSecondaryColor()));

                    thumbnailText.setText(colorCodeModel.getFileType());
                    docText.setText(mSelectedDocumentList.get(0).getName());
                }
            }
        }

        List<String> temporaryShareList = new ArrayList<>();
        if(mSelectedDocumentList != null && mSelectedDocumentList.size() > 0)
        {
            for(GetCategoryDocumentsResponse getcategoryResponseModel : mSelectedDocumentList)
            {
                if(getcategoryResponseModel.getType() != null && getcategoryResponseModel.getType().equalsIgnoreCase("document"))
                {
                    if(getcategoryResponseModel.getIs_shared().equalsIgnoreCase("1"))
                    {
                        temporaryShareList.add("Is_Shared_Available");
                    }
                }

            }
        }

        if(temporaryShareList != null && temporaryShareList.size() == mSelectedDocumentList.size())
        {
            switchButton_share.setChecked(true);
        }
        else
        {
            switchButton_share.setChecked(false);
        }

        List<String> temporarydownloadList = new ArrayList<>();
        if(mSelectedDocumentList != null && mSelectedDocumentList.size() > 0)
        {
            for(GetCategoryDocumentsResponse getcategoryResponseModel : mSelectedDocumentList)
            {
                if(getcategoryResponseModel.getType() != null && getcategoryResponseModel.getType().equalsIgnoreCase("document")) {
                    OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                    if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(getcategoryResponseModel.getDocument_version_id())) {
                        temporarydownloadList.add("Is_Download_Available");
                    }
                }

            }
        }


        if(temporarydownloadList != null && temporarydownloadList.size() == mSelectedDocumentList.size())
        {
            download.setChecked(true);
        }
        else
        {
            download.setChecked(false);
        }


        switchButton_share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(buttonView.isPressed() == true) {

                    if (!isChecked) {
                        switchButton_share.setChecked(true);
                        ArrayList<String> documentIdslist = new ArrayList<>();

                        for(GetCategoryDocumentsResponse categoryDocumentsResponse : mSelectedDocumentList)
                        {
                            documentIdslist.add(categoryDocumentsResponse.getObject_id());
                        }

                        if(documentIdslist !=null && documentIdslist.size() > 0)
                        {
                            showWarningMessageAlertForSharingContent(documentIdslist, mSelectedDocumentList);
                        }


                        mBottomSheetDialog.dismiss();

                    } else {
                        switchButton_share.setChecked(false);

                       /* PreferenceUtils.setCategoryId(context, categoryDocumentsResponse.getCategory_id());
                        PreferenceUtils.setDocument_Id(context, categoryDocumentsResponse.getObject_id());*/

                        Intent mIntent = new Intent(context, MyFolderSharedDocuments.class);
                        mIntent.putExtra(Constants.OBJ, (Serializable) mSelectedDocumentList);
                        startActivity(mIntent);

                        mBottomSheetDialog.dismiss();
                    }


                }

            }
        });

        copyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateMoveAction("copy");
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v)
            {
                mBottomSheetDialog.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.delete_document, null);
                builder.setView(view);
                builder.setCancelable(false);
                Button delete = (Button)view.findViewById(R.id.delete);
                Button delete_historic=(Button)view.findViewById(R.id.movefolder);
                Button delete_all =(Button)view.findViewById(R.id.deleteall);
                Button cancel = (Button)view.findViewById(R.id.canceldel);

                String versionCount = "1";

                if(mSelectedDocumentList != null && mSelectedDocumentList.size() > 0)
                {
                    for(GetCategoryDocumentsResponse categoryDocumentsResponse : mSelectedDocumentList)
                    {
                        if (!categoryDocumentsResponse.getVersion_count().equals(versionCount))
                        {
                            versionCount = "0";
                            break;
                        }
                    }
                }

                if(versionCount.equals("0"))
                {
                    delete_historic.setEnabled(false);
                    delete_historic.setTextColor(R.color.grey);
                    delete_all.setEnabled(false);
                    delete_all.setTextColor(R.color.grey);
                }


                mBackDialog = builder.create();
                mBackDialog.show();
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();
                 //    deleteDocumentsService(document, document_version_id, "0");

                    }
                });


                delete_historic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();

                    //    deleteDocumentsService(document, document_version_id, "1");

                    }
                });

                delete_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                     //   deleteDocumentsService(document, document_version_id, "2");

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();

                    }
                });

            }




        });



        doc_info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                if(mSelectedDocumentList != null && mSelectedDocumentList.size() == 1)
                {
                    PreferenceUtils.setDocumentVersionId(context,mSelectedDocumentList.get(0).getDocument_version_id());
                    PreferenceUtils.setDocument_Id(context, mSelectedDocumentList.get(0).getObject_id());
                }

                Intent intent = new Intent (context,Tab_Activity.class);
                context.startActivity(intent);
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


        moveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initiateMoveAction("move");

            }
        });

        renameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.rename_alert, null);
                builder.setView(view);
                builder.setCancelable(false);

                Button cancel = (Button) view.findViewById(R.id.cancel_b);
                Button allow = (Button) view.findViewById(R.id.allow);
                final EditText namer = (EditText) view.findViewById(R.id.edit_username1);
                allow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String folder = namer.getText().toString().trim();

                        if(folder != null && !folder.isEmpty())
                        {
                            if(mSelectedDocumentList != null && mSelectedDocumentList.size() == 1) {
                                mAlertDialog.dismiss();
                                mBottomSheetDialog.dismiss();
                                if(mSelectedDocumentList.get(0).getType().equalsIgnoreCase("category"))
                                {
                                    renameCategory(mSelectedDocumentList.get(0).getObject_id(), folder, mSelectedDocumentList.get(0).getParent_id());
                                }
                                else
                                {
                                    renameDocument(mSelectedDocumentList.get(0).getDocument_version_id(),folder,"","");
                                }

                            }
                        }
                        else
                        {
                            Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                        mBottomSheetDialog.dismiss();
                    }
                });

                mAlertDialog = builder.create();
                mAlertDialog.show();




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
                    isTouched = false;
                    if (isChecked) {

                        mBottomSheetDialog.dismiss();
                        List<GetCategoryDocumentsResponse> downloadedList = new ArrayList<>();
                        if(mSelectedDocumentList != null && mSelectedDocumentList.size() > 0)
                        {

                            for(GetCategoryDocumentsResponse getCategoryDocumentsResponse : mSelectedDocumentList)
                            {
                                if(getCategoryDocumentsResponse.getType().equalsIgnoreCase("document"))
                                {
                                    downloadedList.add(getCategoryDocumentsResponse);
                                }
                            }
                        }

                        if(downloadedList != null && downloadedList.size() > 0)
                        {
                            //  Toast.makeText(context,String.valueOf(downloadedList.size()), Toast.LENGTH_LONG).show();
                            convertingDownloadUrl(downloadedList);
                        }

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




        if (categoryDocumentsResponseFolderList.size() == 1) {
            if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                String folder_color = mWhiteLabelResponses.get(0).getFolder_Color();
                int selectedColor = Color.parseColor(folder_color);

                String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                int itemselectedColor = Color.parseColor(itemSelectedColor);

                moveImage.setColorFilter(itemselectedColor);
                renameImage.setColorFilter(itemselectedColor);

                if (folder_color != null) {
                    categoryImage.setColorFilter(selectedColor);
                } else {
                    categoryImage.setImageResource(R.mipmap.ic_folder);
                }


            }
            moveLayout.setVisibility(View.VISIBLE);
            renameLayout.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            clearSelectionLayout.setVisibility(View.GONE);
            availableOfflineLayout.setVisibility(View.GONE);
            shareLayout.setVisibility(View.GONE);
            copyLayout.setVisibility(View.GONE);
            folder_layout.setVisibility(View.VISIBLE);
            doc_info_layout.setVisibility(View.GONE);
            line_layout.setVisibility(View.VISIBLE);
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
            moveLayout.setVisibility(View.VISIBLE);
            renameLayout.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            clearSelectionLayout.setVisibility(View.GONE);
            availableOfflineLayout.setVisibility(View.VISIBLE);
            shareLayout.setVisibility(View.VISIBLE);
            copyLayout.setVisibility(View.VISIBLE);
            doc_info_layout.setVisibility(View.VISIBLE);
            folder_layout.setVisibility(View.GONE);
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
            moveLayout.setVisibility(View.VISIBLE);
            renameLayout.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
            clearSelectionLayout.setVisibility(View.VISIBLE);
            availableOfflineLayout.setVisibility(View.VISIBLE);
            shareLayout.setVisibility(View.VISIBLE);
            copyLayout.setVisibility(View.VISIBLE);
            doc_info_layout.setVisibility(View.GONE);
            folder_layout.setVisibility(View.GONE);
            line_layout.setVisibility(View.GONE);
        }

       /* shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), MyFolderSharedDocuments.class);
                mIntent.putExtra(Constants.OBJ, (Serializable) mSelectedDocumentList);
                startActivity(mIntent);
            }
        });*/
    }

    public void initiateMoveAction(String actionName) {
        GlobalVariables.isMoveInitiated = true;
        GlobalVariables.selectedActionName =  actionName;
        Intent intent = new Intent(context, NavigationMyFolderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("ObjectId", "0");
        startActivity(intent);

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

        boolean isDocument = false;
        boolean isFolder = false;

        List<GetCategoryDocumentsResponse> categoryDocumentsResponseFolderList = new ArrayList<>();
        List<GetCategoryDocumentsResponse> categoryDocumentsResponseDocumentList = new ArrayList<>();

        if (mSelectedDocumentList != null && mSelectedDocumentList.size() > 0) {

            for (GetCategoryDocumentsResponse viewListObj : mSelectedDocumentList) {

                if (viewListObj.getType().equalsIgnoreCase("category")) {
                    isFolder = true;
                    categoryDocumentsResponseFolderList.add(viewListObj);
                } else {
                    isDocument = true;
                    categoryDocumentsResponseDocumentList.add(viewListObj);
                }
            }

            if (categoryDocumentsResponseFolderList.size() == 1) {
                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                    int selectedColor = Color.parseColor(itemSelectedColor);

                    // menuItemMove.setIcon(selectedColor);
                    // menuItemMore.setIcon(selectedColor);

                    menuIconColor(menuItemMove,selectedColor);
                    menuIconColor(menuItemMore,selectedColor);
                }
                menuItemDelete.setVisible(true);
                menuItemMove.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemShare.setVisible(false);
                menuItemSearch.setVisible(false);

            } else if (categoryDocumentsResponseFolderList.size() >= 1) {
                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                    int selectedColor = Color.parseColor(itemSelectedColor);

                    menuIconColor(menuItemMove,selectedColor);
                }
                menuItemDelete.setVisible(false);
                menuItemMove.setVisible(true);
                menuItemMore.setVisible(false);
                menuItemShare.setVisible(false);
                menuItemSearch.setVisible(false);
            }

            if (categoryDocumentsResponseDocumentList.size() == 1) {
                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                    int selectedColor = Color.parseColor(itemSelectedColor);

                    menuIconColor(menuItemShare,selectedColor);
                    menuIconColor(menuItemMore,selectedColor);
                }
                menuItemDelete.setVisible(true);
                menuItemShare.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemMove.setVisible(false);
                menuItemSearch.setVisible(false);

            } else if (categoryDocumentsResponseDocumentList.size() >= 1) {
                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                    int selectedColor = Color.parseColor(itemSelectedColor);

                    menuIconColor(menuItemShare,selectedColor);
                    menuIconColor(menuItemMore,selectedColor);
                }
                menuItemDelete.setVisible(true);
                menuItemShare.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemMove.setVisible(false);
                menuItemSearch.setVisible(false);
            }

            if (isFolder && isDocument) {
                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                    int selectedColor = Color.parseColor(itemSelectedColor);

                    menuIconColor(menuItemMove,selectedColor);
                }
                menuItemDelete.setVisible(false);
                menuItemMove.setVisible(true);
                menuItemMore.setVisible(false);
                menuItemShare.setVisible(false);
                menuItemSearch.setVisible(false);
            }

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

    public void getCategoryDocumentsNext(String obj, String page)
    {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(obj, "list", "category", "1", "0");

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = mGetCategoryDocumentsService.getCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(context),page);

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

                                if(isFromList == true)
                                {
                                    setListAdapterToView(mGetCategoryDocumentsResponses);
                                }
                                else
                                {
                                    setGridAdapterToView(mGetCategoryDocumentsResponses);
                                }


                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
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


    private void renameCategory(String object_id, String rename, String parent_id)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final EndUserRenameRequest endUserRenameRequest = new EndUserRenameRequest(object_id,rename,parent_id);

            String request = new Gson().toJson(endUserRenameRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final EndUserRenameService endUserRenameService = retrofitAPI.create(EndUserRenameService.class);

            Call call = endUserRenameService.getRename(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                resetPageNumber();
                                getCategoryDocuments();

                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                    startActivity(new Intent(context, LoginActivity.class));
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

    private void renameDocument(String document_version_id, String rename, String doc_created, String auth)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final EditDocumentPropertiesRequest editDocumentPropertiesRequest = new EditDocumentPropertiesRequest(document_version_id,rename,doc_created,auth);

            String request = new Gson().toJson(editDocumentPropertiesRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final EditDocumentPropertiesService editDocumentPropertiesService = retrofitAPI.create(EditDocumentPropertiesService.class);

            Call call = editDocumentPropertiesService.getRenameDocument(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                resetPageNumber();
                                getCategoryDocuments();
                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                    startActivity(new Intent(context, LoginActivity.class));
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



    public void convertingDownloadUrl(List<GetCategoryDocumentsResponse> downloadedList)
    {
        downloadingUrlDataList = downloadedList;
        if(downloadingUrlDataList.size()> index) {

            getDownloadurlFromService(downloadingUrlDataList.get(index).getObject_id());

        }


    }

    public void getDownloadurlFromService(String document_version_id)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DownloadDocumentService downloadDocumentService = retrofitAPI.create(DownloadDocumentService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            //DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(PreferenceUtils.getDocumentVersionId(this));
            List<String> strlist = new ArrayList<>();
            strlist.add(document_version_id);
            DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(strlist);
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

                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                            transparentProgressDialog.dismiss();
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
                                getDownloadurlFromService(downloadingUrlDataList.get(index).getObject_id());

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
                        else {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();
                           /*// mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener()
                                {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(mActivity, LoginActivity.class));
                                    mActivity.finish();
                                }
                            }, false);
                        */
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                }
            });
        }

    }


    private void getDownloadManagerForDownloading(List<GetCategoryDocumentsResponse> downloadingUrlDataList)
    {


        index = 0;
        for (final GetCategoryDocumentsResponse digitalAsset : downloadingUrlDataList) {
            if (!TextUtils.isEmpty(digitalAsset.getDownloadUrl())) {
                FileDownloadManager fileDownloadManager = new FileDownloadManager(NavigationMyFolderActivity.this);
                fileDownloadManager.setFileTitle(digitalAsset.getName());
                fileDownloadManager.setDownloadUrl(digitalAsset.getDownloadUrl());
                fileDownloadManager.setDigitalAssets(digitalAsset);
                fileDownloadManager.setmFileDownloadListener(new FileDownloadManager.FileDownloadListener() {
                    @Override
                    public void fileDownloadSuccess(String path) {

                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                        if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(digitalAsset.getDocument_version_id())) {
                            offLine_files_repository.deleteAlreadydownloadedFile(digitalAsset.getDocument_version_id());
                            String filepath = offLine_files_repository.getFilePathFromLocalTable(digitalAsset.getDocument_version_id());
                            if(filepath != null && !filepath.isEmpty())
                            {
                                CommonFunctions.deleteFileFromInternalStorage(filepath);
                            }
                            insertIntoOffLineFilesTable(digitalAsset, path);
                        }
                        else
                        {
                            insertIntoOffLineFilesTable(digitalAsset, path);
                        }

                        //    Toast.makeText(mActivity,path, Toast.LENGTH_LONG).show();


                    }

                    @Override
                    public void fileDownloadFailure() {

                    }
                });
                fileDownloadManager.downloadTheFile();
            }
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
        offlineFilesModel.setFileSize(digitalAsset.getVersion_number());
        offlineFilesModel.setSource("Private");

        offLine_files_repository.InsertOfflineFilesData(offlineFilesModel);
    }


    private void showWarningMessageAlertForSharingContent(ArrayList<String> stopSharingList, List<GetCategoryDocumentsResponse> mSelectedDocumentList)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Warning");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText("This action will stop sharing the selected document(s). Company with whom this has been shared will no longer be able to view this document");

        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("CANCEL");

        sendPinButton.setText("OK");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        sendPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

                getInternalStoppingSharingContentAPI(stopSharingList, mSelectedDocumentList);


            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }


    private void getInternalStoppingSharingContentAPI(ArrayList<String> sharingList, List<GetCategoryDocumentsResponse> selectedDocumentList)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();


            final StopSharingRequestModel stopSharingRequestModel = new StopSharingRequestModel(sharingList, selectedDocumentList.get(0).getCategory_id());

            String request = new Gson().toJson(stopSharingRequestModel);


            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetEndUserParentSHaredFoldersService mGetEndUserParentSHaredstopService = retrofitAPI.create(GetEndUserParentSHaredFoldersService.class);

            Call call = mGetEndUserParentSHaredstopService.getEndUserStopSharedDocuments(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<SharedDocumentResponseModel>() {
                @Override
                public void onResponse(Response<SharedDocumentResponseModel> response, Retrofit retrofit) {

                    if (response != null) {

                        transparentProgressDialog.dismiss();

                        if (response.body().getStatus().getCode() instanceof Boolean) {
                            if (response.body().getStatus().getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();



                            }

                        } else if (response.body().getStatus().getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = response.body().getStatus().getMessage().toString();

                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
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


    public void getDownloadurlFromServiceSingleDocument(GetCategoryDocumentsResponse documentsResponse)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DownloadDocumentService downloadDocumentService = retrofitAPI.create(DownloadDocumentService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            //DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(PreferenceUtils.getDocumentVersionId(this));
            List<String> strlist = new ArrayList<>();
            strlist.add(documentsResponse.getObject_id());
            DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(strlist);
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

                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                            transparentProgressDialog.dismiss();
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
                        else {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();
                           /*// mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener()
                                {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(mActivity, LoginActivity.class));
                                    mActivity.finish();
                                }
                            }, false);
                        */
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                }
            });
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        setSortTitle();
        reloadAdapterData();
        if(!GlobalVariables.isMoveInitiated)
        {
            hideBottomView();
        }

        if(GlobalVariables.refreshDMS)
        {
            refreshMyFolderDMS();
            GlobalVariables.refreshDMS = false;
        }



    }




    public void deleteDocumentsService(String document, ArrayList<String> document_version_id, String deleteMode)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();
            DeleteDocumentRequest deleteDocumentRequest= new DeleteDocumentRequest();
            deleteDocumentRequest.setDoc_id(document);
            deleteDocumentRequest.setDoc_version_ids(document_version_id);
            deleteDocumentRequest.setMode(deleteMode);
            DeleteDocumentRequest docs = deleteDocumentRequest;
            final DeleteDocumentRequest.DeleteDocRequest deleteDocRequest = new DeleteDocumentRequest.DeleteDocRequest(new DeleteDocumentRequest[]{docs});

            String request = new Gson().toJson(deleteDocRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final DeleteDocumentService deleteDocumentService = retrofitAPI.create(DeleteDocumentService.class);

            Call call = deleteDocumentService.delete_eu_document(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<DeleteDocumentResponseModel>() {
                @Override
                public void onResponse(Response<DeleteDocumentResponseModel> response, Retrofit retrofit) {
                    DeleteDocumentResponseModel apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.getStatus().getCode() instanceof Boolean) {
                            if (apiResponse.getStatus().getCode() == Boolean.FALSE) {
                                resetPageNumber();
                                getCategoryDocuments();
                            }

                        } else if (apiResponse.getStatus().getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();

                            String mMessage = apiResponse.getStatus().getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                    context.startActivity(new Intent(context, LoginActivity.class));
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


    public void refreshMyFolderDMS()
    {
        resetPageNumber();
        getCategoryDocuments();
    }

}
