package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.GetEndUserSharedParentFoldersResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.SharedDocumentResponseModel;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by harika on 19-07-2018.
 */

public interface GetEndUserParentSHaredFoldersService {

    @Headers({"content-type: application/x-www-form-urlencoded"})
    @POST("/get-end-user-parent-shared-folders")
    Call<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>> getEndUserParentSharedFolders(@Header("access-token") String accessToken);



    @POST("/end-user-stop-sharing-document")
    @FormUrlEncoded
    Call<SharedDocumentResponseModel> getEndUserStopSharedDocuments(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);


}
