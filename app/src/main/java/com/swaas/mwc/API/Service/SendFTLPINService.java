package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.VerifyFTLRequest;
import com.swaas.mwc.API.Model.VerifyFTLResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by harika on 25-06-2018.
 */

public interface SendFTLPINService {

    @POST("/send-ftl-pin")
    @FormUrlEncoded
    Call<BaseApiResponse<VerifyFTLResponse>> getFTLPIN(@FieldMap Map<String,String> params);
}
