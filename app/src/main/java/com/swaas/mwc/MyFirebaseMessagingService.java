package com.swaas.mwc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.swaas.mwc.Login.Dashboard;

/**
 * Created by barath on 7/9/2018.
 */

public class MyFirebaseMessagingService extends Service {

    private static final String TAG = "MyFirebaseMsgService";
   /* @Override*/
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //Displaying data in log

        Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        //Calling method to generate notification

        String to="";
        to = remoteMessage.getData().get("key1");

        //when the notification is disabled then also the notification is coming
        boolean notification_enable=true;
        if(notification_enable) {
            sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),to);
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String title,String messageBody,String to) {
        Intent intent = new Intent(this, Dashboard.class);
        intent.putExtra("key1",to);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                /*.setSmallIcon(R.drawable.noti_icon)*/
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setColor(this.getResources().getColor(R.color.colorAccent))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

