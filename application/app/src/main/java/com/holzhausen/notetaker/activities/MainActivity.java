package com.holzhausen.notetaker.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.holzhausen.notetaker.R;
import com.holzhausen.notetaker.adapters.AdapterHelper;
import com.holzhausen.notetaker.adapters.NoteAdapter;
import com.holzhausen.notetaker.application.NoteTakerApp;
import com.holzhausen.notetaker.daos.NoteDao;
import com.holzhausen.notetaker.models.NoteInfo;
import com.nambimobile.widgets.efab.FabOption;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterHelper {

    private TextView noElementsInfo;

    private RecyclerView recyclerView;

    private FabOption newNoteOption;

    private FabOption scanTextOption;

    private NoteDao noteDao;

    private NoteAdapter adapter;

    private ActivityResultLauncher<Intent> scanResultLauncher;

    private ActivityResultLauncher<Intent> viewImageResultLauncher;

    private String fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        assignViews();
        assignDataToViews();
        newNoteOption.setOnClickListener(this::onNewNoteOptionClicked);
        scanTextOption.setOnClickListener(this::onScanNoteClicked);
        scanResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                this::onScanTaken);
        viewImageResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                this::onImageViewed);
    }

    private void onImageViewed(ActivityResult result) {
        try {
            if(result.getResultCode() == RESULT_OK){
                performTextRecognition(result.getData().getData());
            }
            if(fileName != null) {
                getFileStreamPath(fileName).delete();
                fileName = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    private void performTextRecognition(Uri imageUri) throws IOException{
        InputImage image = InputImage.fromFilePath(this, imageUri);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image)
                .addOnSuccessListener(resultText -> {
                    String result = resultText.getText();
                    Intent intent = new Intent(this, AddNoteActivity.class);
                    intent.putExtra(getString(R.string.scan_text_key), result);
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong2", Toast.LENGTH_SHORT).show();
                });
    }


    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
        }
    }

    private void onScanTaken(ActivityResult result) {
        if(result.getResultCode() == RESULT_OK){
            Uri imageUri = result.getData().getData();
            fileName = result.getData().getStringExtra(getString(R.string.file_name_key));
            Intent intent = new Intent(this, ViewPhotoActivity.class);
            intent.setData(imageUri);
            viewImageResultLauncher.launch(intent);
        }
    }

    private void onScanNoteClicked(View view) {
        Intent intent = new Intent(this, ScanNoteActivity.class);
        scanResultLauncher.launch(intent);
    }


    private void onNewNoteOptionClicked(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    private void assignDataToViews() {
        noteDao = ((NoteTakerApp)getApplication()).getDatabase().noteDao();
        adapter = new NoteAdapter(noteDao.getAll(), this);
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

    @Override
    public View.OnClickListener getItemListener() {
        return view -> {
            int position = recyclerView.getChildLayoutPosition(view);
            NoteInfo noteInfo = adapter.getNotes().get(position);
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(getString(R.string.note_key), noteInfo);
            startActivity(intent);
        };
    }
}