package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetSharedCategoryDocumentsRequest
{
    @SerializedName("category_id")
    @Expose
    private String category_id;


    public GetSharedCategoryDocumentsRequest(String category_id){
        this.category_id = category_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
