package com.swaas.mwc.API.Model;

/**
 * Created by barath on 8/5/2018.
 */

public class EndUserRenameRequest {

    String category_id;
    String category_name;
    String parent_id;

    public EndUserRenameRequest(String object_id, String name, String parentid) {
        this.category_id=object_id;
        this.category_name=name;
        this.parent_id=parentid;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }
}
