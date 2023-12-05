package com.example.soundbox_du_an_md31;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        RemoteMessage.Notification notification = message.getNotification () ;
        if (notification==null){
            return;
        }
        String strTitle = notification.getTitle();
        String strMessage = notification.getBody();
        senNotification(strTitle,strMessage);
    }

    private void senNotification(String strTitle, String strMessage) {
        Intent intent = new Intent(this , MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0 , intent , PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this , MyApplication.ID)
                .setContentTitle(strTitle)
                .setContentText(strMessage)
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager!=null){
            notificationManager.notify(1 , notification);
        }
    }
}
