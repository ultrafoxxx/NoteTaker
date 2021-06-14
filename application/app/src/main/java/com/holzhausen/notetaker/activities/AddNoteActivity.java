package com.holzhausen.notetaker.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.holzhausen.notetaker.R;
import com.holzhausen.notetaker.fragments.DetailsFragment;
import com.holzhausen.notetaker.fragments.NoteFragment;
import com.holzhausen.notetaker.fragments.TagGalleryFragment;

public class AddNoteActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> imageCaptureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        String recognitionResult = getIntent().getStringExtra(getString(R.string.scan_text_key));
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.scan_text_key), recognitionResult);

        imageCaptureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        this::onGetPhoto);

        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container, NoteFragment.class, bundle)
                .commit();
    }

    public ActivityResultLauncher<Intent> getImageCaptureLauncher() {
        return imageCaptureLauncher;
    }

    private void onGetPhoto(ActivityResult result) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(fragment instanceof TagGalleryFragment) {
            TagGalleryFragment tagGalleryFragment = (TagGalleryFragment) fragment;
            tagGalleryFragment.onGetPhoto(result);

        }
    }
}