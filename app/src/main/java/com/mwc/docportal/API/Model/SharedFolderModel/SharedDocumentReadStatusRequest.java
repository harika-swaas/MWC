package com.mwc.docportal.API.Model.SharedFolderModel;

public class SharedDocumentReadStatusRequest
{
    private String document_share_id;


    public SharedDocumentReadStatusRequest(String document_share_id){
        this.document_share_id = document_share_id;

    }

    public String getDocument_share_id() {
        return document_share_id;
    }

    public void setDocument_share_id(String document_share_id) {
        this.document_share_id = document_share_id;
    }
}
