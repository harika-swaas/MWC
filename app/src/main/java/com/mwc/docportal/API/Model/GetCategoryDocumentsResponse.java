package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mwc.docportal.API.Model.SharedFolderModel.SharedDocumentResponseModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by harika on 12-07-2018.
 */

public class GetCategoryDocumentsResponse implements Serializable{

    private int Is_Loading;



    private String object_id;

    private String document_version_id;

    private String name;

    private String created_date;

    private String filetype;

    private String filesize;

    private String unix_date;

    private String type;
    private String shared_date;
    private String downloadUrl;
    int Is_Downloaded;
    long download_Id;
    String version_number;
    String version_count;

    public boolean isMultiselect;

    private String is_shared;
    private String category_id;

    private String file_path;
    private String doc_status;
    private String filename;
    private boolean isSelected = false;
    private String sharetype;


    public String getSharetype() {
        return sharetype;
    }

    public void setSharetype(String sharetype) {
        this.sharetype = sharetype;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @SerializedName("permission")
    @Expose
    private Permission permission;


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getDoc_status() {
        return doc_status;
    }

    public void setDoc_status(String doc_status) {
        this.doc_status = doc_status;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getIs_shared() {
        return is_shared;
    }

    public void setIs_shared(String is_shared) {
        this.is_shared = is_shared;
    }

    public boolean isMultiselect() {
        return isMultiselect;
    }

    public void setMultiselect(boolean multiselect) {
        isMultiselect = multiselect;
    }

    public String getVersion_count() {
        return version_count;
    }

    public void setVersion_count(String version_count) {
        this.version_count = version_count;
    }

    public String getVersion_number() {
        return version_number;
    }

    public void setVersion_number(String version_number) {
        this.version_number = version_number;
    }

    public long getDownload_Id() {
        return download_Id;
    }

    public void setDownload_Id(long download_Id) {
        this.download_Id = download_Id;
    }

    public int getIs_Downloaded() {
        return Is_Downloaded;
    }

    public void setIs_Downloaded(int is_Downloaded) {
        Is_Downloaded = is_Downloaded;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    private String parent_id;
    
    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
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

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getUnix_date() {
        return unix_date;
    }

    public void setUnix_date(String unix_date) {
        this.unix_date = unix_date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIs_Loading() {
        return Is_Loading;
    }

    public void setIs_Loading(int is_Loading) {
        Is_Loading = is_Loading;
    }

    public String getShared_date() {
        return shared_date;
    }

    public void setShared_date(String shared_date) {
        this.shared_date = shared_date;
    }


    public static class Permission implements Serializable{

        @SerializedName("pvt-view-document")
        @Expose
        private boolean canViewDocument = true;

        public boolean isCanViewDocument() {
            return canViewDocument;
        }

        public void setCanViewDocument(boolean canViewDocument) {
            this.canViewDocument = canViewDocument;
        }
    }

}
