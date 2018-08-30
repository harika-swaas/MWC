package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.GetUserPreferencesResponse;

import retrofit.Call;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by harika on 09-07-2018.
 */

public interface GetUserPreferencesService {

    @Headers({"content-type: application/x-www-form-urlencoded"})
    @POST("/get-user-preferences")
    Call<BaseApiResponse<GetUserPreferencesResponse>> getUserPreferences(@Header("access-token") String accessToken);



}
