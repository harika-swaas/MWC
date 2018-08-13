package com.swaas.mwc.DMS;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;
import com.michaelflisar.dragselectrecyclerview.DragSelectionProcessor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.DownloadDocumentRequest;
import com.swaas.mwc.API.Model.DownloadDocumentResponse;
import com.swaas.mwc.API.Model.GetCategoryDocumentsRequest;
import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.UploadEndUserDocumentsRequest;
import com.swaas.mwc.API.Model.UploadEndUsersDocumentResponse;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.API.Service.DownloadDocumentService;
import com.swaas.mwc.API.Service.GetCategoryDocumentsService;
import com.swaas.mwc.API.Service.UploadEndUsersDocumentService;
import com.swaas.mwc.Adapters.DmsAdapter;
import com.swaas.mwc.Adapters.DmsAdapterList;
import com.swaas.mwc.Common.CameraUtils;
import com.swaas.mwc.Common.ServerConfig;
import com.swaas.mwc.Common.SimpleDividerItemDecoration;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Fragments.ItemNavigationFolderFragment;
import com.swaas.mwc.Fragments.ItemNavigationSettingsFragment;
import com.swaas.mwc.Fragments.ItemNavigationSharedFragment;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.RootActivity;
import com.swaas.mwc.Utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by harika on 11-07-2018.
 */

public class MyFoldersDMSActivity extends RootActivity {

    List<DownloadDocumentResponse> downloadDocumentResponse;
    static Boolean isTouched = false;
    public static final int FOLDER_FRAGMENT = 1;
    public static final int SHARED_FRAGMENT = 2;
    public static final int SETTINGS_FRAGMENT = 3;
    BottomNavigationView mBottomNavigationView;
    public static LinearLayout sortingView;
    int mSelectedItem = SHARED_FRAGMENT;
    Menu mBottomNavigationMenu;
    MenuItem mFolderMenuItem, mSharedMenuItem, mSettingsMenuItem;
    ItemNavigationFolderFragment mFolderFragment;
    ItemNavigationSharedFragment mSharedFragment;
    ItemNavigationSettingsFragment mSettingsFragment;
    ImageView select;
    public static RelativeLayout toggleView;
    public static ImageView toggle;
    Button button;
    boolean check = false;
    public boolean isFromList = false;
    int backButtonCount = 0;
    CollapsingToolbarLayout collapsingToolbarLayout;
    boolean sortByName = false;
    boolean sortByNewest = false;
    boolean sortBySize = false;
    boolean sortByDate = false;
    boolean sortByNameAsc = true;
    boolean sortByTypeAsc = true;
    boolean sortBySizeAsc = true;
    boolean sortByDateAsc = true;
    boolean isSortByDefault = true;
    List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    List<GetCategoryDocumentsResponse> listGetCategoryDocuments = new ArrayList<>();
    List<GetCategoryDocumentsResponse> mSelectedDocumentList = new ArrayList<>();
 //   RecyclerView mRecyclerView;
    DmsAdapter mAdapter;
    DmsAdapterList mAdapterList;
    RelativeLayout indicatorParentView;
    private DragSelectionProcessor.Mode mMode = DragSelectionProcessor.Mode.Simple;
    private DragSelectTouchListener mDragSelectTouchListener;
    private DragSelectionProcessor mDragSelectionProcessor;
    MenuItem menuItemSearch, menuItemDelete, menuItemShare, menuItemMove, menuItemMore;
    public static FloatingActionMenu floatingActionMenu;
    FloatingActionButton actionUpload, actionCamera, actionNewFolder, actionVideo;
    Uri fileUri;
    Uri uriVideo;
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int REQUEST_CAPTURE_IMAGE_CODE = 300;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 400;
    private static String imageStoragePath;
    // Gallery directory name to store the images or videos
    public static final String GALLERY_DIRECTORY_NAME = "Hello Camera";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    // Image and Video file extensions
    public static final String IMAGE_EXTENSION = "jpg";
    public static final String VIDEO_EXTENSION = "mp4";
    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.

    private int currentPage = PAGE_START;
    LinearLayoutManager linearLayoutManager;
    public static TextView sort;
    String imageEncoded;
    List<WhiteLabelResponse> mWhiteLabelResponses;
 //   NestedScrollView scrollView;
    int pageNumber = 0;
    int totalPages=1;
    String obj="0";
    Context context=this;
    List<GetCategoryDocumentsResponse> paginationList = new ArrayList<>();
    public static LinearLayout title_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_folders_dms_fragment);

        intializeViews();
     //   mRecyclerView.setNestedScrollingEnabled(false);
        loadBottomNavigation();
        // switchFragment(FOLDER_FRAGMENT);
     //   getCategoryDocuments("0",String.valueOf(pageNumber));
        getWhiteLabelProperities();

        addListenersToViews();

        if (savedInstanceState == null) {
            mBottomNavigationView.setSelectedItemId(R.id.navigation_folder); // change to whichever id should be default
        }



    }



    private void intializeViews() {

    //    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_dms);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        toggle = (ImageView) findViewById(R.id.toggle);
        sortingView = (LinearLayout) findViewById(R.id.sort);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        actionUpload = (FloatingActionButton) findViewById(R.id.menu_upload_item);
        actionCamera = (FloatingActionButton) findViewById(R.id.menu_camera_item);
        actionNewFolder = (FloatingActionButton) findViewById(R.id.menu_new_folder_item);
        actionVideo = (FloatingActionButton) findViewById(R.id.menu_camera_video_item);
        sort = (TextView) findViewById(R.id.name_sort);
        toggleView = (RelativeLayout) findViewById(R.id.toggle_view);
        indicatorParentView=(RelativeLayout) findViewById(R.id.nameIndicatorParentView);
    //    scrollView = (NestedScrollView) findViewById(R.id.nest_scrollview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back));

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.my_folder));

        title_layout = (LinearLayout) findViewById(R.id.l1);

        toolbarTextAppernce();
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, MyFoldersDMSActivity.this);
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
                Log.d(TAG, "Oops! Failed create " + ServerConfig.IMAGE_DIRECTORY_NAME + " directory");
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

    private void addListenersToViews() {

        actionUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openGalleryIntent.setType("*/*");
                openGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
            }
        });

        actionCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(1);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE_CODE);
            }
        });

        actionVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                fileUri = Uri.fromFile(mediaFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);*/

                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_VIDEO);
                if (file != null) {
                    imageStoragePath = file.getAbsolutePath();
                }

                Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);

                // set video quality
                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
                startActivityForResult(takeVideoIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
            }
        });

       /* sortingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });

        toggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check == false) {
                    toggle.setImageResource(R.mipmap.ic_list);
                 //   setListAdapterToView(mGetCategoryDocumentsResponses);
                    isFromList = true;
                    mAdapter.notifyDataSetChanged();
                    check = true;

                } else {
                    toggle.setImageResource(R.mipmap.ic_grid);
                  //  setGridAdapterToView(mGetCategoryDocumentsResponses);
                    mAdapter.notifyDataSetChanged();
                    isFromList = false;
                    check = false;
                }
            }
        });*/
    }



    /*private void loadNextPage() {
        if (NetworkUtils.isNetworkAvailable(this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(this);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(0, "list", "category", "1", "0");

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);
            params.put("page","2");

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = mGetCategoryDocumentsService.getCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(this),"1");

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
                                setGridAdapterToView(mGetCategoryDocumentsResponses);
                                if(response.headers() != null)
                                {
                                    totalPages = Integer.parseInt(response.headers().get("X-Pagination-Page-Count"));
                                    pageNumber = Integer.parseInt(response.headers().get("X-Pagination-Current-Page"));
                                }
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MyFoldersDMSActivity.this);
                                LayoutInflater inflater = (LayoutInflater) MyFoldersDMSActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(MyFoldersDMSActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(MyFoldersDMSActivity.this, LoginActivity.class));
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

    }*/

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

        final Dialog mBottomSheetDialog = new Dialog(MyFoldersDMSActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        if (sortByName == true) {
            sortNameImage.setVisibility(View.VISIBLE);
            sortNewestImage.setVisibility(View.INVISIBLE);
            sortSizeImage.setVisibility(View.INVISIBLE);
            sortDateImage.setVisibility(View.INVISIBLE);

            sortNameDoneImage.setVisibility(View.VISIBLE);
            sortNewestDoneImage.setVisibility(View.INVISIBLE);
            sortSizeDoneImage.setVisibility(View.INVISIBLE);
            sortDateDoneImage.setVisibility(View.INVISIBLE);
        } else if (sortByNewest == true) {
            sortNameImage.setVisibility(View.INVISIBLE);
            sortNewestImage.setVisibility(View.VISIBLE);
            sortSizeImage.setVisibility(View.INVISIBLE);
            sortDateImage.setVisibility(View.INVISIBLE);

            sortNameDoneImage.setVisibility(View.INVISIBLE);
            sortNewestDoneImage.setVisibility(View.VISIBLE);
            sortSizeDoneImage.setVisibility(View.INVISIBLE);
            sortDateDoneImage.setVisibility(View.INVISIBLE);
        } else if (sortBySize == true) {
            sortNameImage.setVisibility(View.INVISIBLE);
            sortNewestImage.setVisibility(View.INVISIBLE);
            sortSizeImage.setVisibility(View.VISIBLE);
            sortDateImage.setVisibility(View.INVISIBLE);

            sortNameDoneImage.setVisibility(View.INVISIBLE);
            sortNewestDoneImage.setVisibility(View.INVISIBLE);
            sortSizeDoneImage.setVisibility(View.VISIBLE);
            sortDateDoneImage.setVisibility(View.INVISIBLE);
        } else if (sortByDate == true) {
            sortNameImage.setVisibility(View.INVISIBLE);
            sortNewestImage.setVisibility(View.INVISIBLE);
            sortSizeImage.setVisibility(View.INVISIBLE);
            sortDateImage.setVisibility(View.VISIBLE);

            sortNameDoneImage.setVisibility(View.INVISIBLE);
            sortNewestDoneImage.setVisibility(View.INVISIBLE);
            sortSizeDoneImage.setVisibility(View.INVISIBLE);
            sortDateDoneImage.setVisibility(View.VISIBLE);
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
            //    pageNumber=0;
                totalPages=1;
                mGetCategoryDocumentsResponses.clear();
                listGetCategoryDocuments.clear();

                getCategoryDocumentsSortByName("1");
           //     scrollView.fullScroll(ScrollView.FOCUS_UP);

            }
        });

        mSortByNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByNewest = true;
                sortByName = false;
                sortBySize = false;
                sortByDate = false;

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
                getCategoryDocumentsSortByNewest("1");
           //     scrollView.fullScroll(ScrollView.FOCUS_UP);

            }
        });

        mSortBySize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortBySize = true;
                sortByNewest = false;
                sortByName = false;
                sortByDate = false;

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
                getCategoryDocumentsSortBySize("1");
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
                getCategoryDocumentsSortByDate("1");
            //    scrollView.fullScroll(ScrollView.FOCUS_UP);


            }
        });
    }

    private void getCategoryDocumentsSortByDate(String page) {

        if (NetworkUtils.isNetworkAvailable(this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(this);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            if (PreferenceUtils.getObjectId(MyFoldersDMSActivity.this).equalsIgnoreCase("")) {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(0, "list", "category", "1", "0");
            } else {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(Integer.parseInt(PreferenceUtils.getObjectId(MyFoldersDMSActivity.this)), "list", "category", "1", "0");
            }

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(sortByDateAsc == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByDate(params, PreferenceUtils.getAccessToken(this),page);
                sortByDateAsc = false;
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByDateDesc(params, PreferenceUtils.getAccessToken(this),page);
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
                               //     setListAdapterToView(mGetCategoryDocumentsResponses);
                                } else {
                                //    setGridAdapterToView(mGetCategoryDocumentsResponses);
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
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MyFoldersDMSActivity.this);
                                LayoutInflater inflater = (LayoutInflater) MyFoldersDMSActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(MyFoldersDMSActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(MyFoldersDMSActivity.this, LoginActivity.class));
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

        if (NetworkUtils.isNetworkAvailable(this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(this);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            if (PreferenceUtils.getObjectId(MyFoldersDMSActivity.this).equalsIgnoreCase("")) {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(0, "list", "category", "1", "0");
            } else {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(Integer.parseInt(PreferenceUtils.getObjectId(MyFoldersDMSActivity.this)), "list", "category", "1", "0");
            }

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(sortBySizeAsc == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortBySize(params, PreferenceUtils.getAccessToken(this),page);
                sortBySizeAsc = false;
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortBySizeDesc(params, PreferenceUtils.getAccessToken(this),page);
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
                                //    setListAdapterToView(mGetCategoryDocumentsResponses);
                                } else {
                                //    setGridAdapterToView(mGetCategoryDocumentsResponses);
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
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MyFoldersDMSActivity.this);
                                LayoutInflater inflater = (LayoutInflater) MyFoldersDMSActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(MyFoldersDMSActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(MyFoldersDMSActivity.this, LoginActivity.class));
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
        if (NetworkUtils.isNetworkAvailable(this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(this);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            if (PreferenceUtils.getObjectId(MyFoldersDMSActivity.this).equalsIgnoreCase("")) {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(0, "list", "category", "1", "0");
            } else {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(Integer.parseInt(PreferenceUtils.getObjectId(MyFoldersDMSActivity.this)), "list", "category", "1", "0");
            }

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(sortByTypeAsc == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByType(params, PreferenceUtils.getAccessToken(this),page);
                sortByTypeAsc = false;
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByTypeDesc(params, PreferenceUtils.getAccessToken(this),page);
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
                                 //   setListAdapterToView(mGetCategoryDocumentsResponses);
                                } else {
                                 //   setGridAdapterToView(mGetCategoryDocumentsResponses);
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
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MyFoldersDMSActivity.this);
                                LayoutInflater inflater = (LayoutInflater) MyFoldersDMSActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(MyFoldersDMSActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(MyFoldersDMSActivity.this, LoginActivity.class));
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

    private void getCategoryDocumentsSortByName(String page) {

        if (NetworkUtils.isNetworkAvailable(this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(this);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            if (PreferenceUtils.getObjectId(MyFoldersDMSActivity.this).equalsIgnoreCase("")) {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(0, "list", "category", "1", "0");
            } else {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(Integer.parseInt(PreferenceUtils.getObjectId(MyFoldersDMSActivity.this)), "list", "category", "1", "0");
            }

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(sortByNameAsc == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByName(params, PreferenceUtils.getAccessToken(this),page);
                sortByNameAsc = false;
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByNameDesc(params, PreferenceUtils.getAccessToken(this),page);
                sortByNameAsc = true;
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
                                //    setListAdapterToView(mGetCategoryDocumentsResponses);
                                } else {
                                //    setGridAdapterToView(mGetCategoryDocumentsResponses);
                                }
                                totalPages = Integer.parseInt(response.headers().get("X-Pagination-Page-Count"));
                                pageNumber = Integer.parseInt(response.headers().get("X-Pagination-Current-Page"));
                               // pageNumber= pageNumber+1;
                            }
                            sort.setText("Name");

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {

                                final AlertDialog.Builder builder = new AlertDialog.Builder(MyFoldersDMSActivity.this);
                                LayoutInflater inflater = (LayoutInflater) MyFoldersDMSActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(MyFoldersDMSActivity.this);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(MyFoldersDMSActivity.this, LoginActivity.class));
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

    private void loadBottomNavigation() {

        mBottomNavigationMenu = mBottomNavigationView.getMenu();
        mFolderMenuItem = mBottomNavigationMenu.findItem(R.id.navigation_folder);
        mSharedMenuItem = mBottomNavigationMenu.findItem(R.id.navigation_shared);
        mSettingsMenuItem = mBottomNavigationMenu.findItem(R.id.navigation_settings);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_folder:
                        switchFragment(FOLDER_FRAGMENT);
                        break;
                    case R.id.navigation_shared:
                        switchFragment(SHARED_FRAGMENT);
                        break;
                    case R.id.navigation_settings:
                        switchFragment(SETTINGS_FRAGMENT);
                        break;
                }
                return false;
            }
        });
    }

    private void switchFragment(int fragment) {
        getSupportFragmentManager().popBackStackImmediate();
        switch (fragment) {
            case 1:
                mSelectedItem = FOLDER_FRAGMENT;
                if (mFolderFragment == null) {
                    mFolderFragment = ItemNavigationFolderFragment.newInstance();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mFolderFragment).addToBackStack(null).commit();
                // set selected color
                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Unselected_Color();
                    int selectedColor = Color.parseColor(itemSelectedColor);
                    Drawable d = getResources().getDrawable(R.mipmap.ic_myfolder);
                    d.setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);

                //    mBottomNavigationView.setItemIconTintList(d);
                }


                break;

            case 2:
                mSelectedItem = SHARED_FRAGMENT;
                if (mSharedFragment == null) {
                    mSharedFragment = ItemNavigationSharedFragment.newInstance();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();

                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                    int selectedColor = Color.parseColor(itemSelectedColor);
                    Drawable d = getResources().getDrawable(R.mipmap.ic_shared);
                    d.setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
                }
                break;

            case 3:
                mSelectedItem = SETTINGS_FRAGMENT;
                if (mSettingsFragment == null) {
                    mSettingsFragment = ItemNavigationSettingsFragment.newInstance();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                    int selectedColor = Color.parseColor(itemSelectedColor);
                    Drawable d = getResources().getDrawable(R.mipmap.ic_settings);
                    d.setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
                }

                break;
        }
    }

    /*private void setGridAdapterToView(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        *//*Collections.sort(mGetCategoryDocumentsResponses, new Comparator<GetCategoryDocumentsResponse>() {
            @Override
            public int compare(GetCategoryDocumentsResponse lhs, GetCategoryDocumentsResponse rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });*//*

        // Setup the RecyclerView
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new DmsAdapter(getCategoryDocumentsResponses, mSelectedDocumentList, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(new DmsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapter.toggleSelection(position);
                mSelectedDocumentList.remove(mGetCategoryDocumentsResponses.get(position));

                updateToolbarMenuItems(mSelectedDocumentList);
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                // if one item is long pressed, we start the drag selection like following:
                // we just call this function and pass in the position of the first selected item
                // the selection processor does take care to update the positions selection mode correctly
                // and will correctly transform the touch events so that they can be directly applied to your adapter!!!
                menuItemDelete.setVisible(true);
                menuItemShare.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemSearch.setVisible(false);

                mDragSelectTouchListener.startDragSelection(position);

                GetCategoryDocumentsResponse documentsResponseObj = mGetCategoryDocumentsResponses.get(position);
                mSelectedDocumentList.add(documentsResponseObj);

                updateToolbarMenuItems(mSelectedDocumentList);

                return true;
            }
        });

        mDragSelectionProcessor = new DragSelectionProcessor(new DragSelectionProcessor.ISelectionHandler() {
            @Override
            public HashSet<Integer> getSelection() {
                return mAdapter.getSelection();
            }

            @Override
            public boolean isSelected(int index) {
                return mAdapter.getSelection().contains(index);
            }

            @Override
            public void updateSelection(int start, int end, boolean isSelected, boolean calledFromOnStart) {
                mAdapter.selectRange(start, end, isSelected);
            }
        })
                .withMode(mMode);
        mDragSelectTouchListener = new DragSelectTouchListener()
                .withSelectListener(mDragSelectionProcessor);
        updateSelectionListener();
        mRecyclerView.addOnItemTouchListener(mDragSelectTouchListener);
    }*/

    private void updateSelectionListener() {
        mDragSelectionProcessor.withMode(mMode);
        //  mToolbar.setSubtitle("Mode: " + mMode.name());
    }

    private void updateToolbarMenuItems(List<GetCategoryDocumentsResponse> mSelectedDocumentList) {

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

    public static void menuIconColor(MenuItem menuItem, int color) {
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public void onBackPressed() {
        if (backButtonCount >= 1) {
            backButtonCount = 0;
            moveTaskToBack(true);
        } else {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

   /* @Override
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
        int i = mAdapter.getArrayList().size();
        switch (item.getItemId()) {
            case android.R.id.home:
                i = i - 2;
                if (i > -1) {
                    String id = mAdapter.getArrayList().get(i);
                    mAdapter.getSubCategoryDocuments(id,String.valueOf(pageNumber));
                    mAdapter.setArrayList(mAdapter.getArrayList().size() - 1);

                } else {
                    startActivity(new Intent(MyFoldersDMSActivity.this, MyFoldersDMSActivity.class));
                    return true;

                }
                break;
            case R.id.action_more:
                openBottomSheetForMultiSelect();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

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

        ImageView clearSelectionImage = (ImageView) view.findViewById(R.id.clear_selection_image);
        ImageView copyImage = (ImageView) view.findViewById(R.id.copy_image);
        ImageView moveImage = (ImageView) view.findViewById(R.id.move_image);
        ImageView renameImage = (ImageView) view.findViewById(R.id.rename_image);
        ImageView shareImage = (ImageView) view.findViewById(R.id.share_image);
        ImageView availableOfflineImage = (ImageView) view.findViewById(R.id.available_offline_image);

        final Dialog mBottomSheetDialog = new Dialog(this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();


        moveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyFoldersDMSActivity.this,MyFolderActivity.class);
                startActivity(intent);

            }
        });

        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                        downloaddoc();
                    } else {
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
                String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                int selectedColor = Color.parseColor(itemSelectedColor);

                moveImage.setColorFilter(selectedColor);
                renameImage.setColorFilter(selectedColor);
            }
            moveLayout.setVisibility(View.VISIBLE);
            renameLayout.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            clearSelectionLayout.setVisibility(View.GONE);
            availableOfflineLayout.setVisibility(View.GONE);
            shareLayout.setVisibility(View.GONE);
            copyLayout.setVisibility(View.GONE);
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
        }

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MyFoldersDMSActivity.this, MyFolderSharedDocuments.class);
                mIntent.putExtra(Constants.OBJ, (Serializable) mSelectedDocumentList);
                startActivity(mIntent);
            }
        });
    }

    private void getWhiteLabelProperities() {

        AccountSettings accountSettings = new AccountSettings(MyFoldersDMSActivity.this);
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

    private void downloaddoc() {

        if (NetworkUtils.isNetworkAvailable(this)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DownloadDocumentService downloadDocumentService = retrofitAPI.create(DownloadDocumentService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(this);
            transparentProgressDialog.show();

            //DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(PreferenceUtils.getDocumentVersionId(this));
            List<String> strlist = new ArrayList<>();
            strlist.add("11917");
            DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(strlist);
            final String request = new Gson().toJson(downloadDocumentRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = downloadDocumentService.download(params, PreferenceUtils.getAccessToken(this));

            call.enqueue(new Callback<ListPinDevicesResponse<DownloadDocumentResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<DownloadDocumentResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                            transparentProgressDialog.dismiss();
                            downloadDocumentResponse = response.body().getData();
                        } else {

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

   /* public void setListAdapterToView(final List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mAdapterList = new DmsAdapterList(getCategoryDocumentsResponses, mSelectedDocumentList, MyFoldersDMSActivity.this);
        mRecyclerView.setAdapter(mAdapterList);

       *//* mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                *//**//*if (!mIsLoading && !mIsLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadNextPage();
                    }
                }*//**//*
            }
        });
*//*
        mAdapterList.setClickListener(new DmsAdapterList.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapterList.toggleSelection(position);

                menuItemDelete.setVisible(false);
                menuItemShare.setVisible(false);
                menuItemMore.setVisible(false);
                menuItemSearch.setVisible(true);

                mSelectedDocumentList.remove(mGetCategoryDocumentsResponses.get(position));

                updateToolbarMenuItems(mSelectedDocumentList);
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                // if one item is long pressed, we start the drag selection like following:
                // we just call this function and pass in the position of the first selected item
                // the selection processor does take care to update the positions selection mode correctly
                // and will correctly transform the touch events so that they can be directly applied to your adapter!!!
                menuItemDelete.setVisible(true);
                menuItemShare.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemSearch.setVisible(false);

                mDragSelectTouchListener.startDragSelection(position);

                GetCategoryDocumentsResponse documentsResponseObj = mGetCategoryDocumentsResponses.get(position);
                mSelectedDocumentList.add(documentsResponseObj);

                updateToolbarMenuItems(mSelectedDocumentList);

                return true;
            }
        });

        mDragSelectionProcessor = new DragSelectionProcessor(new DragSelectionProcessor.ISelectionHandler() {
            @Override
            public HashSet<Integer> getSelection() {
                return mAdapterList.getSelection();
            }

            @Override
            public boolean isSelected(int index) {
                return mAdapterList.getSelection().contains(index);
            }

            @Override
            public void updateSelection(int start, int end, boolean isSelected, boolean calledFromOnStart) {
                mAdapterList.selectRange(start, end, isSelected);
            }
        })
                .withMode(mMode);
        mDragSelectTouchListener = new DragSelectTouchListener()
                .withSelectListener(mDragSelectionProcessor);
        updateSelectionListener();
        mRecyclerView.addOnItemTouchListener(mDragSelectTouchListener);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            fileUri = data.getData();

            Uri uri = null;

            /*Uri selectedImageUri = null;
            String Path = null;
            try {
                Path = data.getExtras().getString("dataImg");

            } catch (Exception e) {
            }
            try {
                if (Path == null) {

                    Path = data.getData().toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (Path == null) {
                    Path = data.getExtras().getString("data");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (Path == null) {
                    Uri newPhotoUri = null;
                    newPhotoUri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};
                    CursorLoader loader = new CursorLoader(this,
                            newPhotoUri, projection, null, null,
                            null);
                    Cursor cursor = loader.loadInBackground();

                    int column_index_data = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    Path = cursor.getString(column_index_data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                if (Path == null) {
                    selectedImageUri = data.getData();
                    if (selectedImageUri == null) {
                        Bitmap photo = (Bitmap) data.getExtras()
                                .get("data");
                        selectedImageUri = getImageUri(
                                MyFoldersDMSActivity.this, photo);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            if (null != data) { // checking empty selection
                if (null != data.getClipData()) { // checking multiple selection or not
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        ArrayList<Uri> uris = new ArrayList<>();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            uri = item.getUri();
                            uris.add(uri);

                            String filePath = getRealPathFromURIPath(uri, MyFoldersDMSActivity.this);

                            File file = new File(filePath);

                            ArrayList<String> filePathList = new ArrayList<String>();
                            filePathList.add(filePath);

                            uploadGalleryImage(file, filePathList);
                        }

                    } else {
                        uri = data.getData();

                        String filePath = getRealPathFromURIPath(uri, MyFoldersDMSActivity.this);
                        File file = new File(filePath);

                        ArrayList<String> filePathList = new ArrayList<String>();
                        filePathList.add(filePath);

                        uploadGalleryImage(file, filePathList);
                    }
                }
            }
        } else if (requestCode == REQUEST_CAPTURE_IMAGE_CODE && resultCode == RESULT_OK) {

            if (resultCode == RESULT_OK) {
                uploadImage(fileUri.getPath());

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "Error capturing image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            // uriVideo = data.getData();
            uploadVideo(imageStoragePath);
        }
    }

    private void uploadVideo(String uriVideo) {
        if (NetworkUtils.isNetworkAvailable(this)) {

            File file = new File(uriVideo);

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final UploadEndUserDocumentsRequest mUploadEndUserDocumentsRequest = new UploadEndUserDocumentsRequest(PreferenceUtils.getObjectId(MyFoldersDMSActivity.this),
                    file.getName(), "", "", "");

            String request = new Gson().toJson(mUploadEndUserDocumentsRequest);

            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            //RequestBody converted Json string data request to API Call
            RequestBody dataRequest = RequestBody.create(MediaType.parse("text/plain"), request);

            //RequestBody filename to API Call
            Map<String, RequestBody> requestBodyMap = new HashMap<>();
            RequestBody reqBody = RequestBody.create(MediaType.parse("video/*"), file);
            requestBodyMap.put("file\"; filename=\"" + file.getName(), reqBody);

            final UploadEndUsersDocumentService mUploadEndUsersDocumentService = retrofitAPI.create(UploadEndUsersDocumentService.class);

            Call call = mUploadEndUsersDocumentService.getUploadUsersDocument(dataRequest, requestBodyMap, PreferenceUtils.getAccessToken(this));

            call.enqueue(new Callback<BaseApiResponse<UploadEndUsersDocumentResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<UploadEndUsersDocumentResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        //  transparentProgressDialog.dismiss();
                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {

                                String mMessage = apiResponse.status.getMessage().toString();
                                Toast.makeText(MyFoldersDMSActivity.this, mMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    // transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }

    private void uploadGalleryImage(File file, ArrayList<String> filePathList) {

        if (NetworkUtils.isNetworkAvailable(this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final UploadEndUserDocumentsRequest mUploadEndUserDocumentsRequest = new UploadEndUserDocumentsRequest(PreferenceUtils.getObjectId(MyFoldersDMSActivity.this),
                    file.getName(), "", "", "");

            String request = new Gson().toJson(mUploadEndUserDocumentsRequest);

            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            //RequestBody converted Json string data request to API Call
            RequestBody dataRequest = RequestBody.create(MediaType.parse("text/plain"), request);

            Map<String, RequestBody> requestBodyMap = new HashMap<>();

            for (int i = 0; i < filePathList.size(); i++) {
                File mFile = new File(filePathList.get(i));
                //RequestBody filename to API Call
                RequestBody reqBody = RequestBody.create(MediaType.parse("image/*"), mFile);
                requestBodyMap.put("file\"; filename=\"" + mFile.getName(), reqBody);
            }

            final UploadEndUsersDocumentService mUploadEndUsersDocumentService = retrofitAPI.create(UploadEndUsersDocumentService.class);

            Call call = mUploadEndUsersDocumentService.getUploadEndUsersDocument(dataRequest, requestBodyMap, PreferenceUtils.getAccessToken(this));

            call.enqueue(new Callback<ListPinDevicesResponse<UploadEndUsersDocumentResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<UploadEndUsersDocumentResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        //  transparentProgressDialog.dismiss();
                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {

                                String mMessage = apiResponse.status.getMessage().toString();
                                Toast.makeText(MyFoldersDMSActivity.this, mMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    // transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void uploadImage(String path) {
        if (NetworkUtils.isNetworkAvailable(this)) {

            File file = new File(path);

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final UploadEndUserDocumentsRequest mUploadEndUserDocumentsRequest = new UploadEndUserDocumentsRequest(PreferenceUtils.getObjectId(MyFoldersDMSActivity.this),
                    file.getName(), "", "", "");

            String request = new Gson().toJson(mUploadEndUserDocumentsRequest);

            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            //RequestBody converted Json string data request to API Call
            RequestBody dataRequest = RequestBody.create(MediaType.parse("text/plain"), request);

            //RequestBody filename to API Call
            Map<String, RequestBody> requestBodyMap = new HashMap<>();
            RequestBody reqBody = RequestBody.create(MediaType.parse("image/*"), file);
            requestBodyMap.put("file\"; filename=\"" + file.getName(), reqBody);

            final UploadEndUsersDocumentService mUploadEndUsersDocumentService = retrofitAPI.create(UploadEndUsersDocumentService.class);

            Call call = mUploadEndUsersDocumentService.getUploadEndUsersDocument(dataRequest, requestBodyMap, PreferenceUtils.getAccessToken(this));

            call.enqueue(new Callback<ListPinDevicesResponse<UploadEndUsersDocumentResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<UploadEndUsersDocumentResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        //  transparentProgressDialog.dismiss();
                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {

                                String mMessage = apiResponse.status.getMessage().toString();
                                Toast.makeText(MyFoldersDMSActivity.this, mMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    // transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }

    private String getRealPathFromURIPath(Uri uri, Activity context) {
        /*Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }*/

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
    protected void onResume() {
        super.onResume();
        if (sortByName == true) {
            sort.setText("Name");
        } else if (sortByNewest == true) {
            sort.setText("Type");
        } else if (sortBySize == true) {
            sort.setText("Size");
        } else if (sortByDate == true) {
            sort.setText("Date");
        }
    }
}
