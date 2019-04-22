package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.ListPinDevices;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.SharedDocumentResponseModel;
import com.mwc.docportal.API.Model.UserProfileUpdateModel.UserProfileCountryModel;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
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

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/get-countries")
    Call<UserProfileCountryModel> getCountriesList(@Header("access-token") String accessToken);


    @POST("/get-pin-auth-device")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<ListPinDevices>> getPinDevicesList(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);
}
