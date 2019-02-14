package com.mwc.docportal.API.Model;

import java.io.Serializable;

public class UploadModel implements Serializable
{
    String filePath;
    boolean success;
    boolean failure;
    boolean yetToStart;
    boolean inProgress;
    String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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


    public boolean isYetToStart() {
        return yetToStart;
    }

    public void setYetToStart(boolean yetToStart) {
        this.yetToStart = yetToStart;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }
}
