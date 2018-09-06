package com.mwc.docportal.API.Model.GlobalSearchModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GlobalSearchResponseModel
{
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("data")
    @Expose
    private Data data;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public class Status {

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

    public class Data {

        @SerializedName("search_id")
        @Expose
        private String searchId;

        public String getSearchId() {
            return searchId;
        }

        public void setSearchId(String searchId) {
            this.searchId = searchId;
        }

    }
}
