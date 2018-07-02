package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.FTLProcessResponse;
import com.swaas.mwc.API.Model.VerifyFTLResponse;
import com.swaas.mwc.Preference.PreferenceUtils;

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
