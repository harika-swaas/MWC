package com.swaas.mwc.DMS;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;
import com.michaelflisar.dragselectrecyclerview.DragSelectionProcessor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.swaas.mwc.API.Model.GetCategoryDocumentsRequest;
import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.UploadEndUserDocumentsRequest;
import com.swaas.mwc.API.Model.UploadEndUsersDocumentResponse;
import com.swaas.mwc.API.Service.GetCategoryDocumentsService;
import com.swaas.mwc.API.Service.UploadEndUsersDocumentService;
import com.swaas.mwc.Adapters.DmsAdapter;
import com.swaas.mwc.Adapters.DmsAdapterList;
import com.swaas.mwc.Common.SimpleDividerItemDecoration;
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

import java.io.File;
import java.io.Serializable;
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

/**
 * Created by harika on 11-07-2018.
 */

public class MyFoldersDMSActivity extends RootActivity {
    static Boolean isTouched = false;
    public static final int FOLDER_FRAGMENT = 1;
    public static final int SHARED_FRAGMENT = 2;
    public static final int SETTINGS_FRAGMENT = 3;
    BottomNavigationView mBottomNavigationView;
    LinearLayout sortingView;
    int mSelectedItem = FOLDER_FRAGMENT;
    Menu mBottomNavigationMenu;
    MenuItem mFolderMenuItem, mSharedMenuItem, mSettingsMenuItem;
    ItemNavigationFolderFragment mFolderFragment;
    ItemNavigationSharedFragment mSharedFragment;
    ItemNavigationSettingsFragment mSettingsFragment;
    ImageView select;
    ImageView toggle;
    Button button;
    boolean check = false;
    int backButtonCount=0;
    CollapsingToolbarLayout collapsingToolbarLayout;
    boolean sortByName = false;
    boolean sortByNewest = false;
    boolean sortBySize = false;
    boolean sortByDate = false;
    boolean isSortByDefault = true;
    List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    List<GetCategoryDocumentsResponse> mSelectedDocumentList = new ArrayList<>();
    RecyclerView mRecyclerView;
    DmsAdapter mAdapter;
    DmsAdapterList mAdapterList;
    private DragSelectionProcessor.Mode mMode = DragSelectionProcessor.Mode.Simple;
    private DragSelectTouchListener mDragSelectTouchListener;
    private DragSelectionProcessor mDragSelectionProcessor;
    MenuItem menuItemAdd, menuItemSearch, menuItemDelete, menuItemShare, menuItemMove, menuItemMore;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton actionUpload,actionCamera,actionNewFolder;
    Uri fileUri;
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_folders_dms_fragment);

        intializeViews();
        loadBottomNavigation();
       // switchFragment(FOLDER_FRAGMENT);
        getCategoryDocuments();

        addListenersToViews();
    }

    private void intializeViews() {

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_dms);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        toggle = (ImageView) findViewById(R.id.toggle);
        sortingView = (LinearLayout) findViewById(R.id.sort);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        actionUpload = (FloatingActionButton) findViewById(R.id.menu_upload_item);
        actionCamera = (FloatingActionButton) findViewById(R.id.menu_camera_item);
        actionNewFolder = (FloatingActionButton) findViewById(R.id.menu_new_folder_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.my_folder));

        toolbarTextAppernce();
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

    private void openFloatingBottomSheet() {

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_floating_action_menu, null);

        final TextView upload = (TextView) view.findViewById(R.id.upload);
        TextView newFolder = (TextView) view.findViewById(R.id.new_folder);

        final Dialog mBottomSheetDialog = new Dialog(MyFoldersDMSActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                openUploadBottomSheet();
            }
        });

        newFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });
    }

    private void openUploadBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_upload, null);

        final TextView camera = (TextView) view.findViewById(R.id.camera);
        TextView photoLibrary = (TextView) view.findViewById(R.id.photo_library);
        TextView browse = (TextView) view.findViewById(R.id.browse);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);

        final Dialog mBottomSheetDialog = new Dialog(MyFoldersDMSActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(1);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent,1001);*/
            }
        });

        photoLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, MyFoldersDMSActivity.this);
    }

    /*public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }*/

    /*private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                ServerConfig.IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + ServerConfig.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }else {
            return null;
        }

        return mediaFile;
    }*/

    private void addListenersToViews() {

        actionUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
            }
        });

        actionCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(1);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent,1001);*/
            }
        });

        sortingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check == true) {
                    toggle.setImageResource(R.mipmap.ic_list);
                    setListAdapterToView(mGetCategoryDocumentsResponses);
                    mAdapter.notifyDataSetChanged();

                    /*if(mSelectedItem != 0) {
                        if (mSelectedItem == FOLDER_FRAGMENT) {

                            Bundle mBundle = new Bundle();
                            ItemNavigationFolderFragment itemNavigationFolderFragment = ItemNavigationFolderFragment.newInstance();
                            mBundle.putBoolean(Constants.TOOGLEGRID, check);

                            itemNavigationFolderFragment.setArguments(mBundle);

                            getSupportFragmentManager().beginTransaction().replace(R.id.container, itemNavigationFolderFragment).addToBackStack(null).commit();

                        } else if (mSelectedItem == SHARED_FRAGMENT) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();

                        } else if (mSelectedItem == SETTINGS_FRAGMENT) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
                        }
                    }*/
                    check = false;

                } else {
                    toggle.setImageResource(R.mipmap.ic_grid);
                    setGridAdapterToView(mGetCategoryDocumentsResponses);
                    mAdapter.notifyDataSetChanged();
                    check = true;
                }
            }
        });
    }

    private void getCategoryDocuments() {

        if (NetworkUtils.isNetworkAvailable(this)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(this);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(0,"list","category","1","0");

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(sortByName == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByName(params, PreferenceUtils.getAccessToken(this));
            } else if(sortByNewest == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByType(params, PreferenceUtils.getAccessToken(this));
            } else if(sortBySize == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortBySize(params, PreferenceUtils.getAccessToken(this));
            } else if(sortByDate == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByDate(params, PreferenceUtils.getAccessToken(this));
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(this));
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
                                setGridAdapterToView(mGetCategoryDocumentsResponses);
                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(MyFoldersDMSActivity.this);
                            LayoutInflater inflater = (LayoutInflater) MyFoldersDMSActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                    startActivity(new Intent(MyFoldersDMSActivity.this, LoginActivity.class));
                                    finish();
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

    private void openBottomSheet() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);

        final TextView mSortByName = (TextView) view.findViewById(R.id.sort_by_name);
        TextView mSortByNewest = (TextView) view.findViewById(R.id.sort_by_newest);
        TextView mSortBySize = (TextView) view.findViewById(R.id.sort_by_size);
        TextView mSortByDate = (TextView) view.findViewById(R.id.sort_by_date);

        final Dialog mBottomSheetDialog = new Dialog(MyFoldersDMSActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        mSortByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByName = true;
                mBottomSheetDialog.dismiss();
            }
        });

        mSortByNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByNewest = true;
                mBottomSheetDialog.dismiss();
            }
        });

        mSortBySize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortBySize = true;
                mBottomSheetDialog.dismiss();
            }
        });

        mSortByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByDate = true;
                mBottomSheetDialog.dismiss();
            }
        });
    }

    private void getCategoryDocumentsBySortName() {

        if(mSelectedItem != 0) {
            if (mSelectedItem == FOLDER_FRAGMENT) {
                Bundle mBundle = new Bundle();
                ItemNavigationFolderFragment itemNavigationFolderFragment = ItemNavigationFolderFragment.newInstance();
                mBundle.putBoolean(Constants.SORT_BY_NAME, true);
                itemNavigationFolderFragment.setArguments(mBundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.container, itemNavigationFolderFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SHARED_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SETTINGS_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
            }
        }
    }

    private void getCategoryDocumentsBySortNewest() {

        if(mSelectedItem != 0) {
            if (mSelectedItem == FOLDER_FRAGMENT) {
                Bundle mBundle = new Bundle();
                ItemNavigationFolderFragment itemNavigationFolderFragment = ItemNavigationFolderFragment.newInstance();
                mBundle.putBoolean(Constants.SORT_BY_NEWEST, true);
                itemNavigationFolderFragment.setArguments(mBundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.container, itemNavigationFolderFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SHARED_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SETTINGS_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
            }
        }
    }

    private void getCategoryDocumentsBySortSize() {

        if(mSelectedItem != 0) {
            if (mSelectedItem == FOLDER_FRAGMENT) {
                Bundle mBundle = new Bundle();
                ItemNavigationFolderFragment itemNavigationFolderFragment = ItemNavigationFolderFragment.newInstance();
                mBundle.putBoolean(Constants.SORT_BY_SIZE, true);
                itemNavigationFolderFragment.setArguments(mBundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.container, itemNavigationFolderFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SHARED_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SETTINGS_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
            }
        }
    }


    private void getCategoryDocumentsBySortDate() {

        if(mSelectedItem != 0) {
            if (mSelectedItem == FOLDER_FRAGMENT) {
                Bundle mBundle = new Bundle();
                ItemNavigationFolderFragment itemNavigationFolderFragment = ItemNavigationFolderFragment.newInstance();
                mBundle.putBoolean(Constants.SORT_BY_DATE, true);
                itemNavigationFolderFragment.setArguments(mBundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.container, itemNavigationFolderFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SHARED_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();

            } else if (mSelectedItem == SETTINGS_FRAGMENT) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
            }
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

    private void switchFragment(int fragment){
        getSupportFragmentManager().popBackStackImmediate();
        switch (fragment) {
            /*case FOLDER_FRAGMENT:
                mSelectedItem = FOLDER_FRAGMENT;
                if (mFolderFragment == null) {
                    mFolderFragment = ItemNavigationFolderFragment.newInstance();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mFolderFragment).addToBackStack(null).commit();
                break;*/

            case R.id.navigation_shared:
                mSelectedItem = SHARED_FRAGMENT;
                if (mSharedFragment == null) {
                    mSharedFragment = ItemNavigationSharedFragment.newInstance();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSharedFragment).addToBackStack(null).commit();
                break;

            case R.id.navigation_settings:
                mSelectedItem = SETTINGS_FRAGMENT;
                if (mSettingsFragment == null) {
                    mSettingsFragment = ItemNavigationSettingsFragment.newInstance();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, mSettingsFragment).addToBackStack(null).commit();
                break;
        }
    }

    private void setGridAdapterToView(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        Collections.sort(mGetCategoryDocumentsResponses, new Comparator<GetCategoryDocumentsResponse>() {
            @Override
            public int compare(GetCategoryDocumentsResponse lhs, GetCategoryDocumentsResponse rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        // Setup the RecyclerView
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mAdapter = new DmsAdapter(getCategoryDocumentsResponses,this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(new DmsAdapter.ItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                mAdapter.toggleSelection(position);
                mSelectedDocumentList.remove(mGetCategoryDocumentsResponses.get(position));

                updateToolbarMenuItems(mSelectedDocumentList);
            }

            @Override
            public boolean onItemLongClick(View view, int position)
            {
                // if one item is long pressed, we start the drag selection like following:
                // we just call this function and pass in the position of the first selected item
                // the selection processor does take care to update the positions selection mode correctly
                // and will correctly transform the touch events so that they can be directly applied to your adapter!!!
                menuItemDelete.setVisible(true);
                menuItemShare.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);

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
    }

    private void updateSelectionListener()
    {
        mDragSelectionProcessor.withMode(mMode);
        //  mToolbar.setSubtitle("Mode: " + mMode.name());
    }

    private void updateToolbarMenuItems(List<GetCategoryDocumentsResponse> mSelectedDocumentList) {

        boolean isDocument = false;
        boolean isFolder = false;

        List<GetCategoryDocumentsResponse> categoryDocumentsResponseFolderList = new ArrayList<>();
        List<GetCategoryDocumentsResponse> categoryDocumentsResponseDocumentList = new ArrayList<>();

        if(mSelectedDocumentList != null && mSelectedDocumentList.size() > 0) {

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
                menuItemDelete.setVisible(true);
                menuItemMove.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemShare.setVisible(false);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);

            } else if (categoryDocumentsResponseFolderList.size() >= 1) {
                menuItemDelete.setVisible(false);
                menuItemMove.setVisible(true);
                menuItemMore.setVisible(false);
                menuItemShare.setVisible(false);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);
            }

            if (categoryDocumentsResponseDocumentList.size() == 1) {
                menuItemDelete.setVisible(true);
                menuItemShare.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemMove.setVisible(false);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);

            } else if (categoryDocumentsResponseDocumentList.size() >= 1) {
                menuItemDelete.setVisible(true);
                menuItemShare.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemMove.setVisible(false);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);
            }

            if (isFolder && isDocument) {
                menuItemDelete.setVisible(false);
                menuItemMove.setVisible(true);
                menuItemMore.setVisible(false);
                menuItemShare.setVisible(false);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);
            }

        } else {
            menuItemDelete.setVisible(false);
            menuItemShare.setVisible(false);
            menuItemMore.setVisible(false);
            menuItemMove.setVisible(false);
            menuItemSearch.setVisible(true);
            menuItemAdd.setVisible(true);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_multi_select, menu);
        menuItemAdd = menu.findItem(R.id.action_add);
        menuItemSearch = menu.findItem(R.id.action_search);
        menuItemDelete = menu.findItem(R.id.action_delete);
        menuItemShare = menu.findItem(R.id.action_share);
        menuItemMore = menu.findItem(R.id.action_more);
        menuItemMove = menu.findItem(R.id.action_move);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_more:
                openBottomSheetForMultiSelect();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openBottomSheetForMultiSelect(){

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_sort, null);

        TextView clearSelection = (TextView) view.findViewById(R.id.clear_selection);
        TextView copy = (TextView) view.findViewById(R.id.copy);
        final TextView move = (TextView) view.findViewById(R.id.move);
        TextView rename = (TextView) view.findViewById(R.id.rename);
        TextView delete = (TextView) view.findViewById(R.id.delete);
        RelativeLayout availableOfflineLayout = (RelativeLayout) view.findViewById(R.id.available_offline_layout);
        RelativeLayout shareLayout = (RelativeLayout) view.findViewById(R.id.share_layout);
        SwitchCompat download=(SwitchCompat) view.findViewById(R.id.switchButton_download);
        final Dialog mBottomSheetDialog = new Dialog(this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MyFoldersDMSActivity.this,Tab_Activity.class);
                startActivity(intent);
            }
        });

        download.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isTouched = true;
                return false;
            }
        });

        download.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isTouched) {
                    isTouched = false;
                    if (isChecked) {

                        downloaddoc();
                    }
                    else {
                    }
                }
            }
        });


        List<GetCategoryDocumentsResponse> categoryDocumentsResponseFolderList = new ArrayList<>();
        List<GetCategoryDocumentsResponse> categoryDocumentsResponseDocumentList = new ArrayList<>();

        for(GetCategoryDocumentsResponse viewListObj : mSelectedDocumentList){

            if(viewListObj.getType().equalsIgnoreCase("category")) {
                categoryDocumentsResponseFolderList.add(viewListObj);
            } else {
                categoryDocumentsResponseDocumentList.add(viewListObj);
            }
        }

        if(categoryDocumentsResponseFolderList.size() == 1){
            move.setVisibility(View.VISIBLE);
            rename.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            clearSelection.setVisibility(View.GONE);
            availableOfflineLayout.setVisibility(View.GONE);
            shareLayout.setVisibility(View.GONE);
            copy.setVisibility(View.GONE);
        }

        if(categoryDocumentsResponseDocumentList.size() == 1){
            move.setVisibility(View.VISIBLE);
            rename.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            clearSelection.setVisibility(View.GONE);
            availableOfflineLayout.setVisibility(View.VISIBLE);
            shareLayout.setVisibility(View.VISIBLE);
            copy.setVisibility(View.VISIBLE);


        } else if(categoryDocumentsResponseDocumentList.size() >= 1){
            move.setVisibility(View.VISIBLE);
            rename.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
            clearSelection.setVisibility(View.VISIBLE);
            availableOfflineLayout.setVisibility(View.VISIBLE);
            shareLayout.setVisibility(View.VISIBLE);
            copy.setVisibility(View.VISIBLE);
        }

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MyFoldersDMSActivity.this,MyFolderSharedDocuments.class);
                mIntent.putExtra(Constants.OBJ, (Serializable) mSelectedDocumentList);
                startActivity(mIntent);
            }
        });
    }

    private void downloaddoc() {


    }

    private void setListAdapterToView(final List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyFoldersDMSActivity.this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mAdapterList = new DmsAdapterList(getCategoryDocumentsResponses,MyFoldersDMSActivity.this);
        mRecyclerView.setAdapter(mAdapterList);

        mAdapterList.setClickListener(new DmsAdapterList.ItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                mAdapterList.toggleSelection(position);

                menuItemDelete.setVisible(false);
                menuItemShare.setVisible(false);
                menuItemMore.setVisible(false);
                menuItemSearch.setVisible(true);
                menuItemAdd.setVisible(true);

                mSelectedDocumentList.remove(mGetCategoryDocumentsResponses.get(position));

                updateToolbarMenuItems(mSelectedDocumentList);
            }

            @Override
            public boolean onItemLongClick(View view, int position)
            {
                // if one item is long pressed, we start the drag selection like following:
                // we just call this function and pass in the position of the first selected item
                // the selection processor does take care to update the positions selection mode correctly
                // and will correctly transform the touch events so that they can be directly applied to your adapter!!!
                menuItemDelete.setVisible(true);
                menuItemShare.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

         if(requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            fileUri = data.getData();

             String filePath = getRealPathFromURIPath(fileUri, MyFoldersDMSActivity.this);
             File file = new File(filePath);

             if (NetworkUtils.isNetworkAvailable(this)) {

                 Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                 /*final UploadEndUserDocumentsRequest mUploadEndUserDocumentsRequest = new UploadEndUserDocumentsRequest(PreferenceUtils.getObjectId(MyFoldersDMSActivity.this),
                         file.getName(),"","","");

                 String request = new Gson().toJson(mUploadEndUserDocumentsRequest);

                 Map<String, String> params = new HashMap<String, String>();
                 params.put("data", request);*/

                 /*Map<String, RequestBody> requestMap = new HashMap<>();

                 requestMap.put("object_id", toRequestBody(PreferenceUtils.getObjectId(MyFoldersDMSActivity.this)));
                 requestMap.put("filename", toRequestBody(file.getName()));
                 requestMap.put("tag", toRequestBody(""));
                 requestMap.put("insurance", toRequestBody(""));
                 requestMap.put("doc_created_date", toRequestBody(""));*/

                 final UploadEndUserDocumentsRequest mUploadEndUserDocumentsRequest = new UploadEndUserDocumentsRequest(PreferenceUtils.getObjectId(MyFoldersDMSActivity.this),
                         file.getName(),"","","");

                 String request = new Gson().toJson(mUploadEndUserDocumentsRequest);

                 Map<String, RequestBody> requestMap = new HashMap<>();
                // requestMap.put("data", request);

                 Map<String, RequestBody> requestBodyMap = new HashMap<>();
                 RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                 requestBodyMap.put("file\"; filename=\"" + file.getName(), requestBody);


                 /*String fileName = "file\"; filename=\"" + file.getName();
                 RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

               //  RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), observation_id);

                 Map<String, RequestBody> requestBodyMap = new HashMap<>();
              //   requestBodyMap.put("id", id);
                 requestBodyMap.put(fileName, requestBody);*/

                 RequestBody fileBody = RequestBody.create(MediaType.parse("*/*"), file);

                 final UploadEndUsersDocumentService mUploadEndUsersDocumentService = retrofitAPI.create(UploadEndUsersDocumentService.class);

                 Call call = mUploadEndUsersDocumentService.getUploadEndUsersDocument(requestMap, requestBodyMap, PreferenceUtils.getAccessToken(this));

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

            /*if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                String filePath = getRealPathFromURIPath(uri, MainActivity.this);
                File file = new File(filePath);
                Log.d(TAG, "Filename " + file.getName());
                //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                RequestBody mFile = RequestBody.create(MediaType.parse("image*//*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_PATH)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                UploadImageInterface uploadImage = retrofit.create(UploadImageInterface.class);
                Call<UploadObject> fileUpload = uploadImage.uploadFile(fileToUpload, filename);
                fileUpload.enqueue(new Callback<UploadObject>() {
                    @Override
                    public void onResponse(Call<UploadObject> call, Response<UploadObject> response) {
                        Toast.makeText(MainActivity.this, "Response " + response.raw().message(), Toast.LENGTH_LONG).show();
                        Toast.makeText(MainActivity.this, "Success " + response.body().getSuccess(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<UploadObject> call, Throwable t) {
                        Log.d(TAG, "Error " + t.getMessage());
                    }
                });
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }*/
        }
    }

    // This method  converts String to RequestBody
    public static RequestBody toRequestBody (String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body ;
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void uploadImage(String mImagePath) {

        final File fileToUpload = new File(mImagePath);
    }
}
