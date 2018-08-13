package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class ExternalShareResponseModel
{

    @SerializedName("type")
    @Expose
    private String type;


    @SerializedName("document_version_id")
    @Expose
    private String document_version_id;


    public ExternalShareResponseModel(String type, String document_version_id){

        this.type = type;
        this.document_version_id = document_version_id;

    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocument_version_id() {
        return document_version_id;
    }

    public void setDocument_version_id(String document_version_id) {
        this.document_version_id = document_version_id;
    }


}
