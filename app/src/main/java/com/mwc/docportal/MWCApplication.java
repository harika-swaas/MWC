package com.mwc.docportal;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.mwc.docportal.API.Model.AccountSettingsResponse;
import com.mwc.docportal.API.Model.PushNotificationRequestModel;
import com.mwc.docportal.API.Model.SharedDocumentResponseModel;
import com.mwc.docportal.API.Service.ShareEndUserDocumentsService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Database.PushNotificatoinSettings_Respository;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.SplashScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by harika on 22-06-2018.
 */

public class MWCApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    private static MWCApplication mMwcApplication;
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;
    PushNotificatoinSettings_Respository pushNotificationSettings;
    List<AccountSettingsResponse> mAccountSettingsResponses = new ArrayList<>();
    KeyguardManager keyguardManager;
    public final int CREDENTIALS_RESULT = 12345;
    @Override
    public void onCreate() {
        super.onCreate();

        final Context context = this;
        mMwcApplication = this;

        Fabric.with(this, new Crashlytics());
        Stetho.initializeWithDefaults(this);
        registerActivityLifecycleCallbacks(this);
    }

    public static MWCApplication getThis() {
        return mMwcApplication;
    }



    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }




    @Override
    public void onActivityStarted(Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            // App enters foreground


            String channalId = "my_channel_01";
            boolean device_status = isNotificationChannelEnabled(activity, channalId);
            pushNotificationSettings = new PushNotificatoinSettings_Respository(activity);
            String pushNotificationStatus = pushNotificationSettings.getPushNotificatonSettingsStatus();

            if(pushNotificationStatus != null && pushNotificationStatus.equals("1") && device_status == false)
            {
                pushNotificationSettings.updatePushNotificatoinStatus("0");
                getPushNotificationDocumentService(activity,"0");

            }
            else if(pushNotificationStatus != null && pushNotificationStatus.equals("2") && device_status == false)
            {
                pushNotificationSettings.updatePushNotificatoinStatus("0");
                getPushNotificationDocumentService(activity,"0");
            }
            else if(pushNotificationStatus != null && pushNotificationStatus.equals("0") && device_status == true)
            {
                pushNotificationSettings.updatePushNotificatoinStatus("1");
                getPushNotificationDocumentService(activity,"1");
            }




            if(!GlobalVariables.isFromForeground && !GlobalVariables.isFromCamerOrVideo)
            {
              //  Toast.makeText(activity, "foreground", Toast.LENGTH_LONG).show();

                GlobalVariables.isComingFromApp = true;

            }

        }

    }


      private void getPushNotificationDocumentService(Activity activity, String register_type)
        {
            if (NetworkUtils.isNetworkAvailable(activity)) {

                Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                PushNotificatoinSettings_Respository pushNotificatoinSettings_respository = new PushNotificatoinSettings_Respository(activity);
                String DeviceTokenId = pushNotificatoinSettings_respository.getDeviceTokenFromTableStatus();

                final PushNotificationRequestModel externalShareResponseModel = new PushNotificationRequestModel(DeviceTokenId, "Android", register_type);

                String request = new Gson().toJson(externalShareResponseModel);

                //Here the json data is add to a hash map with key data
                Map<String, String> params = new HashMap<String, String>();
                params.put("data", request);

                final ShareEndUserDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);

                Call call = mGetCategoryDocumentsService.sendPushNotificatoinStatus(params, PreferenceUtils.getAccessToken(activity));

                call.enqueue(new Callback<SharedDocumentResponseModel>() {
                    @Override
                    public void onResponse(Response<SharedDocumentResponseModel> response, Retrofit retrofit) {

                        if (response != null) {

                            String message = "";
                            if(response.body().getStatus().getMessage() != null)
                            {
                                message = response.body().getStatus().getMessage().toString();
                            }

                            CommonFunctions.isApiSuccess(activity, message, response.body().getStatus().getCode());

                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("PinDevice error", t.getMessage());
                        CommonFunctions.showTimeoutAlert(activity);
                    }
                });
            }

        }


    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            // App enters background
         //   Toast.makeText(activity, "background", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public boolean isNotificationChannelEnabled(Activity context, String channelId){
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!TextUtils.isEmpty(channelId)) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if(manager==null) {
                    NotificationChannel channel = manager.getNotificationChannel(channelId);
                    return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
                }
            }
            return false;
        } else {*/

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
            return areNotificationsEnabled;
    //    }
    }


}
