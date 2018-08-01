package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.DocumentPreviewResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by barath on 7/26/2018.
 */

public interface DocumentPreviewService {

    @POST("/generate-document-pdf-url")
    @FormUrlEncoded
    Call<BaseApiResponse<DocumentPreviewResponse>> getDocumentPreviews(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

}
