package com.swaas.mwc.API.Model;

/**
 * Created by barath on 8/8/2018.
 */

public class MoveCategoryRequest {
    String category_id;

    String document_obj[];

    String category_obj[];

    String is_bu;

    public MoveCategoryRequest(String[] document_ids, String categoryId, String[] categoryobj,String bu) {
        this.category_id=categoryId;
        this.document_obj=document_ids;
        this.category_obj=categoryobj;
        this.is_bu=bu;
    }






    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String[] getDocument_obj() {
        return document_obj;
    }

    public void setDocument_obj(String[] document_obj) {
        this.document_obj = document_obj;
    }

    public String[] getCategory_obj() {
        return category_obj;
    }

    public void setCategory_obj(String[] category_obj) {
        this.category_obj = category_obj;
    }

    public String getIs_bu() {
        return is_bu;
    }

    public void setIs_bu(String is_bu) {
        this.is_bu = is_bu;
    }



}
