package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.DocumentHistoryResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by barath on 7/20/2018.
 */

public interface DocumentHistoryService {

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/get-end-users-document-history")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<DocumentHistoryResponse>> getdocumenthist(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);
}
