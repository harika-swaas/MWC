package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 26-06-2018.
 */

public class UpdateFTLStatusResponse {

    @SerializedName("ftl_status")
    @Expose
    private int ftl_status;

    @SerializedName("username")
    @Expose
    private String username;

    public int getFtl_status() {
        return ftl_status;
    }

    public void setFtl_status(int ftl_status) {
        this.ftl_status = ftl_status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
