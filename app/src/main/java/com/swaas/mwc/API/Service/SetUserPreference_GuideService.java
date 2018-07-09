package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.LoginResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by barath on 7/9/2018.
 */

public interface SetUserPreference_GuideService {
    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/set-user-preferences")
    @FormUrlEncoded
    Call<BaseApiResponse<LoginResponse>> getSetUserPreferences(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);
}
