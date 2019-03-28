package com.mwc.docportal.API.Service;

import com.mwc.docportal.API.Model.APIResponseModel;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.GetTermsPageContentResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.UserProfileModel;
import com.mwc.docportal.API.Model.UserProfileUpdateModel.UserProfileCountryModel;

import retrofit.Call;
import retrofit.http.GET;
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


    @Headers({"content-type: application/x-www-form-urlencoded"})
    @POST("/get-profile")
    Call<UserProfileModel> getUserProfileDetails(@Header("access-token") String accessToken);

}
