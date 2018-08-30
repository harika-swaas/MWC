package com.mwc.docportal.API.Model;

/**
 * Created by barath on 7/19/2018.
 */
public class DocumentPropertiesRequest {


    String document_version_id;

    public DocumentPropertiesRequest(String ver_id) {
        this.document_version_id=ver_id;
    }


    public String getDocument_version_id() {
        return document_version_id;
    }

    public void setDocument_version_id(String document_version_id) {
        this.document_version_id = document_version_id;
    }
}
