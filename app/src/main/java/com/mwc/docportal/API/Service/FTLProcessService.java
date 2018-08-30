package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.FTLProcessResponse;
import com.mwc.docportal.API.Model.VerifyFTLResponse;
import com.mwc.docportal.Preference.PreferenceUtils;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by harika on 26-06-2018.
 */

public interface FTLProcessService {

    @Headers({"content-type: application/x-www-form-urlencoded"})
    @POST("/ftl-process")
    Call<BaseApiResponse<FTLProcessResponse>> getFTLProcess(@Header("access-token") String accessToken);

    @POST("/add-ftl-details")
    @FormUrlEncoded
    Call<BaseApiResponse<VerifyFTLResponse>> getAddFTLDetails(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);
}
