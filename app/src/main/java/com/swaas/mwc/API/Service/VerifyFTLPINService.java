package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.VerifyFTLPINRequest;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by harika on 25-06-2018.
 */

public interface VerifyFTLPINService {

    @POST("/verify-ftl-pin")
    @FormUrlEncoded
    Call<BaseApiResponse<LoginResponse>> getVerifyFTLPIN(@Body VerifyFTLPINRequest verifyFTLPINRequest);
}
