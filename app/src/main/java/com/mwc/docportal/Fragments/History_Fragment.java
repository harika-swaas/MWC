package com.mwc.docportal.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.DocumentHistoryRequest;
import com.mwc.docportal.API.Model.DocumentHistoryResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Service.DocumentHistoryService;
import com.mwc.docportal.Adapters.HistoryAdapter;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.Common.SimpleDividerItemDecoration;
import com.mwc.docportal.DMS.MyFoldersDMSActivity;
import com.mwc.docportal.DMS.Tab_Activity;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by barath on 7/19/2018.
 */

public class History_Fragment extends Fragment {

    RecyclerView mRecyclerView;
    HistoryAdapter mAdapter;

    List<DocumentHistoryResponse> documentHistoryResponses;
    Tab_Activity mActivity;

    public static History_Fragment newInstance() {
        History_Fragment fragment = new History_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mActivity = (Tab_Activity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.tab_fragment_3, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.history_view);
        gethistory();
        setHasOptionsMenu(true);

        return mView;
    }

    public void gethistory() {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DocumentHistoryService documentHistoryService = retrofitAPI.create(DocumentHistoryService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(getActivity());
            transparentProgressDialog.show();

            DocumentHistoryRequest documentHistoryRequest = new DocumentHistoryRequest(PreferenceUtils.getDocument_Id(getActivity()));
            final String request = new Gson().toJson(documentHistoryRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = documentHistoryService.getdocumenthist(params, PreferenceUtils.getAccessToken(getActivity()));

            call.enqueue(new Callback<ListPinDevicesResponse<DocumentHistoryResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<DocumentHistoryResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        transparentProgressDialog.dismiss();
                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {
                            documentHistoryResponses = response.body().getData();
                            setAdapterToView(documentHistoryResponses);
                        }

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.retrofitBadGatewayFailure(mActivity, t);
                }
            });
        }
    }

    private void setAdapterToView(List<DocumentHistoryResponse> documentHistoryResponses) {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity().getApplicationContext()));
        mAdapter = new HistoryAdapter(documentHistoryResponses, getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }



}
