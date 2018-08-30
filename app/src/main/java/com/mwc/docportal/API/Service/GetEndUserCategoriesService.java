package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.GetEndUserCategoriesResponse;
import com.mwc.docportal.API.Model.GetEndUserSharedParentFoldersResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by barath on 8/6/2018.
 */

public interface GetEndUserCategoriesService {
    @POST("/get-end-user-categories")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<GetEndUserCategoriesResponse>> getEndUsercategory(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

}
