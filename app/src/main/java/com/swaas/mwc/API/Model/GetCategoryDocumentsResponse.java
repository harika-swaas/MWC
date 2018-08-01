package com.swaas.mwc.API.Model;

import java.io.Serializable;

/**
 * Created by harika on 12-07-2018.
 */

public class GetCategoryDocumentsResponse implements Serializable{

    private String object_id;

    private String document_version_id;

    private String name;

    private String created_date;

    private String filetype;

    private String filesize;

    private String unix_date;

    private String type;
    
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
