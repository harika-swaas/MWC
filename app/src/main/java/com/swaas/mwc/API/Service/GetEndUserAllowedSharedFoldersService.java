package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.GetEndUserSharedParentFoldersResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;

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
