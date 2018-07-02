package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.LoginRequest;
import com.swaas.mwc.API.Model.LoginResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by harika on 21-06-2018.
 */

public interface LoginService {

    @POST("/login")
    @FormUrlEncoded
    Call<BaseApiResponse<LoginResponse>> getLogin(@FieldMap Map<String,String> params);
}
