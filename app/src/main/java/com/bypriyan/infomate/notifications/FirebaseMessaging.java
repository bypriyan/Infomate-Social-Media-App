package com.bypriyan.infomate.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bypriyan.infomate.Constant;
import com.bypriyan.infomate.activity.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessaging extends FirebaseMessagingService {

        @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

            SharedPreferences sp= getSharedPreferences("SP_USER", MODE_PRIVATE);
            String savedCurrentUser = sp.getString("Current_userid","None");

        String  send = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && send.equals(firebaseUser.getUid())) {

            if(!savedCurrentUser.equals(user)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage);
                } else {
                    sendNotification(remoteMessage);
                }
            }

        }
    }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sendOreoNotification(RemoteMessage remoteMessage){

            String user = remoteMessage.getData().get("user");
            String icon = remoteMessage.getData().get("icon");
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");

            RemoteMessage.Notification notification = remoteMessage.getNotification();
            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            Intent intent = new Intent(this, Chat.class);
            Bundle bundle = new Bundle();
            bundle.putString(Constant.KEY_UID, user);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            OreaNotification notification1 = new OreaNotification(this);

            Notification.Builder builder = notification1.getNotification(title, body, pendingIntent ,defaultSound, icon);

            int i = 0;
            if (j > 0){
                i = j;
            }

            notification1.getManager().notify(i, builder.build());

    }

    public void sendNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, Chat.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_UID, user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0){
            i = j;
        }

        noti.notify(i, builder.build());
    }

}
