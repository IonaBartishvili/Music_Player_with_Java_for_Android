package com.example.musicplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "Channel 1";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.setDescription("Music is Playing Now");

            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
