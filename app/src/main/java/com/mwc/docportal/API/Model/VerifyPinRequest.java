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

    @SerializedName("reset_source")
    @Expose
    private long reset_source;

    public long getReset_source() {
        return reset_source;
    }

    public void setReset_source(long reset_source) {
        this.reset_source = reset_source;
    }

    public VerifyPinRequest(long pin){
        this.pin = pin;
    }

    public VerifyPinRequest(long pin, int resetResource){
        this.pin = pin;
        this.reset_source = resetResource;
    }


    public long getPin() {
        return pin;
    }

    public void setPin(long pin) {
        this.pin = pin;
    }
}
