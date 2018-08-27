package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.ApiResponse;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.BaseApiResponseStatus;
import com.swaas.mwc.API.Model.DeleteDocumentResponseModel;
import com.swaas.mwc.API.Model.LoginResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by barath on 8/20/2018.
 */

public interface DeleteDocumentService {
    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/delete-end-users-documents")
    @FormUrlEncoded
    Call<DeleteDocumentResponseModel> delete_eu_document(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);

}
