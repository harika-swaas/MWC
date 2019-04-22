package com.mwc.docportal.ForgotPassword;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserNameRequestModel {
    @SerializedName("username")
    @Expose
    private String username;


    public UserNameRequestModel(String UserName) {
        this.username = UserName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
