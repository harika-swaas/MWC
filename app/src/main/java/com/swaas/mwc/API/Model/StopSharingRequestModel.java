package com.swaas.mwc.API.Model;

import java.util.ArrayList;

public class StopSharingRequestModel
{

    String document_ids[];
    String category_id;



    public StopSharingRequestModel(ArrayList<String> categoryids, String category_id) {
        this.document_ids= categoryids.toArray(new String[categoryids.size()]);
        this.category_id=category_id;
    }


    public String[] getDocument_ids() {
        return document_ids;
    }

    public void setDocument_ids(String[] document_ids) {
        this.document_ids = document_ids;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
