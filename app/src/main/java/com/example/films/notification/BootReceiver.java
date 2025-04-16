package com.example.films.notification;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.films.model.Event;
import com.example.films.repository.EventRepository;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Reschedule all notifications
            EventRepository repository = new EventRepository((Application) context.getApplicationContext());
            repository.getUpcomingEventsWithNotifications(System.currentTimeMillis(), 
                    events -> rescheduleNotifications(context, events));
        }
    }
    
    private void rescheduleNotifications(Context context, List<Event> events) {
        for (Event event : events) {
            NotificationHelper.scheduleNotification(context, event);
        }
    }
} 