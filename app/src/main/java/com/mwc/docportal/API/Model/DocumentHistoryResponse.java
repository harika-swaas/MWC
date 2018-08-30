package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 7/20/2018.
 */

public class DocumentHistoryResponse {

    @SerializedName("document_version_id")
    @Expose
    String document_version_id;

    @SerializedName("version_number")
    @Expose
    String version_number;

    @SerializedName("filename")
    @Expose
    String filename;

    @SerializedName("created_date")
    @Expose
    String created_date;

    @SerializedName("is_current_version")
    @Expose
    String is_current_version;

    public String getDocument_version_id() {
        return document_version_id;
    }

    public void setDocument_version_id(String document_version_id) {
        this.document_version_id = document_version_id;
    }

    public String getVersion_number() {
        return version_number;
    }

    public void setVersion_number(String version_number) {
        this.version_number = version_number;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDoc_created_date() {
        return created_date;
    }

    public void setDoc_created_date(String doc_created_date) {
        this.created_date = doc_created_date;
    }

    public String getIs_current_version() {
        return is_current_version;
    }

    public void setIs_current_version(String is_current_version) {
        this.is_current_version = is_current_version;
    }
}
