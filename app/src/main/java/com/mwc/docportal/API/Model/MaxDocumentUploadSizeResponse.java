package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MaxDocumentUploadSizeResponse
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

    public class Data {

        @SerializedName("max_document_upload_size")
        @Expose
        private String maxDocumentUploadSize;
        @SerializedName("docusign_max_upload_size")
        @Expose
        private Integer docusignMaxUploadSize;

        public String getMaxDocumentUploadSize() {
            return maxDocumentUploadSize;
        }

        public void setMaxDocumentUploadSize(String maxDocumentUploadSize) {
            this.maxDocumentUploadSize = maxDocumentUploadSize;
        }

        public Integer getDocusignMaxUploadSize() {
            return docusignMaxUploadSize;
        }

        public void setDocusignMaxUploadSize(Integer docusignMaxUploadSize) {
            this.docusignMaxUploadSize = docusignMaxUploadSize;
        }

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
