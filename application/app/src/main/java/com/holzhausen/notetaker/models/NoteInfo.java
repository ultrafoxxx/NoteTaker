package com.holzhausen.notetaker.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class NoteInfo implements Serializable {

    @Embedded
    private Note note;

    @Relation(
            parentColumn = "noteId",
            entityColumn = "tagName",
            associateBy = @Junction(NoteTagsCrossRef.class)
    )
    private List<Tag> tags;

    @Relation(
            parentColumn = "noteId",
            entityColumn = "noteId"
    )
    private List<Image> images;

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteInfo noteInfo = (NoteInfo) o;
        return note.equals(noteInfo.note) &&
                Objects.equals(tags, noteInfo.tags) &&
                Objects.equals(images, noteInfo.images);
    }

    @Override
    public int hashCode() {
        return Objects.hash(note, tags, images);
    }
}
