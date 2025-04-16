package com.example.films.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    
    public static final String EXTRA_EVENT_ID = "event_id";
    public static final String EXTRA_EVENT_TITLE = "event_title";
    public static final String EXTRA_EVENT_DESCRIPTION = "event_description";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        long eventId = intent.getLongExtra(EXTRA_EVENT_ID, 0);
        String title = intent.getStringExtra(EXTRA_EVENT_TITLE);
        String description = intent.getStringExtra(EXTRA_EVENT_DESCRIPTION);
        
        if (title != null) {
            showNotification(context, eventId, title, description);
        }
    }
    
    private void showNotification(Context context, long eventId, String title, String description) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        try {
            notificationManager.notify((int) eventId, 
                    NotificationHelper.createNotification(context, title, description));
        } catch (SecurityException e) {
            // Handle missing notification permission
            e.printStackTrace();
        }
    }
} 