package com.mwc.docportal.API.Model;

import java.util.ArrayList;

/**
 * Created by barath on 8/9/2018.
 */

public class EditDocumentPropertiesRequest {

    String document_version_id;
    String name;
    String doc_created_date;
    String author;

    ArrayList<EditDocumentResponse> tag;

    public EditDocumentPropertiesRequest(String object_id, String name, String doc_created, String auth,  ArrayList<EditDocumentResponse> taglist) {
        this.document_version_id=object_id;
        this.name=name;
        this.doc_created_date=doc_created;
        this.author=auth;
        this.tag = taglist;
    }

    public String getDocument_version_id() {
        return document_version_id;
    }

    public void setDocument_version_id(String document_version_id) {
        this.document_version_id = document_version_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoc_created_date() {
        return doc_created_date;
    }

    public void setDoc_created_date(String doc_created_date) {
        this.doc_created_date = doc_created_date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<EditDocumentResponse> getTag() {
        return tag;
    }

    public void setTag(ArrayList<EditDocumentResponse> tag) {
        this.tag = tag;
    }
}
