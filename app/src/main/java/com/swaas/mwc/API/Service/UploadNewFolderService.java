package com.swaas.mwc.API.Service;

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
 * Created by barath on 8/9/2018.
 */

public interface UploadNewFolderService {

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/end-user-insert-category")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<LoginResponse>> getNewFolder(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);
}
