package com.swaas.mwc.API.Model;

import java.util.ArrayList;

/**
 * Created by barath on 8/14/2018.
 */

public class DeleteEndUserFolderRequest {

    String category_ids[];
    String mode;


    public DeleteEndUserFolderRequest(ArrayList<String> categoryids, String s) {
        this.category_ids= categoryids.toArray(new String[0]);
        this.mode=s;
    }

    public String[] getCategory_ids() {
        return category_ids;
    }

    public void setCategory_ids(String[] category_ids) {
        this.category_ids = category_ids;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }


}
