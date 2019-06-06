package com.hr.techlabapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notifications extends Application {
    public static final String channel_1_ID = "Notification";
    public static final String channel_2_ID = "Notification2";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationsChannels();
    }
    private void createNotificationsChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    channel_1_ID,
                    "notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is channel one");
            NotificationChannel channel2 = new NotificationChannel(
                    channel_2_ID,
                    "notifications2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is channel two");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }

    }
}

