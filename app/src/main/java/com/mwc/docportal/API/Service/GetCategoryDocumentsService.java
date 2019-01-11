package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.APIResponseModel;
import com.mwc.docportal.API.Model.ApiResponse;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.SharedFolderModel.SharedDocumentResponseModel;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by harika on 12-07-2018.
 */

public interface GetCategoryDocumentsService {


    @POST("v2/get-category-documents?")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<GetCategoryDocumentsResponse>> getCategoryDocuments(@FieldMap Map<String,String> params, @Header("access-token") String accessToken,
                                                                                                @Query("page") String page,
                                                                                                @Query("per-page") String per_page,
                                                                                                @Query("sort") String sortType);

    @POST("v2/get-category-documents?")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<GetCategoryDocumentsResponse>> getCategoryDocumentsWithoutSortType(@FieldMap Map<String,String> params, @Header("access-token") String accessToken,
                                                                                    @Query("page") String page,
                                                                                    @Query("per-page") String per_page);

    @POST("get-shared-category-documents?")
    @FormUrlEncoded
    Call<APIResponseModel> getSharedCategoryDocumentsV2(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

    @POST("get-shared-category-documents-tree")
    @FormUrlEncoded
    Call<SharedDocumentResponseModel> getSharedDocumentAndCategoryDocumentsV2(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);
}
