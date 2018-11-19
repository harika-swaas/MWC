package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.DocumentPreviewResponse;
import com.mwc.docportal.API.Model.PdfDocumentResponseModel;
import com.mwc.docportal.API.Model.SharedFolderModel.SharedDocumentReadStatusResponse;

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
    Call<PdfDocumentResponseModel> getDocumentPreviews(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

    @POST("/modify-eu-share-document-status")
    @FormUrlEncoded
    Call<SharedDocumentReadStatusResponse> getDocumentReadStatus(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

}
