package com.mwc.docportal.API.Model;

/**
 * Created by barath on 7/20/2018.
 */

public class DocumentNotesRequest {


    String document_id;
    public  DocumentNotesRequest(String doc_id)
    {
        this.document_id=doc_id;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }
}
