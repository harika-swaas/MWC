package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class UserProfileModel
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


    public static class Data {

        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("customer_id")
        @Expose
        private String customerId;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("firstname")
        @Expose
        private String firstname;
        @SerializedName("surname")
        @Expose
        private String surname;
        @SerializedName("salutation")
        @Expose
        private String salutation;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("workphone")
        @Expose
        private String workphone;
        @SerializedName("workphone_extension")
        @Expose
        private String workphoneExtension;
        @SerializedName("address_line1")
        @Expose
        private String addressLine1;
        @SerializedName("address_line2")
        @Expose
        private String addressLine2;
        @SerializedName("town")
        @Expose
        private String town;
        @SerializedName("postcode")
        @Expose
        private String postcode;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("mobilephone")
        @Expose
        private String mobilephone;
        @SerializedName("reference_no")
        @Expose
        private String referenceNo;
        @SerializedName("line_manager_id")
        @Expose
        private String lineManagerId;
        @SerializedName("linemanager")
        @Expose
        private String linemanager;
        @SerializedName("office_location_id")
        @Expose
        private String officeLocationId;
        @SerializedName("office_location_name")
        @Expose
        private String officeLocationName;
        @SerializedName("manager")
        @Expose
        private String manager;
        @SerializedName("role_id")
        @Expose
        private String roleId;
        @SerializedName("is_sysadmin")
        @Expose
        private String isSysadmin;
        @SerializedName("executor_userid")
        @Expose
        private String executorUserid;
        @SerializedName("usertype_id")
        @Expose
        private String usertypeId;
        @SerializedName("department_id")
        @Expose
        private String departmentId;
        @SerializedName("department")
        @Expose
        private String department;
        @SerializedName("username")
        @Expose
        private String username;
        @SerializedName("ftl_status")
        @Expose
        private String ftlStatus;
        @SerializedName("parent_user_id")
        @Expose
        private String parentUserId;
        @SerializedName("pin_bypass")
        @Expose
        private String pinBypass;
        @SerializedName("is_suspended")
        @Expose
        private String isSuspended;
        @SerializedName("mwc_email")
        @Expose
        private String mwcEmail;
        @SerializedName("is_migrated")
        @Expose
        private String isMigrated;
        
        @SerializedName("systemadmin")
        @Expose
        private String systemadmin;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getSalutation() {
            return salutation;
        }

        public void setSalutation(String salutation) {
            this.salutation = salutation;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getWorkphone() {
            return workphone;
        }

        public void setWorkphone(String workphone) {
            this.workphone = workphone;
        }

        public String getWorkphoneExtension() {
            return workphoneExtension;
        }

        public void setWorkphoneExtension(String workphoneExtension) {
            this.workphoneExtension = workphoneExtension;
        }

        public String getAddressLine1() {
            return addressLine1;
        }

        public void setAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
        }

        public String getAddressLine2() {
            return addressLine2;
        }

        public void setAddressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
        }

        public String getTown() {
            return town;
        }

        public void setTown(String town) {
            this.town = town;
        }

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            this.postcode = postcode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getMobilephone() {
            return mobilephone;
        }

        public void setMobilephone(String mobilephone) {
            this.mobilephone = mobilephone;
        }

        public String getReferenceNo() {
            return referenceNo;
        }

        public void setReferenceNo(String referenceNo) {
            this.referenceNo = referenceNo;
        }

        public String getLineManagerId() {
            return lineManagerId;
        }

        public void setLineManagerId(String lineManagerId) {
            this.lineManagerId = lineManagerId;
        }

        public String getLinemanager() {
            return linemanager;
        }

        public void setLinemanager(String linemanager) {
            this.linemanager = linemanager;
        }

        public String getOfficeLocationId() {
            return officeLocationId;
        }

        public void setOfficeLocationId(String officeLocationId) {
            this.officeLocationId = officeLocationId;
        }

        public String getOfficeLocationName() {
            return officeLocationName;
        }

        public void setOfficeLocationName(String officeLocationName) {
            this.officeLocationName = officeLocationName;
        }

        public String getManager() {
            return manager;
        }

        public void setManager(String manager) {
            this.manager = manager;
        }

        public String getRoleId() {
            return roleId;
        }

        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }

        public String getIsSysadmin() {
            return isSysadmin;
        }

        public void setIsSysadmin(String isSysadmin) {
            this.isSysadmin = isSysadmin;
        }

        public String getExecutorUserid() {
            return executorUserid;
        }

        public void setExecutorUserid(String executorUserid) {
            this.executorUserid = executorUserid;
        }

        public String getUsertypeId() {
            return usertypeId;
        }

        public void setUsertypeId(String usertypeId) {
            this.usertypeId = usertypeId;
        }

        public String getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(String departmentId) {
            this.departmentId = departmentId;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFtlStatus() {
            return ftlStatus;
        }

        public void setFtlStatus(String ftlStatus) {
            this.ftlStatus = ftlStatus;
        }

        public String getParentUserId() {
            return parentUserId;
        }

        public void setParentUserId(String parentUserId) {
            this.parentUserId = parentUserId;
        }

        public String getPinBypass() {
            return pinBypass;
        }

        public void setPinBypass(String pinBypass) {
            this.pinBypass = pinBypass;
        }

        public String getIsSuspended() {
            return isSuspended;
        }

        public void setIsSuspended(String isSuspended) {
            this.isSuspended = isSuspended;
        }

        public String getMwcEmail() {
            return mwcEmail;
        }

        public void setMwcEmail(String mwcEmail) {
            this.mwcEmail = mwcEmail;
        }

        public String getIsMigrated() {
            return isMigrated;
        }

        public void setIsMigrated(String isMigrated) {
            this.isMigrated = isMigrated;
        }

       

        public String getSystemadmin() {
            return systemadmin;
        }

        public void setSystemadmin(String systemadmin) {
            this.systemadmin = systemadmin;
        }

    }


}
