package com.example.essam.noytification.Model;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String documentId;
    private String title ;
    private String description;
    private int priority;

    public Note() {
    }


    public Note(String title, String description,int priority) {
        this.title = title;
        this.description = description;
        this.priority=priority;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
