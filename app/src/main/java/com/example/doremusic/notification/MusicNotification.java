package com.example.doremusic.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.IdRes;

import com.example.doremusic.R;

public class MusicNotification extends Notification {

    private static final String CHANNEL_ID = "PLAYING_CHANNEL";


    private PendingIntent onButtonNotificationClick(@IdRes int id, Context context){
        Intent i = new Intent();
        return PendingIntent.getBroadcast(context, id, i, 0);
    }

    public void createNotificationChanel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "play";
            String description = "just play";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void setView(Context context){
        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification);
        notificationLayout.setOnClickPendingIntent(
                R.id.noti_prev,
                onButtonNotificationClick(R.id.noti_prev, context));
    }

}
