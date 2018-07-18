package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by harika on 12-07-2018.
 */

public interface GetCategoryDocumentsService {

    @POST("/v2/get-category-documents?page=1&per-page=20")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<GetCategoryDocumentsResponse>> getCategoryDocumentsV2(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

    @POST("v2/get-category-documents?page=1&per-page=20&sort=-type")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<GetCategoryDocumentsResponse>> getCategoryDocumentsV2SortByType(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

    @POST("v2/get-category-documents?page=1&per-page=20&sort=-name")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<GetCategoryDocumentsResponse>> getCategoryDocumentsV2SortByName(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

    @POST("v2/get-category-documents?page=1&per-page=20&sort=filesize")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<GetCategoryDocumentsResponse>> getCategoryDocumentsV2SortBySize(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

    @POST("v2/get-category-documents?page=1&per-page=20&sort=-unix_date")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<GetCategoryDocumentsResponse>> getCategoryDocumentsV2SortByDate(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);
}
