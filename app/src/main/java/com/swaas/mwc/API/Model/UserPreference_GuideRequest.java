package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 7/9/2018.
 */

public class UserPreference_GuideRequest {


    @SerializedName("assistance_popup")
    @Expose

    private int assistance_popup;

    public UserPreference_GuideRequest(int i) {
        i=1;
    }

    public int getAssistance_popup() {
        return assistance_popup;
    }

    public void setAssistance_popup(int assistance_popup) {
        this.assistance_popup = assistance_popup;
    }
}
