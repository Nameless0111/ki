package com.example.films.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.films.model.Event;
import com.example.films.repository.EventRepository;

import java.util.Calendar;
import java.util.List;

public class EventViewModel extends AndroidViewModel {
    
    private final EventRepository repository;
    private final LiveData<List<Event>> allEventsOrderedByDate;
    private final LiveData<List<Event>> allEventsOrderedByPriority;
    private final MutableLiveData<Long> startDateFilter = new MutableLiveData<>();
    private final MutableLiveData<Long> endDateFilter = new MutableLiveData<>();
    private final LiveData<List<Event>> filteredEvents;
    
    private final MutableLiveData<Integer> sortType = new MutableLiveData<>();
    public static final int SORT_BY_DATE = 0;
    public static final int SORT_BY_PRIORITY = 1;
    
    public EventViewModel(@NonNull Application application) {
        super(application);
        repository = new EventRepository(application);
        allEventsOrderedByDate = repository.getAllEventsOrderedByDate();
        allEventsOrderedByPriority = repository.getAllEventsOrderedByPriority();
        
        // Initialize with current day
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        startDateFilter.setValue(calendar.getTimeInMillis());
        
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        endDateFilter.setValue(calendar.getTimeInMillis());
        
        filteredEvents = Transformations.switchMap(startDateFilter, startDate -> 
            Transformations.switchMap(endDateFilter, endDate -> 
                repository.getEventsBetweenDates(startDate, endDate)));
        
        // Default sort by date
        sortType.setValue(SORT_BY_DATE);
    }
    
    public LiveData<List<Event>> getAllEvents() {
        return Transformations.switchMap(sortType, type -> {
            if (type == SORT_BY_PRIORITY) {
                return allEventsOrderedByPriority;
            } else {
                return allEventsOrderedByDate;
            }
        });
    }
    
    public LiveData<List<Event>> getFilteredEvents() {
        return filteredEvents;
    }
    
    public void setDateFilter(long startDate, long endDate) {
        startDateFilter.setValue(startDate);
        endDateFilter.setValue(endDate);
    }
    
    public void setSortType(int type) {
        sortType.setValue(type);
    }
    
    public void insert(Event event) {
        repository.insert(event);
    }
    
    public void update(Event event) {
        repository.update(event);
    }
    
    public void delete(Event event) {
        repository.delete(event);
    }
    
    public void getEventById(long id, EventRepository.EventCallback callback) {
        repository.getEventById(id, callback);
    }
    
    // Set filter for today
    public void filterToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startDay = calendar.getTimeInMillis();
        
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endDay = calendar.getTimeInMillis();
        
        setDateFilter(startDay, endDay);
    }
    
    // Set filter for this week
    public void filterThisWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startDay = calendar.getTimeInMillis();
        
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endDay = calendar.getTimeInMillis();
        
        setDateFilter(startDay, endDay);
    }
    
    // Set filter for this month
    public void filterThisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startDay = calendar.getTimeInMillis();
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endDay = calendar.getTimeInMillis();
        
        setDateFilter(startDay, endDay);
    }
} 