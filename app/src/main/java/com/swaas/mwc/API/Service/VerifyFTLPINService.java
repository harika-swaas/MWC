package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.FTLPINResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.VerifyFTLPINRequest;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by harika on 25-06-2018.
 */

public interface VerifyFTLPINService {

    @POST("/verify-ftl-pin")
    @FormUrlEncoded
    Call<BaseApiResponse<FTLPINResponse>> getVerifyFTLPIN(@FieldMap Map<String,String> params);
}
