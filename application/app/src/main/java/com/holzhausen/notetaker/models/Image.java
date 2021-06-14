package com.holzhausen.notetaker.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Image {

    public Image(@NonNull String fileName, long noteId) {
        this.fileName = fileName;
        this.noteId = noteId;
    }

    @PrimaryKey
    @NonNull
    private String fileName;

    private long noteId;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return Objects.equals(fileName, image.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName);
    }
}
