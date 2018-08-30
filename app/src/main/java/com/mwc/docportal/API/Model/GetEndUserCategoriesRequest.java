package com.mwc.docportal.API.Model;

/**
 * Created by barath on 8/6/2018.
 */

public class GetEndUserCategoriesRequest {

    String parent_id;

    public GetEndUserCategoriesRequest(String obj) {
        this.parent_id=obj;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }
}
