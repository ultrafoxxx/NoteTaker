package com.holzhausen.notetaker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.holzhausen.notetaker.R;
import com.holzhausen.notetaker.models.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DetailsFragment extends Fragment {

    private DatePicker datePicker;

    private TimePicker timePicker;

    private Button setAlarmButton;

    private Button cancelAlarmButton;

    private Note note;


    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        note = (Note) requireArguments().getSerializable(getString(R.string.note_key));
        datePicker = view.findViewById(R.id.datePicker);
        timePicker = view.findViewById(R.id.timePicker);
        setAlarmButton = view.findViewById(R.id.set_alarm_button);
        setAlarmButton.setOnClickListener(this::onSetAlarm);
        cancelAlarmButton = view.findViewById(R.id.cancel_alarm_button);
        cancelAlarmButton.setOnClickListener(this::goToNextFragment);
        return view;
    }

    private void onSetAlarm(View view) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                timePicker.getHour(), timePicker.getMinute());
        Date currentDate = new Date();
        Date chosenDate = calendar.getTime();
        if(currentDate.after(chosenDate)){
            Toast.makeText(getContext(), getString(R.string.wrong_alarm_info), Toast.LENGTH_SHORT).show();
            return;
        }
        note.setRemindDate(chosenDate);
        goToNextFragment(view);
    }

    private void goToNextFragment(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.note_key), note);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack("details")
                .replace(R.id.fragment_container, TagGalleryFragment.class, bundle)
                .commit();
    }
}