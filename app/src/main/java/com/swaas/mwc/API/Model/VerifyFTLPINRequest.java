package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 25-06-2018.
 */

public class VerifyFTLPINRequest {

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("mobile")
    @Expose
    private int mobile;

    @SerializedName("pin")
    @Expose
    private int pin;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }
}
