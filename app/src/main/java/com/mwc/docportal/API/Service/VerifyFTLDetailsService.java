package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.VerifyFTLRequest;
import com.mwc.docportal.API.Model.VerifyFTLRequestWithEMail;
import com.mwc.docportal.API.Model.VerifyFTLResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by harika on 25-06-2018.
 */

public interface VerifyFTLDetailsService {

    /*@Headers({"content-type: application/x-www-form-urlencoded"})
    @POST("/verify-ftl-details")
    Call<BaseApiResponse<VerifyFTLResponse>> getVerifyFTLDetailsWithEmail(@Body VerifyFTLRequestWithEMail verifyFTLRequest);

    @Headers({"content-type: application/x-www-form-urlencoded"})
    @POST("/verify-ftl-details")
    Call<BaseApiResponse<VerifyFTLResponse>> getVerifyFTLDetails(@Body VerifyFTLRequest verifyFTLRequest);*/

    @POST("/verify-ftl-details")
    @FormUrlEncoded
    Call<BaseApiResponse<VerifyFTLResponse>> getVerifyFTLDetailsWithEmail(@FieldMap Map<String,String> params);

    @POST("/verify-ftl-details")
    @FormUrlEncoded
    Call<BaseApiResponse<VerifyFTLResponse>> getVerifyFTLDetails(@FieldMap Map<String,String> params);
}
