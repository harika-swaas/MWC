package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.VerifyPinRequest;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by barath on 6/29/2018.
 */

public interface VerifyPinService {

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/verify-pin")
    @FormUrlEncoded
    Call<BaseApiResponse<LoginResponse>> getVerifyPin(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);
}
