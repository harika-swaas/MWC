package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by harika on 25-06-2018.
 */

public class VerifyFTLRequest {

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("build_number")
    @Expose
    private String build_number;

    public VerifyFTLRequest(String email, String mobile, String build_Number){
        this.email = email;
        this.mobile = mobile;
        this.build_number = build_Number;
    }

    public VerifyFTLRequest(String email, String mobile){
        this.email = email;
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
