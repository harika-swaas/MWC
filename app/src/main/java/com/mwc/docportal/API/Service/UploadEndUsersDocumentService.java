package com.mwc.docportal.API.Service;

import com.squareup.okhttp.RequestBody;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.UploadDocumentResponse;
import com.mwc.docportal.API.Model.UploadEndUsersDocumentResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;

/**
 * Created by harika on 20-07-2018.
 */

public interface UploadEndUsersDocumentService {

    @Multipart
    @POST("/upload-end-users-document")
    Call<UploadDocumentResponse> getUploadEndUsersDocument(@Part("data") RequestBody data,
                                                           @PartMap Map<String, RequestBody> fileParams,
                                                           @Header("access-token") String accessToken);

}
