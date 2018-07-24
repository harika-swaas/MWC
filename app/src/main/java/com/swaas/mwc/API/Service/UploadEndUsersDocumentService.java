package com.swaas.mwc.API.Service;

import com.squareup.okhttp.RequestBody;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.UploadEndUsersDocumentResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PartMap;

/**
 * Created by harika on 20-07-2018.
 */

public interface UploadEndUsersDocumentService {

    @Multipart
    @POST("/upload-end-users-document")
    Call<ListPinDevicesResponse<UploadEndUsersDocumentResponse>> getUploadEndUsersDocument(@PartMap Map<String, RequestBody> userParams,
                                                                                           @PartMap Map<String, RequestBody> params,
                                                                                           @Header("access-token") String accessToken);
}
