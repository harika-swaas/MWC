package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.LoginResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by barath on 8/5/2018.
 */

public interface EndUserRenameService {
    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/end-user-rename-category")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<LoginResponse>> getRename(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);
}