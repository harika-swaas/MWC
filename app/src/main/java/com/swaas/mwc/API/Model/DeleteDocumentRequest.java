package com.swaas.mwc.API.Model;

import java.util.ArrayList;

/**
 * Created by barath on 8/20/2018.
 */

public class DeleteDocumentRequest {

    String doc_id;
    String[] doc_version_ids;
    String mode;

    public DeleteDocumentRequest(ArrayList<String> categoryids, String s,String doc_id) {
        this.doc_version_ids= categoryids.toArray(new String[0]);
        this.doc_id=doc_id;
        this.mode=s;
    }

    public DeleteDocumentRequest() {

    }


    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String[] getDoc_version_ids() {
        return doc_version_ids;
    }

    public void setDoc_version_ids(ArrayList<String> doc_version_ids) {
        this.doc_version_ids = doc_version_ids.toArray(new String[0]);
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public static class DeleteDocRequest{
        DeleteDocumentRequest[] docs;
        public DeleteDocRequest(DeleteDocumentRequest []doc) {
            this.docs= doc;

        }

        public DeleteDocumentRequest[] getDocs() {
            return docs;
        }

        public void setDocs(DeleteDocumentRequest[] docs) {
            this.docs = docs;
        }
    }
}

