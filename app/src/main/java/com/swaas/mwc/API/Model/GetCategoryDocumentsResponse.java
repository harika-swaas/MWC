package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harika on 12-07-2018.
 */

public class GetCategoryDocumentsResponse {

    @SerializedName("object_id")
    @Expose
    private String object_id;

    @SerializedName("document_version_id")
    @Expose
    private String document_version_id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("created_date")
    @Expose
    private String created_date;

    @SerializedName("filetype")
    @Expose
    private String filetype;

    @SerializedName("filesize")
    @Expose
    private String filesize;

    @SerializedName("unix_date")
    @Expose
    private String unix_date;

    @SerializedName("type")
    @Expose
    private String type;

    public Permissions permission;

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
}
