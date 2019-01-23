package com.mwc.docportal.DMS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.mwc.docportal.API.Model.ApiResponse;
import com.mwc.docportal.API.Model.ColorCodeModel;
import com.mwc.docportal.API.Model.CopyDocumentRequest;
import com.mwc.docportal.API.Model.DeleteDocumentRequest;
import com.mwc.docportal.API.Model.DeleteDocumentResponseModel;
import com.mwc.docportal.API.Model.DeleteEndUserFolderMoveRequest;
import com.mwc.docportal.API.Model.DeleteEndUserFolderRequest;

import com.mwc.docportal.API.Model.DownloadDocumentRequest;
import com.mwc.docportal.API.Model.DownloadDocumentResponse;
import com.mwc.docportal.API.Model.EditDocumentPropertiesRequest;
import com.mwc.docportal.API.Model.EditDocumentResponse;
import com.mwc.docportal.API.Model.EndUserRenameRequest;
import com.mwc.docportal.API.Model.FileFormatResponse;
import com.mwc.docportal.API.Model.GetCategoryDocumentsRequest;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.GetEndUserAllowedSharedFoldersRequest;
import com.mwc.docportal.API.Model.GetEndUserCategoriesRequest;
import com.mwc.docportal.API.Model.GetEndUserCategoriesResponse;
import com.mwc.docportal.API.Model.GetEndUserSharedParentFoldersResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.MaxDocumentUploadSizeResponse;
import com.mwc.docportal.API.Model.MoveDocumentRequest;
import com.mwc.docportal.API.Model.OfflineFiles;

import com.mwc.docportal.API.Model.ShareEndUserDocumentsRequest;
import com.mwc.docportal.API.Model.SharedDocumentResponseModel;
import com.mwc.docportal.API.Model.StopSharingRequestModel;
import com.mwc.docportal.API.Model.UploadModel;
import com.mwc.docportal.API.Model.UploadNewFolderRequest;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.CopyDocumentService;
import com.mwc.docportal.API.Service.DeleteDocumentService;
import com.mwc.docportal.API.Service.DeleteEndUserFolderService;
import com.mwc.docportal.API.Service.DeleteEndUserMoveService;


import com.mwc.docportal.API.Service.DownloadDocumentService;
import com.mwc.docportal.API.Service.EditDocumentPropertiesService;
import com.mwc.docportal.API.Service.EndUserRenameService;
import com.mwc.docportal.API.Service.GetCategoryDocumentsService;
import com.mwc.docportal.API.Service.GetEndUserAllowedSharedFoldersService;
import com.mwc.docportal.API.Service.GetEndUserCategoriesService;
import com.mwc.docportal.API.Service.GetEndUserParentSHaredFoldersService;
import com.mwc.docportal.API.Service.GetFileFormatService;
import com.mwc.docportal.API.Service.GetMaxUploadService;
import com.mwc.docportal.API.Service.MoveDocumentService;
import com.mwc.docportal.API.Service.ShareEndUserDocumentsService;
import com.mwc.docportal.API.Service.UploadNewFolderService;
import com.mwc.docportal.Adapters.DmsAdapter;
import com.mwc.docportal.Adapters.DmsAdapterList;
import com.mwc.docportal.Common.BackgroundUploadService;
import com.mwc.docportal.Common.CameraUtils;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.FileDownloadManager;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Common.ServerConfig;
import com.mwc.docportal.Common.SimpleDividerItemDecoration;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.OffLine_Files_Repository;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.GlobalSearch.GlobalSearchActivity;
import com.mwc.docportal.GridAutofitLayoutManager;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.OffLine_Files_List;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;
import com.mwc.docportal.Utils.DateHelper;
import com.mwc.docportal.pdf.PdfViewActivity;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.activity.VideoPickActivity;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;
import com.vincent.filepicker.filter.entity.VideoFile;

import java.io.File;
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

import static com.vincent.filepicker.activity.BaseActivity.IS_NEED_FOLDER_LIST;
import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class NavigationMyFolderActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    DmsAdapter mAdapter;
    DmsAdapterList mAdapterList;
    RecyclerView mRecyclerView;
    AlertDialog mCustomAlertDialog;
    AlertDialog mAlertDialog;
    MenuItem menuItemSearch, menuItemDelete, menuItemShare, menuItemMove, menuItemMore;

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
    List<UploadModel> list_upload;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    RelativeLayout toggleView;
    ImageView toggle, sort_image;
    TextView sort;
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
    String objectId, categoryName;
    int pageSize = 20;
    int backButtonCount = 0;
    RelativeLayout move_layout;
    TextView move_textview, cancel_textview;
    LinearLayout sorting_layout;
    BottomNavigationView bottomNavigationLayout;
    public static int mode = 0;
    AlertDialog mBackDialog;
    LinearLayout empty_view;
    TextView no_documents_txt;

    public static final int REQUEST_STORAGE_PERMISSION = 111;
    public static final int REQUEST_CAMERA_PERMISSION = 222;
    public static final int IMAGE_DOWNLOAD_PERMISSION = 333;

    boolean isVideo = false;
    boolean isFromSearchData = false;
    List<GetCategoryDocumentsResponse> downloadingItemsList = new ArrayList<>();
    int downloadIndex = 0;
    LoadingProgressDialog transparentProgressDialog;
    private SwipeRefreshLayout mRefreshLayout;
    LinearLayout bottom_linearlayout;
    Button refreshButton;

    /*public static final String mBroadcastStringAction = "com.mwc.docportal.DMS.Receiver";
    private IntentFilter mIntentFilter;*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list_upload = new ArrayList<>();

       /* mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);*/

        getSharedDocumentsTotalUnreadCount(NavigationMyFolderActivity.this);

        if(getResources().getBoolean(R.bool.portrait_only)){
            pageSize = 20;
        }
        else
        {
            pageSize = 40;
        }

        incrementActivityCount();

        if(PreferenceUtils.getPushNotificationDocumentVersionId(context) != null && !PreferenceUtils.getPushNotificationDocumentVersionId(context).isEmpty())
        {
           Intent intent = new Intent(NavigationMyFolderActivity.this, PdfViewActivity.class);
           startActivity(intent);
        }

        intiaizeViews();
        OnClickListeners();
        getWhiteLabelProperities();
        no_documents_txt.setText("");
        itemSelectedColorApplied();

        transparentProgressDialog = new LoadingProgressDialog(context);

        if(getIntent().getStringExtra("ObjectId") != null)
        {
            objectId = getIntent().getStringExtra("ObjectId");
        }
        else
        {
            objectId = "0";
        }

        if(getIntent().getStringExtra("CategoryName") != null)
        {
            categoryName = getIntent().getStringExtra("CategoryName");
        }
        else
        {
            categoryName = "My Folder";
        }

        toggleAddAndBackButton();
        getCategoryDocuments();

        if (!GlobalVariables.isMoveInitiated) {
            if(NetworkUtils.checkIfNetworkAvailable(this))
                hideBottomView();
        }
        else {
            showBottomView();
        }

        if(PreferenceUtils.getImageUploadList(context, "key") != null && PreferenceUtils.getImageUploadList(context, "key").size() > 0)
        {
            showFailedUploadList();
        }


        mRefreshLayout.setOnRefreshListener(this);

        mRecyclerView.addOnScrollListener(mScrollListener);

        setSortTitle();
        reloadAdapterData(false);

    }

    private void itemSelectedColorApplied()
    {
        if(mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0)
        {
            String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            int selectedColor = Color.parseColor(itemSelectedColor);
            floatingActionMenu.setMenuButtonColorNormal(selectedColor);
            floatingActionMenu.setMenuButtonColorPressed(selectedColor);

            ShapeDrawable shapedrawable = new ShapeDrawable();
            shapedrawable.setShape(new RectShape());
            shapedrawable.getPaint().setColor(selectedColor);
            shapedrawable.getPaint().setStrokeWidth(2f);
            shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
            refreshButton.setBackground(shapedrawable);
            refreshButton.setTextColor(selectedColor);
        }

    }

    private void incrementActivityCount()
    {
        GlobalVariables.activityCount++;
    }


    private void showBottomView()
    {
        bottomNavigationLayout.setVisibility(View.GONE);
        sorting_layout.setVisibility(View.GONE);
        move_layout.setVisibility(View.VISIBLE);
        actionCamera.setVisibility(View.GONE);
        actionUpload.setVisibility(View.GONE);
        actionVideo.setVisibility(View.GONE);

     //   GlobalVariables.sortType = "type";

        if(!GlobalVariables.sortType.equalsIgnoreCase(Constants.NO_SORTING_TEXT))
        {
            GlobalVariables.sortType = "type";
        }


        setMargins(floatingActionMenu,11,0,11, 75);
        setMargins(bottom_linearlayout,0,0,0,122);

        if (GlobalVariables.selectedActionName.equalsIgnoreCase("move") || GlobalVariables.selectedActionName.equalsIgnoreCase("delete"))
        {
            List<GetCategoryDocumentsResponse> documentList = new ArrayList<>();
            for(GetCategoryDocumentsResponse cate : GlobalVariables.selectedDocumentsList)
            {
                if(cate.getType().equalsIgnoreCase("document"))
                {
                    documentList.add(cate);
                }

            }

            if((documentList != null && documentList.size() > 0) || (GlobalVariables.selectedActionName.equalsIgnoreCase("delete")))
            {
                if(!objectId.equals("0"))
                {
                    move_textview.setText("Move Here");
                }
            }
            else
            {
                move_textview.setText("Move Here");
            }
        }
        else if(GlobalVariables.selectedActionName.equalsIgnoreCase("copy") || GlobalVariables.selectedActionName.equalsIgnoreCase("share_copy"))
        {
            List<GetCategoryDocumentsResponse> documentList = new ArrayList<>();
            for(GetCategoryDocumentsResponse cate : GlobalVariables.selectedDocumentsList)
            {
                if(cate.getType().equalsIgnoreCase("document"))
                {
                    documentList.add(cate);
                }
            }

            if(documentList != null && documentList.size() > 0) {
                if(!objectId.equals("0"))
                {
                    move_textview.setText("Copy Here");
                }
            }
            else
            {
                move_textview.setText("Copy Here");
            }
        }
        else if(GlobalVariables.selectedActionName.equalsIgnoreCase("upload"))
        {
            if(!objectId.equals("0"))
            {
                move_textview.setText("Upload");
            }
        }
    }

    private void hideBottomView()
    {
        bottomNavigationLayout.setVisibility(View.VISIBLE);
        if(mGetCategoryDocumentsResponses != null && mGetCategoryDocumentsResponses.size() > 0)
        {
            sorting_layout.setVisibility(View.VISIBLE);
        }
        else
        {
            sorting_layout.setVisibility(View.GONE);
        }

        move_layout.setVisibility(View.GONE);
        setMargins(floatingActionMenu,11,0,11, 65);
        setMargins(bottom_linearlayout,0,0,0,90);

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
                                reloadAdapterData(false);

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

                                sendPinButton.setText("Ok");

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

        if (NetworkUtils.checkIfNetworkAvailable(this)) {

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

            Call call = null;
            if(GlobalVariables.sortType.equalsIgnoreCase(Constants.NO_SORTING_TEXT))
            {
                call = mGetCategoryDocumentsService.getCategoryDocumentsWithoutSortType(params, PreferenceUtils.getAccessToken(context),String.valueOf(pageNumber+1), String.valueOf(pageSize));
            }
            else
            {
                call = mGetCategoryDocumentsService.getCategoryDocuments(params, PreferenceUtils.getAccessToken(context),String.valueOf(pageNumber+1), String.valueOf(pageSize),sorting );
            }

            call.enqueue(new Callback<ListPinDevicesResponse<GetCategoryDocumentsResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetCategoryDocumentsResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }


                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.status.getCode()))
                        {

                            pageNumber++;
                            List<GetCategoryDocumentsResponse> filteredArray = new ArrayList<>();
                            filteredArray = response.body().getData();
                            boolean isFromNewData = false;
                            if(pageNumber == 1)
                            {
                                mGetCategoryDocumentsResponses = filteredArray;
                            }
                            else
                            {
                                listGetCategoryDocuments = filteredArray;
                                mGetCategoryDocumentsResponses.addAll(filteredArray);
                                isFromNewData = true;

                            }
                            totalPages  = Integer.parseInt(response.headers().get("X-Pagination-Page-Count"));
                            setSortTitle();
                            toggleEmptyState();
                            reloadAdapterData(isFromNewData);
                            if(isFromSearchData == true && GlobalVariables.searchKey != null && !GlobalVariables.searchKey.isEmpty())
                            {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(NavigationMyFolderActivity.this, GlobalSearchActivity.class);
                                        startActivity(intent);
                                    }
                                }, 1000);

                            }
                            if(PreferenceUtils.getMaxSizeUpload(context) == null || PreferenceUtils.getMaxSizeUpload(context).isEmpty())
                            {
                                getMaxUploadSize();
                            }

                            if(PreferenceUtils.getSplashLogoImagePath(context) == null || PreferenceUtils.getSplashLogoImagePath(context).isEmpty())
                            {
                                AccountSettings accountSettings = new AccountSettings(NavigationMyFolderActivity.this);
                                String companyName = accountSettings.getCompanyName();
                                getPermissionForExternalStorage(companyName);
                            }
                        }

                    }
                    else
                    {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                    Log.d("Message", t.getMessage());
                }
            });
        }
        else
        {
            internetUnAvailableWithMessage();
        }
    }

    private void getPermissionForExternalStorage(String companyName)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int storagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (storagePermission == PackageManager.PERMISSION_GRANTED) {
                downloadLogoImage(Constants.LOGO_IMAGE_BASE_URL+Constants.Logo_ImagePath+companyName+Constants.Splash_Logo_Image_Name, companyName);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_DOWNLOAD_PERMISSION);
            }
        } else {
            downloadLogoImage(Constants.LOGO_IMAGE_BASE_URL+Constants.Logo_ImagePath+companyName+Constants.Splash_Logo_Image_Name, companyName);
        }
    }

    private void getMaxUploadSize()
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final GetMaxUploadService getMaxUploadService = retrofitAPI.create(GetMaxUploadService.class);

            Call call = getMaxUploadService.getmaxsize(PreferenceUtils.getAccessToken(this));

            call.enqueue(new Callback<MaxDocumentUploadSizeResponse>() {
                @Override
                public void onResponse(Response<MaxDocumentUploadSizeResponse> response, Retrofit retrofit) {
                    MaxDocumentUploadSizeResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.getStatus().getMessage() != null)
                        {
                            message = apiResponse.getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.getStatus().getCode()))
                        {
                            String size = apiResponse.getData().getMaxDocumentUploadSize();
                            int last =size.length();
                            String maxSize = size.substring(0,last-1);
                            PreferenceUtils.setMaxSizeUpload(context,maxSize);

                            if(PreferenceUtils.getFileFormats(context, "key") == null || PreferenceUtils.getFileFormats(context, "key").isEmpty())
                            {
                                getFileFormats();
                            }
                        }
                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("Message", t.getMessage());
                    CommonFunctions.showTimeOutError(context, t);
                }
            });
        }
    }

    private void getFileFormats()
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();


            final GetFileFormatService getFileFormatService = retrofitAPI.create(GetFileFormatService.class);

            Call call = getFileFormatService.getfileformat(PreferenceUtils.getAccessToken(this));

            call.enqueue(new Callback<FileFormatResponse>() {
                @Override
                public void onResponse(Response<FileFormatResponse> response, Retrofit retrofit) {
                    FileFormatResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        String message = "";
                        if(response.body().getStatus().getMessage() != null)
                        {
                            message = response.body().getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.getStatus().getCode())) {
                            List fileformat;
                            fileformat = apiResponse.getData();
                            PreferenceUtils.setFileFormats(context, (ArrayList<String>) fileformat,"key");
                        }
                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("Message", t.getMessage());
                    CommonFunctions.showTimeOutError(context, t);
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
            sort_image.setVisibility(View.VISIBLE);
            sort_image.setImageResource(R.mipmap.ic_sortup);
        }
        else
        {
            sort_image.setVisibility(View.VISIBLE);
            sort_image.setImageResource(R.mipmap.ic_sort);
        }

        if(GlobalVariables.sortType.equalsIgnoreCase(Constants.NO_SORTING_TEXT))
        {
            sort_image.setVisibility(View.VISIBLE);
            sort_image.setImageResource(R.mipmap.ic_sortup);
            sort.setText(Constants.NO_SORTING_TEXT);
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
            actionCamera.setVisibility(View.GONE);
            actionUpload.setVisibility(View.GONE);
            actionVideo.setVisibility(View.GONE);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void OnClickListeners()
    {

        move_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(GlobalVariables.selectedActionName.equalsIgnoreCase("move"))
                {

                    List<GetCategoryDocumentsResponse> filteredListForDuplicate = new ArrayList<>();
                    for(GetCategoryDocumentsResponse cate : GlobalVariables.selectedDocumentsList)
                    {
                        if(cate.getType().equalsIgnoreCase("category"))
                        {
                            if(cate.getObject_id().equalsIgnoreCase(objectId))
                            {
                                filteredListForDuplicate.add(cate);
                            }
                        }

                    }

                    if(filteredListForDuplicate != null && filteredListForDuplicate.size() > 0)
                    {
                        showWarningMessageAlertForSourceFolderWrong();
                        filteredListForDuplicate.clear();
                    }
                    else
                    {
                        moveDocuments();
                    }

                }
                else if(GlobalVariables.selectedActionName.equalsIgnoreCase("copy") || GlobalVariables.selectedActionName.equalsIgnoreCase("share_copy"))
                {
                    copyDocuments();
                }
                else if(GlobalVariables.selectedActionName.equalsIgnoreCase("delete"))
                {
                    if (mode == 1)
                    {
                        List<GetCategoryDocumentsResponse> filteredList = new ArrayList<>();
                        for(GetCategoryDocumentsResponse cate : GlobalVariables.selectedDocumentsList)
                        {
                            if(cate.getType().equalsIgnoreCase("category"))
                            {
                                if(cate.getObject_id().equalsIgnoreCase(objectId))
                                {
                                    filteredList.add(cate);
                                }
                            }

                        }

                        if(filteredList != null && filteredList.size() > 0)
                        {
                            showWarningMessageAlertForSourceFolderWrong();
                            filteredList.clear();
                        }
                        else
                        {
                            delete_Folder_Move();
                        }

                    }
                }
                else if(GlobalVariables.selectedActionName.equalsIgnoreCase("upload"))
                {
                    if(GlobalVariables.otherAppDocumentList.size() > 0)
                    {
                        uploadValidation();
                    }
                }
            }
        });

        cancel_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.isMoveInitiated = false;
                GlobalVariables.selectedDocumentsList.clear();
                GlobalVariables.selectedActionName = "";
                mAdapterList.clearAll();
                hideBottomView();
                reloadAdapterData(false);
                GlobalVariables.activityFinishCount = (GlobalVariables.activityCount - GlobalVariables.moveOriginIndex) - 1;
                finish();

                if( GlobalVariables.otherAppDocumentList.size() > 0)
                {
                    GlobalVariables.otherAppDocumentList.clear();
                    finishAffinity();
                    System.exit(0);
                }

            }
        });

        toggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.isTileView = !GlobalVariables.isTileView;
                reloadAdapterData(false);
            }
        });

        sortingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });


        actionUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storageAccessPermission();


            }
        });


        actionCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isVideo = false;
                cameraAndStoragePermission();

            }

        });



        actionVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideo = true;
                cameraAndStoragePermissionForVideo();
            }
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
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(45);
            namer.setFilters(FilterArray);
            namer.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            allow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String folder_name = namer.getText().toString().trim();

                    if(folder_name == null || folder_name.isEmpty())
                    {
                        Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show();
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
                                    String message = "";
                                    if(apiResponse.status.getMessage() != null)
                                    {
                                        message = apiResponse.status.getMessage().toString();
                                    }

                                    if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.status.getCode())) {
                                        resetPageNumber();
                                        getCategoryDocuments();
                                    }

                                }
                                else {
                                    transparentProgressDialog.dismiss();
                                    CommonFunctions.serverErrorExceptions(context, response.code());
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                transparentProgressDialog.dismiss();
                                CommonFunctions.showTimeOutError(context, t);
                                Log.d("Message", t.getMessage());
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


       /* scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView != null) {
                    if (scrollView.getChildAt(0).getBottom() == (scrollView.getHeight() + scrollView.getScrollY())) {

                        if(pageNumber + 1 <= totalPages) {
                            getCategoryDocuments();
                        }
                    }
                    else {
                        //scroll view is not at bottom
                    }
                }
            }
        });*/

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });

        /*floatingActionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  floatingActionMenu.close(true);
                Toast.makeText(context, "Button clicked",Toast.LENGTH_SHORT).show();
            }
        });*/

        floatingActionMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolderUploadBottomSheet();
                floatingActionMenu.close(true);
            }
        });



    }




    private void showWarningMessageAlertForSourceFolderWrong()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);
        txtMessage.setText("The Destination folder is the subfolder of the source folder.");
        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setVisibility(View.GONE);
        okButton.setText("Ok");


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void storageAccessPermission()
    {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int storagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (storagePermission == PackageManager.PERMISSION_GRANTED) {
                    documentUpload();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                }
            } else {
                documentUpload();
            }

    }

    private void documentUpload()
    {
        floatingActionMenu.close(true);
        if(Build.VERSION.SDK_INT >= 24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

   //     pickFile();
    }

    private void cameraAndStoragePermissionForVideo()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA);
            int storagePermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED) {

                videoAccess();

            } else if (permission == PackageManager.PERMISSION_GRANTED && storagePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            } else if (permission != PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            }
        } else {
            videoAccess();
        }
    }

    private void videoAccess()
    {
        floatingActionMenu.close(true);
     //   GlobalVariables.isFromCamerOrVideo = true;
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
    }

    private void showWarningAlertAlreadyFolderExist(String mMessage)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);

        Button BtnAllow = (Button) view.findViewById(R.id.allow_button);
        BtnAllow.setText("Ok");
        final Button BtnCancel = (Button) view.findViewById(R.id.cancel_button);
        BtnCancel.setText("Cancel");
        BtnCancel.setVisibility(View.GONE);
        TextView textView =(TextView) view.findViewById(R.id.txt_message);
        textView.setVisibility(View.GONE);
        TextView text = (TextView) view.findViewById(R.id.message);
        text.setText(mMessage);

        BtnAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomAlertDialog.dismiss();

            }
        });

        mCustomAlertDialog = builder.create();
        mCustomAlertDialog.show();
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
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {
                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.status.getCode())) {
                            GlobalVariables.isMoveInitiated = false;
                            hideBottomView();
                            resetPageNumber();
                            getCategoryDocuments();
                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
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


            final DeleteEndUserFolderMoveRequest deleteEndUserFolderMoveRequest = new DeleteEndUserFolderMoveRequest(documentIds,1,objectId);

            String request = new Gson().toJson(deleteEndUserFolderMoveRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final DeleteEndUserMoveService deleteEndUserMoveService = retrofitAPI.create(DeleteEndUserMoveService.class);

            Call call = deleteEndUserMoveService.delete_eu_move(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Response<ApiResponse> response, Retrofit retrofit) {
                    ApiResponse apiResponse = response.body();
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.status.getCode())) {
                        //    hideBottomView();
                         //   resetPageNumber();
                         //   getCategoryDocuments();
                            GlobalVariables.isMoveInitiated = false;
                            GlobalVariables.refreshPage = true;
                          //  Toast.makeText(context, "Selected item(s) moved successfully", Toast.LENGTH_LONG).show();
                            cancel_textview.performClick();
                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
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
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

                        String message = "";
                        if(response.body().status.getMessage() != null)
                        {
                            message = response.body().status.getMessage().toString();
                        }


                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, response.body().status.getCode())) {
                         //   hideBottomView();
                          //  resetPageNumber();
                            GlobalVariables.isMoveInitiated = false;
                            if(GlobalVariables.selectedActionName.equalsIgnoreCase("copy"))
                            {
                                GlobalVariables.refreshPage = true;
                            }
                            isFromSearchData = true;
                            cancel_textview.performClick();
                          //  getCategoryDocuments();
                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
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
            ArrayList<String> categoryIds = new ArrayList<>();

            for(GetCategoryDocumentsResponse cate : GlobalVariables.selectedDocumentsList)
            {
                if(cate.getType().equalsIgnoreCase("category"))
                {
                    categoryIds.add(cate.getObject_id());
                }
                else
                {
                    documentIds.add(cate.getObject_id());
                }

            }



            final MoveDocumentRequest moveDocumentRequest = new MoveDocumentRequest(documentIds, categoryIds, objectId);

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
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

                        String message = "";
                        if(response.body().status.getMessage() != null)
                        {
                            message = response.body().status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, response.body().status.getCode())) {
                         //   hideBottomView();
                        //   resetPageNumber();
                            GlobalVariables.isMoveInitiated = false;
                            GlobalVariables.refreshPage = true;
                       //     Toast.makeText(context, "Selected item(s) moved successfully", Toast.LENGTH_LONG).show();
                            isFromSearchData = true;
                            cancel_textview.performClick();
                          //  getCategoryDocuments();
                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                }
            });
        }
    }

    private void reloadAdapterData(boolean isFromNewData)
    {
        if (GlobalVariables.isTileView && !GlobalVariables.isMoveInitiated)
        {
            toggle.setImageResource(R.mipmap.ic_list);
            if(isFromNewData)
            {
                mAdapter.notifyDataSetChanged();
            }
            else
            {
                setGridAdapterToView(mGetCategoryDocumentsResponses);
                mAdapter.notifyDataSetChanged();
            }


            isFromList = false;
        }
        else
        {
            toggle.setImageResource(R.mipmap.ic_grid);

            if(isFromNewData)
            {
                mAdapterList.notifyDataSetChanged();
            }
            else
            {
                setListAdapterToView(mGetCategoryDocumentsResponses);
                mAdapterList.notifyDataSetChanged();
            }

            isFromList = true;

        }

        if(pageNumber == 1)
        {
          //  scrollView.fullScroll(ScrollView.FOCUS_UP);
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

        String timeStamp = new SimpleDateFormat("ddMMyyHHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "Image_" + timeStamp + ".jpeg");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void intiaizeViews()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_dms);
        mRecyclerView.setNestedScrollingEnabled(false);
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
        floatingActionMenu.setClosedOnTouchOutside(true);
        empty_view = (LinearLayout)findViewById(R.id.empty_view);
        no_documents_txt = (TextView)findViewById(R.id.no_documents_txt);

        actionUpload = (FloatingActionButton) findViewById(R.id.menu_upload_item);
        actionCamera = (FloatingActionButton)findViewById(R.id.menu_camera_item);
        actionNewFolder = (FloatingActionButton) findViewById(R.id.menu_new_folder_item);
        actionVideo = (FloatingActionButton)findViewById(R.id.menu_camera_video_item);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        bottom_linearlayout = (LinearLayout) findViewById(R.id.bottom_linearlayout);
        refreshButton = (Button) findViewById(R.id.refresh_button);

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

   /* public void pickFile() {
        DialogProperties properties = new DialogProperties();
        *//*properties.extensions = new String[]{".pdf", ".doc", ".docx", ".xlsx", ".txt", ".jpg", ".png", ".bmp", ".gif", ".tiff", ".jpeg", ".xls" ,".mp4",".mp3",".wav",".mov",".avi",".m4a",".jpeg",".mkv",".ppt",".pptx"};
         *//*
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
               // if (list_upload.size() + files.length <= 10) {
                    int fileCount = files.length;
                    for (int i = 0; i <= fileCount - 1; i++) {
                        list_upload.add(String.valueOf(new File(files[i])));
                        PreferenceUtils.setupload(context,list_upload,"key");
                    }

             //   }
                *//*else
                {
                    Toast.makeText(context,"Please select less then 10 documents ",Toast.LENGTH_SHORT).show();
                }*//*
                Intent intent = new Intent(context,UploadListActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }
*/

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
        } else if (GlobalVariables.sortType.equalsIgnoreCase(Constants.NO_SORTING_TEXT)) {
             sortNameImage.setVisibility(View.INVISIBLE);
             sortNewestImage.setVisibility(View.INVISIBLE);
             sortSizeImage.setVisibility(View.INVISIBLE);
             sortDateImage.setVisibility(View.INVISIBLE);

             sortNameDoneImage.setVisibility(View.INVISIBLE);
             sortNewestDoneImage.setVisibility(View.INVISIBLE);
             sortSizeDoneImage.setVisibility(View.INVISIBLE);
             sortDateDoneImage.setVisibility(View.INVISIBLE);

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

             //   scrollView.fullScroll(ScrollView.FOCUS_UP);
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
            //    scrollView.fullScroll(ScrollView.FOCUS_UP);

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
           //     scrollView.fullScroll(ScrollView.FOCUS_UP);

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
            //    scrollView.fullScroll(ScrollView.FOCUS_UP);


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

        Log.d("OnActivity Result", "Intent received");
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

                            UploadModel uploadModel = new UploadModel();
                            uploadModel.setFilePath(String.valueOf(filePath));
                            list_upload.add(uploadModel);
                            PreferenceUtils.setImageUploadList(context,list_upload,"key");
                            //   PreferenceUtils.setupload(getContext(),list_upload,"key");


                        }

                    } else {
                        uri = data.getData();

                        String filePath = getRealPathFromURIPath(uri, context);
                        //File file = new File(filePath);

                      /*  ArrayList<String> filePathList = new ArrayList<String>();
                        filePathList.add(filePath);*/
                        list_upload = PreferenceUtils.getImageUploadList(context,"key");
                        UploadModel uploadModel = new UploadModel();
                        uploadModel.setFilePath(String.valueOf(filePath));
                        list_upload.add(uploadModel);
                        PreferenceUtils.setImageUploadList(context,list_upload,"key");

                        //   uploadGalleryImage(file, filePathList);
                    }
                }
                if((list_upload == null )||(list_upload.size()==0))
                {
                    String filepath = getRealPathFromURIPath(fileUri, context);
                    UploadModel uploadModel = new UploadModel();
                    uploadModel.setFilePath(filepath);
                    list_upload.add(uploadModel);
                    PreferenceUtils.setImageUploadList(context,list_upload,"key");
                }
            }
            Intent intent = new Intent (context,UploadListActivity.class);
            startActivity(intent);

        }
        else if (requestCode == REQUEST_CAPTURE_IMAGE_CODE && resultCode == RESULT_OK) {

            list_upload = new ArrayList<>();
            if (resultCode == RESULT_OK) {

                String filePath = fileUri.getPath();

                UploadModel uploadModel = new UploadModel();
                uploadModel.setFilePath(String.valueOf(filePath));
                list_upload.add(uploadModel);
                PreferenceUtils.setImageUploadList(context,list_upload,"key");


                Intent intent = new Intent (context,UploadListActivity.class);
                startActivity(intent);


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(context, "User cancelled image capture", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "Error capturing image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            String filePath =imageStoragePath;

            if(PreferenceUtils.getImageUploadList(context,"key") != null && PreferenceUtils.getImageUploadList(context,"key").size() > 0)
            {
                list_upload = PreferenceUtils.getImageUploadList(context,"key");
            }
            else
            {
                list_upload = new ArrayList<>();
            }

            UploadModel uploadModel = new UploadModel();
            uploadModel.setFilePath(String.valueOf(filePath));
            list_upload.add(uploadModel);
            PreferenceUtils.setImageUploadList(context,list_upload,"key");
            list_upload.clear();

            Intent intent = new Intent (context,UploadListActivity.class);
            startActivity(intent);


        }
        else if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK)
        {
            list_upload = new ArrayList<>();
            ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
            for (ImageFile file : list) {
                String path = file.getPath();
                list_upload = PreferenceUtils.getImageUploadList(NavigationMyFolderActivity.this, "key");
                if(list_upload == null)
                {
                    list_upload = new ArrayList<>();
                }

                UploadModel uploadModel = new UploadModel();
                uploadModel.setFilePath(path);
                list_upload.add(uploadModel);

                PreferenceUtils.setImageUploadList(NavigationMyFolderActivity.this,list_upload,"key");
                Intent intent = new Intent (context,UploadListActivity.class);
                startActivity(intent);
            }


        }
        else if (requestCode == Constant.REQUEST_CODE_PICK_VIDEO && resultCode == RESULT_OK)
        {
            list_upload = new ArrayList<>();
            ArrayList<VideoFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
            for (VideoFile file : list) {
                String path = file.getPath();
                list_upload = PreferenceUtils.getImageUploadList(NavigationMyFolderActivity.this, "key");
                if(list_upload == null)
                {
                    list_upload = new ArrayList<>();
                }
                if(path != null)
                {
                    UploadModel uploadModel = new UploadModel();
                    uploadModel.setFilePath(path);
                    list_upload.add(uploadModel);
                }

                PreferenceUtils.setImageUploadList(NavigationMyFolderActivity.this,list_upload,"key");
                Intent intent = new Intent (context,UploadListActivity.class);
                startActivity(intent);
            }

        }
        else if (requestCode == Constant.REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK)
        {
            list_upload = new ArrayList<>();
            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
            for (NormalFile file : list) {
                String path = file.getPath();
                list_upload = PreferenceUtils.getImageUploadList(NavigationMyFolderActivity.this, "key");
                if(list_upload == null)
                {
                    list_upload = new ArrayList<>();
                }
                if(path != null)
                {
                    UploadModel uploadModel = new UploadModel();
                    uploadModel.setFilePath(path);
                    list_upload.add(uploadModel);
                }
                PreferenceUtils.setImageUploadList(NavigationMyFolderActivity.this,list_upload,"key");
                Intent intent = new Intent (context,UploadListActivity.class);
                startActivity(intent);
            }
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


    /*@Override
    protected void onRestart() {
        super.onRestart();
        list_upload = PreferenceUtils.getImageUploadList(NavigationMyFolderActivity.this,"key");
        if ((list_upload!=null)&&(list_upload.size()>0)){
            final AlertDialog.Builder builder = new AlertDialog.Builder(NavigationMyFolderActivity.this);
            LayoutInflater inflater = (LayoutInflater) NavigationMyFolderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.custom_dialog, null);
            builder.setView(view);
            builder.setCancelable(false);

            final Button BtnAllow = (Button) view.findViewById(R.id.allow_button);
            BtnAllow.setText("Retry");
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
    }*/



    public void setListAdapterToView(final List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses)
    {
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        mAdapterList = new DmsAdapterList(getCategoryDocumentsResponses, NavigationMyFolderActivity.this);
        mRecyclerView.setAdapter(mAdapterList);

    }

    private void setGridAdapterToView(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        toggle.setImageResource(R.mipmap.ic_list);
        int mNoOfColumns = GridAutofitLayoutManager.calculateNoOfColumns(getApplicationContext());
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, mNoOfColumns));
        while (mRecyclerView.getItemDecorationCount() > 0) {
            mRecyclerView.removeItemDecorationAt(0);
        }
        mAdapter = new DmsAdapter(getCategoryDocumentsResponses, NavigationMyFolderActivity.this);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (GlobalVariables.isTileView && !GlobalVariables.isMoveInitiated)
        {
            int mNoOfColumns = GridAutofitLayoutManager.calculateNoOfColumns(getApplicationContext());
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mNoOfColumns));
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

          searchIconDisplay();


          if(mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0)
          {
              String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
              int selectedColor = Color.parseColor(itemSelectedColor);

              menuIconColor(menuItemSearch,selectedColor);
          }



          menuItemSearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
              @Override
              public boolean onMenuItemClick(MenuItem item) {

                  Intent intent = new Intent(NavigationMyFolderActivity.this, GlobalSearchActivity.class);
                  startActivity(intent);

                  return false;
              }
          });



          return true;
      }

    private void searchIconDisplay()
    {
        if (!GlobalVariables.isMoveInitiated)
        {
            menuItemSearch.setVisible(true);
        }
        else
        {
            menuItemSearch.setVisible(false);
        }

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
                  CommonFunctions.setSelectedItems(mSelectedDocumentList);
                  GlobalVariables.isMultiSelect = false;
                  List<GetCategoryDocumentsResponse> categoryDocumentsResponseFolderList = new ArrayList<>();
                  List<GetCategoryDocumentsResponse> categoryDocumentsResponseDocumentList = new ArrayList<>();


                  for (GetCategoryDocumentsResponse viewListObj : mSelectedDocumentList) {

                      if (viewListObj.getType().equalsIgnoreCase("category")) {
                          categoryDocumentsResponseFolderList.add(viewListObj);
                      } else {
                          categoryDocumentsResponseDocumentList.add(viewListObj);
                      }
                  }

                  clearSelectedListAfterOperation();

                  if(categoryDocumentsResponseFolderList != null && categoryDocumentsResponseFolderList.size() > 0)
                  {
                      showDeleteAlertDialogForCategory();
                  }
                  else
                  {
                      showDeleteAlertDialogForDocuments();
                  }
                  break;
              case R.id.action_move:
                  CommonFunctions.setSelectedItems(mSelectedDocumentList);
                  GlobalVariables.isMultiSelect = false;
                  assigningMoveOriginIndex();
                  clearSelectedListAfterOperation();
                  initiateMoveAction("move");

                   break;

              case R.id.action_share:
                  CommonFunctions.setSelectedItems(mSelectedDocumentList);
                  GlobalVariables.isMultiSelect = false;
                  assigningMoveOriginIndex();
                  clearSelectedListAfterOperation();
                  initiateMoveAction("copy");

                  break;

          }
          return super.onOptionsItemSelected(item);
      }

    @Override
    public void onBackPressed() {

        decrementActivityCount();

        if(objectId.equals("0"))
        {
                if (backButtonCount >= 1) {
                    if(GlobalVariables.otherAppDocumentList.size() > 0)
                    {
                       cancel_textview.performClick();
                    }
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

    private void decrementActivityCount()
    {
        if(GlobalVariables.activityCount > 0)
        {
            GlobalVariables.activityCount--;
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
                clearSelectedListAfterOperation();
                GlobalVariables.isMultiSelect = false;
                if(buttonView.isClickable() == true) {
                    mBottomSheetDialog.dismiss();
                    if (!isChecked) {
                        switchButton_share.setChecked(false);
                        ArrayList<String> documentIdslist = new ArrayList<>();
                        for(GetCategoryDocumentsResponse categoryDocumentsResponse :  GlobalVariables.selectedDocumentsList)
                        {
                            documentIdslist.add(categoryDocumentsResponse.getObject_id());
                        }

                        if(documentIdslist !=null && documentIdslist.size() > 0)
                        {
                            showWarningMessageAlertForSharingContent(documentIdslist,  GlobalVariables.selectedDocumentsList);
                        }

                    } else {
                        switchButton_share.setChecked(true);

                        CommonFunctions.checkMultipleSharedLevel(NavigationMyFolderActivity.this, true);

                        /*GlobalVariables.isMoveInitiated = true;
                        GlobalVariables.selectedActionName =  "share";
                        Intent intent = new Intent(context, NavigationSharedActivity.class);
                        intent.putExtra("ObjectId", "0");
                        startActivity(intent);*/

                    }


                }

            }
        });

        copyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                GlobalVariables.isMultiSelect = false;
                clearSelectedListAfterOperation();
                assigningMoveOriginIndex();
                initiateMoveAction("copy");
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v)
            {
                GlobalVariables.isMultiSelect = false;
                mBottomSheetDialog.dismiss();
                List<GetCategoryDocumentsResponse> categoryDocumentsResponseFolderList = new ArrayList<>();
                List<GetCategoryDocumentsResponse> categoryDocumentsResponseDocumentList = new ArrayList<>();


                for (GetCategoryDocumentsResponse viewListObj : mSelectedDocumentList) {

                    if (viewListObj.getType().equalsIgnoreCase("category")) {
                        categoryDocumentsResponseFolderList.add(viewListObj);
                    } else {
                        categoryDocumentsResponseDocumentList.add(viewListObj);
                    }
                }


                if(categoryDocumentsResponseFolderList != null && categoryDocumentsResponseFolderList.size() > 0)
                {
                    showDeleteAlertDialogForCategory();
                }
                else
                {
                    showDeleteAlertDialogForDocuments();
                }
                clearSelectedListAfterOperation();
            }

        });



        doc_info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.isMultiSelect = false;
                mBottomSheetDialog.dismiss();
                if(mSelectedDocumentList != null && mSelectedDocumentList.size() == 1)
                {
                    PreferenceUtils.setDocumentVersionId(context,mSelectedDocumentList.get(0).getDocument_version_id());
                    PreferenceUtils.setDocument_Id(context, mSelectedDocumentList.get(0).getObject_id());
                }

                Intent intent = new Intent (context,Tab_Activity.class);
                intent.putExtra("IsFromMyFolder", true);
                intent.putExtra(Constants.DOCUMENT_NAME, mSelectedDocumentList.get(0).getName());
                startActivity(intent);
              //  clearSelectedListAfterOperation();
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
                mBottomSheetDialog.dismiss();
                GlobalVariables.isMultiSelect = false;
                assigningMoveOriginIndex();
                clearSelectedListAfterOperation();
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

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(45);
                namer.setFilters(FilterArray);
                namer.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
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
                            Toast.makeText(context, getString(R.string.newname_txt), Toast.LENGTH_SHORT).show();

                        }


                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                        mBottomSheetDialog.dismiss();
                        clearSelectedListAfterOperation();
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
                    GlobalVariables.isMultiSelect = false;
                    isTouched = false;
                    mBottomSheetDialog.dismiss();
                    if (isChecked) {

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
                            convertingDownloadUrl(downloadedList);
                        }

                    }
                    else
                    {
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

    public void assigningMoveOriginIndex()
    {
        GlobalVariables.moveOriginIndex = GlobalVariables.activityCount;
    }

    private void showDeleteAlertDialogForDocuments()
    {
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

                if (categoryDocumentsResponse.getVersion_count() != null && !categoryDocumentsResponse.getVersion_count().equals(versionCount))
                {
                    versionCount = "0";
                    break;
                }
            }
        }

        if(versionCount.equals("0"))
        {
            delete_historic.setEnabled(false);
            delete_historic.setTextColor(getResources().getColor(R.color.grey));
            delete_all.setEnabled(false);
            delete_all.setTextColor(getResources().getColor(R.color.grey));
        }


        mBackDialog = builder.create();
        mBackDialog.show();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackDialog.dismiss();


                deleteDocumentsService("0");

            }
        });


        delete_historic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackDialog.dismiss();

                    deleteDocumentsService( "1");

            }
        });

        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackDialog.dismiss();

                   deleteDocumentsService( "2");

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackDialog.dismiss();

                clearSelectedListAfterOperation();

            }
        });
    }

    private void showDeleteAlertDialogForCategory()
    {
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
                assigningMoveOriginIndex();
                clearSelectedListAfterOperation();
                initiateMoveAction("delete");

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

                clearSelectedListAfterOperation();


            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();

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

    public void initiateMoveAction(String actionName) {
        GlobalVariables.isMoveInitiated = true;
        GlobalVariables.selectedActionName =  actionName;
        Intent intent = new Intent(context, NavigationMyFolderActivity.class);
    //    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.status.getCode())) {
                            clearSelectedListAfterOperation();
                            resetPageNumber();
                            getCategoryDocuments();
                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
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

            ArrayList<EditDocumentResponse> documentResponses = new ArrayList<>();
            final EditDocumentPropertiesRequest editDocumentPropertiesRequest = new EditDocumentPropertiesRequest(document_version_id,rename,doc_created,auth, documentResponses);

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
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {
                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.status.getCode())) {
                            clearSelectedListAfterOperation();
                            resetPageNumber();
                            getCategoryDocuments();
                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                }
            });
        }
    }



    public void convertingDownloadUrl(List<GetCategoryDocumentsResponse> downloadedList)
    {
        downloadingUrlDataList = downloadedList;
        if(downloadingUrlDataList.size()> index) {

            getDownloadurlFromService(downloadingUrlDataList.get(index).getObject_id(), downloadingUrlDataList.get(index).getIs_shared());

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
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.status.getCode())) {

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
                                getDownloadurlFromService(downloadingUrlDataList.get(index).getObject_id(),downloadingUrlDataList.get(index).getIs_shared());

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
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                }
            });
        }

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
            FileDownloadManager fileDownloadManager = new FileDownloadManager(NavigationMyFolderActivity.this);
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
                    transparentProgressDialog.dismiss();

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
        offlineFilesModel.setDocumentName(digitalAsset.getFilename());
        offlineFilesModel.setDocumentVersionId(digitalAsset.getDocument_version_id());
        offlineFilesModel.setDownloadDate(DateHelper.getCurrentDate());
        offlineFilesModel.setFilename(digitalAsset.getName());
        offlineFilesModel.setFilePath(path);
        offlineFilesModel.setFiletype(digitalAsset.getFiletype());
        offlineFilesModel.setFileSize(digitalAsset.getFilesize());
        offlineFilesModel.setVersionNumber(digitalAsset.getVersion_number());
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
        title.setText("Stop Sharing");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText(context.getString(R.string.stop_sharing_text));
        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("Cancel");

        sendPinButton.setText("Ok");

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
                    transparentProgressDialog.dismiss();
                    if (response.body() != null) {

                        String message = "";
                        if(response.body().getStatus().getMessage() != null)
                        {
                            message = response.body().getStatus().getMessage().toString();
                        }

                       if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, response.body().getStatus().getCode()))
                       {
                           resetPageNumber();
                           getCategoryDocuments();
                       }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
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
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.status.getCode())) {
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
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                }
            });
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Navigation act", "Intent called");
        clearingMoveOrCopyActivity();


        /*setSortTitle();
        reloadAdapterData(false);*/
        if(!GlobalVariables.isMoveInitiated && NetworkUtils.checkIfNetworkAvailable(this))
        {
            hideBottomView();
        }

        if(GlobalVariables.refreshDMS)
        {
            refreshMyFolderDMS();
            GlobalVariables.refreshDMS = false;
            clearSelectedListAfterOperation();
        }

        if (objectId.equals("0")){
            collapsingToolbarLayout.setTitle(getResources().getString(R.string.my_folder));
        }
        else
        {
            collapsingToolbarLayout.setTitle(categoryName);
        }

        if(!NetworkUtils.checkIfNetworkAvailable(this))
        {
            internetUnAvailableWithMessage();
            mRecyclerView.setVisibility(View.GONE);
        }
        else
        {
            mRecyclerView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
        }

        if(PreferenceUtils.getIsfromPushnotification(context) != null)
        {
            getSharedDocumentsTotalUnreadCount(NavigationMyFolderActivity.this);
            PreferenceUtils.setIsfromPushnotification(context, null);
        }

    //    registerReceiver(mReceiver,mIntentFilter);

    }

    private void clearingMoveOrCopyActivity()
    {
        if(GlobalVariables.activityFinishCount > 0)
        {
            GlobalVariables.activityFinishCount--;
            GlobalVariables.activityCount--;
            finish();
        }
        else if(GlobalVariables.activityFinishCount == 0 && GlobalVariables.refreshPage == true)
        {
            refreshMyFolderDMS();
            GlobalVariables.refreshPage = false;
        }
    }


    public void deleteDocumentsService(String deleteMode)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();



            ArrayList<DeleteDocumentRequest> docsList = new ArrayList<>();

            for(GetCategoryDocumentsResponse category : GlobalVariables.selectedDocumentsList)
            {
                if (category.getType().equalsIgnoreCase("document"))
                {
                    ArrayList<String> documentVersionIdList = new ArrayList<>();
                    documentVersionIdList.add(category.getDocument_version_id());
                    DeleteDocumentRequest deleteDocumentRequest= new DeleteDocumentRequest();
                    deleteDocumentRequest.setDoc_id(category.getObject_id());
                    deleteDocumentRequest.setDoc_version_ids(documentVersionIdList);
                    deleteDocumentRequest.setMode(deleteMode);

                    docsList.add(deleteDocumentRequest);

                }

            }



            final DeleteDocumentRequest.DeleteDocRequest deleteDocRequest = new DeleteDocumentRequest.DeleteDocRequest(docsList);

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
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {


                        String message = "";
                        if(apiResponse.getStatus().getMessage() != null)
                        {
                            message = apiResponse.getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.getStatus().getCode())) {
                            resetPageNumber();
                            getCategoryDocuments();

                            if(deleteMode.equals("0"))
                            {
                                for(GetCategoryDocumentsResponse category : GlobalVariables.selectedDocumentsList)
                                {
                                    if (category.getType().equalsIgnoreCase("document"))
                                    {
                                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                                        String filepath = offLine_files_repository.getFilePathFromLocalTable(category.getDocument_version_id());
                                        if(filepath != null && !filepath.isEmpty())
                                        {
                                            CommonFunctions.deleteFileFromInternalStorage(filepath);
                                        }

                                        offLine_files_repository.deleteAlreadydownloadedFile(category.getDocument_version_id());

                                    }

                                }
                            }
                            else if(deleteMode.equals("1"))
                            {
                                for(GetCategoryDocumentsResponse category : GlobalVariables.selectedDocumentsList)
                                {
                                    if (category.getType().equalsIgnoreCase("document"))
                                    {
                                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);

                                        List<OfflineFiles> offlineFileList = offLine_files_repository.getFilePathFromLocalTableBasedUponCondition(category.getDocument_version_id(),
                                                category.getObject_id());

                                        if(offlineFileList != null && offlineFileList.size() > 0)
                                        {
                                            for(OfflineFiles offlineFilesModel : offlineFileList)
                                            {
                                                CommonFunctions.deleteFileFromInternalStorage(offlineFilesModel.getFilePath());
                                            }

                                        }

                                        offLine_files_repository.deleteAlreadydownloadedFileBasedUPonCondition(category.getDocument_version_id(), category.getObject_id());

                                    }

                                }
                            }
                            else if(deleteMode.equals("2"))
                            {
                                for(GetCategoryDocumentsResponse category : GlobalVariables.selectedDocumentsList)
                                {
                                    if (category.getType().equalsIgnoreCase("document"))
                                    {
                                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);

                                        List<OfflineFiles> offlineFileList = offLine_files_repository.getFilePathFromLocalTableBasedOnVersionId(category.getObject_id());

                                        if(offlineFileList != null && offlineFileList.size() > 0)
                                        {
                                            for(OfflineFiles offlineFilesModel : offlineFileList)
                                            {
                                                CommonFunctions.deleteFileFromInternalStorage(offlineFilesModel.getFilePath());
                                            }

                                        }

                                        offLine_files_repository.deleteAlreadydownloadedFileBasedOnVersionId(category.getObject_id());

                                    }
                                }
                            }

                        }
                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                }
            });
        }
    }


    public void refreshMyFolderDMS()
    {
        resetPageNumber();
        getCategoryDocuments();
        toggleEmptyState();
    }

    public void toggleEmptyState()
    {
        no_documents_txt.setText("");
        if(mGetCategoryDocumentsResponses != null && mGetCategoryDocumentsResponses.size() > 0)
        {
            empty_view.setVisibility(View.GONE);
            if(GlobalVariables.isMoveInitiated)
            {
                sorting_layout.setVisibility(View.GONE);
            }
            else
            {
                sorting_layout.setVisibility(View.VISIBLE);
            }

        }
        else
        {
            empty_view.setVisibility(View.VISIBLE);
            no_documents_txt.setText("No files found.");
            sorting_layout.setVisibility(View.GONE);
        }
    }


    public void showFailedUploadList()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);

        Button BtnAllow = (Button) view.findViewById(R.id.allow_button);
        BtnAllow.setText("Ok");
        Button BtnCancel = (Button) view.findViewById(R.id.cancel_button);
        BtnCancel.setText("Cancel");
        BtnCancel.setVisibility(View.GONE);
        TextView textView =(TextView) view.findViewById(R.id.txt_message);
        textView.setVisibility(View.GONE);
        TextView text = (TextView) view.findViewById(R.id.message);
       // text.setText("One or more files not uploaded");
        text.setText(getString(R.string.upload_failed_txt));

        BtnAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a click listener for separate like Implementing clickListener

                if(mCustomAlertDialog != null)
                {
                    mCustomAlertDialog.dismiss();
                }

                gotoUploadListActivity();

            }
        });

        mCustomAlertDialog = builder.create();
        mCustomAlertDialog.show();
    }

    private void gotoUploadListActivity()
    {
        Intent intent = new Intent(NavigationMyFolderActivity.this, UploadListActivity.class);
        startActivity(intent);
    }

    public void cameraAccess()
    {
        floatingActionMenu.close(true);

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

    }




    public void cameraAndStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA);
            int storagePermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED) {

                cameraAccess();

            } else if (permission == PackageManager.PERMISSION_GRANTED && storagePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            } else if (permission != PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            }
        } else {
            cameraAccess();
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 1) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                        showVideoOrCameraAccess();

                    } else {
                        if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                            floatingActionMenu.close(true);
                            Toast.makeText(context, "Camera and storage access permission denied", Toast.LENGTH_LONG).show();
                        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                            floatingActionMenu.close(true);
                            Toast.makeText(context, "Storage access permission denied", Toast.LENGTH_LONG).show();
                        } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                            floatingActionMenu.close(true);
                            Toast.makeText(context, "Camera access permission denied", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        showVideoOrCameraAccess();
                    } else {
                        floatingActionMenu.close(true);
                        Toast.makeText(context, "Camera access permission denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    documentUpload();
                } else {
                    floatingActionMenu.close(true);
                    Toast.makeText(context, "Storage access permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            case IMAGE_DOWNLOAD_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AccountSettings accountSettings = new AccountSettings(NavigationMyFolderActivity.this);
                    String companyName = accountSettings.getCompanyName();
                    downloadLogoImage(Constants.LOGO_IMAGE_BASE_URL+Constants.Logo_ImagePath+companyName+Constants.Splash_Logo_Image_Name, companyName);
                } else {
                    Toast.makeText(context, "Storage access permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void showVideoOrCameraAccess()
    {
        if(isVideo)
        {
            videoAccess();
        }
        else
        {
            cameraAccess();
        }
    }

    public void showUnAuthorizedMessageAlert(String mMessage)
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

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }



    @Override
    public void onRefresh() {

        GlobalVariables.isMultiSelect = false;
        if (NetworkUtils.checkIfNetworkAvailable(this)) {
            List<GetCategoryDocumentsResponse> dummyList = new ArrayList<>();
            updateToolbarMenuItems(dummyList);
            mRefreshLayout.setRefreshing(false);
            searchIconDisplay();
                refreshMyFolderDMS();

        } else {
            mRefreshLayout.setRefreshing(false);
            mRefreshLayout.setVisibility(View.GONE);
            internetUnAvailableWithMessage();
        }
    }


    public void showInternalShareAlertMessage()
    {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);
        txtMessage.setText(context.getString(R.string.internal_share_txt));
        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("Cancel");

        okButton.setText("Ok");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                GlobalVariables.isMoveInitiated = true;
                GlobalVariables.selectedActionName =  "share";
                Intent intent = new Intent(context, NavigationSharedActivity.class);
                intent.putExtra("ObjectId", "0");
                startActivity(intent);

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }




    @Override
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras != null){
            if(extras.containsKey("IsFromUpload"))
            {
                String uploadString = extras.getString("IsFromUpload");
                if(uploadString != null && uploadString.equalsIgnoreCase("Upload"))
                {
                    refreshMyFolderDMS();
                }
            }
        }

    }





    RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            if (!recyclerView.canScrollVertically(1) && dy > 0) {
                if(pageNumber + 1 <= totalPages) {
                    getCategoryDocuments();
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


    public void internetUnAvailableWithMessage()
    {
        empty_view.setVisibility(View.VISIBLE);
        no_documents_txt.setText(getString(R.string.internet_failure_txt));
        sorting_layout.setVisibility(View.GONE);
        setLinkTextView();
    }

    public void setLinkTextView()
    {
        SpannableString spannableString = new SpannableString(getString(R.string.internet_failure_txt));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent mIntent = new Intent(context, OffLine_Files_List.class);
                startActivity(mIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // set to false to remove underline
            }
        };
       spannableString.setSpan(clickableSpan, 53,
                63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        no_documents_txt.setText(spannableString,
                TextView.BufferType.SPANNABLE);
        no_documents_txt.setMovementMethod(LinkMovementMethod.getInstance());

    }


    private void openFolderUploadBottomSheet()
    {
        TextView camera, video, pick_image,pick_documents, pick_video, cancel, folder_creation;
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_upload, null);
        camera = (TextView) view.findViewById(R.id.camera);
        video = (TextView) view.findViewById(R.id.video);
        pick_image = (TextView) view.findViewById(R.id.pick_image);
        pick_documents = (TextView) view.findViewById(R.id.pick_documents);
        pick_video = (TextView) view.findViewById(R.id.pick_video);
        cancel =(TextView) view.findViewById(R.id.cancel);
        folder_creation =(TextView) view.findViewById(R.id.folder_creation);

        final Dialog mBottomSheetDialog = new Dialog(NavigationMyFolderActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();


        if (objectId.equals("0") || GlobalVariables.isMoveInitiated){
            camera.setVisibility(View.GONE);
            video.setVisibility(View.GONE);
            pick_image.setVisibility(View.GONE);
            pick_video.setVisibility(View.GONE);
            pick_documents.setVisibility(View.GONE);
        }
        else
        {
            camera.setVisibility(View.VISIBLE);
            video.setVisibility(View.VISIBLE);
            pick_image.setVisibility(View.VISIBLE);
            pick_video.setVisibility(View.VISIBLE);
            pick_documents.setVisibility(View.VISIBLE);

        }


        folder_creation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.newfolder, null);
                builder.setView(view1);
                builder.setCancelable(false);

                Button cancel = (Button) view1.findViewById(R.id.cancel_b);
                Button allow = (Button) view1.findViewById(R.id.allow);
                final EditText namer = (EditText) view1.findViewById(R.id.edit_username1);
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(45);
                namer.setFilters(FilterArray);
                namer.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                allow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String folder_name = namer.getText().toString().trim();

                        if(folder_name == null || folder_name.isEmpty())
                        {
                            Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show();
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
                                    transparentProgressDialog.dismiss();
                                    if (apiResponse != null) {

                                        String message = "";
                                        if(apiResponse.status.getMessage() != null)
                                        {
                                            message = apiResponse.status.getMessage().toString();
                                        }

                                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.status.getCode())) {
                                            resetPageNumber();
                                            getCategoryDocuments();
                                        }

                                    }
                                    else
                                    {
                                        CommonFunctions.serverErrorExceptions(context, response.code());

                                    }
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    transparentProgressDialog.dismiss();
                                    CommonFunctions.showTimeOutError(context, t);
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


            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                isVideo = false;
                cameraAndStoragePermission();
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                isVideo = true;
                cameraAndStoragePermissionForVideo();
            }
        });
        pick_image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                Intent intent1 = new Intent(NavigationMyFolderActivity.this, ImagePickActivity.class);
                intent1.putExtra(IS_NEED_CAMERA, false);
                intent1.putExtra(IS_NEED_FOLDER_LIST, false);
                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);

            }
        });

        pick_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                Intent intent2 = new Intent(context, VideoPickActivity.class);
                intent2.putExtra(IS_NEED_CAMERA, false);
                intent2.putExtra(IS_NEED_FOLDER_LIST, false);
                startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);
            }
        });


        pick_documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();

                List<String> fileformats = PreferenceUtils.getFileFormats(context, "key");

                ArrayList<String> remainingFileformats = new ArrayList<>();
                if(fileformats != null && fileformats.size() > 0)
                {
                    for(String fileformat : fileformats)
                    {
                        fileformat = "."+fileformat;
                        String extension = MimeTypeMap.getFileExtensionFromUrl(fileformat);
                        String type = null;
                        if (extension != null) {
                            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            if((type != null && type.contains("image")) || (type != null && type.contains("video")))   {
                            }
                            else
                            {
                                remainingFileformats.add(fileformat);
                            }
                        }
                    }

                }

                if(remainingFileformats != null && remainingFileformats.size() > 0)
                {
                    String[] documentArray = new String[remainingFileformats.size()];
                    documentArray = remainingFileformats.toArray(documentArray);
                    Intent intent4 = new Intent(context, NormalFilePickActivity.class);
                    intent4.putExtra(IS_NEED_FOLDER_LIST, false);
                    intent4.putExtra(NormalFilePickActivity.SUFFIX, documentArray);
                    startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                }

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();

            }
        });
    }


    public void checkMultipleSharedLevel()
    {
        if (NetworkUtils.isNetworkAvailable(context)) {
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

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.status.getCode()))
                        {
                            List<GetEndUserSharedParentFoldersResponse> sharedParentList = response.body().getData();

                            if(sharedParentList != null && sharedParentList.size() == 1)
                            {
                                checkNextSharedLevelData(sharedParentList.get(0).getCategory_id(), sharedParentList.get(0).getWorkspace_id());
                            }
                            else if(sharedParentList != null && sharedParentList.size() > 1)
                            {
                                transparentProgressDialog.dismiss();
                                GlobalVariables.isMoveInitiated = true;
                                GlobalVariables.selectedActionName =  "share";
                                Intent intent = new Intent(context, NavigationSharedActivity.class);
                                intent.putExtra("ObjectId", "0");
                                startActivity(intent);

                            }


                        }

                    }else {
                        transparentProgressDialog.dismiss();
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                    Log.d("Message", t.getMessage());
                }
            });
        }
    }

    private void checkNextSharedLevelData(String category_id, String workspace_id)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            GetEndUserAllowedSharedFoldersRequest mGetEndUserAllowedSharedFoldersRequest = new GetEndUserAllowedSharedFoldersRequest(workspace_id, category_id);

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
                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.status.getCode()))
                        {
                            List<GetEndUserSharedParentFoldersResponse> sharedParentAllowedList = response.body().getData();

                            if(sharedParentAllowedList != null && sharedParentAllowedList.size() == 1)
                            {
                                shareEndUserDocuments(sharedParentAllowedList.get(0).getCategory_id(), sharedParentAllowedList.get(0).getWorkspace_id() );
                              //  showSharedDocumentAlert(sharedParentAllowedList.get(0).getWorkspace_id(), sharedParentAllowedList.get(0).getCategory_id());
                            }
                            else if(sharedParentAllowedList != null && sharedParentAllowedList.size() > 1)
                            {
                                transparentProgressDialog.dismiss();
                                GlobalVariables.isMoveInitiated = true;
                                GlobalVariables.selectedActionName =  "share";
                                Intent intent = new Intent(context, NavigationSharedActivity.class);
                                intent.putExtra("ObjectId", "0");
                                startActivity(intent);
                            }

                        }

                    }
                    else {
                        transparentProgressDialog.dismiss();
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                    Log.d("Message", t.getMessage());
                }
            });

        }
    }

    private void showSharedDocumentAlert(String workspace_id, String category_id)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Share");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);
        txtMessage.setText("Are you sure?");

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("Cancel");

        okButton.setText("Share");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void shareEndUserDocuments(String category_id, String workspace_id)
    {
        Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();


        ArrayList<String> document_ids = new ArrayList<>();
        if(GlobalVariables.selectedDocumentsList.size() > 0)
        {
            for(GetCategoryDocumentsResponse categoryDocumentsResponse : GlobalVariables.selectedDocumentsList)
            {
                document_ids.add(categoryDocumentsResponse.getObject_id());
            }
        }

        ShareEndUserDocumentsRequest mShareEndUserDocumentsRequest = new ShareEndUserDocumentsRequest(document_ids,workspace_id, category_id);

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
                transparentProgressDialog.dismiss();
                if (apiResponse != null) {

                    String message = "";
                    if(apiResponse.status.getMessage() != null)
                    {
                        message = apiResponse.status.getMessage().toString();
                    }

                    if(CommonFunctions.isApiSuccess(NavigationMyFolderActivity.this, message, apiResponse.status.getCode()))
                    {
                        GlobalVariables.isMoveInitiated = false;
                        GlobalVariables.selectedActionName = "";
                        GlobalVariables.selectedDocumentsList.clear();

                        refreshMyFolderDMS();

                        /*Intent intent = new Intent(context, NavigationMyFolderActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("IsFromUpload", "Upload");
                        startActivity(intent);
                        finish();*/

                    }


                }
                else {
                    CommonFunctions.serverErrorExceptions(context, response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                transparentProgressDialog.dismiss();
                CommonFunctions.showTimeOutError(context, t);

            }
        });
    }

    public void downloadLogoImage(String imageUrl, String companyName)
    {
        if (!TextUtils.isEmpty(imageUrl)) {
            FileDownloadManager fileDownloadManager = new FileDownloadManager(NavigationMyFolderActivity.this);
            GetCategoryDocumentsResponse categoryDocumentsResponse = new GetCategoryDocumentsResponse();
            categoryDocumentsResponse.setDownloadUrl(imageUrl);
            categoryDocumentsResponse.setDocument_version_id("123456");
            categoryDocumentsResponse.setName("Logo");

            fileDownloadManager.setFileTitle("Logo");
            fileDownloadManager.setDownloadUrl(imageUrl);
            fileDownloadManager.setDigitalAssets(categoryDocumentsResponse);
            fileDownloadManager.setmFileDownloadListener(new FileDownloadManager.FileDownloadListener() {
                @Override
                public void fileDownloadSuccess(String path) {

                    if(path != null && !path.isEmpty())
                    {
                        PreferenceUtils.setSplashLogoImagePath(context, path);
                    }

                    if(PreferenceUtils.getSettingsLogoImagePath(context) == null || PreferenceUtils.getSettingsLogoImagePath(context).isEmpty())
                    {
                        downloadSettingsLogoImage(Constants.LOGO_IMAGE_BASE_URL+Constants.Logo_ImagePath+companyName+
                                Constants.Settings_Logo_Image_Name);
                    }
                }

                @Override
                public void fileDownloadFailure() {

                }
            });
            fileDownloadManager.downloadTheFile();
        }
    }

    private void downloadSettingsLogoImage(String imageUrl)
    {
        if (!TextUtils.isEmpty(imageUrl)) {
            FileDownloadManager fileDownloadManager = new FileDownloadManager(NavigationMyFolderActivity.this);
            GetCategoryDocumentsResponse categoryDocumentsResponse = new GetCategoryDocumentsResponse();
            categoryDocumentsResponse.setDownloadUrl(imageUrl);
            categoryDocumentsResponse.setDocument_version_id("67890");
            categoryDocumentsResponse.setName("Logo");

            fileDownloadManager.setFileTitle("Logo");
            fileDownloadManager.setDownloadUrl(imageUrl);
            fileDownloadManager.setDigitalAssets(categoryDocumentsResponse);
            fileDownloadManager.setmFileDownloadListener(new FileDownloadManager.FileDownloadListener() {
                @Override
                public void fileDownloadSuccess(String path) {

                    if(path != null && !path.isEmpty())
                    {
                        PreferenceUtils.setSettingsLogoImagePath(context, path);
                    }
                }

                @Override
                public void fileDownloadFailure() {

                }
            });
            fileDownloadManager.downloadTheFile();
        }
    }


   /* private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mBroadcastStringAction)) {
                Toast.makeText(NavigationMyFolderActivity.this,"Upload completed", Toast.LENGTH_LONG).show();
                stopService(new Intent(NavigationMyFolderActivity.this, BackgroundUploadService.class));
            }

           *//* Bundle bundle = intent.getExtras();
            if (bundle != null) {
                //  String string = bundle.getString(DownloadService.FILEPATH);
                int resultCode = bundle.getInt("Result");
                if (resultCode == RESULT_OK) {
                    Toast.makeText(NavigationMyFolderActivity.this,"Upload completed", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(NavigationMyFolderActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                    stopService(new Intent(NavigationMyFolderActivity.this, BackgroundUploadService.class));
                }
            }*//*
        }
    };*/

    /*@Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }
*/


    private void uploadValidation()
    {
        List<UploadModel> userSharedDocList =  GlobalVariables.otherAppDocumentList;
        List<String> fileSizeExceedList = new ArrayList<>();
        List<UploadModel> belowSizeFileList = new ArrayList<>();
        if(userSharedDocList != null && userSharedDocList.size() > 0)
        {
            String size = PreferenceUtils.getMaxSizeUpload(NavigationMyFolderActivity.this);
            float sizeAPI = Float.parseFloat(size);
            for(UploadModel fileItem : userSharedDocList)
            {
                File file = new File(fileItem.getFilePath());
                float file_size = Float.parseFloat(String.valueOf(file.length() / 1024 / 1024));
                if (file_size > sizeAPI) {
                    fileSizeExceedList.add(fileItem.getFilePath());
                }
                else
                {
                    UploadModel uploadModel = new UploadModel();
                    uploadModel.setFilePath(fileItem.getFilePath());
                    belowSizeFileList.add(uploadModel);
                }
            }
        }


      //  List<UploadModel> filteredUploadList = GlobalVariables.otherAppDocumentList;

        if(belowSizeFileList != null && belowSizeFileList.size() > 0)
        {
            if(fileSizeExceedList != null && fileSizeExceedList.size() > 0)
            {
                List<String> fileNameList = new ArrayList<>();
                fileNameList.clear();
                for(String filePath : fileSizeExceedList)
                {
                    File fileData = new File(filePath);
                    fileNameList.add(fileData.getName());

                }
                String joinedString = TextUtils.join(", ", fileNameList);
                Toast.makeText(context, joinedString+ " file(s) exceed more than "+PreferenceUtils.getMaxSizeUpload(context) + " MB", Toast.LENGTH_SHORT).show();
            }

            List<UploadModel> filteredDataList = new ArrayList<>();
            for(UploadModel fileName : belowSizeFileList)
            {
                String pathName = fileName.getFilePath().substring(fileName.getFilePath().lastIndexOf(".")+1);
                if(pathName != null && !pathName.equalsIgnoreCase(fileName.getFilePath()))
                {
                    UploadModel uploadModel = new UploadModel();
                    uploadModel.setFilePath(fileName.getFilePath());
                    filteredDataList.add(uploadModel);

                }

            }

            ArrayList<String> fileFormatList = PreferenceUtils.getFileFormats(NavigationMyFolderActivity.this, "key");

            List<UploadModel> OriginalUploadList = new ArrayList<>();
          //  List<String> unSupportedFormatList = new ArrayList<>();
            if(filteredDataList != null && filteredDataList.size() > 0)
            {
                for(UploadModel fileItem : filteredDataList)
                {
                    String[] fileParts = fileItem.getFilePath().split("\\.");
                    String fileExtension = fileParts[fileParts.length - 1];

                    for(String fileFormat : fileFormatList)
                    {
                        if(fileExtension.equalsIgnoreCase(fileFormat))
                        {
                            UploadModel uploadModel = new UploadModel();
                            uploadModel.setFilePath(fileItem.getFilePath());
                            OriginalUploadList.add(uploadModel);
                        }
                    }
                }

            }

            if(OriginalUploadList != null && OriginalUploadList.size() > 0)
            {
               /* if(unSupportedFormatList != null && unSupportedFormatList.size() > 0)
                {
                    List<String> fileNameList = new ArrayList<>();
                    fileNameList.clear();
                    for(String filePath : unSupportedFormatList)
                    {
                        File fileData = new File(filePath);
                        fileNameList.add(fileData.getName());

                    }
                    String joinedString = TextUtils.join(", ", fileNameList);
                    Toast.makeText(context, joinedString +" file format(s) not supported", Toast.LENGTH_SHORT).show();
                }*/

                List<UploadModel> uploadedDAta = OriginalUploadList;
                Intent intent1 = new Intent(NavigationMyFolderActivity.this, BackgroundUploadService.class);
                intent1.putExtra("UploadedList", (ArrayList<UploadModel>)uploadedDAta);
                startService(intent1);
                callExitApp();

            }
            else
            {
                /*List<String> fileNameList = new ArrayList<>();
                fileNameList.clear();
                for(String filePath : unSupportedFormatList)
                {
                    File fileData = new File(filePath);
                    fileNameList.add(fileData.getName());
                }

                String joinedString = TextUtils.join(", ", fileNameList);*/
                Toast.makeText(context, "File format(s) not supported", Toast.LENGTH_SHORT).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 5000ms
                        callExitApp();
                    }
                }, 5000);

            }
        }
        else if(fileSizeExceedList != null && fileSizeExceedList.size() > 0)
        {
            List<String> fileNameList = new ArrayList<>();
            fileNameList.clear();
            for(String filePath : fileSizeExceedList)
            {
                File fileData = new File(filePath);
                fileNameList.add(fileData.getName());

            }
            String joinedString = TextUtils.join(", ", fileNameList);
            Toast.makeText(context, joinedString+ " file(s) exceed more than "+PreferenceUtils.getMaxSizeUpload(context) + " MB", Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 5000ms
                    callExitApp();
                }
            }, 5000);

        }
    }

    private void callExitApp() {
        cancel_textview.performClick();
        finishAffinity();
        System.exit(0);
    }

}
