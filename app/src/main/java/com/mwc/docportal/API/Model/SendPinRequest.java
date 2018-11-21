package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 6/29/2018.
 */

public class SendPinRequest {

    @SerializedName("user_pin_device_id")
    @Expose
    private String user_pin_device_id;

    @SerializedName("override_pin_bypass")
    @Expose
    private boolean override_pin_bypass;


    public boolean isOverride_pin_bypass() {
        return override_pin_bypass;
    }

    public void setOverride_pin_bypass(boolean override_pin_bypass) {
        this.override_pin_bypass = override_pin_bypass;
    }

    public SendPinRequest(String user_pin_device_id, boolean isOverride_pin_bypass){
        this.user_pin_device_id = user_pin_device_id;
        this.override_pin_bypass = isOverride_pin_bypass;
    }

    public String getUser_pin_device_id() {
        return user_pin_device_id;
    }

    public void setUser_pin_device_id(String user_pin_device_id) {
        this.user_pin_device_id = user_pin_device_id;
    }
}
