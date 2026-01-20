package com.example.sakhi;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class RemainderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 1. Get the Title/Message sent from the Activity
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        // 2. Create Notification Channel (Required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("sakhi_reminders", "Sakhi Reminders", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for Sakhi Habit Reminders");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // 3. Build the Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "sakhi_reminders")
                .setSmallIcon(R.mipmap.ic_launcher) // Ensure this icon exists
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // 4. Show the Notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(200, builder.build());
        }
    }
}

