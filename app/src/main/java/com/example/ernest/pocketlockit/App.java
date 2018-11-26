package com.example.ernest.pocketlockit;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.net.Uri;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL = "channel";

    @Override
    public void onCreate(){
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL,
                    "channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Motion detected");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
