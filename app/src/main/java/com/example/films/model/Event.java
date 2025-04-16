package com.example.films.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "events")
public class Event {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String title;
    private String description;
    private long dateTime;  // Stored as milliseconds since epoch
    private int priority;   // 1 - Low, 2 - Medium, 3 - High
    private boolean notificationEnabled;
    
    public Event() {
    }
    
    public Event(String title, String description, long dateTime, int priority, boolean notificationEnabled) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.priority = priority;
        this.notificationEnabled = notificationEnabled;
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public long getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }
    
    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }
    
    // Helper methods
    public Date getDate() {
        return new Date(dateTime);
    }
} 