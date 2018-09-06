package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.ApiResponse;
import com.mwc.docportal.API.Model.GlobalSearchModel.GlobalSearchDataResponseModel;
import com.mwc.docportal.API.Model.GlobalSearchModel.GlobalSearchResponseModel;
import com.mwc.docportal.API.Model.LoginResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

public interface EndUserGlobalSearchService
{
    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/end-user-global-search")
    @FormUrlEncoded
    Call<GlobalSearchResponseModel> endUserGlobalSearch(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("v2/get-end-user-global-search-data")
    @FormUrlEncoded
    Call<GlobalSearchDataResponseModel> endUserGlobalSearchData(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);


}
