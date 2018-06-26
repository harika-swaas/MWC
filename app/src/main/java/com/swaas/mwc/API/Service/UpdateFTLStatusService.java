package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.UpdateFTLStatusResponse;
import com.swaas.mwc.API.Model.VerifyFTLResponse;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by harika on 26-06-2018.
 */

public interface UpdateFTLStatusService {

    @POST("/update-ftl-status")
    @FormUrlEncoded
    Call<BaseApiResponse<VerifyFTLResponse>> getUpdateFTLStatus(@Body UpdateFTLStatusResponse updateFTLStatusResponse);
}
