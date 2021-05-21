package com.holzhausen.notetaker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.holzhausen.notetaker.R;
import com.holzhausen.notetaker.models.NoteInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{

    private List<NoteInfo> notes;

    private CompositeDisposable compositeDisposable;

    private AdapterHelper helper;

    public NoteAdapter(Flowable<List<NoteInfo>> noteFlowable, AdapterHelper helper){
        compositeDisposable = new CompositeDisposable();
        this.helper = helper;
        Disposable disposable = noteFlowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notes -> {
                    this.notes = notes;
                    notifyDataSetChanged();
                    helper.onDataChanged(notes);
                });
        compositeDisposable.add(disposable);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_itemd, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getNoteTitle().setText(notes.get(position).getNote().getNoteTitle());
        holder.getCreationDate()
                .setText(new SimpleDateFormat(helper.getContext().getString(R.string.date_format), Locale.GERMANY)
                .format(notes.get(position).getNote().getCreationDate()));
        final Date remindDate = notes.get(position).getNote().getRemindDate();
        if(remindDate != null){
            holder.getGroup().setVisibility(View.VISIBLE);
            holder.getAlertDate()
                    .setText(new SimpleDateFormat(helper.getContext().getString(R.string.date_format), Locale.GERMANY)
                            .format(notes.get(position).getNote().getRemindDate()));
        }

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        compositeDisposable.dispose();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView noteTitle;

        private final TextView creationDate;

        private final TextView alertDate;

        private final Group group;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.note_title);
            creationDate = itemView.findViewById(R.id.creationDate);
            alertDate = itemView.findViewById(R.id.alertDate);
            group = itemView.findViewById(R.id.group);

        }

        public TextView getNoteTitle() {
            return noteTitle;
        }

        public TextView getCreationDate() {
            return creationDate;
        }

        public TextView getAlertDate() {
            return alertDate;
        }

        public Group getGroup() {
            return group;
        }
    }

}
