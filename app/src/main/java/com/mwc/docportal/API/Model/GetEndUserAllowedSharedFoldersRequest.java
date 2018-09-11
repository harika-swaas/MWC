package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 19-07-2018.
 */

public class GetEndUserAllowedSharedFoldersRequest {

    @SerializedName("workspace_id")
    @Expose
    private String workspace_id;

    @SerializedName("category_id")
    @Expose
    private String category_id;

    public GetEndUserAllowedSharedFoldersRequest(String workspace_id,String category_id){
        this.workspace_id = workspace_id;
        this.category_id = category_id;
    }

    public String getWorkspace_id() {
        return workspace_id;
    }

    public void setWorkspace_id(String workspace_id) {
        this.workspace_id = workspace_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
