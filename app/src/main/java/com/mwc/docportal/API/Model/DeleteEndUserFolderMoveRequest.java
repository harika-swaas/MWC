package com.mwc.docportal.API.Model;

import java.util.ArrayList;

/**
 * Created by barath on 8/14/2018.
 */

public class DeleteEndUserFolderMoveRequest {

    int mode;

    ArrayList<String> category_ids;
    String workspace_id;
    String destination_category_id;



    public DeleteEndUserFolderMoveRequest(ArrayList<String> categoryids, int mode,String destination_category_id) {
        this.category_ids= categoryids;
        this.mode=mode;
        this.destination_category_id=destination_category_id;
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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public ArrayList<String> getCategory_ids() {
        return category_ids;
    }

    public void setCategory_ids(ArrayList<String> category_ids) {
        this.category_ids = category_ids;
    }
}
