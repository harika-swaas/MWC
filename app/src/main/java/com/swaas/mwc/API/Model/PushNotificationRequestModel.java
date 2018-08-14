package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PushNotificationRequestModel
{

    @SerializedName("device_id")
    @Expose
    private String device_id;

    @SerializedName("device")
    @Expose
    private String device;

    @SerializedName("register_type")
    @Expose
    private String register_type;

    public PushNotificationRequestModel(String device_id, String device, String register_type){

        this.device_id = device_id;
        this.device = device;
        this.register_type = register_type;

    }


    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getRegister_type() {
        return register_type;
    }

    public void setRegister_type(String register_type) {
        this.register_type = register_type;
    }
}
