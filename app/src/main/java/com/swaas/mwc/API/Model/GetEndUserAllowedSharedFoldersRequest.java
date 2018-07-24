package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 19-07-2018.
 */

public class GetEndUserAllowedSharedFoldersRequest {

    @SerializedName("workspace_id")
    @Expose
    private int workspace_id;

    @SerializedName("category_id")
    @Expose
    private int category_id;

    public GetEndUserAllowedSharedFoldersRequest(int workspace_id,int category_id){
        this.workspace_id = workspace_id;
        this.category_id = category_id;
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
