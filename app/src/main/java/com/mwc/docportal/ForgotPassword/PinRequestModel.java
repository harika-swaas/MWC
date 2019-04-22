package com.mwc.docportal.ForgotPassword;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PinRequestModel
{
    @SerializedName("user_pin_device_id")
    @Expose
    private String user_pin_device_id;


    @SerializedName("reset_source")
    @Expose
    private int reset_source;

    @SerializedName("build_number")
    @Expose
    private String build_number;

    @SerializedName("override_pin_bypass")
    @Expose
    private boolean override_pin_bypass;

    public PinRequestModel(String user_pin_device_id, boolean isoverrideBypass, int reset_source, String build_number){
        this.user_pin_device_id = user_pin_device_id;
        this.override_pin_bypass = isoverrideBypass;
        this.reset_source = reset_source;
        this.build_number = build_number;
    }


    public String getUser_pin_device_id() {
        return user_pin_device_id;
    }

    public void setUser_pin_device_id(String user_pin_device_id) {
        this.user_pin_device_id = user_pin_device_id;
    }

    public int getReset_source() {
        return reset_source;
    }

    public void setReset_source(int reset_source) {
        this.reset_source = reset_source;
    }
}
