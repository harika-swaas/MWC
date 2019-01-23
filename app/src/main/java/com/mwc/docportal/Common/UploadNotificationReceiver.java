package com.mwc.docportal.Common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.mwc.docportal.API.Model.UploadModel;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.Fragments.FTLPinVerificationFragment;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class UploadNotificationReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int notificationId = 1234;
        if(action.equals(Constants.ACTION_CANCEL)){

         //   Toast.makeText(context, "Cancel clicked", Toast.LENGTH_SHORT).show();
            List<UploadModel> uploadlist = new ArrayList<>();
            PreferenceUtils.setImageUploadList(context, uploadlist, "key");
            PreferenceUtils.setNotificationDelete(context, "DeleteNotification");

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(BackgroundUploadService.TAG,notificationId);
        }
    }
}
