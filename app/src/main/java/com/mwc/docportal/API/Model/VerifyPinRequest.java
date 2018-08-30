package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 6/29/2018.
 */

public class VerifyPinRequest {

    @SerializedName("pin")
    @Expose
    private int pin;

    public VerifyPinRequest(int pin){
        this.pin = pin;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }
}
