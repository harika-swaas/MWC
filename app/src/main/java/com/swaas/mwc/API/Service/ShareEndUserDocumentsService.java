package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.GetEndUserSharedParentFoldersResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.SharedDocumentResponseModel;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by harika on 20-07-2018.
 */

public interface ShareEndUserDocumentsService {

    @POST("/share-end-user-document")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>> getSharedEndUserDocuments(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

    @POST("/audit-log")
    @FormUrlEncoded
    Call<SharedDocumentResponseModel> getSharedDocumentDetails(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

    @POST("/register-mobile-device")
    @FormUrlEncoded
    Call<SharedDocumentResponseModel> sendPushNotificatoinStatus(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);


    @POST("/audit-log")
    @FormUrlEncoded
    Call<SharedDocumentResponseModel> sendFingerPrintStatus(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);
}
