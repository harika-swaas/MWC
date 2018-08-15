package com.swaas.mwc.API.Model;

/**
 * Created by barath on 8/14/2018.
 */

public class DeleteEndUserFolderRequest {

    String category_ids[];
    String mode;
    String workspace_id;
    String destination_category_id;

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

    public String getWorkspace_id() {
        return workspace_id;
    }

    public void setWorkspace_id(String workspace_id) {
        this.workspace_id = workspace_id;
    }

    public String getDestination_category_id() {
        return destination_category_id;
    }

    public void setDestination_category_id(String destination_category_id) {
        this.destination_category_id = destination_category_id;
    }
}
