package com.holzhausen.notetaker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.holzhausen.notetaker.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ScanNoteActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private PreviewView cameraPreview;

    private ImageCapture imageCapture;

    private File imageFile;

    private boolean wasPictureTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_note);

        fab = findViewById(R.id.take_photo);
        cameraPreview = findViewById(R.id.camera_preview);
        startCamera();
        fab.setOnClickListener(this::onShootPhoto);
    }

    private void onShootPhoto(View view) {
        if(imageFile != null) {
            imageFile.delete();
        }
        try {
            imageFile = createImageFile(this);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions
                .Builder(imageFile).build();
        final Context context = this;
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NotNull ImageCapture.OutputFileResults outputFileResults) {
                        wasPictureTaken = true;
                        Intent intent = new Intent();
                        intent.setData(Uri.fromFile(imageFile));
                        intent.putExtra(getString(R.string.file_name_key), imageFile.getName());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    @Override
                    public void onError(@NotNull ImageCaptureException error) {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> listenable = ProcessCameraProvider.getInstance(this);
        listenable.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = listenable.get();
                Preview preview = new Preview.Builder().build();
                CameraSelector selector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());
                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(getWindow().getDecorView().getDisplay().getRotation())
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build();
                cameraProvider.bindToLifecycle(this, selector, imageCapture, preview);
            } catch (ExecutionException | InterruptedException e){
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY).format(new Date());
        String imageFileName = "Photo_" + timeStamp + "_";
        File storageDir = context.getFilesDir();
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!wasPictureTaken && imageFile != null) {
            imageFile.delete();
        }
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