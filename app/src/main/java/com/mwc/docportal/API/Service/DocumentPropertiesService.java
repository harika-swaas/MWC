package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.DocumentPropertiesResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by barath on 7/19/2018.
 */

public interface DocumentPropertiesService {

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/get-document-properties")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<DocumentPropertiesResponse>> getdocumentprop(@FieldMap Map<String,
            String> params, @Header("access-token") String accessToken);
}
