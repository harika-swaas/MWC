package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 28-06-2018.
 */

public class AddFTLDetailsRequest {

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("ftl_completed_using")
    @Expose
    private String ftl_completed_using;

    public AddFTLDetailsRequest(String username, String password, String ftl_completed_using){
        this.username = username;
        this.password = password;
        this.ftl_completed_using = ftl_completed_using;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFtl_completed_using() {
        return ftl_completed_using;
    }

    public void setFtl_completed_using(String ftl_completed_using) {
        this.ftl_completed_using = ftl_completed_using;
    }
}
