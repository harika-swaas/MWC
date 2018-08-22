package com.swaas.mwc.Fragments;


import android.app.Activity;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;

import com.swaas.mwc.API.Model.ApiResponse;
import com.swaas.mwc.API.Model.DownloadDocumentRequest;
import com.swaas.mwc.API.Model.DownloadDocumentResponse;
import com.swaas.mwc.API.Model.EditDocumentPropertiesRequest;
import com.swaas.mwc.API.Model.EndUserRenameRequest;
import com.swaas.mwc.API.Model.GetCategoryDocumentsRequest;
import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.OfflineFiles;
import com.swaas.mwc.API.Model.SharedDocumentResponseModel;
import com.swaas.mwc.API.Model.StopSharingRequestModel;
import com.swaas.mwc.API.Model.UploadNewFolderRequest;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.API.Service.DownloadDocumentService;
import com.swaas.mwc.API.Service.EditDocumentPropertiesService;
import com.swaas.mwc.API.Service.EndUserRenameService;
import com.swaas.mwc.API.Service.GetCategoryDocumentsService;
import com.swaas.mwc.API.Service.GetEndUserParentSHaredFoldersService;
import com.swaas.mwc.API.Service.UploadNewFolderService;
import com.swaas.mwc.Adapters.DmsAdapter;
import com.swaas.mwc.Adapters.DmsAdapterList;

import com.swaas.mwc.Common.CameraUtils;
import com.swaas.mwc.Common.FileDownloadManager;
import com.swaas.mwc.Common.ServerConfig;
import com.swaas.mwc.Common.SimpleDividerItemDecoration;
import com.swaas.mwc.DMS.MyFolderActivity;
import com.swaas.mwc.DMS.MyFolderSharedDocuments;
import com.swaas.mwc.DMS.MyFoldersDMSActivity;
import com.swaas.mwc.DMS.Tab_Activity;
import com.swaas.mwc.DMS.UploadListActivity;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Database.OffLine_Files_Repository;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Login.Touchid;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;
import com.swaas.mwc.Utils.DateHelper;
import com.swaas.mwc.pdf.PdfViewActivity;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by harika on 11-07-2018.
 */

public class ItemNavigationFolderFragment extends Fragment {
    List<DownloadDocumentResponse> downloadDocumentResponse;
    DmsAdapter mAdapter;
    DmsAdapterList mAdapterList;
    RecyclerView mRecyclerView;
    MyFoldersDMSActivity mActivity;
    AlertDialog mCustomAlertDialog;
    View mView;
    AlertDialog mAlertDialog;
    List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses = new ArrayList<>();
    List<GetCategoryDocumentsResponse> mSelectedDocumentList = new ArrayList<>();
    List<GetCategoryDocumentsResponse> listGetCategoryDocuments = new ArrayList<>();
    boolean mToogleGrid;
    boolean isSortByName, isSortByNewest, isSortBySize, isSortByDate;
   /* private DragSelectionProcessor.Mode mMode = DragSelectionProcessor.Mode.Simple;
    private DragSelectTouchListener mDragSelectTouchListener;
    private DragSelectionProcessor mDragSelectionProcessor;*/
    MenuItem menuItemAdd, menuItemSearch, menuItemDelete, menuItemShare, menuItemMove, menuItemMore;
    String pageCount = "1";
    NestedScrollView scrollView;
    int pageNumber = 0;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList();
    int totalPages=1;
    static Boolean isTouched = false;
    boolean sortByNameAsc = true;
    boolean sortByTypeAsc = true;
    boolean sortBySizeAsc = true;
    boolean sortByDateAsc = true;
    boolean check = false;
    boolean isFromList = false;
    LinearLayoutManager linearLayoutManager;
    boolean sortByName = false;
    boolean sortByNewest = false;
    boolean sortBySize = false;
    boolean sortByDate = false;
    public static final String GALLERY_DIRECTORY_NAME = "Hello Camera";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    Toolbar toolbar;
    public static final int REQUEST_GALLERY_CODE = 200;
    public static final int REQUEST_CAPTURE_IMAGE_CODE = 300;
    public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 400;
    Uri fileUri;
    private  String imageStoragePath;
    public  FloatingActionMenu floatingActionMenu;
    public  FloatingActionButton actionUpload, actionCamera, actionNewFolder, actionVideo;
    ArrayList<String>list_upload = new ArrayList<>();
    boolean floatingActionButton_visible = false;

    List<GetCategoryDocumentsResponse> downloadingUrlDataList = new ArrayList<>();
    int index=0;
    Uri uri = null;
    public static ItemNavigationFolderFragment newInstance() {
        ItemNavigationFolderFragment fragment = new ItemNavigationFolderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MyFoldersDMSActivity) getActivity();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.fragment_item_navigation_folder, container, false);


        setHasOptionsMenu(true);

        intiaizeViews();
        mRecyclerView.setNestedScrollingEnabled(false);
     //   getBundleArguments();

        getWhiteLabelProperities();

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView != null) {
                    if (scrollView.getChildAt(0).getBottom() == (scrollView.getHeight() + scrollView.getScrollY())) {
                        //scroll view is at bottom
                        String object= PreferenceUtils.getObjectId(mActivity);

                    //    Toast.makeText(mActivity, "end position", Toast.LENGTH_SHORT).show();

                        if(pageNumber < totalPages) {
                            pageNumber=pageNumber+1;
                            getCategoryDocumentsNext(object, String.valueOf(pageNumber));

                        }
                    }
                    else {
                        //scroll view is not at bottom
                    }
                }
            }
        });

        return mView;
    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        MyFoldersDMSActivity.toggleView= (RelativeLayout) getActivity().findViewById(R.id.toggle_view);
        MyFoldersDMSActivity.toggle = (ImageView) getActivity().findViewById(R.id.toggle);
        MyFoldersDMSActivity.sortingView = (LinearLayout) getActivity().findViewById(R.id.sort);
        MyFoldersDMSActivity.sort = (TextView) getActivity().findViewById(R.id.name_sort);



        actionUpload = (FloatingActionButton)getActivity(). findViewById(R.id.menu_upload_item);
       actionCamera = (FloatingActionButton)getActivity(). findViewById(R.id.menu_camera_item);
       actionNewFolder = (FloatingActionButton)getActivity(). findViewById(R.id.menu_new_folder_item);
        actionVideo = (FloatingActionButton)getActivity(). findViewById(R.id.menu_camera_video_item);
        // your TextView must be declared as (public static TextView text_view) in the Activity

        MyFoldersDMSActivity.title_layout= (LinearLayout) getActivity().findViewById(R.id.linearlayout1);
        MyFoldersDMSActivity.title_layout.setVisibility(View.VISIBLE);

       floatingActionMenu = (FloatingActionMenu) getActivity().findViewById(R.id.floating_action_menu);
       floatingActionMenu.setVisibility(View.VISIBLE);

        /*if(floatingActionButton_visible == true)
        {
            actionUpload.setVisibility(View.VISIBLE);
            actionCamera.setVisibility(View.VISIBLE);
            actionVideo.setVisibility(View.VISIBLE);
        }
        else
        {
            actionUpload.setVisibility(View.GONE);
            actionCamera.setVisibility(View.GONE);
            actionVideo.setVisibility(View.GONE);
        }*/



        MyFoldersDMSActivity.toggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check == false) {
                    MyFoldersDMSActivity.toggle.setImageResource(R.mipmap.ic_list);
                    setListAdapterToView(mGetCategoryDocumentsResponses);
                    isFromList = true;
                    mAdapterList.notifyDataSetChanged();
                    check = true;

                } else {
                    MyFoldersDMSActivity.toggle.setImageResource(R.mipmap.ic_grid);
                    setGridAdapterToView(mGetCategoryDocumentsResponses);
                    mAdapter.notifyDataSetChanged();
                    isFromList = false;
                    check = false;
                }
            }
        });


        MyFoldersDMSActivity.sortingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });

        actionUpload.setOnClickListener(v -> {
    /*        FilePickerDialogFragment filePickerDialogFragment =null;
            DialogConfig dialogConfig = new DialogConfig.Builder()
                    .enableMultipleSelect(true) // default is false
                    .enableFolderSelect(true) // default is false
                    .initialDirectory(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android") // default is sdcard
                    .supportFiles(new SupportFile(".jpg", R.drawable.ic_audiotrack_light), new SupportFile(".png", 0), new SupportFile(".pdf", R.drawable.ic_pdfviewpager)) // default is showing all file types.
                    .build();

            new FilePickerDialogFragment.Builder()
                    .configs(dialogConfig)
                    .onFilesSelected(new FilePickerDialogFragment.OnFilesSelectedListener() {
                        @Override
                        public void onFileSelected(List<File> list) {
                            Log.e(TAG, "total Selected file: " + list.size());
                            for (File file : list) {
                                Log.e(TAG, "Selected file: " + file.getAbsolutePath());
                            }
                        }
                    })
                   *//* .onFolderLoadListener(new FilePickerDialogFragment.OnFolderLoadListener() {
                        @Override
                        public void onLoadFailed(String path) {
                            //Could not access folder because of user permissions, sdcard is not readable...
                        }*//*
                   // })
                    .build()
                    .show(getSupportFragmentManager(),null);*/
            // requestStoragePermission();
            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            Intent openGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            openGalleryIntent.setType("*/*");
           openGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
           openGalleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
        });

        actionCamera.setOnClickListener(v -> {

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

            Uri fileUri = CameraUtils.getOutputMediaFileUri(mActivity, file);

            // set video quality
            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takeVideoIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
        });
        actionNewFolder.setOnClickListener(v -> {


            final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view1 = inflater.inflate(R.layout.newfolder, null);
            builder.setView(view1);
            builder.setCancelable(false);

            Button cancel = (Button) view1.findViewById(R.id.cancel_b);
            Button allow = (Button) view1.findViewById(R.id.allow);
            final EditText namer = (EditText) view1.findViewById(R.id.edit_username1);
            allow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String folder = namer.getText().toString().trim();


                    if (NetworkUtils.isNetworkAvailable(mActivity)) {

                        Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                        final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
                        transparentProgressDialog.show();

                        final UploadNewFolderRequest uploadNewFolderRequest = new UploadNewFolderRequest(PreferenceUtils.getCategoryId(mActivity), folder);

                        String request = new Gson().toJson(uploadNewFolderRequest);

                        //Here the json data is add to a hash map with key data
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("data", request);

                        final UploadNewFolderService uploadNewFolderService = retrofitAPI.create(UploadNewFolderService.class);

                        Call call = uploadNewFolderService.getNewFolder(params, PreferenceUtils.getAccessToken(mActivity));

                        call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                            @Override
                            public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                                ListPinDevicesResponse apiResponse = response.body();
                                if (apiResponse != null) {

                                    transparentProgressDialog.dismiss();

                                    if (apiResponse.status.getCode() instanceof Boolean) {
                                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                                            transparentProgressDialog.dismiss();

                                            // getCategoryDocuments(PreferenceUtils.getCategoryId(context),"1");

                                        }

                                    } else if (apiResponse.status.getCode() instanceof Integer) {
                                        transparentProgressDialog.dismiss();
                                        String mMessage = apiResponse.status.getMessage().toString();

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                                mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
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




        // now access the TextView as you want
    }

    private FragmentManager getSupportFragmentManager() {
        return getFragmentManager();
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

        final Dialog mBottomSheetDialog = new Dialog(getActivity(), R.style.MaterialDialogSheet);
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
                scrollView.fullScroll(ScrollView.FOCUS_UP);


            }
        });
    }


    private void getCategoryDocumentsSortByDate(String page) {

        if (NetworkUtils.isNetworkAvailable(getActivity())) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(getActivity());
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            if (PreferenceUtils.getObjectId(getActivity()).equalsIgnoreCase("")) {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest("0", "list", "category", "1", "0");
            } else {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(PreferenceUtils.getObjectId(getActivity()), "list", "category", "1", "0");
            }

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(sortByDateAsc == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByDate(params, PreferenceUtils.getAccessToken(getActivity()),page);
                sortByDateAsc = false;
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByDateDesc(params, PreferenceUtils.getAccessToken(getActivity()),page);
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

                            MyFoldersDMSActivity.sort.setText("Date");

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(getActivity());
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
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
        if (NetworkUtils.isNetworkAvailable(getActivity())) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(getActivity());
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            if (PreferenceUtils.getObjectId(getActivity()).equalsIgnoreCase("")) {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest("0", "list", "category", "1", "0");
            } else {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(PreferenceUtils.getObjectId(getActivity()), "list", "category", "1", "0");
            }

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(sortByTypeAsc == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByType(params, PreferenceUtils.getAccessToken(getActivity()),page);
                sortByTypeAsc = false;
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByTypeDesc(params, PreferenceUtils.getAccessToken(getActivity()),page);
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

                            MyFoldersDMSActivity.sort.setText("Type");

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(getActivity());
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
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

        if (NetworkUtils.isNetworkAvailable(getActivity())) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(getActivity());
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            if (PreferenceUtils.getObjectId(getActivity()).equalsIgnoreCase("")) {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest("0", "list", "category", "1", "0");
            } else {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(PreferenceUtils.getObjectId(getActivity()), "list", "category", "1", "0");
            }

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(sortBySizeAsc == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortBySize(params, PreferenceUtils.getAccessToken(getActivity()),page);
                sortBySizeAsc = false;
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortBySizeDesc(params, PreferenceUtils.getAccessToken(getActivity()),page);
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

                            MyFoldersDMSActivity.sort.setText("Size");

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(getActivity());
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
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

    public void getCategoryDocumentsNext(String obj, String page)
    {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(obj, "list", "category", "1", "0");

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = mGetCategoryDocumentsService.getCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(mActivity),page);

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
                                if(isFromList == true)
                                {
                                    setListAdapterToView(mGetCategoryDocumentsResponses);
                                }
                                else
                                {
                                    setGridAdapterToView(mGetCategoryDocumentsResponses);
                                }

                                //     paginationList.clear();



                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(mActivity);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(mActivity, LoginActivity.class));
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

    private void intiaizeViews() {

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_dms);
        scrollView = (NestedScrollView) mView.findViewById(R.id.nest_scrollview);


    }



    private void setGridAdapterToView(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

    /* Collections.sort(mGetCategoryDocumentsResponses, new Comparator<GetCategoryDocumentsResponse>() {
            @Override
            public int compare(GetCategoryDocumentsResponse lhs, GetCategoryDocumentsResponse rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });*/

        // Setup the RecyclerView
        MyFoldersDMSActivity.toggle.setImageResource(R.mipmap.ic_grid);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mAdapter = new DmsAdapter(getCategoryDocumentsResponses, getActivity(), ItemNavigationFolderFragment.this);
        mRecyclerView.setAdapter(mAdapter);



    }



    public void getCategoryDocuments(final String obj, String page) {

        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(obj, "list", "category", "1", "0");

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = mGetCategoryDocumentsService.getCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(mActivity),page);

            call.enqueue(new Callback<ListPinDevicesResponse<GetCategoryDocumentsResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetCategoryDocumentsResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                if (obj.equals("0")){
                                    ActionBar actionBar = mActivity.getSupportActionBar();
                                    actionBar.setDisplayHomeAsUpEnabled(false);
                                    actionCamera.setVisibility(View.GONE);
                                    actionUpload.setVisibility(View.GONE);
                                    actionVideo.setVisibility(View.GONE);
                                }
                                else
                                {
                                    ActionBar actionBar = mActivity.getSupportActionBar();
                                    actionBar.setDisplayHomeAsUpEnabled(true);
                                    actionCamera.setVisibility(View.VISIBLE);
                                    actionUpload.setVisibility(View.VISIBLE);
                                    actionVideo.setVisibility(View.VISIBLE);
                                }

                                //      listGetCategoryDocuments = response.body().getData();
                                mGetCategoryDocumentsResponses = response.body().getData();

                               /* if(obj.equals("0"))
                                {
                                    floatingActionButton_visible = true;
                                }*/


                                totalPages  = Integer.parseInt(response.headers().get("X-Pagination-Page-Count"));
                                pageNumber = Integer.parseInt(response.headers().get("X-Pagination-Current-Page"));

                                setGridAdapterToView(mGetCategoryDocumentsResponses);
                                List<GetCategoryDocumentsResponse> dummyList = new ArrayList<>();
                                updateToolbarMenuItems(dummyList);


                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(mActivity);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(mActivity, LoginActivity.class));
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
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mAdapterList = new DmsAdapterList(getCategoryDocumentsResponses, getActivity(), ItemNavigationFolderFragment.this);
        mRecyclerView.setAdapter(mAdapterList);


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


    public static void menuIconColor(MenuItem menuItem, int color) {
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void getWhiteLabelProperities() {

        AccountSettings accountSettings = new AccountSettings(getActivity());
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int i = PreferenceUtils.getBackButtonList(mActivity,"key").size();
        switch (item.getItemId()) {
            case android.R.id.home:
               i = i - 2;
                if (i >-1) {
                    String id = PreferenceUtils.getBackButtonList(mActivity,"key").get(i);
                    getCategoryDocuments(id,String.valueOf(pageNumber));
                    ArrayList<String> temp ;
                    temp = PreferenceUtils.getBackButtonList(mActivity,"key");
                    temp.remove(i);
                    PreferenceUtils.setBackButtonList(mActivity,temp,"key");

                } else {
                    ArrayList<String> temp ;
                    temp = PreferenceUtils.getBackButtonList(mActivity,"key");
                    temp.clear();
                    PreferenceUtils.setBackButtonList(mActivity,temp,"key");
                    startActivity(new Intent(getActivity(), MyFoldersDMSActivity.class));
                    return true;

                }
                break;
            case R.id.action_more:
                openBottomSheetForMultiSelect();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_multi_select, menu);
        menuItemSearch = menu.findItem(R.id.action_search);
        menuItemDelete = menu.findItem(R.id.action_delete);
        menuItemShare = menu.findItem(R.id.action_share);
        menuItemMore = menu.findItem(R.id.action_more);
        menuItemMove = menu.findItem(R.id.action_move);

        String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
        int selectedColor = Color.parseColor(itemSelectedColor);

        menuIconColor(menuItemSearch,selectedColor);

        super.onCreateOptionsMenu(menu, inflater);
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mActivity.finish();
                return true;

            case R.id.action_more:
                openBottomSheetForMultiSelect();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/

    private void getCategoryDocumentsSortByName(String page) {

        if (NetworkUtils.isNetworkAvailable(getActivity())) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(getActivity());
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest;

            if (PreferenceUtils.getObjectId(getActivity()).equalsIgnoreCase("")) {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest("0", "list", "category", "1", "0");
            } else {
                mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(PreferenceUtils.getObjectId(getActivity()), "list", "category", "1", "0");
            }

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(sortByNameAsc == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByName(params, PreferenceUtils.getAccessToken(getActivity()),page);
                sortByNameAsc = false;
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByNameDesc(params, PreferenceUtils.getAccessToken(getActivity()),page);
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
                                    setListAdapterToView(mGetCategoryDocumentsResponses);
                                } else {
                                    setGridAdapterToView(mGetCategoryDocumentsResponses);
                                }
                                totalPages = Integer.parseInt(response.headers().get("X-Pagination-Page-Count"));
                                pageNumber = Integer.parseInt(response.headers().get("X-Pagination-Current-Page"));
                                // pageNumber= pageNumber+1;
                            }
                            MyFoldersDMSActivity.sort.setText("Name");



                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            Object obj = 401.0;
                            if (obj.equals(401.0)) {

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(getActivity());
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
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




        final Dialog mBottomSheetDialog = new Dialog(getActivity(), R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        if(mSelectedDocumentList != null && mSelectedDocumentList.size() == 1)
        {
            if(mSelectedDocumentList.get(0).getType().equalsIgnoreCase("category"))
            {
                categoryText.setText(mSelectedDocumentList.get(0).getName());
            }
            else {

                if (mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("pdf")) {
                    //holder.imageView.setImageResource(R.mipmap.ic_pdf);
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_pdf_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_pdf_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                } else if (mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("xlsx") ||
                        mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("xls") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("xlsm")
                        || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("csv")) {
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_xls_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_xls_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                } else if (mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("doc") ||
                        mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("docx") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("docm")
                        || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("gdoc") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("keynote")) {
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_doc_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_doc_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                } else if (mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("ppt") ||
                        mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("pptx") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("pps")
                        || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("ai")) {
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_ppt_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_ppt_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                } else if (mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("xml") ||
                        mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("log") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("zip")
                        || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("rar") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("zipx")
                        || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("mht")) {
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_xml_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_xml_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                } else if (mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("xml") ||
                        mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("log") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("rtf") ||
                        mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("txt") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("epub")) {
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_xml_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_xml_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                } else if (mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("msg") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("dot") ||
                        mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("odt")
                        || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("ott")) {
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_msg_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_msg_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                } else if (mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("pages")) {
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_pages_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_pages_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                } else if (mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("pub") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("ods")) {
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_pub_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_pub_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                } else if (mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("gif") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("jpeg")
                        || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("jpg") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("png") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("bmp")
                        || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("tif") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("tiff") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("eps")
                        || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("svg") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("odp")
                        || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("otp")) {
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_gif_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_gif_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                } else if (mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("avi")
                        || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("flv") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("mpeg") ||
                        mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("mpg") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("swf") ||
                        mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("wmv")) {
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_avi_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_avi_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                } else if (mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("mp3")
                        || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("wav") || mSelectedDocumentList.get(0).getFiletype().equalsIgnoreCase("wma")) {
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_mp3_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_mp3_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                } else {
                    thumbnailIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_default_color));
                    thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(mActivity, R.color.thumbnail_default_corner_color));
                    thumbnailText.setText(mSelectedDocumentList.get(0).getFiletype());
                }
                docText.setText(mSelectedDocumentList.get(0).getName());

            }



            // category


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
                    OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(mActivity);
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

                        Intent mIntent = new Intent(getActivity(), MyFolderSharedDocuments.class);
                        mIntent.putExtra(Constants.OBJ, (Serializable) mSelectedDocumentList);
                        startActivity(mIntent);

                        mBottomSheetDialog.dismiss();
                    }


                }

            }
        });


        doc_info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                if(mSelectedDocumentList != null && mSelectedDocumentList.size() == 1)
                {
                    PreferenceUtils.setDocumentVersionId(mActivity,mSelectedDocumentList.get(0).getDocument_version_id());
                    PreferenceUtils.setDocument_Id(mActivity, mSelectedDocumentList.get(0).getObject_id());
                }

                Intent intent = new Intent (mActivity,Tab_Activity.class);
                mActivity.startActivity(intent);
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
                mBottomSheetDialog.dismiss();
                List<GetCategoryDocumentsResponse> dummyList = new ArrayList<>();
                updateToolbarMenuItems(dummyList);
            }
        });


        moveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MyFolderActivity.class);
                startActivity(intent);

            }
        });

        renameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                            Toast.makeText(mActivity, "Please enter name", Toast.LENGTH_SHORT).show();
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
                          //  Toast.makeText(mActivity,String.valueOf(downloadedList.size()), Toast.LENGTH_LONG).show();
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
                String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
                int selectedColor = Color.parseColor(itemSelectedColor);

                moveImage.setColorFilter(selectedColor);
                renameImage.setColorFilter(selectedColor);

                if (itemSelectedColor != null) {
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

    private void showWarningMessageAlertForSharingContent(ArrayList<String> stopSharingList, List<GetCategoryDocumentsResponse> mSelectedDocumentList)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();


            final StopSharingRequestModel stopSharingRequestModel = new StopSharingRequestModel(sharingList, selectedDocumentList.get(0).getCategory_id());

            String request = new Gson().toJson(stopSharingRequestModel);


            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetEndUserParentSHaredFoldersService mGetEndUserParentSHaredstopService = retrofitAPI.create(GetEndUserParentSHaredFoldersService.class);

            Call call = mGetEndUserParentSHaredstopService.getEndUserStopSharedDocuments(params, PreferenceUtils.getAccessToken(mActivity));

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
                                final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                        AccountSettings accountSettings = new AccountSettings(mActivity);
                                        accountSettings.deleteAll();
                                        startActivity(new Intent(mActivity, LoginActivity.class));
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

    private void renameDocument(String document_version_id, String rename, String doc_created, String auth)
    {
        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            final EditDocumentPropertiesRequest editDocumentPropertiesRequest = new EditDocumentPropertiesRequest(document_version_id,rename,doc_created,auth);

            String request = new Gson().toJson(editDocumentPropertiesRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final EditDocumentPropertiesService editDocumentPropertiesService = retrofitAPI.create(EditDocumentPropertiesService.class);

            Call call = editDocumentPropertiesService.getRenameDocument(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();

                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                    mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
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

    private void renameCategory(String object_id, String rename, String parent_id)
    {
        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            final EndUserRenameRequest endUserRenameRequest = new EndUserRenameRequest(object_id,rename,parent_id);

            String request = new Gson().toJson(endUserRenameRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final EndUserRenameService endUserRenameService = retrofitAPI.create(EndUserRenameService.class);

            Call call = endUserRenameService.getRename(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();


                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                    mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
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

   /* @Override
    public void onItemClick(View view, int position) {

    }*/



    public void convertingDownloadUrl(List<GetCategoryDocumentsResponse> downloadedList)
    {
        downloadingUrlDataList = downloadedList;
        if(downloadingUrlDataList.size()> index) {

            getDownloadurlFromService(downloadingUrlDataList.get(index).getObject_id());

        }


    }

    public void getDownloadurlFromService(String document_version_id)
    {
        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DownloadDocumentService downloadDocumentService = retrofitAPI.create(DownloadDocumentService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            //DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(PreferenceUtils.getDocumentVersionId(this));
            List<String> strlist = new ArrayList<>();
            strlist.add(document_version_id);
            DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(strlist);
            final String request = new Gson().toJson(downloadDocumentRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = downloadDocumentService.download(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<ApiResponse<DownloadDocumentResponse>>() {
                @Override
                public void onResponse(Response<ApiResponse<DownloadDocumentResponse>> response, Retrofit retrofit) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                            transparentProgressDialog.dismiss();
                            DownloadDocumentResponse downloadDocumentResponse = response.body().getData();

                            String downloaded_url = downloadDocumentResponse.getData();

                            String access_Token = PreferenceUtils.getAccessToken(mActivity);

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
                FileDownloadManager fileDownloadManager = new FileDownloadManager(mActivity);
                fileDownloadManager.setFileTitle(digitalAsset.getName());
                fileDownloadManager.setDownloadUrl(digitalAsset.getDownloadUrl());
                fileDownloadManager.setDigitalAssets(digitalAsset);
                fileDownloadManager.setmFileDownloadListener(new FileDownloadManager.FileDownloadListener() {
                    @Override
                    public void fileDownloadSuccess(String path) {

                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(mActivity);
                        if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(digitalAsset.getDocument_version_id())) {
                            offLine_files_repository.deleteAlreadydownloadedFile(digitalAsset.getDocument_version_id());
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
        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(mActivity);
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




    public void getDownloadurlFromServiceSingleDocument(GetCategoryDocumentsResponse documentsResponse)
    {
        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DownloadDocumentService downloadDocumentService = retrofitAPI.create(DownloadDocumentService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            //DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(PreferenceUtils.getDocumentVersionId(this));
            List<String> strlist = new ArrayList<>();
            strlist.add(documentsResponse.getObject_id());
            DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(strlist);
            final String request = new Gson().toJson(downloadDocumentRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = downloadDocumentService.download(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<ApiResponse<DownloadDocumentResponse>>() {
                @Override
                public void onResponse(Response<ApiResponse<DownloadDocumentResponse>> response, Retrofit retrofit) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                            transparentProgressDialog.dismiss();
                            DownloadDocumentResponse downloadDocumentResponse = response.body().getData();

                            String downloaded_url = downloadDocumentResponse.getData();

                            String access_Token = PreferenceUtils.getAccessToken(mActivity);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_GALLERY_CODE && resultCode == RESULT_OK) {
            fileUri = data.getData();



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

                            String filePath = getRealPathFromURIPath(uri, mActivity);

                            //  File file = new File(filePath);



                            list_upload.add(String.valueOf(filePath));
                            PreferenceUtils.setupload(getContext(),list_upload,"key");
                            PreferenceUtils.setupload(getContext(),list_upload,"key");


                        }

                    } else {
                        uri = data.getData();

                        String filePath = getRealPathFromURIPath(uri, mActivity);
                        //File file = new File(filePath);

                      /*  ArrayList<String> filePathList = new ArrayList<String>();
                        filePathList.add(filePath);*/
                        list_upload = PreferenceUtils.getupload(getContext(),"key");
                        list_upload.add(String.valueOf(filePath));
                        PreferenceUtils.setupload(getContext(),list_upload,"key");

                        //   uploadGalleryImage(file, filePathList);
                    }
                }
                if((list_upload == null )||(list_upload.size()==0))
                {
                    String filepath = getRealPathFromURIPath(fileUri, mActivity);
                    list_upload.add(filepath);
                    PreferenceUtils.setupload(getContext(),list_upload,"key");
                }
            }
            Intent intent = new Intent (getContext(),UploadListActivity.class);
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
                PreferenceUtils.setupload(getContext(),list_upload,"key");
                // list_upload.clear();

                Intent intent = new Intent (getContext(),UploadListActivity.class);
                startActivity(intent);


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), "Error capturing image", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            // uriVideo = data.getData();
            // uploadVideo(imageStoragePath);



            String filePath =imageStoragePath;
/*
                File file = new File(filePath);
*/

            ArrayList<String> filePathList = new ArrayList<String>();
            filePathList.add(filePath);
            list_upload = PreferenceUtils.getupload(mActivity,"key");
            list_upload.add(String.valueOf(filePath));
            PreferenceUtils.setupload(getContext(),list_upload,"key");
            list_upload.clear();
            Intent intent = new Intent (getContext(),UploadListActivity.class);
            startActivity(intent);

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
    public void onResume() {
        super.onResume();

        getCategoryDocuments("0",String.valueOf(pageNumber));


        if (sortByName == true) {
            MyFoldersDMSActivity.sort.setText("Name");
        } else if (sortByNewest == true) {
            MyFoldersDMSActivity.sort.setText("Type");
        } else if (sortBySize == true) {
            MyFoldersDMSActivity.sort.setText("Size");
        } else if (sortByDate == true) {
            MyFoldersDMSActivity.sort.setText("Date");
        }

        list_upload = PreferenceUtils.getupload(mActivity,"key");
        if ((list_upload!=null)&&(list_upload.size()>1)){
            final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.custom_dialog, null);
            builder.setView(view);
            builder.setCancelable(false);

            final Button BtnAllow = (Button) view.findViewById(R.id.allow_button);
            BtnAllow.setText("RETRY");
            final Button BtnCancel = (Button) view.findViewById(R.id.cancel_button);
            TextView textView =(TextView) view.findViewById(R.id.txt_message);
            textView.setVisibility(View.GONE);
            TextView text = (TextView) view.findViewById(R.id.message);
            text.setText("Some Documents failed to upload");
            mCustomAlertDialog = builder.create();
            mCustomAlertDialog.show();

            BtnAllow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCustomAlertDialog.dismiss();
                    Intent intent = new Intent (mActivity,UploadListActivity.class);
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



        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener

                    if (isFromList == true) {
                        mAdapterList.clearAll();
                    } else {
                        mAdapter.clearAll();
                    }
                    List<GetCategoryDocumentsResponse> dummyList = new ArrayList<>();
                    updateToolbarMenuItems(dummyList);

                    int i = PreferenceUtils.getBackButtonList(mActivity,"key").size();
                    i = i - 2;
                    if (i >-1) {
                        String id = PreferenceUtils.getBackButtonList(mActivity,"key").get(i);
                        getCategoryDocuments(id,String.valueOf(pageNumber));
                        ArrayList<String> temp ;
                        temp = PreferenceUtils.getBackButtonList(mActivity,"key");
                        temp.remove(i);
                        PreferenceUtils.setBackButtonList(mActivity,temp,"key");

                    } else {
                        ArrayList<String> temp ;
                        temp = PreferenceUtils.getBackButtonList(mActivity,"key");
                        temp.clear();
                        PreferenceUtils.setBackButtonList(mActivity,temp,"key");
                        startActivity(new Intent(getActivity(), MyFoldersDMSActivity.class));
                        return true;

                    }


                    return true;
                }
                return false;
            }
        });




    }
}
