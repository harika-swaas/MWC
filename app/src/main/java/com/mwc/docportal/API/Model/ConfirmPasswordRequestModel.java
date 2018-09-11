package com.mwc.docportal.API.Model;

public class ConfirmPasswordRequestModel
{
    String password;

    public ConfirmPasswordRequestModel(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
