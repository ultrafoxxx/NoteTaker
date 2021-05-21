package com.holzhausen.notetaker.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.holzhausen.notetaker.converters.Converters;
import com.holzhausen.notetaker.daos.ImageDao;
import com.holzhausen.notetaker.daos.NoteDao;
import com.holzhausen.notetaker.daos.TagDao;
import com.holzhausen.notetaker.models.Image;
import com.holzhausen.notetaker.models.Note;
import com.holzhausen.notetaker.models.NoteTagsCrossRef;
import com.holzhausen.notetaker.models.Tag;

@Database(entities = {
        Note.class,
        Tag.class,
        NoteTagsCrossRef.class,
        Image.class
}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract ImageDao imageDao();
    public abstract NoteDao noteDao();
    public abstract TagDao tagDao();

}
