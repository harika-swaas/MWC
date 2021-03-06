package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.VerifyFTLResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by harika on 26-06-2018.
 */

public interface SetTermsAcceptanceService {

    @POST("/set-terms-acceptance")
    @FormUrlEncoded
    Call<BaseApiResponse<VerifyFTLResponse>> getTermsAcceptance(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);
}
