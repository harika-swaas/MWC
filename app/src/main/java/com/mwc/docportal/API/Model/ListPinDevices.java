package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 6/28/2018.
 */

public class ListPinDevices {

    @SerializedName("user_pin_device_id")
    @Expose
    public String user_pin_device_id;

    @SerializedName("device_name")
    @Expose
    private String device_name;

    @SerializedName("detail")
    @Expose
    private String detail;

    @SerializedName("is_default")
    @Expose
    private boolean is_default;

    @SerializedName("device_type")
    @Expose
    private String device_type;

    public String getUser_pin_device_id() {
        return user_pin_device_id;
    }

    public void setUser_pin_device_id(String user_pin_device_id) {
        this.user_pin_device_id = user_pin_device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }
}
