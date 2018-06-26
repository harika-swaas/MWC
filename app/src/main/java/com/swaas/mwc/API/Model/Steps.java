package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 26-06-2018.
 */

public class Steps {

    @SerializedName("user_details_required")
    @Expose
    private boolean user_details_required;

    @SerializedName("pin_device_details_required")
    @Expose
    private boolean pin_device_details_required;

    @SerializedName("memorable_data_required")
    @Expose
    private boolean memorable_data_required;

    @SerializedName("emailmwc_required")
    @Expose
    private boolean emailmwc_required;

    public boolean isUser_details_required() {
        return user_details_required;
    }

    public void setUser_details_required(boolean user_details_required) {
        this.user_details_required = user_details_required;
    }

    public boolean isPin_device_details_required() {
        return pin_device_details_required;
    }

    public void setPin_device_details_required(boolean pin_device_details_required) {
        this.pin_device_details_required = pin_device_details_required;
    }

    public boolean isMemorable_data_required() {
        return memorable_data_required;
    }

    public void setMemorable_data_required(boolean memorable_data_required) {
        this.memorable_data_required = memorable_data_required;
    }

    public boolean isEmailmwc_required() {
        return emailmwc_required;
    }

    public void setEmailmwc_required(boolean emailmwc_required) {
        this.emailmwc_required = emailmwc_required;
    }
}
