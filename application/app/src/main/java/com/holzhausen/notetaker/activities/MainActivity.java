package com.holzhausen.notetaker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.holzhausen.notetaker.R;
import com.holzhausen.notetaker.adapters.AdapterHelper;
import com.holzhausen.notetaker.adapters.NoteAdapter;
import com.holzhausen.notetaker.application.NoteTakerApp;
import com.holzhausen.notetaker.daos.NoteDao;
import com.holzhausen.notetaker.models.NoteInfo;
import com.nambimobile.widgets.efab.FabOption;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterHelper {

    private TextView noElementsInfo;

    private RecyclerView recyclerView;

    private FabOption newNoteOption;

    private FabOption scanTextOption;

    private NoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assignViews();
        assignDataToViews();
        newNoteOption.setOnClickListener(this::onNewNoteOptionClicked);
    }

    private void onNewNoteOptionClicked(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    private void assignDataToViews() {
        noteDao = ((NoteTakerApp)getApplication()).getDatabase().noteDao();
        final NoteAdapter adapter = new NoteAdapter(noteDao.getAll(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void assignViews() {
        noElementsInfo = findViewById(R.id.no_elements_info);
        recyclerView = findViewById(R.id.recyclerView);
        newNoteOption = findViewById(R.id.new_note_option);
        scanTextOption = findViewById(R.id.scan_text);
    }

    @Override
    public void onDataChanged(List<NoteInfo> data) {
        if(data.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            noElementsInfo.setVisibility(View.VISIBLE);
        }
        else {
            noElementsInfo.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }
}