package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;

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
    Call<BaseApiResponse<GetCategoryDocumentsResponse>> getCategoryDocumentsV2(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);
}
