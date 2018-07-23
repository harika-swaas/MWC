package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 7/20/2018.
 */

public class DocumentNotesResponse {

    @SerializedName("notes_id")
    @Expose
    String notes_id;

    @SerializedName("date")
    @Expose
    String date;

    @SerializedName("subject")
    @Expose
    String subject;

    @SerializedName("document_id")
    @Expose
    String document_id;

    @SerializedName("createdby")
    @Expose
    String createdby;

    @SerializedName("document")
    @Expose
    String document;

    @SerializedName("editable")
    @Expose
    String editable;

    @SerializedName("type")
    @Expose
    String type;

    public String getNotes_id() {
        return notes_id;
    }

    public void setNotes_id(String notes_id) {
        this.notes_id = notes_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getEditable() {
        return editable;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
