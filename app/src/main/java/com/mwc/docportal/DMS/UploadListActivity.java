package com.mwc.docportal.DMS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.lang.UProperty;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.gson.Gson;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.GlobalSearch.GlobalSearchActivity;
import com.mwc.docportal.RootActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.mwc.docportal.API.Model.UploadDocumentResponse;
import com.mwc.docportal.API.Model.UploadEndUserDocumentsRequest;
import com.mwc.docportal.API.Service.UploadEndUsersDocumentService;
import com.mwc.docportal.Common.CameraUtils;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.mwc.docportal.Common.CameraUtils.getOutputMediaFile;


/**
 * Created by barath on 8/8/2018.
 */

public class UploadListActivity extends RootActivity{

    RecyclerView upload_list;
    Uri fileUri;
    TextView camera,video,browse,cancel;
    ArrayList<String> UploadList;
    ArrayList<String> uploadFailedList = new ArrayList<>();
    private  String imageStoragePath;
    String path;
    UploadListAdapter customAdapter;
    public static final int REQUEST_GALLERY_CODE = 200;
    public static final int REQUEST_CAPTURE_IMAGE_CODE = 300;
    public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 400;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    int fileindex;

    AlertDialog mCustomAlertDialog;
    Context context = this;

    public static final int REQUEST_STORAGE_PERMISSION = 111;
    public static final int REQUEST_CAMERA_PERMISSION = 222;
    boolean isVideo = false;
    Toolbar toolbar;
    MenuItem menuItemUpload, menuItemAdd;
    LinearLayout empty_view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_list);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back));
        getSupportActionBar().setTitle("Upload");
        empty_view = (LinearLayout)findViewById(R.id.empty_view);


        upload_list = (RecyclerView) findViewById(R.id.list_upload);
        upload_list.setLayoutManager(new LinearLayoutManager(this));
        UploadList = PreferenceUtils.getupload(UploadListActivity.this, "key");
        customAdapter = new UploadListAdapter(UploadListActivity.this, UploadList);
        upload_list.setAdapter(customAdapter);

        if(UploadList != null && UploadList.size() > 0)
        {
            empty_view.setVisibility(View.GONE);
        }
        else
        {
            empty_view.setVisibility(View.VISIBLE);
        }

    }

    public void upload(final int i)
    {
        customAdapter.ActivateLoad(true,fileindex);
        customAdapter.notifyDataSetChanged();
        UploadList = PreferenceUtils.getupload(UploadListActivity.this, "key");
        int size = UploadList.size();

        if (size > fileindex) {

            path = String.valueOf(UploadList.get(fileindex));

            if (NetworkUtils.isNetworkAvailable(UploadListActivity.this)) {

                File file = new File(path);

                Retrofit retrofitAPI = RetrofitAPIBuilder.getUploadInstance();

                final UploadEndUserDocumentsRequest mUploadEndUserDocumentsRequest = new UploadEndUserDocumentsRequest(PreferenceUtils.getObjectId(UploadListActivity.this),
                        file.getName(), "", "", "");

                String request = new Gson().toJson(mUploadEndUserDocumentsRequest);

                Map<String, String> params = new HashMap<String, String>();
                params.put("data", request);

                //RequestBody converted Json string data request to API Call
                RequestBody dataRequest = RequestBody.create(MediaType.parse("text/plain"), request);

                //RequestBody filename to API Call
                Map<String, RequestBody> requestBodyMap = new HashMap<>();
                RequestBody reqBody = RequestBody.create(MediaType.parse("*/*"), file);
                requestBodyMap.put("file\"; filename=\"" + file.getName(), reqBody);

                final UploadEndUsersDocumentService mUploadEndUsersDocumentService = retrofitAPI.create(UploadEndUsersDocumentService.class);

                Call call = mUploadEndUsersDocumentService.getUploadEndUsersDocument(dataRequest, requestBodyMap, PreferenceUtils.getAccessToken(UploadListActivity.this));

                call.enqueue(new Callback<UploadDocumentResponse>() {
                    @Override
                    public void onResponse(Response<UploadDocumentResponse> response, Retrofit retrofit) {
                        UploadDocumentResponse apiResponse = response.body();
                        if (apiResponse != null)
                        {
                            Log.d("Upload status", apiResponse.toString());

                            if (apiResponse.getStatus().getCode() == true)
                            {
                                if(!((Activity) context ).isFinishing())
                                {
                                    showAlertMessage(apiResponse.getStatus().getMessage(), false, "");
                                }
                                uploadFailedList.add(UploadList.get(i));
                                PreferenceUtils.setupload(context, uploadFailedList, "key");
                            }

                            UploadList.remove(i);
                            PreferenceUtils.setupload(UploadListActivity.this, UploadList, "key");
                            upload(0);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("PinDevice error", t.getMessage());
                    }
                });
            }
        } else {

            PreferenceUtils.setupload(context, uploadFailedList, "key");
            customAdapter = new UploadListAdapter(UploadListActivity.this, uploadFailedList);
            upload_list.setAdapter(customAdapter);

            if (uploadFailedList.size() == 0)
            {
                if(!((Activity) context ).isFinishing()) {
                    showAlertMessage("All documents uploaded successfully", true, "");
                }
                empty_view.setVisibility(View.VISIBLE);
            }
            else
            {
                empty_view.setVisibility(View.GONE);
                uploadFailedList.clear();

                final AlertDialog.Builder builder = new AlertDialog.Builder(UploadListActivity.this);
                LayoutInflater inflater = (LayoutInflater) UploadListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                        upload(0);
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
    }

    private void openBottomSheet() {

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_upload, null);
        camera = (TextView) view.findViewById(R.id.camera);
        video = (TextView) view.findViewById(R.id.video);
        browse = (TextView) view.findViewById(R.id.browse);
        cancel =(TextView) view.findViewById(R.id.cancel);
        final Dialog mBottomSheetDialog = new Dialog(UploadListActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

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
        browse.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                storageAccessPermission();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();

            }
        });
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

        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);

        // set video quality
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(takeVideoIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
        upload_list.setAdapter(customAdapter);
    }

    private void cameraAndStoragePermission()
    {
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

    private void cameraAccess()
    {
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(1);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        UploadList =  PreferenceUtils.getupload(UploadListActivity.this, "key");
        startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE_CODE);
        upload_list.setAdapter(customAdapter);
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
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        pickFile();
    }

    public void pickFile() {
        DialogProperties properties = new DialogProperties();
       // properties.extensions = new String[]{".pdf", ".doc", ".docx", ".xlsx", ".txt", ".jpg", ".png", ".bmp", ".gif", ".tiff", ".jpeg", ".xls" ,".mp4",".mp3",".wav",".mov",".avi",".m4a",".jpeg",".mkv",".ppt",".pptx"};
        properties.selection_mode = DialogConfigs.MULTI_MODE;
        FilePickerDialog dialog = new FilePickerDialog(UploadListActivity.this, properties);
        dialog.setTitle("Select a File");
        UploadList = PreferenceUtils.getupload(UploadListActivity.this,"key");
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (UploadList.size() + files.length <= 10) {
                    int fileCount = files.length;
                    for (int i = 0; i <= fileCount - 1; i++) {
                        UploadList.add(String.valueOf(new File(files[i])));
                        PreferenceUtils.setupload(UploadListActivity.this,UploadList,"key");
                    }

                }
                else
                {
                    Toast.makeText(UploadListActivity.this,"Please select less then 10 documents ",Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(UploadListActivity.this,UploadListActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
  /*  public void requestRuntimePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(UploadListActivity.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UploadListActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            fileUri = data.getData();
          /*  UploadList=PreferenceUtils.getupload(UploadListActivity.this,"key");
            UploadList.add(String.valueOf(fileUri));
            PreferenceUtils.setupload(UploadListActivity.this,UploadList,"key");*/


            Uri uri = null;

            if (null != data) { // checking empty selection
                if (null != data.getClipData()) { // checking multiple selection or not
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        ArrayList<Uri> uris = new ArrayList<>();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            uri = item.getUri();
                            uris.add(uri);

                            String filePath = getRealPathFromURIPath(uri, UploadListActivity.this);

                            //File file = new File(filePath);

                            //ArrayList<String> filePathList = new ArrayList<String>();
                            //filePathList.add(String.valueOf(filePath));
                            UploadList =  PreferenceUtils.getupload(UploadListActivity.this, "key");
                            UploadList.add(filePath);
                            PreferenceUtils.setupload(UploadListActivity.this,UploadList,"key");
                        }

                    } else {
                        uri = data.getData();
                        String filePath = getRealPathFromURIPath(uri, UploadListActivity.this);
                        //File file = new File(filePath);

                        //ArrayList<String> filePathList = new ArrayList<String>();
                        //filePathList.add(filePath);
                        UploadList =  PreferenceUtils.getupload(UploadListActivity.this, "key");
                        UploadList.add(filePath);
                        PreferenceUtils.setupload(UploadListActivity.this,UploadList,"key");


                        //uploadGalleryImage(file, filePathList);
                    }
                }

                if((UploadList == null )||(UploadList.size()==0))
                {
                    String filepath = getRealPathFromURIPath(fileUri, UploadListActivity.this);
                    UploadList = PreferenceUtils.getupload(UploadListActivity.this,"key");
                    UploadList.add(filepath);
                    PreferenceUtils.setupload(UploadListActivity.this,UploadList,"key");
                }
            }


            UploadList =  PreferenceUtils.getupload(UploadListActivity.this, "key");
            UploadListAdapter customAdapter = new UploadListAdapter(UploadListActivity.this, UploadList);
            upload_list.setAdapter(customAdapter);
        }
        else if (requestCode == REQUEST_CAPTURE_IMAGE_CODE && resultCode == RESULT_OK) {

            if (resultCode == RESULT_OK) {
                // uploadImage(fileUri.getPath());

               /* Uri uri = data.getData();

                String filePath = getRealPathFromURIPath(uri, MyFoldersDMSActivity.this);
                File file = new File(filePath);

                ArrayList<String> filePathList = new ArrayList<String>();
                filePathList.add(filePath);*/

                String filePath = fileUri.getPath();
/*
                File file = new File(filePath);
*/

                // ArrayList<String> filePathList = new ArrayList<String>();
                //  filePathList.add(filePath);
                UploadList =  PreferenceUtils.getupload(UploadListActivity.this, "key");
                UploadList.add(String.valueOf(filePath));
                PreferenceUtils.setupload(UploadListActivity.this,UploadList,"key");
                upload_list.setAdapter(customAdapter);



            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "Error capturing image", Toast.LENGTH_SHORT).show();
            }
            upload_list.setAdapter(customAdapter);
        }
        else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            // uriVideo = data.getData();
            // uploadVideo(imageStoragePath);
            String filePath = imageStoragePath;
/*
                File file = new File(filePath);
*/

            //  ArrayList<String> filePathList = new ArrayList<String>();
            // filePathList.add(filePath);
            UploadList = PreferenceUtils.getupload(UploadListActivity.this,"key");
            UploadList.add(String.valueOf(filePath));
            PreferenceUtils.setupload(UploadListActivity.this,UploadList,"key");
            upload_list.setAdapter(customAdapter);




        }
        upload_list.setAdapter(customAdapter);
        finish();
        startActivity(getIntent());
    }

    private String getRealPathFromURIPath(Uri uri, Activity context) {

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
    public void onBackPressed() {

          if(PreferenceUtils.getupload(context, "key") != null && PreferenceUtils.getupload(context, "key").size() > 0)
           {
               showUploadWarningMessage();
           }
           else
          {
              Intent intent= new Intent(UploadListActivity.this,NavigationMyFolderActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              startActivity(intent);
          }


    }

    private void showUploadWarningMessage()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(UploadListActivity.this);
        LayoutInflater inflater = (LayoutInflater) UploadListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.upload_cancel_alert, null);
        builder.setView(view);
        builder.setCancelable(false);

        final Button BtnAllow = (Button) view.findViewById(R.id.allow_button);
        BtnAllow.setText("Continue Upload");
        final Button BtnCancel = (Button) view.findViewById(R.id.cancel_button);
        BtnCancel.setText("Cancel Upload");
        TextView textView =(TextView) view.findViewById(R.id.txt_message);
        textView.setVisibility(View.GONE);
        TextView text = (TextView) view.findViewById(R.id.message);
        text.setText("Some of the files are not yet uploaded. Do you want to cancel all the uploads?");


    //    getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        BtnAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomAlertDialog.dismiss();

            }
        });

        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> uploadlist = new ArrayList<>();
                PreferenceUtils.setupload(context, uploadlist, "key");
                Intent intent= new Intent(UploadListActivity.this,NavigationMyFolderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        mCustomAlertDialog = builder.create();
        mCustomAlertDialog.show();

    }



    public void showAlertMessage(String message, boolean buttonEnabled, String unsupported)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(UploadListActivity.this);
        LayoutInflater inflater = (LayoutInflater) UploadListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);

        final Button BtnAllow = (Button) view.findViewById(R.id.allow_button);
        BtnAllow.setText("Ok");
        final Button BtnCancel = (Button) view.findViewById(R.id.cancel_button);
        TextView textView =(TextView) view.findViewById(R.id.txt_message);
        textView.setVisibility(View.GONE);
        TextView text = (TextView) view.findViewById(R.id.message);
        text.setText(message);


     //   getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        BtnCancel.setVisibility(View.GONE);
        BtnAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomAlertDialog.dismiss();

                if(!unsupported.isEmpty() && unsupported.equalsIgnoreCase("unsupported"))
                {
                    menuItemUpload.setVisible(true);
                }

                if(buttonEnabled == true)
                {
                    menuItemAdd.setVisible(true);
                //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }

            }
        });

        mCustomAlertDialog = builder.create();
        mCustomAlertDialog.show();

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
                            Toast.makeText(context, "Camera and storage access permission denied", Toast.LENGTH_LONG).show();
                        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(context, "Storage access permission denied", Toast.LENGTH_LONG).show();
                        } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(context, "Camera access permission denied", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        Toast.makeText(context, "Permission denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    documentUpload();
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.upload_items, menu);
         menuItemUpload = menu.findItem(R.id.action_upload);
         menuItemAdd = menu.findItem(R.id.action_add);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_upload:
                uploadDocuments();
                break;

            case R.id.action_add:
                openBottomSheet();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadDocuments()
    {
        if (NetworkUtils.isNetworkAvailable(context)) {
            int choosenFilesCount = PreferenceUtils.getupload(UploadListActivity.this, "key").size();
            menuItemAdd.setVisible(false);
            menuItemUpload.setVisible(false);
         //   getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            Boolean isError = false;

            if (choosenFilesCount > 10) {
                if(!((Activity) context ).isFinishing()) {
                    showAlertMessage("Please select less then 10 documents", false, "");
                }
                isError = true;
            } else {
                for (int i = 0; i < choosenFilesCount; i++) {
                    File file = new File(UploadList.get(i));
                    float file_size = Float.parseFloat(String.valueOf(file.length() / 1024 / 1024));
                    String size = PreferenceUtils.getMaxSizeUpload(UploadListActivity.this);
                    float sizeAPI = Float.parseFloat(size);

                    if (file_size > sizeAPI) {
                        if(!((Activity) context ).isFinishing()) {
                            showAlertMessage(UploadList.get(i) + "exceeds" + PreferenceUtils.getMaxSizeUpload(UploadListActivity.this) + "MB", false, "");
                        }
                        isError = true;
                        break;
                    } else {
                        String[] fileParts = UploadList.get(i).split("\\.");
                        String fileExtension = fileParts[fileParts.length - 1];
                        Boolean validFormat = false;

                        for (int j = 0; j < PreferenceUtils.getFileFormats(UploadListActivity.this, "key").size(); j++) {
                            if (fileExtension.equalsIgnoreCase(PreferenceUtils.getFileFormats(UploadListActivity.this, "key").get(j))) {
                                validFormat = true;
                                break;
                            }
                        }

                        if (!validFormat) {

                            menuItemUpload.setVisible(false);
                            if(!((Activity) context ).isFinishing()) {
                                showAlertMessage(UploadList.get(i) + " " + fileExtension + " is unsupported", false, "unsupported");
                            }
                            isError = true;
                            break;
                        }
                    }
                }

                if (!isError) {
                    uploadFailedList.clear();

                    if (choosenFilesCount > 0) {
                        upload(0);
                    }
                } else {
                    menuItemUpload.setVisible(true);
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCustomAlertDialog != null) {
            mCustomAlertDialog.dismiss();
            mCustomAlertDialog = null;
        }
    }

}
