package com.swaas.mwc.API.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by barath on 6/28/2018.
 */

public class ListPinDevicesResponse<T> {

    public BaseApiResponseStatus status;

    @SerializedName("data")
    @Expose
    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
