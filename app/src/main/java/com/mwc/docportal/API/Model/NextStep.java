package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 22-06-2018.
 */

public class NextStep {

    @SerializedName("pin_authentication_required")
    @Expose
    private boolean pin_authentication_required;

    @SerializedName("ftl_required")
    @Expose
    private boolean ftl_required;

    public boolean isPin_authentication_required() {
        return pin_authentication_required;
    }

    public void setPin_authentication_required(boolean pin_authentication_required) {
        this.pin_authentication_required = pin_authentication_required;
    }

    public boolean isFtl_required() {
        return ftl_required;
    }

    public void setFtl_required(boolean ftl_required) {
        this.ftl_required = ftl_required;
    }
}
