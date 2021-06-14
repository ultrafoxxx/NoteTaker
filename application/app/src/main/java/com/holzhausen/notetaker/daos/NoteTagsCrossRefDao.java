package com.holzhausen.notetaker.daos;

import androidx.room.Dao;
import androidx.room.Insert;

import com.holzhausen.notetaker.models.NoteTagsCrossRef;

import io.reactivex.Completable;

@Dao
public interface NoteTagsCrossRefDao {

    @Insert
    Completable insert(NoteTagsCrossRef... refs);

}
