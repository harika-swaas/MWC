package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by harika on 22-06-2018.
 */

public class Status {

    @SerializedName("code")
    @Expose
    private boolean code;

    @SerializedName("message")
    @Expose
    private Object message;

    public boolean getCode() {
        return code;
    }

    public void setCode(boolean code) {
        this.code = code;
    }

    /*public boolean isCode() {
        return code;
    }

    public void setCode(boolean code) {
        this.code = code;
    }*/

    /*public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }*/

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
