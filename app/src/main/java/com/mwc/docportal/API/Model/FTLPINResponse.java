package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 28-06-2018.
 */

public class FTLPINResponse {

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("userType")
    @Expose
    private String userType;

    @SerializedName("isSysAdmin")
    @Expose
    private boolean isSysAdmin;

    @SerializedName("userName")
    @Expose
    private String userName;

    @SerializedName("accessToken")
    @Expose
    private String accessToken;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("company_name")
    @Expose
    private String company_name;

    @SerializedName("request_pin")
    @Expose
    private boolean request_pin;

    @SerializedName("ftl_complete")
    @Expose
    private boolean ftl_complete;

    public NextStep nextStep;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isSysAdmin() {
        return isSysAdmin;
    }

    public void setSysAdmin(boolean sysAdmin) {
        isSysAdmin = sysAdmin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public boolean isRequest_pin() {
        return request_pin;
    }

    public void setRequest_pin(boolean request_pin) {
        this.request_pin = request_pin;
    }

    public boolean isFtl_complete() {
        return ftl_complete;
    }

    public void setFtl_complete(boolean ftl_complete) {
        this.ftl_complete = ftl_complete;
    }
}
