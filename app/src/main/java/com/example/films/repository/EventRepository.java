package com.example.films.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.films.database.EventDao;
import com.example.films.database.EventDatabase;
import com.example.films.model.Event;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository {
    
    private final EventDao eventDao;
    private final LiveData<List<Event>> allEventsOrderedByDate;
    private final LiveData<List<Event>> allEventsOrderedByPriority;
    private final ExecutorService executorService;
    
    public EventRepository(Application application) {
        EventDatabase database = EventDatabase.getInstance(application);
        eventDao = database.eventDao();
        allEventsOrderedByDate = eventDao.getAllEventsOrderedByDate();
        allEventsOrderedByPriority = eventDao.getAllEventsOrderedByPriority();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    public LiveData<List<Event>> getAllEventsOrderedByDate() {
        return allEventsOrderedByDate;
    }
    
    public LiveData<List<Event>> getAllEventsOrderedByPriority() {
        return allEventsOrderedByPriority;
    }
    
    public LiveData<List<Event>> getEventsBetweenDates(long startDate, long endDate) {
        return eventDao.getEventsBetweenDates(startDate, endDate);
    }
    
    public void insert(Event event) {
        executorService.execute(() -> eventDao.insert(event));
    }
    
    public void update(Event event) {
        executorService.execute(() -> eventDao.update(event));
    }
    
    public void delete(Event event) {
        executorService.execute(() -> eventDao.delete(event));
    }
    
    public void getEventById(long id, EventCallback callback) {
        executorService.execute(() -> {
            Event event = eventDao.getEventById(id);
            callback.onEventLoaded(event);
        });
    }
    
    public void getUpcomingEventsWithNotifications(long currentTime, NotificationEventsCallback callback) {
        executorService.execute(() -> {
            List<Event> events = eventDao.getUpcomingEventsWithNotifications(currentTime);
            callback.onEventsLoaded(events);
        });
    }
    
    public interface EventCallback {
        void onEventLoaded(Event event);
    }
    
    public interface NotificationEventsCallback {
        void onEventsLoaded(List<Event> events);
    }
} 