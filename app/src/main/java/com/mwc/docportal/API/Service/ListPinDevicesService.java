package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.ListPinDevices;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;

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
    Call<ListPinDevicesResponse<ListPinDevices>> getPinDevices(@Header("access-token") String accessToken);
}
