package com.mwc.docportal.Login;


import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.UploadModel;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Common.PathUtil;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Fragments.LoginFragment;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.RootActivity;
import com.mwc.docportal.Utils.Constants;
import com.mwc.docportal.Utils.SplashScreen;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by harika on 13-06-2018.
 */

public class LoginActivity extends RootActivity {
    LoginFragment mLoginFragment;
    KeyguardManager keyguardManager;
    public final int CREDENTIALS_RESULT = 12345;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    int backButtonCount;

    String documentVersionId = "";
    String notificationType = "";
    Context context = this;
    public static final int REQUEST_STORAGE_PERMISSION = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

          /*  LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();*/


            if (imageUri != null) {

                if(imageUri.getScheme().contains("file"))
                {
                    String filePath = imageUri.getPath();

                    if (filePath != null && !filePath.isEmpty()) {
                        UploadModel fileUploadModel = new UploadModel();
                        fileUploadModel.setFilePath(filePath);
                        GlobalVariables.otherAppDocumentList.add(fileUploadModel);
                    }
                }
                else {

                    String fileName = getFileNameFromUri(imageUri);

                    if (fileName == null) {
                        fileName = " ";
                    }
                    String filePath = storeAllDataInLocalFromUri(imageUri, fileName);

                    if (filePath != null && !filePath.isEmpty()) {
                        UploadModel fileUploadModel = new UploadModel();
                        fileUploadModel.setFilePath(filePath);
                        GlobalVariables.otherAppDocumentList.add(fileUploadModel);
                    }

                    Log.d("FilePath", filePath);
                }


/*
                if(imageUri.toString().contains("com.google.android.apps.docs.storage"))
                {
                    String fileName = getFileNameFromUri(imageUri);
                    if(fileName != null && !fileName.isEmpty())
                    {
                        String filePath = storeAllDataInLocalFromUri(imageUri, fileName);

                        if (filePath != null && !filePath.isEmpty()) {
                            UploadModel fileUploadModel = new UploadModel();
                            fileUploadModel.setFilePath(filePath);
                            GlobalVariables.otherAppDocumentList.add(fileUploadModel);
                        }

                        Log.d("FilePath", filePath);

                    }

                }
                else {

                    String filePath;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        filePath = getRealImagePathFromURI(imageUri);
                    } else {
                        //   filePath = getRealPathFromURIPath(imageUri, context);
                        filePath = PathUtil.getPath(context, imageUri);
                    }

                    if (filePath == null) {
                        filePath = getVideoUriRealPath(imageUri);

                        //  filePath = imageUri.getPath();

                        //        getImgaeRealPathFromUri(imageUri);
                    }

                    if (filePath != null && !filePath.isEmpty()) {
                        UploadModel fileUploadModel = new UploadModel();
                        fileUploadModel.setFilePath(filePath);
                        GlobalVariables.otherAppDocumentList.add(fileUploadModel);
                    }
                }*/
            }
            else
            {
                GlobalVariables.isFromDocumentLink = true;
            }

           /* if(transparentProgressDialog != null && transparentProgressDialog.isShowing())
            {
                transparentProgressDialog.dismiss();
            }*/
        }
        else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            List<UploadModel> fileUploadList = null;
            ArrayList<Uri> imageUrisList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

           /* LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();*/

            if (imageUrisList != null) {
                for (Uri fileUri : imageUrisList) {

                    if(fileUri.getScheme().contains("file"))
                    {
                        String filePath = fileUri.getPath();
                        if(fileUploadList == null)
                        {
                            fileUploadList = new ArrayList<>();
                        }

                        if (filePath != null && !filePath.isEmpty()) {
                            UploadModel uploadModel = new UploadModel();
                            uploadModel.setFilePath(filePath);
                            fileUploadList.add(uploadModel);
                        }
                    }
                    else
                    {
                        String fileName = getFileNameFromUri(fileUri);
                        if(fileName == null)
                        {
                            fileName = " ";
                        }

                        String filePath = storeAllDataInLocalFromUri(fileUri, fileName);
                        if(fileUploadList == null)
                        {
                            fileUploadList = new ArrayList<>();
                        }

                        if (filePath != null && !filePath.isEmpty()) {
                            UploadModel uploadModel = new UploadModel();
                            uploadModel.setFilePath(filePath);
                            fileUploadList.add(uploadModel);
                        }

                        Log.d("FilePath", filePath);

                    }




                    /*String path;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        path = getRealImagePathFromURI(fileUri);
                    }
                    else
                    {
                        path = PathUtil.getPath(context, fileUri);
                    }*/
                 //   String path = getRealPathFromURIPath(fileUri, context);


                   /* if(path != null && !path.isEmpty())
                    {
                        UploadModel uploadModel = new UploadModel();
                        uploadModel.setFilePath(path);
                        fileUploadList.add(uploadModel);
                    }*/

                }

                if(fileUploadList != null && fileUploadList.size() >0)
                {
                    GlobalVariables.otherAppDocumentList.addAll(fileUploadList);
                }
            }

        //    getWindow().setBackgroundDrawableResource(R.drawable.empty_drawable_file);
          /*  if(transparentProgressDialog != null && transparentProgressDialog.isShowing())
            {
                transparentProgressDialog.dismiss();
            }*/

        }

        if(GlobalVariables.otherAppDocumentList != null && GlobalVariables.otherAppDocumentList.size() > 0)
        {
            GlobalVariables.isMoveInitiated = true;
            GlobalVariables.selectedActionName = "upload";
            GlobalVariables.activityFinishCount = 0;
        }




        if(getIntent().getStringExtra("document_version_id") != null)
        {
            documentVersionId = getIntent().getStringExtra("document_version_id");
            notificationType = getIntent().getStringExtra("notification_type");
            PreferenceUtils.setPushNotificationDocumentVersionId(LoginActivity.this, documentVersionId);
            PreferenceUtils.setPushNotificationDocumentShare(LoginActivity.this, notificationType);
        }


        mLoginFragment = new LoginFragment();
        loadFTLFragment();
    }



    private void loadFTLFragment() {

        getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment, mLoginFragment).
                addToBackStack(null).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
           storageAccessPermission();
    }


    private void storageAccessPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int storagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (storagePermission == PackageManager.PERMISSION_GRANTED) {
                checkAppStatus();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        } else {
            checkAppStatus();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkAppStatus() {
        String loginStatus = "";
        getLoggedInStatus();

        if(mAccountSettingsResponses != null && mAccountSettingsResponses.size() > 0) {
            loginStatus = mAccountSettingsResponses.get(0).getLogin_Complete_Status();
        } else {
            loginStatus = "";
        }


        if (TextUtils.isEmpty(loginStatus)) {

            if(GlobalVariables.isFromDocumentLink == true)
            {
                showAlertForLinkDocument();
            }

        }
        else
        {
            if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.All_Settings_Completed)))
            {
                gotoSplashScreenPage();
            }
            else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Login_Completed)))
            {
                boolean isTouchIdEnabled = checkTouchIdEnabled();
                if(isTouchIdEnabled)
                {

                //    checkTouchIdCredentials();

                    Intent intent = new Intent(LoginActivity.this, Touchid.class);
                    startActivity(intent);
                    LoginActivity.this.finish();


                }
                else
                {
                    Intent intent = new Intent(LoginActivity.this, Notifiy.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }
            }
            else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Local_Auth_Completed)))
            {
                Intent intent = new Intent(LoginActivity.this, Notifiy.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
            else if(mAccountSettingsResponses.get(0).getIs_Terms_Accepted().equals("0"))
            {
                startActivity(new Intent(LoginActivity.this, LoginAgreeTermsAcceptanceActivity.class));
                LoginActivity.this.finish();
            }
            else if(mAccountSettingsResponses.get(0).getIs_Help_Accepted().equals("1"))
            {
                startActivity(new Intent(LoginActivity.this, LoginHelpUserGuideActivity.class));
                LoginActivity.this.finish();

            }
           /* else
            {
                gotoSplashScreenPage();
            }*/

        }

       /* if (TextUtils.isEmpty(loginStatus)) {
        }
        else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Login_Completed))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkSecurity();
            }
        }
        else if(loginStatus.equalsIgnoreCase(String.valueOf(Constants.Local_Auth_Completed))) {

            Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
            startActivity(intent);
            finish();

            int timeout = 2000; // make the activity visible for 2 seconds

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    finish();
                    checkCredentials();

                    startActivity(new Intent(LoginActivity.this, Notifiy.class));
                    LoginActivity.this.finish();

                }
            }, timeout);
        }
        else {
            checkAppStatusAfterPushNotification(mAccountSettingsResponses);
        }*/
    }





    private void getLoggedInStatus() {

        AccountSettings accountSettings = new AccountSettings(LoginActivity.this);
        accountSettings.SetLoggedInCB(new AccountSettings.GetLoggedInCB() {
            @Override
            public void getLoggedInSuccessCB(List<AccountSettingsResponse> accountSettingsResponse) {
                if (accountSettingsResponse != null && accountSettingsResponse.size() > 0) {
                    mAccountSettingsResponses = accountSettingsResponse;
                }
            }

            @Override
            public void getLoggedInFailureCB(String message) {

            }
        });

        accountSettings.getLoggedInStatusDetails();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkSecurity() {
        KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure() == true) {
            Intent intent = new Intent(LoginActivity.this, Touchid.class);
            startActivity(intent);
            LoginActivity.this.finish();
        } else {

                Intent intent = new Intent(LoginActivity.this, Notifiy.class);
                startActivity(intent);
                LoginActivity.this.finish();


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

    private void gotoSplashScreenPage()
    {
        Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
        startActivity(intent);
        finish();
    }



    private boolean checkTouchIdEnabled()
    {
        KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.isKeyguardSecure();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void checkTouchIdCredentials()
    {

        keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        Intent credentialsIntent = keyguardManager.createConfirmDeviceCredentialIntent(Constants.ConfirmPassword + getResources().getString(R.string.app_name), Constants.PatternLockMessage);

        if (credentialsIntent != null) {
            startActivityForResult(credentialsIntent, CREDENTIALS_RESULT);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREDENTIALS_RESULT) {

            if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(LoginActivity.this, Notifiy.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    LoginActivity.this.finish();

            } else {
              //  Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show();
                showAuthenticationFailureMessage();

            }

        }

    }

    private void showAuthenticationFailureMessage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText("You have clicked the cancel button. Unable to complete authentication.");

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setVisibility(View.GONE);

        okButton.setText("Ok");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                finish();
                moveTaskToBack(true);
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkAppStatus();
                } else {
                    finish();
                    moveTaskToBack(true);
                    Toast.makeText(context, "Storage access permission denied", Toast.LENGTH_LONG).show();
                }
                break;
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


    public String getRealImagePathFromURI(final Uri uri)
    {
        Uri queryUri = MediaStore.Files.getContentUri("external");
        String columnData = MediaStore.Files.FileColumns.DATA;
        String columnSize = MediaStore.Files.FileColumns.SIZE;

        String[] projectionData = {MediaStore.Files.FileColumns.DATA};

        String name = null;
        String size = null;
        String path = null;

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if ((cursor != null)&&(cursor.getCount()>0)) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

            cursor.moveToFirst();

            name = cursor.getString(nameIndex);
            size = cursor.getString(sizeIndex);

            cursor.close();
        }

        if ((name!=null)&&(size!=null)){
            String selectionNS = columnData + " LIKE '%" + name + "' AND " +columnSize + "='" + size +"'";

            Cursor cursorLike = getContentResolver().query(queryUri, projectionData, selectionNS, null, null);

            if ((cursorLike != null)&&(cursorLike.getCount()>0)) {
                cursorLike.moveToFirst();
                int indexData = cursorLike.getColumnIndex(columnData);
                if (cursorLike.getString(indexData) != null) {
                    path = cursorLike.getString(indexData);
                }
                cursorLike.close();
            }
        }

        return path;

    }


    public String getRealPathFromURI(Uri contentUri)
    {
        String path = "";
        Cursor cursor = null;//  w ww. j a va  2s  .c  om
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj,
                    null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path =  cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return path;
    }

    public String getImgaeRealPathFromUri(Uri contenturi)
    {
        String path = "";
        if (contenturi == null) {
            return path;
        }
        final ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(contenturi, null, null, null, null);
       // Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
         //       projection, selection, null, null);
        if (cursor == null) {
            path = contenturi.getPath();
        } else if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int idx = 0;
            int i = cursor.getCount();
            int colCount = cursor.getColumnCount();

            int colpos = 0;

            String[] columnNames = cursor.getColumnNames();



            if(idx > 0)
            path = cursor.getString(idx);
            else
                path = "";

        }
        if (cursor != null) {
            cursor.close();
        }
        return path;
    }


    public String handleContentUri(Uri beamUri) {
        String filePath = null;
        // Position of the filename in the query Cursor
        int filenameIndex;
        // File object for the filename
        File copiedFile;
        // The filename stored in MediaStore
        String fileName;
        // Test the authority of the URI
        if (!TextUtils.equals(beamUri.getAuthority(), MediaStore.AUTHORITY)) {
            /*
             * Handle content URIs for other content providers
             */
            // For a MediaStore content URI
        } else {
            // Get the column that contains the file name
            String[] projection = { MediaStore.MediaColumns.DATA };
            Cursor pathCursor =
                    getContentResolver().query(beamUri, projection,
                            null, null, null);
            // Check for a valid cursor
            if (pathCursor != null &&
                    pathCursor.moveToFirst()) {
                // Get the column index in the Cursor
                filenameIndex = pathCursor.getColumnIndex(
                        MediaStore.MediaColumns.DATA);
                // Get the full file name including path
                fileName = pathCursor.getString(filenameIndex);
                // Create a File object for the filename
                copiedFile = new File(fileName);
                // Return the parent directory of the file
                filePath = copiedFile.getParentFile().getPath();
            } else {
                // The query didn't work; return null
                return null;
            }
        }
        return filePath;
    }


    protected String getVideoUriRealPath(Uri contentURI) {
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(contentURI.toString());
        if (!m.find()) {
         //   Log.e(ImageConverter.class.getSimpleName(), "ID for requested image not found: " + uri.toString());
            return filePath;
        }
        String imgId = m.group();

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ imgId }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }

    public String storeAllDataInLocalFromUri(Uri uri, String fileName) {

        String path = null;
        if (uri != null) {
            File file = new File(getCacheDir(), fileName);
            try
            {
                InputStream inputStream=getContentResolver().openInputStream(uri);
                try {

                    OutputStream output = new FileOutputStream(file);
                    try {
                        byte[] buffer = new byte[4 * 1024]; // or other buffer size
                        int read;

                        while ((read = inputStream.read(buffer)) != -1) {
                            output.write(buffer, 0, read);
                        }

                        output.flush();
                    } finally {
                        output.close();
                    }
                } finally {
                    inputStream.close();
                    path = file.getPath();
                  //  byte[] bytes = getFileFromPath(file);
                    //Upload Bytes.
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return path;
    }

    public static byte[] getFileFromPath(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }

    public String getFileNameFromUri(Uri contentUri)
    {

        String fileName = "";
        if (contentUri == null) {
            return fileName;
        }
        final ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(contentUri, null, null, null, null);
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();

         //   String[] columnNames = cursor.getColumnNames();
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            fileName = cursor.getString(nameIndex).trim();



            String fileExtensionData = fileName.substring( fileName.lastIndexOf(".") + 1);
            String extension = "." + GetFileExtension(contentUri);
            if(extension.equalsIgnoreCase(".null"))
            {
                if(!fileName.equalsIgnoreCase(fileExtensionData))
                {
                    extension ="."+ fileExtensionData;
                }
                else
                {
                     String mimeType = getMimeType(context, contentUri);
                     if(mimeType != null)
                     {
                        extension ="."+ PathUtil.guessExtensionFromMimeType(mimeType);
                     }
                }

            }

            if(!fileName.contains(extension))
            {
              fileName = fileName + extension;
            }

        }
        if (cursor != null) {
            cursor.close();
        }
        return fileName;
    }

    // Get Extension
    public String GetFileExtension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

        // Return file Extension
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri).toLowerCase());
    }



    private void showAlertForLinkDocument()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText("Links from other apps cannot be saved in Doc Portal. You may download file(s) and share them separately into Doc Portal.");

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("Cancel");
        cancelButton.setVisibility(View.GONE);

        okButton.setText("Ok");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                GlobalVariables.isFromDocumentLink = false;
                finishAffinity();

            }
        });



        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    public String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

   /* public void getFCMTokenRefresh()
    {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
//To do//
                            return;
                        }

// Get the Instance ID token//
                        String token = task.getResult().getToken();
                        String msg = getString(R.string.fcm_token, token);
                        Log.d(TAG, msg);

                    }
                });

    }*/


}
