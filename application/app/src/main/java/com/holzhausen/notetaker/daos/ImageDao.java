package com.holzhausen.notetaker.daos;

import androidx.room.Dao;
import androidx.room.Insert;

import com.holzhausen.notetaker.models.Image;

import io.reactivex.Completable;

@Dao
public interface ImageDao {

    @Insert
    Completable insert(Image... images);

}
