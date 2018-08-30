package com.mwc.docportal.API.Model;

public class OfflineFiles
{
    String offline_file_id;
    String documentId;
    String documentName;
    String documentVersionId;
    String downloadDate;
    String filename;
    String filePath;
    String fileSize;
    String versionNumber;
    long download_Id;
    int Is_Downloaded;
    String DA_Offline_URL;
    String filetype;
    String source;




    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public int getIs_Downloaded() {
        return Is_Downloaded;
    }

    public void setIs_Downloaded(int is_Downloaded) {
        Is_Downloaded = is_Downloaded;
    }

    public long getDownload_Id() {
        return download_Id;
    }

    public void setDownload_Id(long download_Id) {
        this.download_Id = download_Id;
    }

    public String getOffline_file_id() {
        return offline_file_id;
    }

    public void setOffline_file_id(String offline_file_id) {
        this.offline_file_id = offline_file_id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentVersionId() {
        return documentVersionId;
    }

    public void setDocumentVersionId(String documentVersionId) {
        this.documentVersionId = documentVersionId;
    }

    public String getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(String downloadDate) {
        this.downloadDate = downloadDate;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }


    public String getDA_Offline_URL() {
        return DA_Offline_URL;
    }

    public void setDA_Offline_URL(String DA_Offline_URL) {
        this.DA_Offline_URL = DA_Offline_URL;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
