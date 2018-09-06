package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by barath on 7/21/2018.
 */

public class DownloadDocumentRequest {

    @SerializedName("document_ids")
    @Expose
    List<String> document_ids;


    @SerializedName("is_shared")
    @Expose
    private String is_shared;

    public DownloadDocumentRequest(List<String>id, String is_shared)
    {
        this.document_ids=id;
        this.is_shared = is_shared;
    }
    public List<String> getDocument_ids() {
        return document_ids;
    }

    public void setDocument_ids(List<String> document_ids) {
        this.document_ids = document_ids;
    }

    public String getIs_shared() {
        return is_shared;
    }

    public void setIs_shared(String is_shared) {
        this.is_shared = is_shared;
    }
}
