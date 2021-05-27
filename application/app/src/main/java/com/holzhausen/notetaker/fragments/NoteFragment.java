package com.holzhausen.notetaker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.holzhausen.notetaker.R;


public class NoteFragment extends Fragment {

    private TextView contentTextView;

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
        contentTextView = view.findViewById(R.id.content_text_input);
        String recognizedText = requireArguments()
                .getString(getContext().getString(R.string.scan_text_key), "");
        if(recognizedText != null && !recognizedText.isEmpty()){
            contentTextView.setText(recognizedText);
        }
        return view;
    }
}