package com.swaas.mwc.API.Service;

import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.GetTermsPageContentResponse;

import retrofit.Call;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by harika on 10-07-2018.
 */

public interface GetTermsPageContentService {

    @Headers({"content-type: application/x-www-form-urlencoded"})
    @POST("/get-terms-page-content")
    Call<BaseApiResponse<GetTermsPageContentResponse>> getTermsPageContent(@Header("access-token") String accessToken);
}
