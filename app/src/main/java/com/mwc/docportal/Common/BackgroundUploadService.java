package com.mwc.docportal.Common;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.UploadDocumentResponse;
import com.mwc.docportal.API.Model.UploadEndUserDocumentsRequest;
import com.mwc.docportal.API.Model.UploadModel;
import com.mwc.docportal.API.Service.UploadEndUsersDocumentService;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.DMS.UploadListActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.mwc.docportal.Utils.Constants.CHANNEL_ID;

public class BackgroundUploadService extends IntentService
{
    int index = 0;
    List<UploadModel> uploadDataList;

    int notificationId = 1234;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BackgroundUploadService(String name) {
        super(name);

    }

    public BackgroundUploadService() {
        super("BackgroundUploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        uploadDataList = (ArrayList<UploadModel>)intent.getSerializableExtra("UploadedList");
        startInForeground();
        if(uploadDataList.size()> index) {
            uploadData(uploadDataList.get(index).getFilePath());
        }
    }

    private void startInForeground()
    {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 123456;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            if(mChannel != null)
            {
                mNotifyManager.createNotificationChannel(mChannel);
            }
        }

        mBuilder = new NotificationCompat.Builder(BackgroundUploadService.this, channelId);
        mBuilder.setContentTitle("Uploading to Doc Portal")
                .setContentText(uploadDataList.size()+" file(s)")
                .setSmallIcon(R.mipmap.ic_notification_icon)
                .setOnlyAlertOnce(true);


        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_notification_icon));
        }
        // must set color for notification icon
        mBuilder.setColor(getResources().getColor(R.color.notification_icon_color));



        mBuilder.setProgress(100, 5, false);
        // Displays the progress bar for the first time.
        assert mNotifyManager != null;
        mNotifyManager.notify(notificationId, mBuilder.build());

        // Start a the operation in a background thread
      /*  new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 100; incr+=5) {
                            // Sets the progress indicator to a max value, the current completion percentage and "determinate" state
                            mBuilder.setProgress(100, incr, false);
                            // Displays the progress bar for the first time.
                            mNotifyManager.notify(notificationId, mBuilder.build());
                            // Sleeps the thread, simulating an operation
                            try {
                                // Sleep for 1 second
                                Thread.sleep(1*1000);
                            } catch (InterruptedException e) {
                                Log.d("TAG", "sleep failure");
                            }
                        }
                        // When the loop is finished, updates the notification
                        mBuilder.setContentTitle("Upload complete")
                                .setContentText(uploadDataList.size()+" file(s) uploaded")
                                // Removes the progress bar
                                .setProgress(0,0,false);
                        mNotifyManager.notify(notificationId, mBuilder.build());
                    }
                }
                // Starts the thread by calling the run() method in its Runnable
        ).start();*/
    }


    private void uploadData(String filePath)
    {
        if (NetworkUtils.isNetworkAvailable(BackgroundUploadService.this)) {

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
                            //    showAlertDialogForAccessDenied(context, message);
                            }
                            else if(status_value ==  401 || status_value ==  401.0)
                            {
                            //    showAlertDialogForSessionExpiry(context, message);
                            }
                        }
                        else if(response.body().getStatus().getCode() instanceof Integer)
                        {
                            int integerValue = new Integer(response.body().getStatus().getCode().toString());
                            if(integerValue ==  401)
                            {
                            //  showAlertDialogForSessionExpiry(context, message);
                            }
                        }
                        else if(response.body().getStatus().getCode() instanceof Boolean)
                        {
                            if (response.body().getStatus().getCode() == Boolean.TRUE)
                            {
                                /*if(!((Activity) context ).isFinishing())
                                {
                                    showAlertMessage(apiResponse.getStatus().getMessage(), false, "");
                                }

                                UploadModel uploadModel = new UploadModel();
                                uploadModel.setFilePath(UploadList.get(index).getFilePath());
                                uploadFailedList.add(uploadModel);
                                PreferenceUtils.setImageUploadList(context, uploadFailedList, "key");

                                UploadList.get(index).setFailure(true);
                                customAdapter.notifyDataSetChanged();

                                index++;

                                if(uploadDataList.size()> index) {
                                    uploadData(uploadDataList.get(index).getFilePath());
                                }
                                else
                                {
                                    index = 0;
                                    failedListDataMessage();
                                }*/

                            }
                            else {
                                index++;
                               int progress = (int)((index / (float) uploadDataList.size()) * 100);
                                mBuilder.setProgress(100, progress, false);
                                // Displays the progress bar for the first time.
                                assert mNotifyManager != null;
                                mNotifyManager.notify(notificationId, mBuilder.build());
                                // Sleeps the thread, simulating an operation

                                     if(uploadDataList.size()> index) {
                                     uploadData(uploadDataList.get(index).getFilePath());
                                }
                                else
                                {
                                    index = 0;

                                    mBuilder.setContentTitle("Upload complete")
                                            .setContentText(uploadDataList.size()+" file(s) uploaded")
                                            // Removes the progress bar
                                            .setProgress(0,0,false);
                                    assert mNotifyManager != null;
                                    mNotifyManager.notify(notificationId, mBuilder.build());

                                //    uploadCompleted();
                                    stopSelf();
                                }
                            }

                        }

                    }
                   /* else {
                        if(transparentProgressDialog.isShowing())
                        {
                            transparentProgressDialog.dismiss();
                        }
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }*/
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("Message", t.getMessage());
                    /*if(transparentProgressDialog.isShowing())
                    {
                        transparentProgressDialog.dismiss();
                    }*/
                    CommonFunctions.showTimeOutError(BackgroundUploadService.this, t);
                }
            });
        }
    }

   /* private void uploadCompleted()
    {
       *//* Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("Result", Activity.RESULT_OK);
        sendBroadcast(intent);*//*

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NavigationMyFolderActivity.mBroadcastStringAction);
      //  broadcastIntent.putExtra("Data", "Broadcast Data");
        sendBroadcast(broadcastIntent);
    }
*/


}
