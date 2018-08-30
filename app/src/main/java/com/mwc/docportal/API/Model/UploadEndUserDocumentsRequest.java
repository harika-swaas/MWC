package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 20-07-2018.
 */

public class UploadEndUserDocumentsRequest {

    @SerializedName("category_id")
    @Expose
    private String category_id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("tag")
    @Expose
    private String tag;

    @SerializedName("doc_created_date")
    @Expose
    private String doc_created_date;

    public UploadEndUserDocumentsRequest(String category_id, String name, String author, String tag, String doc_created_date){
        this.category_id = category_id;
        this.name = name;
        this.author = author;
        this.tag = tag;
        this.doc_created_date = doc_created_date;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDoc_created_date() {
        return doc_created_date;
    }

    public void setDoc_created_date(String doc_created_date) {
        this.doc_created_date = doc_created_date;
    }
}
