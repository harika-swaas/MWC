package com.mwc.docportal.API.Model;

import java.util.ArrayList;

/**
 * Created by barath on 8/8/2018.
 */

public class CopyDocumentRequest {

    String category_id;

    ArrayList<String> document_ids;


    public CopyDocumentRequest(ArrayList<String> document_ids , String categoryId) {
        this.category_id=categoryId;
        this.document_ids=document_ids;

    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public ArrayList<String> getDocument_ids() {
        return document_ids;
    }

    public void setDocument_ids(ArrayList<String> document_ids) {
        this.document_ids = document_ids;
    }
}
