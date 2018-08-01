package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 7/26/2018.
 */

public class DocumentPreviewRequest {

    @SerializedName("document_version_id")
    @Expose
    private String document_version_id;

    public DocumentPreviewRequest(String document_version_id){
        this.document_version_id = document_version_id;
    }

    public String getDocument_version_id() {
        return document_version_id;
    }

    public void setDocument_version_id(String document_version_id) {
        this.document_version_id = document_version_id;
    }
}
