package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 21-06-2018.
 */

public class LoginResponse {

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

    @SerializedName("text_background_color")
    @Expose
    private String text_background_color;

    @SerializedName("text_foreground_color")
    @Expose
    private String text_foreground_color;

    @SerializedName("is_migrated")
    @Expose
    private String is_migrated;

    @SerializedName("app_background_color")
    @Expose
    private String app_background_color;

    @SerializedName("request_pin")
    @Expose
    private boolean request_pin;

    @SerializedName("terms_accept")
    @Expose
    private String terms_accept;

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

    public String getText_background_color() {
        return text_background_color;
    }

    public void setText_background_color(String text_background_color) {
        this.text_background_color = text_background_color;
    }

    public String getText_foreground_color() {
        return text_foreground_color;
    }

    public void setText_foreground_color(String text_foreground_color) {
        this.text_foreground_color = text_foreground_color;
    }

    public String getIs_migrated() {
        return is_migrated;
    }

    public void setIs_migrated(String is_migrated) {
        this.is_migrated = is_migrated;
    }

    public String getApp_background_color() {
        return app_background_color;
    }

    public void setApp_background_color(String app_background_color) {
        this.app_background_color = app_background_color;
    }

    public boolean isRequest_pin() {
        return request_pin;
    }

    public void setRequest_pin(boolean request_pin) {
        this.request_pin = request_pin;
    }

    public NextStep getNextStep() {
        return nextStep;
    }

    public void setNextStep(NextStep nextStep) {
        this.nextStep = nextStep;
    }

    public String getTerms_accept() {
        return terms_accept;
    }

    public void setTerms_accept(String terms_accept) {
        this.terms_accept = terms_accept;
    }
}
