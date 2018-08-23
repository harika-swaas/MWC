package com.swaas.mwc.API.Model;

import java.util.ArrayList;

/**
 * Created by barath on 8/6/2018.
 */

public class MoveDocumentRequest {

    String category_id;



    ArrayList<String> document_obj;


    public MoveDocumentRequest(ArrayList<String> document_ids, String categoryId) {
        this.category_id=categoryId;
        this.document_obj=document_ids;
    }


    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public ArrayList<String> getDocument_obj() {
        return document_obj;
    }

    public void setDocument_obj(ArrayList<String> document_obj) {
        this.document_obj = document_obj;
    }


}
