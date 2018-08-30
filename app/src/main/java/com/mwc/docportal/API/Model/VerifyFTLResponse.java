package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 25-06-2018.
 */

public class VerifyFTLResponse {

    @SerializedName("check_mobile")
    @Expose
    private boolean check_mobile;

    public boolean isCheck_mobile() {
        return check_mobile;
    }

    public void setCheck_mobile(boolean check_mobile) {
        this.check_mobile = check_mobile;
    }
}
