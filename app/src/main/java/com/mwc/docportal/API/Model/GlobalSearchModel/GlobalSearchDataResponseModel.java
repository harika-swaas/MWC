package com.mwc.docportal.API.Model.GlobalSearchModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GlobalSearchDataResponseModel
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
    public class Result {

        @SerializedName("subject")
        @Expose
        private String subject;
        @SerializedName("document_id")
        @Expose
        private String documentId;
        @SerializedName("document_version_id")
        @Expose
        private String documentVersionId;
        @SerializedName("file_path")
        @Expose
        private String filePath;
        @SerializedName("uploaded_date")
        @Expose
        private String uploadedDate;
        @SerializedName("shared_date")
        @Expose
        private String sharedDate;
        @SerializedName("doc_category_id")
        @Expose
        private String docCategoryId;
        @SerializedName("doc_is_shared")
        @Expose
        private String docIsShared;
        @SerializedName("doc_is_having_notes")
        @Expose
        private String docIsHavingNotes;
        @SerializedName("doc_size")
        @Expose
        private String docSize;
        @SerializedName("doc_version_count")
        @Expose
        private String docVersionCount;
        @SerializedName("upload_source")
        @Expose
        private String uploadSource;
        @SerializedName("filetype")
        @Expose
        private String filetype;
        @SerializedName("doc_status")
        @Expose
        private String docStatus;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("permission")
        @Expose
        private Permission permission;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getDocumentId() {
            return documentId;
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }

        public String getDocumentVersionId() {
            return documentVersionId;
        }

        public void setDocumentVersionId(String documentVersionId) {
            this.documentVersionId = documentVersionId;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getUploadedDate() {
            return uploadedDate;
        }

        public void setUploadedDate(String uploadedDate) {
            this.uploadedDate = uploadedDate;
        }

        public String getSharedDate() {
            return sharedDate;
        }

        public void setSharedDate(String sharedDate) {
            this.sharedDate = sharedDate;
        }

        public String getDocCategoryId() {
            return docCategoryId;
        }

        public void setDocCategoryId(String docCategoryId) {
            this.docCategoryId = docCategoryId;
        }

        public String getDocIsShared() {
            return docIsShared;
        }

        public void setDocIsShared(String docIsShared) {
            this.docIsShared = docIsShared;
        }

        public String getDocIsHavingNotes() {
            return docIsHavingNotes;
        }

        public void setDocIsHavingNotes(String docIsHavingNotes) {
            this.docIsHavingNotes = docIsHavingNotes;
        }

        public String getDocSize() {
            return docSize;
        }

        public void setDocSize(String docSize) {
            this.docSize = docSize;
        }

        public String getDocVersionCount() {
            return docVersionCount;
        }

        public void setDocVersionCount(String docVersionCount) {
            this.docVersionCount = docVersionCount;
        }

        public String getUploadSource() {
            return uploadSource;
        }

        public void setUploadSource(String uploadSource) {
            this.uploadSource = uploadSource;
        }

        public String getFiletype() {
            return filetype;
        }

        public void setFiletype(String filetype) {
            this.filetype = filetype;
        }

        public String getDocStatus() {
            return docStatus;
        }

        public void setDocStatus(String docStatus) {
            this.docStatus = docStatus;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Permission getPermission() {
            return permission;
        }

        public void setPermission(Permission permission) {
            this.permission = permission;
        }

    }
    public class Status {

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


    public class DataResult {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("results")
        @Expose
        private List<Result> results = null;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

    }


    public class Data {

        @SerializedName("sequence_id")
        @Expose
        private String sequenceId;
        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("error_msg")
        @Expose
        private String errorMsg;
        @SerializedName("results_count")
        @Expose
        private String resultsCount;
        @SerializedName("data_results")
        @Expose
        private List<DataResult> dataResults = null;

        public String getSequenceId() {
            return sequenceId;
        }

        public void setSequenceId(String sequenceId) {
            this.sequenceId = sequenceId;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String getResultsCount() {
            return resultsCount;
        }

        public void setResultsCount(String resultsCount) {
            this.resultsCount = resultsCount;
        }

        public List<DataResult> getDataResults() {
            return dataResults;
        }

        public void setDataResults(List<DataResult> dataResults) {
            this.dataResults = dataResults;
        }

    }

    public class Permission {


    }


}
