package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class APIResponseModel {


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

        @SerializedName("documents")
        @Expose
        private List<Document> documents = null;
        @SerializedName("categories")
        @Expose
        private List<Category> categories = null;

        public List<Document> getDocuments() {
            return documents;
        }

        public void setDocuments(List<Document> documents) {
            this.documents = documents;
        }

        public List<Category> getCategories() {
            return categories;
        }

        public void setCategories(List<Category> categories) {
            this.categories = categories;
        }

    }
    public static class Document{
        String document_id;
        String document_version_id;
        String name;
        String type;
        String document_share_id;
        String filesize;
        String sharetype;
        String filetype;
        String shared_date;

        public String getDocument_id() {
            return document_id;
        }

        public void setDocument_id(String document_id) {
            this.document_id = document_id;
        }

        public String getDocument_version_id() {
            return document_version_id;
        }

        public void setDocument_version_id(String document_version_id) {
            this.document_version_id = document_version_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDocument_share_id() {
            return document_share_id;
        }

        public void setDocument_share_id(String document_share_id) {
            this.document_share_id = document_share_id;
        }

        public String getFilesize() {
            return filesize;
        }

        public void setFilesize(String filesize) {
            this.filesize = filesize;
        }

        public String getSharetype() {
            return sharetype;
        }

        public void setSharetype(String sharetype) {
            this.sharetype = sharetype;
        }

        public String getFiletype() {
            return filetype;
        }

        public void setFiletype(String filetype) {
            this.filetype = filetype;
        }

        public String getShared_date() {
            return shared_date;
        }

        public void setShared_date(String shared_date) {
            this.shared_date = shared_date;
        }
    }

    public static class Category {

        @SerializedName("category_id")
        @Expose
        private String categoryId;
        @SerializedName("migr_category_id")
        @Expose
        private String migrCategoryId;
        @SerializedName("parent_id")
        @Expose
        private String parentId;
        @SerializedName("migr_parent_id")
        @Expose
        private String migrParentId;
        @SerializedName("category_type")
        @Expose
        private String categoryType;
        @SerializedName("owner")
        @Expose
        private String owner;
        @SerializedName("owner_type")
        @Expose
        private String ownerType;
        @SerializedName("category_name")
        @Expose
        private String categoryName;
        @SerializedName("is_advanced_security")
        @Expose
        private String isAdvancedSecurity;
        @SerializedName("advanced_type")
        @Expose
        private String advancedType;
        @SerializedName("advance_security_initiatedby")
        @Expose
        private String advanceSecurityInitiatedby;
        @SerializedName("allow_eu_shared")
        @Expose
        private String allowEuShared;
        @SerializedName("is_active")
        @Expose
        private String isActive;
        @SerializedName("created_date")
        @Expose
        private String createdDate;
        @SerializedName("modified_date")
        @Expose
        private String modifiedDate;
        @SerializedName("createdby")
        @Expose
        private String createdby;
        @SerializedName("modifiedby")
        @Expose
        private String modifiedby;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("workspace_id")
        @Expose
        private String workspaceId;
        @SerializedName("subcategory_count")
        @Expose
        private String subcategoryCount;
        @SerializedName("full_path")
        @Expose
        private String fullPath;
        @SerializedName("type")
        @Expose
        private String type;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getMigrCategoryId() {
            return migrCategoryId;
        }

        public void setMigrCategoryId(String migrCategoryId) {
            this.migrCategoryId = migrCategoryId;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getMigrParentId() {
            return migrParentId;
        }

        public void setMigrParentId(String migrParentId) {
            this.migrParentId = migrParentId;
        }

        public String getCategoryType() {
            return categoryType;
        }

        public void setCategoryType(String categoryType) {
            this.categoryType = categoryType;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getOwnerType() {
            return ownerType;
        }

        public void setOwnerType(String ownerType) {
            this.ownerType = ownerType;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getIsAdvancedSecurity() {
            return isAdvancedSecurity;
        }

        public void setIsAdvancedSecurity(String isAdvancedSecurity) {
            this.isAdvancedSecurity = isAdvancedSecurity;
        }

        public String getAdvancedType() {
            return advancedType;
        }

        public void setAdvancedType(String advancedType) {
            this.advancedType = advancedType;
        }

        public String getAdvanceSecurityInitiatedby() {
            return advanceSecurityInitiatedby;
        }

        public void setAdvanceSecurityInitiatedby(String advanceSecurityInitiatedby) {
            this.advanceSecurityInitiatedby = advanceSecurityInitiatedby;
        }

        public String getAllowEuShared() {
            return allowEuShared;
        }

        public void setAllowEuShared(String allowEuShared) {
            this.allowEuShared = allowEuShared;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getModifiedDate() {
            return modifiedDate;
        }

        public void setModifiedDate(String modifiedDate) {
            this.modifiedDate = modifiedDate;
        }

        public String getCreatedby() {
            return createdby;
        }

        public void setCreatedby(String createdby) {
            this.createdby = createdby;
        }

        public String getModifiedby() {
            return modifiedby;
        }

        public void setModifiedby(String modifiedby) {
            this.modifiedby = modifiedby;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getWorkspaceId() {
            return workspaceId;
        }

        public void setWorkspaceId(String workspaceId) {
            this.workspaceId = workspaceId;
        }

        public String getSubcategoryCount() {
            return subcategoryCount;
        }

        public void setSubcategoryCount(String subcategoryCount) {
            this.subcategoryCount = subcategoryCount;
        }

        public String getFullPath() {
            return fullPath;
        }

        public void setFullPath(String fullPath) {
            this.fullPath = fullPath;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }
}
