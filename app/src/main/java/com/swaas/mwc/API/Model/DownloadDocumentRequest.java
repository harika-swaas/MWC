package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 7/21/2018.
 */

public class DownloadDocumentRequest {

    @SerializedName("document_ids")
    @Expose
    String document_ids;

    public String getDocument_ids() {
        return document_ids;
    }

    public void setDocument_ids(String document_ids) {
        this.document_ids = document_ids;
    }
}
