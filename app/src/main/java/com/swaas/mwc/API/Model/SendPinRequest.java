package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 6/29/2018.
 */

public class SendPinRequest {

    @SerializedName("user_pin_device_id")
    @Expose
    private String user_pin_device_id;

    public SendPinRequest(String user_pin_device_id){
        this.user_pin_device_id = user_pin_device_id;
    }

    public String getUser_pin_device_id() {
        return user_pin_device_id;
    }

    public void setUser_pin_device_id(String user_pin_device_id) {
        this.user_pin_device_id = user_pin_device_id;
    }
}
