package com.mwc.docportal.API.Model.GlobalSearchModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GlobalSearchRequestModel
{
    @SerializedName("search_key")
    @Expose
    private String search_key;


    public GlobalSearchRequestModel(String search_key){
        this.search_key = search_key;

    }


    public String getSearch_key() {
        return search_key;
    }

    public void setSearch_key(String search_key) {
        this.search_key = search_key;
    }
}
