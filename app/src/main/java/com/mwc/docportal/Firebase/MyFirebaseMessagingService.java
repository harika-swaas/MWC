package com.mwc.docportal.Firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
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
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

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
        /**Creates an explicit intent for an Activity in your app**/
        Intent intent = new Intent(context , LoginActivity.class);
        intent.putExtra("document_version_id", documentVersionId);
        intent.putExtra("notification_type", notificationType);


        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.mipmap.ic_notification_icon);
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_notification_icon));
            // must set color for notification icon
            mBuilder.setColor(getResources().getColor(R.color.notification_icon_color));

        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_notification_icon);
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_notification_icon));
        }

        mBuilder.setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(resultPendingIntent);

        NotificationManager  mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(notificationId/* Request Code */, mBuilder.build());


/*


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String appName = getResources().getString(R.string.app_name);
            String CHANNEL_ID = "my_channel_01";
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("document_version_id", documentVersionId);
            intent.putExtra("notification_type", notificationType);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this);
            notificationCompatBuilder
                    .setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID)
                    .setSound(null);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, appName, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(notificationId, notificationCompatBuilder.build());

        }
        else
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


        }*/


    }
}