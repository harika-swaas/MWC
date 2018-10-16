package com.mwc.docportal.API.Model.SharedFolderModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.List;


public class SharedDocumentResponseModel
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



    public static class Status {

        @SerializedName("code")
        @Expose
        public Object code;
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

    public static class Data {

        @SerializedName("categories")
        @Expose
        private List<Object> categories = null;


        @SerializedName("documents")
        @Expose
        private List<Object> documents = null;


        public List<Object> getDocuments() {
            return documents;
        }

        public void setDocuments(List<Object> documents) {
            this.documents = documents;
        }

        public List<Object> getCategories() {
            return categories;
        }

        public void setCategories(List<Object> categories) {
            this.categories = categories;
        }

    }


}
