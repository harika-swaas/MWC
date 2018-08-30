package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 21-06-2018.
 */

public class LoginRequest {

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("override_pin_bypass")
    @Expose
    private boolean override_pin_bypass;


    public LoginRequest(String UserName, String Password,boolean overide) {
        this.username = UserName;
        this.password = Password;
        this.override_pin_bypass=overide;
    }

   /* public LoginRequest(String UserName, String Password) {
        this.username = UserName;
        this.password = Password;
    }*/


    public boolean isOverride_pin_bypass() {
        return override_pin_bypass;
    }

    public void setOverride_pin_bypass(boolean override_pin_bypass) {
        this.override_pin_bypass = override_pin_bypass;
    }




    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        password = password;
    }
}
