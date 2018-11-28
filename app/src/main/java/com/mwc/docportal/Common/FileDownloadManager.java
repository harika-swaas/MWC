package com.mwc.docportal.Common;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;


import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.Database.OffLine_Files_Repository;
import com.mwc.docportal.RootActivity;
import com.mwc.docportal.Utils.Constants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by dinesh on 2/10/2017.
 */

public class FileDownloadManager extends RootActivity {
    String downloadUrl;
    DownloadManager downloadmanager;
    DownloadManager.Request request;
    Long imageDownloadId;
    Activity context;
    String fileTile;
    String fileDescription;
    String fileName, storagePath;
    static FileDownloadManager mFileDownloadManager;
    long downloadId;
    GetCategoryDocumentsResponse digitalAssets;
    FileDownloadListener mFileDownloadListener;
    public static final int REQUEST_STORAGE_PERMISSION = 4444;

    public FileDownloadManager(Activity context) {
        this.context = context;
    }

    public static FileDownloadManager getInstance(Activity context) {
        if (mFileDownloadManager == null) {
            mFileDownloadManager = new FileDownloadManager(context);
        }
        return mFileDownloadManager;
    }


    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String getDownloadUrl) {
        this.downloadUrl = getDownloadUrl;
    }

    public String getFileTile() {
        return fileTile;
    }

    public void setFileTitle(String fileTile) {
        this.fileTile = fileTile;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void downloadTheFile() {
        try {
            if (getDownloadUrl() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int storagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (storagePermission == PackageManager.PERMISSION_GRANTED) {
                        if (digitalAssets == null) {
                            successToDownload();
                        } else {
                            successToAssetDownload();
                        }
                    } else {
                        setmFileDownloadManager(mFileDownloadManager);
                        context.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                    }
                } else {
                    if (digitalAssets == null) {
                        successToDownload();
                    } else {
                        successToAssetDownload();
                    }
                }
            }
        } catch (Exception e) {
            Log.d("parm exception", String.valueOf(e));
        }

    }

    void successToDownload() {
        downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    //    Toast.makeText(context, "Downloading...", Toast.LENGTH_LONG).show();
        Uri uri = Uri.parse(getDownloadUrl());
        request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        if (getNetworkType().equalsIgnoreCase("WIFI")) {
            request.setAllowedOverRoaming(false);
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                if (isDataRoamingEnabled(context)) {
                    request.setAllowedOverRoaming(true);
                } else {
                    request.setAllowedOverRoaming(true);
                }
            } else {
                request.setAllowedOverRoaming(true);
            }
        }

        request.setTitle(getFileTile());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(getDownloadUrl()));
        request.setMimeType(GetFileExtension());
        request.setDescription(getFileDescription());
        request.setVisibleInDownloadsUi(false);
        if (getFileTile() != null) {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getFileTile());
        } else {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "DocPortal" + MimeTypeMap.getFileExtensionFromUrl(getFileName()));
        }

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        downloadId = downloadmanager.enqueue(request);
    }

    void successToAssetDownload() {
        downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
   //     Toast.makeText(context, "Downloading...", Toast.LENGTH_LONG).show();
        Uri uri = Uri.parse(getDownloadUrl());
        request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        if (getNetworkType().equalsIgnoreCase("WIFI")) {
            request.setAllowedOverRoaming(false);
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                if (isDataRoamingEnabled(context)) {
                    request.setAllowedOverRoaming(true);
                } else {
                    request.setAllowedOverRoaming(true);
                }
            } else {
                request.setAllowedOverRoaming(true);
            }
        }
        request.setTitle(getFileTile());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(getDownloadUrl()));
        request.setMimeType(GetFileExtension());
        request.setDescription(getFileDescription());
        request.setVisibleInDownloadsUi(false);
        if (getFileTile() != null) {

            File dir = new File(new File(Environment.getExternalStorageDirectory(), Constants.Folder_Name), "");
            if (!dir.exists()){
                dir.mkdir();
            }

           /* final File file = new File(context.getCacheDir().getAbsolutePath() + File.separator + digitalAssets.getDocument_version_id());
            file.mkdirs();*/
            String[] array = getDownloadUrl().split("/");
          //  final String fileName = array[array.length - 1];
            String fileName = "";
            if(digitalAssets.getFilename() != null && !digitalAssets.getFilename().isEmpty())
            {
                fileName = digitalAssets.getDocument_version_id()+"-"+digitalAssets.getFilename();
            }
            else
            {
                fileName = digitalAssets.getDocument_version_id()+"-"+digitalAssets.getName();
            }




            File FILE = new File(new File(Environment.getExternalStorageDirectory(), Constants.Folder_Name), fileName);
            //File FILE = new File(Environment.getExternalStorageDirectory().toString()+"/"+Constants.Foldername+"/"+fileName);

            if (FILE.exists()){
                FILE.delete();
                FILE = new File(new File(Environment.getExternalStorageDirectory(), Constants.Folder_Name), fileName);
            }


            request.setDestinationInExternalPublicDir("/"+Constants.Folder_Name, fileName);
            String finalFileName = fileName;
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent)
                {

                    if (getDownloadUrl().endsWith("zip")) {

                        if(unpackZip(Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + Constants.Folder_Name, finalFileName)){

                            digitalAssets.setIs_Downloaded(1);
                            if (!getDownloadUrl().endsWith("zip")) {
                               /* try {
                                    OutputStream output = new FileOutputStream(new File(new File(Environment.getExternalStorageDirectory(), Constants.Folder_Name ), fileName));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }*/
                               // outputfilepath = Environment.getExternalStorageDirectory()+"/"+Constants.Folder_Name+"/"+fileName;

                                digitalAssets.setDownloadUrl(Environment.getExternalStorageDirectory()+"/"+Constants.Folder_Name+"/"+ finalFileName);
                            } else {
                                digitalAssets.setDownloadUrl(Environment.getExternalStorageDirectory()+"/"+Constants.Folder_Name+"/"+ finalFileName);
                            }

                            if (mFileDownloadListener != null) {
                                mFileDownloadListener.fileDownloadSuccess(digitalAssets.getDownloadUrl());
                            }

                        }else{
                            if (mFileDownloadListener != null) {
                                mFileDownloadListener.fileDownloadFailure();
                            }
                        }
                    }
                    else if (digitalAssets.getIs_Downloaded() != 1) {

                        digitalAssets.setIs_Downloaded(1);
                        if (!getDownloadUrl().endsWith("zip")) {
                            digitalAssets.setDownloadUrl(Environment.getExternalStorageDirectory()+"/"+Constants.Folder_Name+"/"+ finalFileName);
                        } else {
                            digitalAssets.setDownloadUrl(Environment.getExternalStorageDirectory()+"/"+Constants.Folder_Name+"/"+ finalFileName);
                        }


                        String action = intent.getAction();
                        if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                            if (mFileDownloadListener != null) {
                                mFileDownloadListener.fileDownloadSuccess(digitalAssets.getDownloadUrl());
                            }
                        }
                    }
                }
            };
            context.getApplicationContext().registerReceiver(onComplete, new
                    IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } else {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "DocPortal" + MimeTypeMap.getFileExtensionFromUrl(getFileName()));
        }

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        digitalAssets.setDownload_Id(downloadmanager.enqueue(request));
    }

    public String GetFileExtension() {
        String url = getDownloadUrl().toLowerCase();
        String type = "";
        if (url.lastIndexOf(".") != -1) {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(ext);
        } else {
            type = null;
        }
        return type;
    }

    public String getNetworkType() {
        String networkType = "";
        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting()) {
            networkType = "WIFI";
        } else if (mobile.isConnectedOrConnecting()) {
            networkType = "MOBILE";
        } else {
        }
        return networkType;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public Boolean isDataRoamingEnabled(Context context) {
        try {
            NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
// return true or false if data roaming is enabled or not
//alertfunction("13- 170 Filedownloadmanager isDataRoamingEnabled : "+ String.valueOf(info.isRoaming()));
//alertfunction("13- 170 Filedownloadmanager isDataRoamingEnabled : "+ String.valueOf(Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.DATA_ROAMING) == 1));
//return info.isRoaming();
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.DATA_ROAMING) == 1;
//return Settings.Global.getInt(context.getContentResolver(), Settings.Global.DATA_ROAMING) == 1;
        } catch (Exception e) {
//alertfunction("14- 175 Filedownloadmanager isDataRoamingEnabled excempt : "+ e.toString());
// return null if no such settings exist (device with no radio data ?)
            Log.d("Error", e.toString());
            return null;
        }
    }

   /* public void downloadAssetThumnail(OfflineFiles digitalAssets) {
        downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Toast.makeText(context, "Downloading...", Toast.LENGTH_LONG).show();
        Uri uri = Uri.parse(digitalAssets.getDA_Thumbnail_URL());
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        final File file = new File(context.getCacheDir().getAbsolutePath() + File.separator + digitalAssets.getDA_Code());
        file.mkdir();
        String extension = digitalAssets.getDA_Thumbnail_URL().split("\\.")[digitalAssets.getDA_Thumbnail_URL().split("\\.").length - 1];
        request.setDestinationInExternalPublicDir(file.getAbsolutePath(), digitalAssets.getDA_Code() + "_thumbnail." + extension);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        digitalAssets.setDownload_Id(downloadmanager.enqueue(request));
    }*/

    public void cancelDownload(GetCategoryDocumentsResponse digitalAsset) {
        if (digitalAsset != null) {
            downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadmanager.remove(digitalAsset.getDownload_Id());
        }
    }

    public GetCategoryDocumentsResponse getDigitalAssets() {
        return digitalAssets;
    }

    public void setDigitalAssets(GetCategoryDocumentsResponse digitalAssets) {
        this.digitalAssets = digitalAssets;
    }

    private boolean unpackZip(String path, String zipname) {
        InputStream is;
        ZipInputStream zis;
        try {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;
            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();
                File file = new File(path + filename);
                if (!file.exists())
                    file.getParentFile().mkdirs();
                else if (!file.isDirectory() && file.canWrite()) {
                    file.delete();
                    file.getParentFile().mkdirs();
                }
                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }
                FileOutputStream fout = new FileOutputStream(path + filename);
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }
                fout.close();
                zis.closeEntry();
            }

            zis.close();
            File file = new File(path + zipname);
            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (mFileDownloadListener != null) {
                mFileDownloadListener.fileDownloadFailure();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (mFileDownloadListener != null) {
                mFileDownloadListener.fileDownloadFailure();
            }
        }

        return true;
    }

    public interface FileDownloadListener {
        void fileDownloadSuccess(String path);

        void fileDownloadFailure();
    }

    public void setmFileDownloadListener(FileDownloadListener fileDownloadListener) {
        mFileDownloadListener = fileDownloadListener;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (digitalAssets == null) {
                        successToDownload();
                    } else {
                        successToAssetDownload();
                    }
                } else {
                    Toast.makeText(context, "Storage access permission denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}