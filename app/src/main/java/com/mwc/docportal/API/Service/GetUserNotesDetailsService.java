package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.GetUserNotesDetailsResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by barath on 7/24/2018.
 */

public interface GetUserNotesDetailsService {


    @POST("/get-user-notes-details")
    @FormUrlEncoded
    Call<BaseApiResponse<GetUserNotesDetailsResponse>> getUserNotesDetails(@FieldMap Map<String, String> params, @Header("access-token") String accessToken);

}
