package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by harika on 20-07-2018.
 */

public class ShareEndUserDocumentsRequest {

    @SerializedName("document_ids")
    @Expose
    private String[] document_ids;

    @SerializedName("workspace_id")
    @Expose
    private int workspace_id;

    @SerializedName("category_id")
    @Expose
    private int category_id;

    public ShareEndUserDocumentsRequest(String[] document_ids,int workspace_id,int category_id){
        this.document_ids = document_ids;
        this.workspace_id = workspace_id;
        this.category_id = category_id;
    }

    public String[] getDocument_ids() {
        return document_ids;
    }

    public void setDocument_ids(String[] document_ids) {
        this.document_ids = document_ids;
    }

    public int getWorkspace_id() {
        return workspace_id;
    }

    public void setWorkspace_id(int workspace_id) {
        this.workspace_id = workspace_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
}
