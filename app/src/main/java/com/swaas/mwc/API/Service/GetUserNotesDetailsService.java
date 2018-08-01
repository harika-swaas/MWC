package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.GetUserNotesDetailsResponse;

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
