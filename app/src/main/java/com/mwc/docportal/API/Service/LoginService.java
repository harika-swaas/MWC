package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.ApiResponse;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.LoginRequest;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.PasswordResetModel.UserNameResponseModel;

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
    Call<ApiResponse<LoginResponse>> getLogin(@FieldMap Map<String,String> params);

    @POST("/self-reset")
    @FormUrlEncoded
    Call<UserNameResponseModel> checkUserName(@FieldMap Map<String,String> params);
}
