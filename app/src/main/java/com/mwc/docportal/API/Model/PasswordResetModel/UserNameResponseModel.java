package com.mwc.docportal.API.Model.PasswordResetModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserNameResponseModel
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

        @SerializedName("userId")
        @Expose
        private String userId;
        @SerializedName("userType")
        @Expose
        private String userType;
        @SerializedName("isSysAdmin")
        @Expose
        private Boolean isSysAdmin;
        @SerializedName("userName")
        @Expose
        private String userName;
        @SerializedName("accessToken")
        @Expose
        private String accessToken;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("company_name")
        @Expose
        private String companyName;
        @SerializedName("is_migrated")
        @Expose
        private String isMigrated;
        @SerializedName("is_first_user")
        @Expose
        private Integer isFirstUser;
        @SerializedName("terms_accept")
        @Expose
        private String termsAccept;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public Boolean getIsSysAdmin() {
            return isSysAdmin;
        }

        public void setIsSysAdmin(Boolean isSysAdmin) {
            this.isSysAdmin = isSysAdmin;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getIsMigrated() {
            return isMigrated;
        }

        public void setIsMigrated(String isMigrated) {
            this.isMigrated = isMigrated;
        }

        public Integer getIsFirstUser() {
            return isFirstUser;
        }

        public void setIsFirstUser(Integer isFirstUser) {
            this.isFirstUser = isFirstUser;
        }

        public String getTermsAccept() {
            return termsAccept;
        }

        public void setTermsAccept(String termsAccept) {
            this.termsAccept = termsAccept;
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
