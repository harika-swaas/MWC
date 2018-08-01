package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 7/26/2018.
 */

public class DocumentPreviewResponse {

    @SerializedName("document_pdf_url")
    @Expose
    private String document_pdf_url;

    public String getDocument_pdf_url() {
        return document_pdf_url;
    }

    public void setDocument_pdf_url(String document_pdf_url) {
        this.document_pdf_url = document_pdf_url;
    }
}
