package com.holzhausen.notetaker.models;

import android.net.Uri;

import java.util.List;

public class GalleryElement {

    private String fileName;

    private List<String> recognizedElements;

    private Uri fileUri;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getRecognizedElements() {
        return recognizedElements;
    }

    public void setRecognizedElements(List<String> recognizedElements) {
        this.recognizedElements = recognizedElements;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }
}
