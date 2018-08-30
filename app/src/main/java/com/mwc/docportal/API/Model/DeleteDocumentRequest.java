package com.mwc.docportal.API.Model;

import java.util.ArrayList;

/**
 * Created by barath on 8/20/2018.
 */

public class DeleteDocumentRequest {

    String doc_id;
   ArrayList<String> doc_version_ids;
    String mode;

    public DeleteDocumentRequest(ArrayList<String> categoryids, String mode,String doc_id) {
        this.doc_version_ids = categoryids;
        this.doc_id = doc_id;
        this.mode = mode;
    }

    public DeleteDocumentRequest() {

    }


    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public ArrayList<String> getDoc_version_ids() {
        return doc_version_ids;
    }

    public void setDoc_version_ids(ArrayList<String> doc_version_ids) {
        this.doc_version_ids = doc_version_ids;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public static class DeleteDocRequest{

        ArrayList<DeleteDocumentRequest> docs;
        public DeleteDocRequest(ArrayList<DeleteDocumentRequest> doc) {
            this.docs= doc;

        }

        public ArrayList<DeleteDocumentRequest> getDocs() {
            return docs;
        }

        public void setDocs(ArrayList<DeleteDocumentRequest> docs) {
            this.docs = docs;
        }
    }
}

