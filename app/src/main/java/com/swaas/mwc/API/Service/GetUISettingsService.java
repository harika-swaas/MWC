package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.GetUISettingsResponse;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by harika on 26-06-2018.
 */

public interface GetUISettingsService {

    @POST("/get-ui-settings")
    @FormUrlEncoded
    Call<BaseApiResponse<GetUISettingsResponse>> getUISettings();
}
