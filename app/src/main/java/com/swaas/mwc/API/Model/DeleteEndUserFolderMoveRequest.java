package com.swaas.mwc.API.Model;

/**
 * Created by barath on 8/14/2018.
 */

public class DeleteEndUserFolderMoveRequest {

    String mode;
    String category_ids[];

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String[] getCategory_ids() {
        return category_ids;
    }

    public void setCategory_ids(String[] category_ids) {
        this.category_ids = category_ids;
    }
}
