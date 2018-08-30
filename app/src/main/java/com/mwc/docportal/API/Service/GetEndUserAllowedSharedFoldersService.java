package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.GetEndUserSharedParentFoldersResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by harika on 19-07-2018.
 */

public interface GetEndUserAllowedSharedFoldersService {

    @POST("/get-end-user-allowed-shared-folders")
    @FormUrlEncoded
    Call<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>> getEndUserAllowedSharedFolders(@FieldMap Map<String,String> params, @Header("access-token") String accessToken);

}
