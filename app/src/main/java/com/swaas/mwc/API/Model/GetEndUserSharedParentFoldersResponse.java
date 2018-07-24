package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 19-07-2018.
 */

public class GetEndUserSharedParentFoldersResponse {

    @SerializedName("category_name")
    @Expose
    private String category_name;

    @SerializedName("category_id")
    @Expose
    private String category_id;

    @SerializedName("full_path")
    @Expose
    private String full_path;

    @SerializedName("workspace_id")
    @Expose
    private String workspace_id;

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getFull_path() {
        return full_path;
    }

    public void setFull_path(String full_path) {
        this.full_path = full_path;
    }

    public String getWorkspace_id() {
        return workspace_id;
    }

    public void setWorkspace_id(String workspace_id) {
        this.workspace_id = workspace_id;
    }
}
