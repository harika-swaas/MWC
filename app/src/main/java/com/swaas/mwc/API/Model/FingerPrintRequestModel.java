package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FingerPrintRequestModel
{
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("device")
    @Expose
    private String device;

    @SerializedName("selection")
    @Expose
    private String selection;

    public FingerPrintRequestModel(String type, String device, String selection){

        this.type = type;
        this.device = device;
        this.selection = selection;

    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }
}
