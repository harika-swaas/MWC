package com.swaas.mwc.API.Model;

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
    public DownloadDocumentRequest(List<String>id)
    {
        this.document_ids=id;
    }
    public List<String> getDocument_ids() {
        return document_ids;
    }

    public void setDocument_ids(List<String> document_ids) {
        this.document_ids = document_ids;
    }
}
