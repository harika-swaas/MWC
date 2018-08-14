package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.ApiResponse;
import com.swaas.mwc.API.Model.DownloadDocumentResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by barath on 7/24/2018.
 */

public interface DownloadDocumentService{
        @POST("/download-end-users-documents")
        @FormUrlEncoded
        Call<ApiResponse<DownloadDocumentResponse>> download(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);
}
