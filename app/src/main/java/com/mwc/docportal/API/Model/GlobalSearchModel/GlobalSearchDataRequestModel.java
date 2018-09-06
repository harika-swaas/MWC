package com.mwc.docportal.API.Model.GlobalSearchModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GlobalSearchDataRequestModel
{
    @SerializedName("search_id")
    @Expose
    private String search_id;

    @SerializedName("sequence_id")
    @Expose
    private String sequence_id;

    @SerializedName("limit")
    @Expose
    private int limit;

    public GlobalSearchDataRequestModel(String search_id, String sequence_id, int limit){
        this.search_id = search_id;
        this.sequence_id = sequence_id;
        this.limit = limit;

    }


    public String getSearch_id() {
        return search_id;
    }

    public void setSearch_id(String search_id) {
        this.search_id = search_id;
    }

    public String getSequence_id() {
        return sequence_id;
    }

    public void setSequence_id(String sequence_id) {
        this.sequence_id = sequence_id;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
