package com.swaas.mwc.API.Service;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;

import java.util.Map;


/**
 * Created by barath on 6/29/2018.
 */

public interface SendPinService {
    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/send-pin")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<LoginResponse>> getSendPin(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);
}
