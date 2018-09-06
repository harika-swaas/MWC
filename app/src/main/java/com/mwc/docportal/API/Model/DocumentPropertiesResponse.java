package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 7/19/2018.
 */

public class DocumentPropertiesResponse {

    @SerializedName("version_number")
    @Expose
    String version_number;

    @SerializedName("document_name")
    @Expose
    String document_name;

    @SerializedName("filename")
    @Expose
    String filename;

    @SerializedName("filesize")
    @Expose
    String filesize;

    @SerializedName("filetype")
    @Expose
    String filetype;

    @SerializedName("uploaded_date")
    @Expose
    String uploaded_date;

    @SerializedName("author")
    @Expose
    String author;

    @SerializedName("creation_date")
    @Expose
    String creation_date;

    @SerializedName("tag")
    @Expose
    String tag;

    @SerializedName("document_id")
    @Expose
    String document_id;

    @SerializedName("category_id")
    @Expose
    String category_id;

    @SerializedName("is_shared")
    @Expose
    String is_shared;


    @SerializedName("version_count")
    @Expose
    String version_count;


    public String getVersion_count() {
        return version_count;
    }

    public void setVersion_count(String version_count) {
        this.version_count = version_count;
    }

    public String getIs_shared() {
        return is_shared;
    }

    public void setIs_shared(String is_shared) {
        this.is_shared = is_shared;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getVersion_number() {
        return version_number;
    }

    public void setVersion_number(String version_number) {
        this.version_number = version_number;
    }

    public String getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name = document_name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getUploaded_date() {
        return uploaded_date;
    }

    public void setUploaded_date(String uploaded_date) {
        this.uploaded_date = uploaded_date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
