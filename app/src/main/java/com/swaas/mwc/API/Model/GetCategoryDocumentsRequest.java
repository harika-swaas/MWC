package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 12-07-2018.
 */

public class GetCategoryDocumentsRequest {

    @SerializedName("category_id")
    @Expose
    private int category_id;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("categoryType")
    @Expose
    private String categoryType;

    @SerializedName("get_folders")
    @Expose
    private String get_folders;

    @SerializedName("isBuUser")
    @Expose
    private String isBuUser;

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getGet_folders() {
        return get_folders;
    }

    public void setGet_folders(String get_folders) {
        this.get_folders = get_folders;
    }

    public String getIsBuUser() {
        return isBuUser;
    }

    public void setIsBuUser(String isBuUser) {
        this.isBuUser = isBuUser;
    }
}
