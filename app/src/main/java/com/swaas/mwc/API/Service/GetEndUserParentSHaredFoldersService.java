package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.GetEndUserSharedParentFoldersResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;

import retrofit.Call;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by harika on 19-07-2018.
 */

public interface GetEndUserParentSHaredFoldersService {

    @Headers({"content-type: application/x-www-form-urlencoded"})
    @POST("/get-end-user-parent-shared-folders")
    Call<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>> getEndUserParentSharedFolders(@Header("access-token") String accessToken);
}
