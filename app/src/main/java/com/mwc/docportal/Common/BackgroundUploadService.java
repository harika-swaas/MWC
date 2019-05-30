package com.mwc.docportal.Common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.UploadDocumentResponse;
import com.mwc.docportal.API.Model.UploadEndUserDocumentsRequest;
import com.mwc.docportal.API.Model.UploadModel;
import com.mwc.docportal.API.Service.UploadEndUsersDocumentService;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.DMS.UploadListActivity;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.vincent.filepicker.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class BackgroundUploadService extends IntentService
{
    int index = 0;
 //   List<UploadModel> uploadDataList;
    int notificationId = 1234;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    List<UploadModel> uploadFailedList;
    private Context mContext;
    public static final String TAG = "FCMNotification";
    NotificationCompat.BigTextStyle bigText;
    //  UploadNotificationReceiver uploadNotificationReceiver;
    public BackgroundUploadService(String name) {
        super(name);
    }

    public BackgroundUploadService() {
        super("BackgroundUploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        List<UploadModel> uploadDataList = (ArrayList<UploadModel>)intent.getSerializableExtra("UploadedList");
        if(uploadDataList != null && uploadDataList.size() > 0)
        {
            GlobalVariables.isBackgroundProcessRunning = true;
            List<UploadModel> removeingList = null;
        //    removeingList = PreferenceUtils.getFailureUploadList(mContext, "key");
            if(removeingList ==  null)
            {
                removeingList = new ArrayList<>();
            }
            removeingList.addAll(uploadDataList);
            PreferenceUtils.setCurrentUploadlist(mContext, removeingList,"key");

            uploadFailedList = new ArrayList<>();
            uploadFailedList.clear();
            startNotification();
            if(PreferenceUtils.getCurrentUploadList(mContext, "key").size()> index) {
                uploadData(PreferenceUtils.getCurrentUploadList(mContext, "key").get(index));
            }
        }
    }

    private void startNotification()
    {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "channel-01";
        String channelName = getResources().getString(R.string.app_name);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            if(mChannel != null)
            {
                mNotifyManager.createNotificationChannel(mChannel);
            }
        }

        mBuilder = new NotificationCompat.Builder(mContext, channelId);
        // This is for cancel action Click
        Intent snoozeIntent = new Intent();
        snoozeIntent.setAction(Constants.ACTION_CANCEL);
        snoozeIntent.setClass(this, UploadNotificationReceiver.class);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, notificationId, snoozeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Long notification text

        bigText = new NotificationCompat.BigTextStyle();



        // This is for Notification Click
        Intent  intent = new Intent(this,LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        bigText.bigText(PreferenceUtils.getCurrentUploadList(mContext, "key")+" file(s)");

        mBuilder.setContentTitle("Uploading to "+getResources().getString(R.string.app_name))
                //  .setContentText(uploadDataList.size()+" file(s)")
                .setSmallIcon(R.mipmap.ic_notification_icon)
                .setStyle(bigText)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_cancel_btn, "CANCEL UPLOADS", snoozePendingIntent);



        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_notification_icon));
        }
        // must set color for notification icon
        mBuilder.setColor(getResources().getColor(R.color.notification_icon_color));
        mBuilder.setProgress(PreferenceUtils.getCurrentUploadList(mContext, "key").size(), index, false);
        if(PreferenceUtils.getCurrentUploadList(mContext, "key").size() == 1)
        {
            bigText.bigText(PreferenceUtils.getCurrentUploadList(mContext, "key").size()+" file remaining");
            mBuilder.setStyle(bigText);
            //  mBuilder.setContentText(uploadDataList.size()+" file remaining");
        }
        else
        {
            bigText.bigText(PreferenceUtils.getCurrentUploadList(mContext, "key").size()+" files remaining");
            mBuilder.setStyle(bigText);
            //  mBuilder.setContentText(uploadDataList.size()+" files remaining");
        }

        // Displays the progress bar for the first time.
        assert mNotifyManager != null;
        mNotifyManager.notify(TAG,notificationId, mBuilder.build());
    }


    private void uploadData(UploadModel uploadData)
    {
        if(checkIfNetworkAvailable(BackgroundUploadService.this))
        {
            File file = new File(uploadData.getFilePath());

            Retrofit retrofitAPI = RetrofitAPIBuilder.getUploadInstance();

            final UploadEndUserDocumentsRequest mUploadEndUserDocumentsRequest = new UploadEndUserDocumentsRequest(uploadData.getObjectId(),
                    uploadData.getFileName(), "", "", "");

            String request = new Gson().toJson(mUploadEndUserDocumentsRequest);

            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            //RequestBody converted Json string data request to API Call
            RequestBody dataRequest = RequestBody.create(MediaType.parse("text/plain"), request);

            //RequestBody filename to API Call
            Map<String, RequestBody> requestBodyMap = new HashMap<>();
            RequestBody reqBody = RequestBody.create(MediaType.parse("*/*"), file);
            requestBodyMap.put("file\"; filename=\"" + uploadData.getFileName(), reqBody);

            UploadEndUsersDocumentService mUploadEndUsersDocumentService = retrofitAPI.create(UploadEndUsersDocumentService.class);

            Call call = mUploadEndUsersDocumentService.getUploadEndUsersDocument(dataRequest, requestBodyMap, PreferenceUtils.getAccessToken(BackgroundUploadService.this));

            call.enqueue(new Callback<UploadDocumentResponse>() {
                @Override
                public void onResponse(Response<UploadDocumentResponse> response, Retrofit retrofit) {
                    UploadDocumentResponse apiResponse = response.body();
                    if (apiResponse != null)
                    {
                     //   Log.d("Upload status", apiResponse.toString());

                        String message = "";
                        if(apiResponse.getStatus().getMessage() != null)
                        {
                            message = apiResponse.getStatus().getMessage().toString();
                        }

                        if(apiResponse.getStatus().getCode() instanceof Double)
                        {
                            double status_value = new Double(response.body().getStatus().getCode().toString());
                            if (status_value == 401.3)
                            {
                                uploadFailedMessage("Access denied", true, uploadData);
                            }
                            else if(status_value ==  401 || status_value ==  401.0)
                            {
                                uploadFailedMessage("Session expired", false, uploadData);
                            }
                        }
                        else if(response.body().getStatus().getCode() instanceof Integer)
                        {
                            int integerValue = new Integer(response.body().getStatus().getCode().toString());
                            if(integerValue ==  401)
                            {
                                uploadFailedMessage("Session expired", false, uploadData);
                            }
                        }
                        else if(response.body().getStatus().getCode() instanceof Boolean)
                        {
                            if (response.body().getStatus().getCode() == Boolean.TRUE)
                            {
                                if(PreferenceUtils.getNotificationDelete(mContext) == null || PreferenceUtils.getNotificationDelete(mContext).isEmpty()) {
                                    UploadModel uploadModel = new UploadModel();
                                    uploadModel.setFilePath(uploadData.getFilePath());
                                    uploadModel.setObjectId(uploadData.getObjectId());
                                    uploadModel.setFileName(uploadData.getFileName());
                                    uploadFailedList.add(uploadModel);
                                    PreferenceUtils.setFailureUploadlist(BackgroundUploadService.this, uploadFailedList, "key");

                                    PreferenceUtils.getCurrentUploadList(mContext, "key").get(index).setFailure(true);
                                    PreferenceUtils.getCurrentUploadList(mContext, "key").get(index).setSuccess(true);
                                   /* uploadData.setFailure(true);
                                    uploadData.setSuccess(true); // getting failure documents purpose*/
                                   Log.d("Upload failed: ", String.valueOf(index));

                                    nextItemUpload();
                                }
                                else
                                {
                                    clearNotification();
                                }

                            }
                            else {

                                if(PreferenceUtils.getNotificationDelete(mContext) == null || PreferenceUtils.getNotificationDelete(mContext).isEmpty())
                                {
                                   /* List<UploadModel> removeingList = new ArrayList<>();
                                    removeingList = PreferenceUtils.getImageUploadList(mContext, "key");
                                    if (removeingList.size() > 0) {
                                        removeingList.remove(0);
                                        PreferenceUtils.setImageUploadList(mContext, removeingList, "key");
                                    }*/

                                  //  uploadData.setSuccess(true);
                                    PreferenceUtils.getCurrentUploadList(mContext, "key").get(index).setSuccess(true);

                                    Log.d("Upload completed: ", String.valueOf(index));

                                    index++;
                                    mBuilder.setProgress(PreferenceUtils.getCurrentUploadList(mContext, "key").size(), index, false);

                                    if (PreferenceUtils.getCurrentUploadList(mContext, "key").size() > 1) {
                                        if (index == 1) {
                                            bigText.bigText(PreferenceUtils.getCurrentUploadList(mContext, "key").size() - index + " file(s) remaining; " + index + " file added");
                                            mBuilder.setStyle(bigText);
                                            // mBuilder.setContentText(uploadDataList.size() - index + " file(s) remaining; " + index + " file added");
                                        } else {
                                            bigText.bigText(PreferenceUtils.getCurrentUploadList(mContext, "key").size() - index + " file(s) remaining; " + index + " files added");
                                            mBuilder.setStyle(bigText);
                                            //  mBuilder.setContentText(uploadDataList.size() - index + " file(s) remaining; " + index + " files added");
                                        }
                                    } else {
                                        bigText.bigText("1 file remaining");
                                        mBuilder.setStyle(bigText);
                                        //   mBuilder.setContentText("1 file remaining");
                                    }

                                    assert mNotifyManager != null;
                                    mNotifyManager.notify(TAG, notificationId, mBuilder.build());

                                    if (PreferenceUtils.getCurrentUploadList(mContext, "key").size() > index) {
                                        uploadData(PreferenceUtils.getCurrentUploadList(mContext, "key").get(index));
                                    } else {
                                        uploadCompleteMessage();
                                    }
                                }
                                else
                                {
                                    clearNotification();
                                }
                            }

                        }

                    }
                    else {
                        uploadFailedMessage("Uploading Error", true, uploadData);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("Message", t.getMessage());
                    uploadFailedMessage("Try again after sometime", true, uploadData);
                }
            });
        }
        else
        {
            if(PreferenceUtils.getNotificationDelete(mContext) == null || PreferenceUtils.getNotificationDelete(mContext).isEmpty()) {
                UploadModel uploadModel = new UploadModel();
                uploadModel.setFilePath(uploadData.getFilePath());
                uploadModel.setObjectId(uploadData.getObjectId());
                uploadModel.setFileName(uploadData.getFileName());
                uploadFailedList.add(uploadModel);
                PreferenceUtils.setFailureUploadlist(BackgroundUploadService.this, uploadFailedList, "key");

                PreferenceUtils.getCurrentUploadList(mContext, "key").get(index).setFailure(true);
              //  PreferenceUtils.getCurrentUploadList(mContext, "key").get(index).setSuccess(true);
                                   /* uploadData.setFailure(true);
                                    uploadData.setSuccess(true); // getting failure documents purpose*/
                Log.d("Upload failed: ", String.valueOf(index));

                nextItemUpload();
            }
            else
            {
                clearNotification();
            }

        }

    }

    @SuppressLint("RestrictedApi")
    private void uploadCompleteMessage()
    {
        if(PreferenceUtils.getFailureUploadList(mContext, "key") != null && PreferenceUtils.getFailureUploadList(mContext, "key").size() > 0)
        {
            bigText.bigText(PreferenceUtils.getFailureUploadList(mContext, "key").size()+" file(s) not uploaded.");
            GlobalVariables.isBackgroundProcessRunning = false;
            index = 0;
            mBuilder.setContentTitle("Upload failed")
                    .setProgress(0, 0, false)
                    .setStyle(bigText)
                    .mActions.clear();
            mBuilder.setAutoCancel(true);
        }
        else
        {
            bigText.bigText(PreferenceUtils.getCurrentUploadList(mContext, "key").size()+" file(s) have been uploaded and are being processed. They will be available in a few minutes.");
            GlobalVariables.isBackgroundProcessRunning = false;
            index = 0;
            mBuilder.setContentTitle("Upload completed")
                    .setProgress(0, 0, false)
                    .setStyle(bigText)
                    .mActions.clear();
            mBuilder.setAutoCancel(true);

        }



       /* if(uploadDataList.size() == 1)
        {
            mBuilder.setContentTitle("Upload completed")
                    .setContentText(uploadDataList.size() + " file uploaded.")
                    .setProgress(0, 0, false)
                    .mActions.clear();
        }
        else
        {
            mBuilder.setContentTitle("Upload completed")
                    .setContentText(uploadDataList.size() + " files uploaded.")
                    .setProgress(0, 0, false)
                    .mActions.clear();
        }*/
        assert mNotifyManager != null;
        mNotifyManager.notify(TAG, notificationId, mBuilder.build());
        List<UploadModel> uploadList = new ArrayList<>();
        PreferenceUtils.setCurrentUploadlist(mContext, uploadList, "key");
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        return super.onStartCommand(intent, flags, startId);
    }


    public  boolean checkIfNetworkAvailable(final Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        }
        return false;
    }

    @SuppressLint("RestrictedApi")
    public void uploadFailedMessage(String uploadMessage, boolean isFailure, UploadModel uploadDAta)
    {
        if(isFailure)
        {
            if(PreferenceUtils.getNotificationDelete(mContext) == null || PreferenceUtils.getNotificationDelete(mContext).isEmpty()) {
                UploadModel uploadModel = new UploadModel();
                uploadModel.setFilePath(uploadDAta.getFilePath());
                uploadModel.setObjectId(uploadDAta.getObjectId());
                uploadModel.setFileName(uploadDAta.getFileName());
                uploadFailedList.add(uploadModel);
                PreferenceUtils.setFailureUploadlist(BackgroundUploadService.this, uploadFailedList, "key");

                PreferenceUtils.getCurrentUploadList(mContext, "key").get(index).setFailure(true);
             //   PreferenceUtils.getCurrentUploadList(mContext, "key").get(index).setSuccess(true); // getting failure documents purpose
                                   /* uploadData.setFailure(true);
                                    uploadData.setSuccess(true); */
                Log.d("Upload failed: ", String.valueOf(index));

                nextItemUpload();
            }
            else
            {
                clearNotification();
            }

        /*    List<UploadModel> remainingList = PreferenceUtils.getFailureUploadList(mContext,"key");
            if(remainingList == null)
            {
                remainingList = new ArrayList<>();
            }
            for(UploadModel uploadModel : PreferenceUtils.getCurrentUploadList(mContext,"key"))
            {
                if(uploadModel.isSuccess() == false)
                {
                    remainingList.add(uploadModel);
                }
            }

            if(remainingList != null && remainingList.size() > 0)
            {
                PreferenceUtils.setFailureUploadlist(mContext, remainingList, "key");
            }

            List<UploadModel> uploadList = new ArrayList<>();
            PreferenceUtils.setCurrentUploadlist(mContext, uploadList, "key");*/

        }
        else
        {
            bigText.bigText(uploadMessage);
            mBuilder.setContentTitle("Upload failed")
                    //  .setContentText(uploadMessage)
                    .setProgress(0,0,false)
                    .setStyle(bigText)
                    .mActions.clear();
            mBuilder.setAutoCancel(true);
            assert mNotifyManager != null;
            mNotifyManager.notify(TAG, notificationId, mBuilder.build());
        }

        stopSelf();
        GlobalVariables.isBackgroundProcessRunning = false;
    }

    public void clearNotification()
    {
        index = 0;
        stopSelf();
        PreferenceUtils.setNotificationDelete(mContext, null);
        GlobalVariables.isBackgroundProcessRunning = false;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String channelId = "channel-01";
            String channelName = getResources().getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(notificationId, notification);
        }
    }


    private void nextItemUpload()
    {
        index++;
        mBuilder.setProgress(PreferenceUtils.getCurrentUploadList(mContext, "key").size(), index, false);
        // Displays the progress bar for the first time.

        assert mNotifyManager != null;
        mNotifyManager.notify(TAG, notificationId, mBuilder.build());

        if (PreferenceUtils.getCurrentUploadList(mContext, "key").size() > index) {
            uploadData(PreferenceUtils.getCurrentUploadList(mContext, "key").get(index));
        } else {
            uploadCompleteMessage();
        }

    }
}
