package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by harika on 26-06-2018.
 */

public class BaseApiResponseStatus {

    @SerializedName("code")
    @Expose
    private boolean code;

    @SerializedName("message")
    @Expose
    private String message;

    public boolean isCode() {
        return code;
    }

    public void setCode(boolean code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
