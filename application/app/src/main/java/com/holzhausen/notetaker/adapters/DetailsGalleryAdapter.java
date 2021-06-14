package com.holzhausen.notetaker.adapters;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holzhausen.notetaker.R;
import com.holzhausen.notetaker.models.Image;

import java.util.List;

public class DetailsGalleryAdapter extends RecyclerView.Adapter<DetailsGalleryAdapter.ViewHolder>{

    private List<Image> images;

    private DetailsGalleryHelper helper;

    public DetailsGalleryAdapter(List<Image> images, DetailsGalleryHelper helper) {
        this.images = images;
        this.helper = helper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_gallery_row, parent, false);
        return new ViewHolder(view, helper);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String leftImageName = images.get(position * 2).getFileName();
        holder.getLeftImage().setImageURI(Uri.fromFile(helper.getImageFile(leftImageName)));
        if(images.size() - 1 == position * 2){
            holder.getRightImage().setVisibility(View.GONE);
            return;
        }
        String rightImageName = images.get(position * 2 + 1).getFileName();
        holder.getRightImage().setImageURI(Uri.fromFile(helper.getImageFile(rightImageName)));
    }

    @Override
    public int getItemCount() {
        return (images.size() + 1) / 2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView leftImage;

        private final ImageView rightImage;

        private final DetailsGalleryHelper helper;


        public ViewHolder(@NonNull View itemView, DetailsGalleryHelper helper) {
            super(itemView);

            leftImage = itemView.findViewById(R.id.left_image);
            leftImage.setOnClickListener(this);
            rightImage = itemView.findViewById(R.id.right_image);
            rightImage.setOnClickListener(this);
            this.helper = helper;
        }

        public ImageView getRightImage() {
            return rightImage;
        }

        public ImageView getLeftImage() {
            return leftImage;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == rightImage.getId()) {
                helper.showImage(getAdapterPosition() * 2 + 1);
            }
            else if (v.getId() == leftImage.getId()) {
                helper.showImage(getAdapterPosition() * 2);
            }
        }
    }

}
