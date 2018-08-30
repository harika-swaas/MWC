package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 26-06-2018.
 */

public class UIObjects {

    @SerializedName("share_button_text")
    @Expose
    private String share_button_text;

    public String getShare_button_text() {
        return share_button_text;
    }

    public void setShare_button_text(String share_button_text) {
        this.share_button_text = share_button_text;
    }
}
