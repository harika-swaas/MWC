package com.mwc.docportal.API.Model.SharedFolderModel;

import java.util.List;

public class SharedDocumentReadStatusResponse
{
    private Status status;
    private List<Object> data = null;
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public class Status {


        private Boolean code;

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
