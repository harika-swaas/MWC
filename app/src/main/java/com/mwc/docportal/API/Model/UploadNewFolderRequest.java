package com.mwc.docportal.API.Model;

/**
 * Created by barath on 8/9/2018.
 */

public class UploadNewFolderRequest {
    String parent_id ;

    public UploadNewFolderRequest(String categoryId, String folder) {
        this.parent_id=categoryId;
        this.category_name=folder;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    String category_name;

}
