package com.holzhausen.notetaker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.holzhausen.notetaker.R;
import com.holzhausen.notetaker.models.Note;

import java.util.Objects;


public class NoteFragment extends Fragment {

    private TextView contentTextView;

    private TextView titleTextView;

    private Button confirmNoteButton;

    public NoteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        titleTextView = view.findViewById(R.id.title_text_input);
        confirmNoteButton = view.findViewById(R.id.confirm_note_button);
        confirmNoteButton.setOnClickListener(this::onConfirmNote);
        contentTextView = view.findViewById(R.id.content_text_input);
        String recognizedText = requireArguments()
                .getString(getString(R.string.scan_text_key), "");
        if(!isNullOrEmpty(recognizedText)){
            contentTextView.setText(recognizedText);
        }
        return view;
    }

    private void onConfirmNote(View view) {

        String title = titleTextView.getText().toString();
        String noteContent = contentTextView.getText().toString();

        if(isNullOrEmpty(title) || isNullOrEmpty(noteContent)){
            Toast.makeText(getContext(), getString(R.string.empty_fields_info), Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note();
        note.setNoteTitle(title);
        note.setNoteContent(noteContent);

        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.note_key), note);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack("note")
                .replace(R.id.fragment_container, DetailsFragment.class, bundle)
                .commit();
    }

    private boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}