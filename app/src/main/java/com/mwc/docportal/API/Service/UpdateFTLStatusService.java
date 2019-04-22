package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.UpdateFTLStatusRequest;
import com.mwc.docportal.API.Model.VerifyFTLResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by harika on 26-06-2018.
 */

public interface UpdateFTLStatusService {

    @POST("/update-ftl-status")
    @FormUrlEncoded
    Call<BaseApiResponse<VerifyFTLResponse>> getUpdateFTLStatus(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

    @POST("/reset-details")
    @FormUrlEncoded
    Call<BaseApiResponse<VerifyFTLResponse>> getUpdateNewPassword(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);
}
