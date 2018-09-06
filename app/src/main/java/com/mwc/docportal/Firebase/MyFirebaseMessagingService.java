package com.mwc.docportal.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.PushNotificatoinSettings_Respository;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;

import java.util.Date;

/**
 * Created by barath on 8/24/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Context context = this;

    PushNotificatoinSettings_Respository pushNotificationSettings;

    int notificationId;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        pushNotificationSettings = new PushNotificatoinSettings_Respository(context);

        String pushNotificationStatus = pushNotificationSettings.getPushNotificatonSettingsStatus();

        if(pushNotificationStatus != null && pushNotificationStatus.equalsIgnoreCase("1"))
        {
            String documentVersionId = "0";
            String notificationType = "";
            //if the message contains data payload
            //It is a map of custom keyvalues
            //we can read it easily
            if(remoteMessage.getData().size() > 0){
                //handle the data message here
                documentVersionId = remoteMessage.getData().get("document_version_id");
                notificationType = remoteMessage.getData().get("notification_type");

            }

            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), documentVersionId, notificationType);
        }
        else
        {
            Log.e("Disabled", "ReactFireBaseMessagingService: Notifications Are Disabled by User");
        }







        //getting the title and the body


        //then here we can use the title and body to build a notification
    }

    private void sendNotification(String body, String title, String documentVersionId, String notificationType)
    {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("document_version_id", documentVersionId);
        intent.putExtra("notification_type", notificationType);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());


    }
}