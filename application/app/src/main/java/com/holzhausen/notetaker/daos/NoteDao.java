package com.holzhausen.notetaker.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.holzhausen.notetaker.models.Note;
import com.holzhausen.notetaker.models.NoteInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface NoteDao {

    @Insert
    Single<Long> insert(Note note);

    @Update
    Completable update(Note note);

    @Delete
    Completable delete(Note note);

    @Transaction
    @Query("SELECT * FROM Note")
    Flowable<List<NoteInfo>> getAll();

}
