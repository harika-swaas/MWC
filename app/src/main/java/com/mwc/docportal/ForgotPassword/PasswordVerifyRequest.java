package com.mwc.docportal.ForgotPassword;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PasswordVerifyRequest
{
    @SerializedName("reset_source")
    @Expose
    private String reset_source;

    @SerializedName("password")
    @Expose
    private String password;


    public String getReset_source() {
        return reset_source;
    }

    public void setReset_source(String reset_source) {
        this.reset_source = reset_source;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PasswordVerifyRequest(String resetSource, String password){
        this.reset_source = resetSource;
        this.password = password;
    }
}
