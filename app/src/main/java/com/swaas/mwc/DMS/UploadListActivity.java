package com.swaas.mwc.DMS;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.swaas.mwc.API.Model.ApiResponse;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.ListPinDevices;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.UploadDocumentResponse;
import com.swaas.mwc.API.Model.UploadEndUserDocumentsRequest;
import com.swaas.mwc.API.Model.UploadEndUsersDocumentResponse;
import com.swaas.mwc.API.Service.UploadEndUsersDocumentService;
import com.swaas.mwc.Common.CameraUtils;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.LoginAgreeTermsAcceptanceActivity;
import com.swaas.mwc.Login.LoginHelpUserGuideActivity;
import com.swaas.mwc.Login.Notifiy;
import com.swaas.mwc.Login.Pin;
import com.swaas.mwc.Login.RadioAdapter;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.BottomSheet;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.swaas.mwc.Common.CameraUtils.getOutputMediaFile;
import static com.swaas.mwc.Common.CameraUtils.getOutputMediaFileUri;


/**
 * Created by barath on 8/8/2018.
 */

public class UploadListActivity extends Activity{

    ImageView back;
    ImageView add;
    ImageView upload;
    RecyclerView upload_list;
    Uri fileUri;
    TextView camera,video,browse,cancel;
    ArrayList<String> UploadList=new ArrayList<>();
    private  String imageStoragePath;
    String path;
    UploadListAdapter customAdapter;
    public static final int REQUEST_GALLERY_CODE = 200;
    public static final int REQUEST_CAPTURE_IMAGE_CODE = 300;
    public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 400;
    public static final  int REQUEST_STORAGE_PERMISSION = 500;
    public static final int REQUEST_CAMERA_PERMISSION=100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    int fileindex;
    int size;
    AlertDialog mCustomAlertDialog;
    String filepath=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_list);


        back = (ImageView) findViewById(R.id.back_image_view);
        add = (ImageView) findViewById(R.id.add);
        upload = (ImageView) findViewById(R.id.uploadlist);
        upload_list = (RecyclerView) findViewById(R.id.list_upload);
        upload_list.setLayoutManager(new LinearLayoutManager(this));
        final ArrayList<String> UploadList = PreferenceUtils.getupload(UploadListActivity.this, "key");
        customAdapter = new UploadListAdapter(UploadListActivity.this, UploadList);
        upload_list.setAdapter(customAdapter);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openBottomSheet();

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;
                size = PreferenceUtils.getupload(UploadListActivity.this,"key").size();
                add.setEnabled(false);
                upload.setEnabled(false);
                back.setEnabled(false);
                if(size>10){
                    Toast.makeText(UploadListActivity.this,"Please select less then 10 documents ",Toast.LENGTH_SHORT).show();

                }
                else{
                    upload(index);
                }



            }

        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void upload(final int i) {

        fileindex = i;
        customAdapter.ActivateLoad(true,fileindex);
        customAdapter.notifyDataSetChanged();
        UploadList = PreferenceUtils.getupload(UploadListActivity.this, "key");
        size = UploadList.size();

        if (size > fileindex) {

            path = String.valueOf(UploadList.get(fileindex));

            if (NetworkUtils.isNetworkAvailable(UploadListActivity.this)) {

                File file = new File(path);

                Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

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
                        if (apiResponse != null) {

                            if (apiResponse.getStatus().getCode() == Boolean.FALSE) {

                                UploadList.remove(fileindex);
/*
                                    fileindex+=1;
*/
                                PreferenceUtils.setupload(UploadListActivity.this, UploadList, "key");
                                upload(fileindex);

                                String mMessage = apiResponse.getStatus().getMessage().toString();
                                //Toast.makeText(UploadListActivity.this, mMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                String mMessage = apiResponse.getStatus().getMessage().toString();
                                // Toast.makeText(UploadListActivity.this, mMessage, Toast.LENGTH_SHORT).show();
                                fileindex += 1;
                                upload(fileindex);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("PinDevice error", t.getMessage());
                    }
                });
            }
        } else {
            customAdapter.ActivateLoad(false,fileindex);
            customAdapter.notifyDataSetChanged();
            UploadList = PreferenceUtils.getupload(UploadListActivity.this,"key");
            int size1 = UploadList.size();
            if (size1 == 0) {
                customAdapter.ActivateDone(true,fileindex);
                customAdapter.notifyDataSetChanged();
                //Toast.makeText(UploadListActivity.this, "All files were uploaded Successfully", Toast.LENGTH_SHORT).show();
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
                text.setText("All Documents were Uploaded Successfully");
                back.setEnabled(false);
                BtnCancel.setVisibility(View.INVISIBLE);
                BtnAllow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomAlertDialog.dismiss();
                        Intent intent = new Intent(UploadListActivity.this,MyFoldersDMSActivity.class);
                        startActivity(intent);

                    }
                });

              /*  BtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomAlertDialog.dismiss();
                    }
                });*/

                mCustomAlertDialog = builder.create();
                mCustomAlertDialog.show();
            } else {
                // Toast.makeText(UploadListActivity.this, String.valueOf(size1) + "files were not uploaded", Toast.LENGTH_SHORT).show();

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
                        UploadList = PreferenceUtils.getupload(UploadListActivity.this,"key");
                        customAdapter = new UploadListAdapter(UploadListActivity.this, UploadList);
                        upload_list.setAdapter(customAdapter);
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

                if(Build.VERSION.SDK_INT>=24){
                    try{
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                // requestCameraPermission();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(1);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                UploadList =  PreferenceUtils.getupload(UploadListActivity.this, "key");
                startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE_CODE);
                upload_list.setAdapter(customAdapter);
                mBottomSheetDialog.dismiss();

            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT>=24){
                    try{
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                //  requestCameraPermission();
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
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(takeVideoIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
                upload_list.setAdapter(customAdapter);
                mBottomSheetDialog.dismiss();

            }
        });
        browse.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=24){
                    try{
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                //requestStoragePermission();
              //  Intent openGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
               // openGalleryIntent.setType("*/*");
               // openGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
               // startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
                pickFile();
                mBottomSheetDialog.dismiss();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                onBackPressed();
            }
        });
    }

    public void pickFile() {
        DialogProperties properties = new DialogProperties();
        properties.extensions = new String[]{".pdf", ".doc", ".docx", ".xlsx", ".txt", ".jpg", ".png", ".bmp", ".gif", ".tiff", ".jpeg", ".xls" ,".mp4",".mp3",".wav",".mov",".avi",".m4a",".jpeg",".mkv",".ppt",".pptx"};
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
        super.onBackPressed();
        UploadList.clear();
        PreferenceUtils.setupload(UploadListActivity.this,UploadList,"key");
        Intent intent= new Intent(UploadListActivity.this,MyFoldersDMSActivity.class);
        startActivity(intent);
    }

   /* private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE_PERMISSION);
    }

    private void requestCameraPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION);
    }


   *//* private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE_PERMISSION);
    }*//*


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == REQUEST_STORAGE_PERMISSION){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == REQUEST_CAMERA_PERMISSION){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }*/

       /* if(requestCode == REQUEST_STORAGE_PERMISSION){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }*/
    // }

/*
    public static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                MyFoldersDMSActivity.GALLERY_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(MyFoldersDMSActivity.GALLERY_DIRECTORY_NAME, "Oops! Failed create "
                        + MyFoldersDMSActivity.GALLERY_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Preparing media file naming convention
        // adds timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MyFoldersDMSActivity.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + "." + MyFoldersDMSActivity.IMAGE_EXTENSION);
        } else if (type == MyFoldersDMSActivity.MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + "." + MyFoldersDMSActivity.VIDEO_EXTENSION);
        } else {
            return null;
        }

        return mediaFile;
    }
*/

}
