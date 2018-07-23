package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.DocumentNotesResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by barath on 7/20/2018.
 */

public interface DocumentNotesService {

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("/get-user-notes-list")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<DocumentNotesResponse>> getdocumentnotes(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);
}
