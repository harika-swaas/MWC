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
 * Created by barath on 8/6/2018.
 */

public interface MoveDocumentService {

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/move-document")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<LoginResponse>> getMoveDocuemnt(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);

}
