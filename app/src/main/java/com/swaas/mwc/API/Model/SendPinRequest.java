package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 6/29/2018.
 */

public class SendPinRequest {


    @SerializedName("user_pin_device_id")
    @Expose

    private int user_pin_device_id;

    public int getUser_pin_device_id() {
        return user_pin_device_id;
    }

    public void setUser_pin_device_id(int user_pin_device_id) {
        this.user_pin_device_id = user_pin_device_id;
    }

}
