package com.swaas.mwc.API.Model;

/**
 * Created by barath on 8/8/2018.
 */

public class CopyDocumentRequest {

    String category_id;

    String document_ids[];






    public CopyDocumentRequest(String[] document_ids, String categoryId) {
        this.category_id=categoryId;
        this.document_ids=document_ids;

    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String[] getDocument_obj() {
        return document_ids;
    }

    public void setDocument_obj(String[] document_obj) {
        this.document_ids = document_obj;
    }

}
