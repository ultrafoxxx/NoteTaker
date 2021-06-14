package com.holzhausen.notetaker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holzhausen.notetaker.R;
import com.holzhausen.notetaker.models.GalleryElement;

import java.util.LinkedList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final List<GalleryElement> elements;

    private final GalleryHelper helper;

    public GalleryAdapter(GalleryHelper helper) {
        elements = new LinkedList<>();
        this.helper = helper;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_element, parent, false);
        return new ViewHolder(view, this::deleteElement);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getGalleryElement().setImageURI(elements.get(position).getFileUri());
        String recognizedElements = "Recognized elements: "
                + String.join(" ", elements.get(position).getRecognizedElements());
        holder.getRecognizedElements().setText(recognizedElements);
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public List<GalleryElement> getElements() {
        return elements;
    }

    public void insertElement(GalleryElement element) {
        int size = elements.size();
        elements.add(element);
        notifyItemInserted(size);
    }

    private void deleteElement(int position) {
        GalleryElement galleryElement = elements.remove(position);
        notifyItemRemoved(position);
        helper.deleteFile(galleryElement.getFileName());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView galleryElement;

        private final TextView recognizedElements;

        private final ImageView deleteView;

        private ViewHolderHelper viewHolderHelper;

        public ViewHolder(@NonNull View itemView, ViewHolderHelper helper) {
            super(itemView);
            galleryElement = itemView.findViewById(R.id.gallery_item_photo);
            recognizedElements = itemView.findViewById(R.id.recognized_elements);
            deleteView = itemView.findViewById(R.id.delete_view);
            deleteView.setOnClickListener(this);
            viewHolderHelper = helper;
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == deleteView.getId()) {
                viewHolderHelper.deleteElement(getAdapterPosition());
            }
        }

        public ImageView getGalleryElement() {
            return galleryElement;
        }

        public TextView getRecognizedElements() {
            return recognizedElements;
        }

    }

    @FunctionalInterface
    private interface ViewHolderHelper {
        void deleteElement(int position);
    }

}
