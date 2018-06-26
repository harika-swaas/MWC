package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.VerifyFTLRequest;
import com.swaas.mwc.API.Model.VerifyFTLResponse;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by harika on 25-06-2018.
 */

public interface SendFTLPINService {

    @POST("/send-ftl-pin")
    @FormUrlEncoded
    Call<BaseApiResponse<VerifyFTLResponse>> getFTLPIN(@Body VerifyFTLRequest verifyFTLRequest);
}
