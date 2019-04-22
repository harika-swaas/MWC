package com.mwc.docportal.API.Model.PasswordResetModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PinDeviceRequestModel {
    @SerializedName("device_for_reset")
    @Expose
    private int device_for_reset;


    public PinDeviceRequestModel(int deviceReset){
        this.device_for_reset = deviceReset;
    }

    public int getDevice_for_reset() {
        return device_for_reset;
    }

    public void setDevice_for_reset(int device_for_reset) {
        this.device_for_reset = device_for_reset;
    }
}
