package com.holzhausen.notetaker.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.holzhausen.notetaker.models.Tag;

import io.reactivex.Completable;

@Dao
public interface TagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Tag... tags);

}
