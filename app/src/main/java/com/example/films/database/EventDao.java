package com.example.films.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.films.model.Event;

import java.util.List;

@Dao
public interface EventDao {
    
    @Insert
    long insert(Event event);
    
    @Update
    void update(Event event);
    
    @Delete
    void delete(Event event);
    
    @Query("SELECT * FROM events ORDER BY dateTime ASC")
    LiveData<List<Event>> getAllEventsOrderedByDate();
    
    @Query("SELECT * FROM events ORDER BY priority DESC")
    LiveData<List<Event>> getAllEventsOrderedByPriority();
    
    @Query("SELECT * FROM events WHERE dateTime BETWEEN :startDate AND :endDate ORDER BY dateTime ASC")
    LiveData<List<Event>> getEventsBetweenDates(long startDate, long endDate);
    
    @Query("SELECT * FROM events WHERE id = :id")
    Event getEventById(long id);
    
    @Query("SELECT * FROM events WHERE notificationEnabled = 1 AND dateTime > :currentTime ORDER BY dateTime ASC")
    List<Event> getUpcomingEventsWithNotifications(long currentTime);
} 