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
    private Object code;

    @SerializedName("message")
    @Expose
    private String message;


    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
