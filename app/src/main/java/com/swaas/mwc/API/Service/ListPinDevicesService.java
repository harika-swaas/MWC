package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.ListPinDevices;
import retrofit.Call;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by barath on 6/28/2018.
 */

public interface ListPinDevicesService {

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/get-pin-auth-device")
    Call<BaseApiResponse<ListPinDevices>> getPinDevices(@Header("access-token") String accessToken);
}
