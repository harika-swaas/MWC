package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.UserProfileModel;
import com.mwc.docportal.API.Model.UserProfileUpdateModel.UserProfileCountryModel;
import com.mwc.docportal.API.Model.UserProfileUpdateModel.UserProfileResponseData;
import com.mwc.docportal.API.Model.VerifyFTLResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

public interface CountryListService
{


    @POST("/edit-profile")
    @FormUrlEncoded
    Call<UserProfileResponseData> getUserProfileUpdateDetails(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

}
