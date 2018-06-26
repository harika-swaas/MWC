package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.FTLProcessResponse;

import retrofit.Call;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by harika on 26-06-2018.
 */

public interface FTLProcessService {

    @POST("/ftl-process")
    @FormUrlEncoded
    Call<BaseApiResponse<FTLProcessResponse>> getFTLProcess();
}
