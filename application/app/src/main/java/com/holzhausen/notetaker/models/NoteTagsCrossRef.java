package com.holzhausen.notetaker.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"noteId", "tagName"})
public class NoteTagsCrossRef {

    private long noteId;

    @NonNull
    private String tagName;

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
