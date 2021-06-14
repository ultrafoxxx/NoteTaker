package com.holzhausen.notetaker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.holzhausen.notetaker.R;
import com.holzhausen.notetaker.adapters.DetailsGalleryAdapter;
import com.holzhausen.notetaker.adapters.DetailsGalleryHelper;
import com.holzhausen.notetaker.models.Note;
import com.holzhausen.notetaker.models.NoteInfo;
import com.holzhausen.notetaker.models.Tag;

import java.io.File;

public class DetailsActivity extends AppCompatActivity implements DetailsGalleryHelper {

    private TextView title;

    private TextView content;

    private RecyclerView images;

    private ChipGroup tagContainer;

    private NoteInfo note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        note = (NoteInfo) getIntent().getSerializableExtra(getString(R.string.note_key));

        assignViews();
        propagateData();

    }

    private void assignViews() {
        title = findViewById(R.id.title_detail);
        content = findViewById(R.id.content_detail);
        images = findViewById(R.id.detail_images);
        tagContainer = findViewById(R.id.tag_group_details);
    }

    private void propagateData() {
        title.setText(note.getNote().getNoteTitle());
        content.setText(note.getNote().getNoteContent());
        note.getTags().forEach(this::addTag);

        DetailsGalleryAdapter adapter = new DetailsGalleryAdapter(note.getImages(), this);
        images.setAdapter(adapter);
        images.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addTag(Tag tag) {
        Chip chip = (Chip) LayoutInflater.from(this)
                .inflate(R.layout.standalone_chip, tagContainer, false);
        chip.setText(tag.getTagName());
        tagContainer.addView(chip);
    }

    @Override
    public void showImage(int position) {

        Uri imageUri = FileProvider.getUriForFile(this, getString(R.string.authority),
                getImageFile(note.getImages().get(position).getFileName()));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(imageUri, getString(R.string.vie_image_type));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public File getImageFile(String fileName) {
        return getFileStreamPath(fileName);
    }
}