package com.holzhausen.notetaker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.holzhausen.notetaker.R;

public class ViewPhotoActivity extends AppCompatActivity {

    private ImageView imageView;

    private ConstraintLayout buttonContainer;

    private Button scanButton;

    private Button cancelButton;

    private boolean isContainerShown;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        assignViews();
        isContainerShown = true;
        imageUri = getIntent().getData();
        imageView.setImageURI(imageUri);

        imageView.setOnClickListener(this::onImageClick);
        scanButton.setOnClickListener(this::onScanClick);
        cancelButton.setOnClickListener(this::onCancelClick);
    }

    private void onCancelClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void onScanClick(View view) {
        Intent intent = new Intent();
        intent.setData(imageUri);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onImageClick(View view) {
        if(isContainerShown){
            buttonContainer.setVisibility(View.GONE);
            isContainerShown = false;
        }
        else {
            buttonContainer.setVisibility(View.VISIBLE);
            isContainerShown = true;
        }
    }

    private void assignViews() {
        imageView = findViewById(R.id.image_capture);
        buttonContainer = findViewById(R.id.button_container);
        scanButton = findViewById(R.id.scan_button);
        cancelButton = findViewById(R.id.cancel_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}