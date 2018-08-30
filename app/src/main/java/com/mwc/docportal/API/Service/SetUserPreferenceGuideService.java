package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.LoginResponse;

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

public interface SetUserPreferenceGuideService {

    @POST("/set-user-preferences")
    @FormUrlEncoded
    Call<BaseApiResponse<LoginResponse>> getSetUserPreferences(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);
}
