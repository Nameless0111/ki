package com.example.films.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.films.model.Event;

@Database(entities = {Event.class}, version = 1, exportSchema = false)
public abstract class EventDatabase extends RoomDatabase {
    
    private static EventDatabase instance;
    
    public abstract EventDao eventDao();
    
    public static synchronized EventDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    EventDatabase.class,
                    "event_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
} 