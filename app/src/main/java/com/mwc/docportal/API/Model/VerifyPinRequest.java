package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 6/29/2018.
 */

public class VerifyPinRequest {

    @SerializedName("pin")
    @Expose
    private long pin;

    public VerifyPinRequest(long pin){
        this.pin = pin;
    }

    public long getPin() {
        return pin;
    }

    public void setPin(long pin) {
        this.pin = pin;
    }
}
