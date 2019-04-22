package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.VerifyPinRequest;

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

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/verify-pin")
    @FormUrlEncoded
    Call<BaseApiResponse<LoginResponse>> getForgetPasswordVerifyPin(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);
}
