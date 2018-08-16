package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.ApiResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by barath on 8/8/2018.
 */

public interface CopyDocumentService {

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/copy-end-users-document")
    @FormUrlEncoded
    Call<ApiResponse<LoginResponse>> getCopyDocuemnt(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);

}
