package com.mwc.docportal.API.Service;


import com.mwc.docportal.API.Model.FileFormatResponse;

import retrofit.Call;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by barath on 8/25/2018.
 */

public interface GetFileFormatService {

    @Headers({"content-type: application/x-www-form-urlencoded"})
    @POST("/get-file-formats")
    Call<FileFormatResponse> getfileformat(@Header("access-token") String accessToken);

}

