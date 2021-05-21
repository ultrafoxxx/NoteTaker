package com.holzhausen.notetaker.adapters;

import android.content.Context;

import com.holzhausen.notetaker.models.NoteInfo;

import java.util.List;

public interface AdapterHelper {

    void onDataChanged(List<NoteInfo> data);

    Context getContext();

}
