package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.MaxDocumentUploadSizeResponse;

import retrofit.Call;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;


public interface GetMaxUploadService {


    @Headers({"content-type: application/x-www-form-urlencoded"})
    @POST("/get-server-config")
    Call<MaxDocumentUploadSizeResponse> getmaxsize(@Header("access-token") String accessToken);

}