package com.mwc.docportal.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.PushNotificatoinSettings_Respository;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by barath on 8/24/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Context context = this;

    PushNotificatoinSettings_Respository pushNotificationSettings;
    String GROUP_KEY_PUSH_NOTIFICATION = "com.mwc.docportal.PUSH_NOTIFICATION";

    int notificationId;
    PendingIntent pendingIntent;

    public MyFirebaseMessagingService() {
        super();
    }



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


                sendNotification1(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), documentVersionId, notificationType);

            /*else
            {
                Toast.makeText(context, "no document", Toast.LENGTH_SHORT).show();
            }*/

        }
        else
        {
            Log.e("Disabled", "ReactFireBaseMessagingService: Notifications Are Disabled by User");
        }


        //getting the title and the body


        //then here we can use the title and body to build a notification
    }


   /* private void sendNotification(String body, String title, String documentVersionId, String notificationType)
    {
        *//**Creates an explicit intent for an Activity in your app**//*
        Intent intent = new Intent(context , LoginActivity.class);
        intent.putExtra("document_version_id", documentVersionId);
        intent.putExtra("notification_type", notificationType);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager  mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            String id = context.getString(R.string.default_notification_channel_id); // default_channel_id
            String title1 = context.getString(R.string.default_notification_channel_title); // Default Channel

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = mNotificationManager.getNotificationChannel(id);

            if(notificationChannel == null)
            {
                notificationChannel = new NotificationChannel(id, title1, importance);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setDescription(body);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mNotificationManager.createNotificationChannel(notificationChannel);
            }

           *//* NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mBuilder1.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);*//*
            int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                    notificationId *//* Request code *//*, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder1 = new NotificationCompat.Builder(context, id);

            mBuilder1.setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setGroup(""+notificationId)
                    .setGroupSummary(true)
                    .setSmallIcon(R.mipmap.ic_notification_icon)
                    .setColor(getResources().getColor(R.color.notification_icon_color))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(body))
                    .setChannelId(id)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setContentIntent(resultPendingIntent);

            //   mBuilder1.setChannelId(NOTIFICATION_CHANNEL_ID);




            mNotificationManager.notify(notificationId*//* Request Code *//*, mBuilder1.build());

        }
        else
        {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.mipmap.ic_notification_icon);

                // For Notification right side icon avoid
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.ic_notification_icon));
                }
                // must set color for notification icon
                mBuilder.setColor(getResources().getColor(R.color.notification_icon_color));

            } else {
                mBuilder.setSmallIcon(R.mipmap.ic_notification_icon);
                mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_notification_icon));

            }

            int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                    uniqueInt *//* Request code *//*, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            mBuilder.setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setGroupSummary(true)
                    .setColor(getResources().getColor(R.color.notification_icon_color))
                    .setGroup(GROUP_KEY_PUSH_NOTIFICATION)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(body))
                    .setContentIntent(resultPendingIntent);
            mNotificationManager.notify(notificationId*//* Request Code *//*, mBuilder.build());
        }



       *//* if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            String NOTIFICATION_CHANNEL_ID = getRandomString();
            NotificationCompat.Builder mBuilder1 = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
            mBuilder1.setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setGroupSummary(true)
                    .setSmallIcon(R.mipmap.ic_notification_icon)
                    .setColor(getResources().getColor(R.color.notification_icon_color))
                    .setGroup(GROUP_KEY_PUSH_NOTIFICATION)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(body))
                    .setContentIntent(resultPendingIntent);

         //   mBuilder1.setChannelId(NOTIFICATION_CHANNEL_ID);

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mBuilder1.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);


            mNotificationManager.notify(notificationId*//**//* Request Code *//**//*, mBuilder1.build());

        }
        else
        {
            mNotificationManager.notify(notificationId*//**//* Request Code *//**//*, mBuilder.build());
        }
*//*



*//*


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


        }*//*


      *//*  NotificationManager notifManager = null;
      //  final int NOTIFY_ID = 1002;

        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        Intent intent1;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (notifManager == null) {
            notifManager =
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setLightColor(Color.GREEN);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);

            intent1 = new Intent(this, LoginActivity.class);
            intent1.putExtra("document_version_id", documentVersionId);
            intent1.putExtra("notification_type", notificationType);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, notificationId, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentTitle(title)  // required
                    .setSmallIcon(R.mipmap.ic_notification_icon) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setTicker(title)
                    .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(body))
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {

            builder = new NotificationCompat.Builder(this);

            intent1 = new Intent(this, LoginActivity.class);
            intent1.putExtra("document_version_id", documentVersionId);
            intent1.putExtra("notification_type", notificationType);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, notificationId, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentTitle(title)                           // required
                    .setSmallIcon(R.mipmap.ic_notification_icon) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setTicker(title)
                    .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(body))
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        Notification notification = builder.build();
        notifManager.notify(notificationId, notification);*//*



    }

    public int generateRandom(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }
    private  String getRandomString()
    {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }*/

    private void sendNotification1(String body, String title, String documentVersionId, String notificationType) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int notificationId = 123456;
        String channelId = "channel-01";
        String channelName = getResources().getString(R.string.app_name)+" Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            if(mChannel != null)
            {
                manager.createNotificationChannel(mChannel);
            }
        }


        NotificationCompat.Builder notification = new NotificationCompat.Builder(MyFirebaseMessagingService.this, channelId)
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(R.mipmap.ic_notification_icon);

            // For Notification right side icon avoid
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                notification.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_notification_icon));
            }
            // must set color for notification icon
            notification.setColor(getResources().getColor(R.color.notification_icon_color));

        } else {
            notification.setSmallIcon(R.mipmap.ic_notification_icon);
            notification.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_notification_icon));

        }

        Intent  intent = new Intent(this,LoginActivity.class);
        intent.putExtra("document_version_id", documentVersionId);
        intent.putExtra("notification_type", notificationType);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);

        pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        assert manager != null;
        manager.notify(notificationId, notification.build());

    }

}