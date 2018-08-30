package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 7/24/2018.
 */

public class GetUserNotesDetailsRequest {
    @SerializedName("notes_id")
    @Expose
    private int notes_id;

    public GetUserNotesDetailsRequest(int id) {
        this.notes_id = id;
    }

    public int getNotes_id() {
        return notes_id;
    }

    public void setNotes_id(int notes_id) {
        this.notes_id = notes_id;
    }
}
