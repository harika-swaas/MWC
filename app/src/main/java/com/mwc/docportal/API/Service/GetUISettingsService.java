package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.GetUISettingsResponse;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by harika on 26-06-2018.
 */

public interface GetUISettingsService {

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/get-ui-settings")
    Call<BaseApiResponse<GetUISettingsResponse>> getUISettings(@Header("access-token") String accessToken);
}
