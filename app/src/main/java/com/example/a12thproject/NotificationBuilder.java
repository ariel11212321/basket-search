package com.example.a12thproject;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.a12thproject.activities.MainActivity;

public class NotificationBuilder {
    private static final int NOTIFICATION_ID = 1; // Unique ID for the notification
    private static final String CHANNEL_ID = "my_channel"; // Unique ID for the notification channel

    public static void createNotification(Context context, String title, String message) {
        // Create a notification channel

        String channelId = "my_channel_id";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "My Channel Name";
            String channelDescription = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            // Register the notification channel with the system
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.court22) // Set the notification icon
                .setContentTitle(title) // Set the title of the notification
                .setContentText(message) // Set the message of the notification
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // Set the priority of the notification

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
           return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
