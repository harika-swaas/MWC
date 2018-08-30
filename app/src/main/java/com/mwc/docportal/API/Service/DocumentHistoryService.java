package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.DocumentHistoryResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by barath on 7/20/2018.
 */

public interface DocumentHistoryService {

    @POST("/get-end-users-document-history")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<DocumentHistoryResponse>> getdocumenthist(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);
}
