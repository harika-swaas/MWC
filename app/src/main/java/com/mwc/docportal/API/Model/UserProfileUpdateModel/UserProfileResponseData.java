package com.mwc.docportal.API.Model.UserProfileUpdateModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileResponseData
{
    @SerializedName("status")
    @Expose
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public class Status {

        @SerializedName("code")
        @Expose
        private Boolean code;
        @SerializedName("message")
        @Expose
        private String message;

        public Boolean getCode() {
            return code;
        }

        public void setCode(Boolean code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
