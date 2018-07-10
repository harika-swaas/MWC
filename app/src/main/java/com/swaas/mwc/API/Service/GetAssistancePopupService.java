package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.GetAssistancePopupContentResponse;

import retrofit.Call;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by harika on 10-07-2018.
 */

public interface GetAssistancePopupService {

    @Headers({"content-type: application/x-www-form-urlencoded"})
    @POST("/get-assistance-popup-content")
    Call<BaseApiResponse<GetAssistancePopupContentResponse>> getAssistancePopupContent(@Header("access-token") String accessToken);
}
