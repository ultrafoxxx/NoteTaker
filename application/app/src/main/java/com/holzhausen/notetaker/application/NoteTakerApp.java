package com.holzhausen.notetaker.application;

import android.app.Application;

import androidx.room.Room;

import com.holzhausen.notetaker.database.AppDatabase;

public class NoteTakerApp extends Application {

    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,
                "note-taker-database").build();
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
