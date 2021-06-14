package com.holzhausen.notetaker.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.holzhausen.notetaker.R;
import com.holzhausen.notetaker.activities.AddNoteActivity;
import com.holzhausen.notetaker.activities.ScanNoteActivity;
import com.holzhausen.notetaker.adapters.GalleryAdapter;
import com.holzhausen.notetaker.adapters.GalleryHelper;
import com.holzhausen.notetaker.application.NoteTakerApp;
import com.holzhausen.notetaker.daos.ImageDao;
import com.holzhausen.notetaker.daos.NoteDao;
import com.holzhausen.notetaker.daos.NoteTagsCrossRefDao;
import com.holzhausen.notetaker.daos.TagDao;
import com.holzhausen.notetaker.models.GalleryElement;
import com.holzhausen.notetaker.models.Image;
import com.holzhausen.notetaker.models.Note;
import com.holzhausen.notetaker.models.NoteTagsCrossRef;
import com.holzhausen.notetaker.models.Tag;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class TagGalleryFragment extends Fragment implements GalleryHelper {

    private ProgressBar progressBar;

    private EditText tagEditText;

    private Button addTagButton;

    private ChipGroup tagContainer;

    private RecyclerView gallery;

    private Button addPhotoButton;

    private Button addNoteButton;

    private Note note;

    private List<String> tags;

    private GalleryAdapter adapter;

    private CompositeDisposable compositeDisposable;

    private boolean wasFinishedProperly;

    public TagGalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_gallery, container, false);
        assignViews(view);
        note = (Note) requireArguments().getSerializable(getString(R.string.note_key));
        addTagButton.setOnClickListener(this::onAddTag);
        tags = new LinkedList<>();
        adapter = new GalleryAdapter(this);
        gallery.setAdapter(adapter);
        gallery.setLayoutManager(new LinearLayoutManager(requireContext()));
        gallery.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        addPhotoButton.setOnClickListener(this::onAddPhoto);
        addNoteButton.setOnClickListener(this::onAddNote);
        compositeDisposable = new CompositeDisposable();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        if(!wasFinishedProperly){
            adapter.getElements().forEach(galleryElement -> {
                requireActivity().getFileStreamPath(galleryElement.getFileName()).delete();
            });
        }
    }

    private void onAddNote(View view) {
        note.setCreationDate(new Date());
        NoteDao noteDao = ((NoteTakerApp)requireActivity().getApplication()).getDatabase().noteDao();
        Disposable disposable = noteDao.insert(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::insertTags);
        compositeDisposable.add(disposable);
    }

    private void insertTags(Long noteId) {
        note.setNoteId(noteId);
        TagDao tagDao = ((NoteTakerApp)requireActivity().getApplication()).getDatabase().tagDao();
        Tag[] tags = this.tags.stream().map(Tag::new).toArray(Tag[]::new);
        Disposable disposable = tagDao
                .insert(tags)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> insertCrossRefs(tags));
        compositeDisposable.add(disposable);
    }


    private void insertCrossRefs(Tag[] tags) {
        NoteTagsCrossRefDao dao = ((NoteTakerApp)requireActivity().getApplication()).getDatabase().noteTagsCrossRefDao();
        NoteTagsCrossRef[] crossRefs = Arrays.stream(tags).map(tag -> {
            NoteTagsCrossRef crossRef = new NoteTagsCrossRef();
            crossRef.setNoteId(note.getNoteId());
            crossRef.setTagName(tag.getTagName());
            return crossRef;
        }).toArray(NoteTagsCrossRef[]::new);
        Disposable disposable = dao.insert(crossRefs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::insertImages);
        compositeDisposable.add(disposable);
    }

    private void insertImages() {
        ImageDao dao = ((NoteTakerApp)requireActivity().getApplication()).getDatabase().imageDao();
        Image[] images = adapter.getElements().
                stream()
                .map(galleryElement -> new Image(galleryElement.getFileName(), note.getNoteId()))
                .toArray(Image[]::new);
        Disposable disposable = dao.insert(images)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::finishActivity);
        compositeDisposable.add(disposable);
    }

    private void finishActivity() {
        wasFinishedProperly = true;
        requireActivity().finish();
    }

    public void onGetPhoto(ActivityResult result) {
        requireActivity();
        if(result.getResultCode() != Activity.RESULT_OK) {
            return;
        }
        Uri imageUri = result.getData().getData();
        String fileName = result.getData().getStringExtra(getString(R.string.file_name_key));
        InputImage inputImage;
        try {
            inputImage = InputImage.fromFilePath(requireContext(), imageUri);
        } catch (IOException e){
            onError(e, fileName);
            return;
        }
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        progressBar.setVisibility(View.VISIBLE);
        labeler.process(inputImage).addOnSuccessListener(imageLabels -> onLabelingSuccess(imageLabels, fileName, imageUri))
                .addOnFailureListener(e -> {
                    onError(e, fileName);
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void onError(Exception e, String fileName) {
        e.printStackTrace();
        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        deleteFile(fileName);
    }

    private void onLabelingSuccess(List<ImageLabel> imageLabels, String fileName, Uri fileUri) {
        List<String> labels = imageLabels.stream()
                .filter(imageLabel -> imageLabel.getConfidence() > 0.7)
                .map(ImageLabel::getText)
                .collect(Collectors.toList());
        GalleryElement element = new GalleryElement();
        element.setFileName(fileName);
        element.setFileUri(fileUri);
        element.setRecognizedElements(labels);
        labels.forEach(this::addTag);
        adapter.insertElement(element);
        progressBar.setVisibility(View.GONE);
    }

    private void onAddPhoto(View view) {
        Intent intent = new Intent(requireContext(), ScanNoteActivity.class);
        ((AddNoteActivity)requireActivity()).getImageCaptureLauncher().launch(intent);
    }

    private void onAddTag(View view) {

        String tagName = tagEditText.getText().toString();
        addTag(tagName);
    }

    private void addTag(String tagName) {
        if(tagName != null && !tagName.isEmpty()){
            Chip chip = (Chip) LayoutInflater.from(requireContext())
                    .inflate(R.layout.standalone_chip, tagContainer, false);
            chip.setText(tagName);
            chip.setOnCloseIconClickListener(this::onRemoveTag);
            tagEditText.setText("");
            tagContainer.addView(chip);
            tags.add(tagName);
        }
    }

    private void onRemoveTag(View view) {
        if(!(view instanceof Chip)){
            return;
        }
        Chip chip = (Chip) view;
        String tagName = chip.getText().toString();
        tags.remove(tagName);
        tagContainer.removeView(chip);
    }

    private void assignViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        tagEditText = view.findViewById(R.id.tag_text_input);
        addTagButton = view.findViewById(R.id.add_tag_button);
        tagContainer = view.findViewById(R.id.tag_group);
        gallery = view.findViewById(R.id.gallery);
        addPhotoButton = view.findViewById(R.id.add_photo_button);
        addNoteButton = view.findViewById(R.id.add_note_button);
    }

    @Override
    public void deleteFile(String fileName) {
        File file = requireContext().getFileStreamPath(fileName);
        file.delete();
    }
}