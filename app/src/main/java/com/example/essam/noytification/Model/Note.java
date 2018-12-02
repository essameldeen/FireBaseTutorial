package com.example.essam.noytification.Model;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String documentId;
    private String title ;
    private String description;

    public Note() {
    }


    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
