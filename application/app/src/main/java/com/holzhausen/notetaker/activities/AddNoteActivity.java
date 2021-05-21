package com.holzhausen.notetaker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.holzhausen.notetaker.R;
import com.holzhausen.notetaker.fragments.NoteFragment;

public class AddNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container, NoteFragment.class, null)
                .commit();
    }
}