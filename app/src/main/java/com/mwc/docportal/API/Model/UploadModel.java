package com.mwc.docportal.API.Model;

public class UploadModel
{
    String filePath;
    boolean success;
    boolean failure;
    boolean yetToStarted;
    boolean started;

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isFailure() {
        return failure;
    }

    public void setFailure(boolean failure) {
        this.failure = failure;
    }

    public boolean isYetToStarted() {
        return yetToStarted;
    }

    public void setYetToStarted(boolean yetToStarted) {
        this.yetToStarted = yetToStarted;
    }
}
