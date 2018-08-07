package com.swaas.mwc.API.Model;

/**
 * Created by barath on 8/6/2018.
 */

public class MoveDocumentRequest {

    String category_id;

    String document_obj[];


    public MoveDocumentRequest(String[] document_ids, String categoryId, String [] category_objs) {
        this.category_id=categoryId;
        this.document_obj=document_ids;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String[] getDocument_obj() {
        return document_obj;
    }

    public void setDocument_obj(String[] document_obj) {
        this.document_obj = document_obj;
    }
}
