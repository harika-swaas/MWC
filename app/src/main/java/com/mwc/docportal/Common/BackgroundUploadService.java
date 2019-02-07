package com.mwc.docportal.Common;

import android.app.Activity;
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
    List<UploadModel> uploadDataList;
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
        uploadDataList = (ArrayList<UploadModel>)intent.getSerializableExtra("UploadedList");
        if(uploadDataList != null && uploadDataList.size() > 0)
        {
            GlobalVariables.isBackgroundProcessRunning = true;
            List<UploadModel> removeingList;
            removeingList = PreferenceUtils.getImageUploadList(mContext, "key");
            if(removeingList ==  null)
            {
                removeingList = new ArrayList<>();
            }
            removeingList.addAll(uploadDataList);
            PreferenceUtils.setImageUploadList(mContext, removeingList,"key");

            uploadFailedList = new ArrayList<>();
            uploadFailedList.clear();
            startNotification();
            if(uploadDataList.size()> index) {
                uploadData(uploadDataList.get(index).getFilePath());
            }
        }
    }

    private void startNotification()
    {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "channel-01";
        String channelName = getResources().getString(R.string.app_name)+"Channel";
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
        bigText.bigText("("+uploadDataList.size()+") file(s) have been uploaded and are being processed. They will be available in a few minutes.");


        // This is for Notification Click
        Intent  intent = new Intent(this,LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);



        mBuilder.setContentTitle("Uploading to "+getResources().getString(R.string.app_name))
                .setContentText(uploadDataList.size()+" file(s)")
                .setSmallIcon(R.mipmap.ic_notification_icon)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_cancel_btn, "CANCEL", snoozePendingIntent);



        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_notification_icon));
        }
        // must set color for notification icon
        mBuilder.setColor(getResources().getColor(R.color.notification_icon_color));
        mBuilder.setProgress(uploadDataList.size(), index, false);
        if(uploadDataList.size() == 1)
        {
            mBuilder.setContentText(uploadDataList.size()+" file remaining");
        }
        else
        {
            mBuilder.setContentText(uploadDataList.size()+" files remaining");
        }

        // Displays the progress bar for the first time.
        assert mNotifyManager != null;
        mNotifyManager.notify(TAG,notificationId, mBuilder.build());
    }


    private void uploadData(String filePath)
    {
        if(checkIfNetworkAvailable(BackgroundUploadService.this))
        {
            File file = new File(filePath);

            Retrofit retrofitAPI = RetrofitAPIBuilder.getUploadInstance();

            final UploadEndUserDocumentsRequest mUploadEndUserDocumentsRequest = new UploadEndUserDocumentsRequest(PreferenceUtils.getObjectId(BackgroundUploadService.this),
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

            UploadEndUsersDocumentService mUploadEndUsersDocumentService = retrofitAPI.create(UploadEndUsersDocumentService.class);

            Call call = mUploadEndUsersDocumentService.getUploadEndUsersDocument(dataRequest, requestBodyMap, PreferenceUtils.getAccessToken(BackgroundUploadService.this));

            call.enqueue(new Callback<UploadDocumentResponse>() {
                @Override
                public void onResponse(Response<UploadDocumentResponse> response, Retrofit retrofit) {
                    UploadDocumentResponse apiResponse = response.body();
                    if (apiResponse != null)
                    {
                        Log.d("Upload status", apiResponse.toString());

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
                                uploadFailedMessage("Access denied");
                            }
                            else if(status_value ==  401 || status_value ==  401.0)
                            {
                                uploadFailedMessage("Session expired");
                            }
                        }
                        else if(response.body().getStatus().getCode() instanceof Integer)
                        {
                            int integerValue = new Integer(response.body().getStatus().getCode().toString());
                            if(integerValue ==  401)
                            {
                                uploadFailedMessage("Session expired");
                            }
                        }
                        else if(response.body().getStatus().getCode() instanceof Boolean)
                        {
                            if (response.body().getStatus().getCode() == Boolean.TRUE)
                            {
                                if(PreferenceUtils.getNotificationDelete(mContext) == null || PreferenceUtils.getNotificationDelete(mContext).isEmpty()) {
                                    UploadModel uploadModel = new UploadModel();
                                    uploadModel.setFilePath(uploadDataList.get(index).getFilePath());
                                    uploadFailedList.add(uploadModel);
                                    PreferenceUtils.setImageUploadList(BackgroundUploadService.this, uploadFailedList, "key");


                                    index++;
                                    mBuilder.setProgress(uploadDataList.size(), index, false);
                                    // Displays the progress bar for the first time.

                                    assert mNotifyManager != null;
                                    mNotifyManager.notify(TAG, notificationId, mBuilder.build());

                                    if (uploadDataList.size() > index) {
                                        uploadData(uploadDataList.get(index).getFilePath());
                                    } else {
                                        uploadCompleteMessage();
                                    }
                                }
                                else
                                {
                                   clearNotification();
                                }

                            }
                            else {

                                if(PreferenceUtils.getNotificationDelete(mContext) == null || PreferenceUtils.getNotificationDelete(mContext).isEmpty())
                                {
                                    List<UploadModel> removeingList = new ArrayList<>();
                                    removeingList = PreferenceUtils.getImageUploadList(mContext, "key");
                                    if (removeingList.size() > 0) {
                                        removeingList.remove(0);
                                        PreferenceUtils.setImageUploadList(mContext, removeingList, "key");
                                    }


                                    index++;
                                    mBuilder.setProgress(uploadDataList.size(), index, false);

                                    if (uploadDataList.size() > 1) {
                                        if (index == 1) {
                                            mBuilder.setContentText(uploadDataList.size() - index + " file(s) remaining; " + index + " file added");
                                        } else {
                                            mBuilder.setContentText(uploadDataList.size() - index + " file(s) remaining; " + index + " files added");
                                        }
                                    } else {
                                        mBuilder.setContentText("1 file remaining");
                                    }

                                    assert mNotifyManager != null;
                                    mNotifyManager.notify(TAG, notificationId, mBuilder.build());

                                    if (uploadDataList.size() > index) {
                                        uploadData(uploadDataList.get(index).getFilePath());
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
                        uploadFailedMessage("Uploading Error");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("Message", t.getMessage());
                    uploadFailedMessage("Try again after sometime");
                }
            });
        }

    }

    private void uploadCompleteMessage()
    {
        GlobalVariables.isBackgroundProcessRunning = false;
        index = 0;
        mBuilder.setContentTitle("Upload completed")
                .setProgress(0, 0, false)
                .setStyle(bigText)
                .mActions.clear();

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

    public void uploadFailedMessage(String uploadMessage)
    {
        mBuilder.setContentTitle("Upload failed")
                .setContentText(uploadMessage)
                .setProgress(0,0,false)
                .mActions.clear();
        assert mNotifyManager != null;
        mNotifyManager.notify(TAG, notificationId, mBuilder.build());
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
            String channelName = getResources().getString(R.string.app_name)+" Channel";
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

}
