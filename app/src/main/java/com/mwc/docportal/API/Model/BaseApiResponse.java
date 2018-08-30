package com.mwc.docportal.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by harika on 21-06-2018.
 */

public class BaseApiResponse<T> {

    public BaseApiResponseStatus status;

   // public List<Categories> categories;
    //public List<Documents> documents;

    @SerializedName("data")
    @Expose
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    /*public List<Categories> getCategories() {
        return categories;
    }

    public void setCategories(List<Categories> categories) {
        this.categories = categories;
    }

    public List<Documents> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Documents> documents) {
        this.documents = documents;
    }*/

    public static class Documents {
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


    public static class Categories {
        String category_id;
        String parent_id;
        String type;
        String workspace_id;

        public String getCategory_id() {
            return category_id;
        }

        public void setCategory_id(String category_id) {
            this.category_id = category_id;
        }

        public String getParent_id() {
            return parent_id;
        }

        public void setParent_id(String parent_id) {
            this.parent_id = parent_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getWorkspace_id() {
            return workspace_id;
        }

        public void setWorkspace_id(String workspace_id) {
            this.workspace_id = workspace_id;
        }
    }



}
