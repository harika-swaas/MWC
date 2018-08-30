package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 10-07-2018.
 */

public class GetAssistancePopupContentResponse {

    @SerializedName("assistance_popup_message")
    @Expose
    private String assistance_popup_message;

    @SerializedName("help_guide_url")
    @Expose
    private String help_guide_url;

    public String getAssistance_popup_message() {
        return assistance_popup_message;
    }

    public void setAssistance_popup_message(String assistance_popup_message) {
        this.assistance_popup_message = assistance_popup_message;
    }

    public String getHelp_guide_url() {
        return help_guide_url;
    }

    public void setHelp_guide_url(String help_guide_url) {
        this.help_guide_url = help_guide_url;
    }
}
