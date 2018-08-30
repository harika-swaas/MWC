package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.FTLPINResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.VerifyFTLPINRequest;

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
