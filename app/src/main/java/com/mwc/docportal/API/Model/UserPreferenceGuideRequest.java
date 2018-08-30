package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by barath on 7/9/2018.
 */

public class UserPreferenceGuideRequest {

    @SerializedName("assistance_popup")
    @Expose
    private int assistance_popup;

    public UserPreferenceGuideRequest(int assistance_popup) {
        this.assistance_popup = assistance_popup;
    }

    public int getAssistance_popup() {
        return assistance_popup;
    }

    public void setAssistance_popup(int assistance_popup) {
        this.assistance_popup = assistance_popup;
    }
}
