package com.swaas.mwc.Fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.DocumentHistoryRequest;
import com.swaas.mwc.API.Model.DocumentHistoryResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Service.DocumentHistoryService;
import com.swaas.mwc.Adapters.HistoryAdapter;
import com.swaas.mwc.DMS.Tab_Activity;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;

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

public class History_Fragment extends android.support.v4.app.Fragment {
    Tab_Activity mActivity;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;

    List<DocumentHistoryResponse> documentHistoryResponses;
    public static History_Fragment newInstance() {
        History_Fragment fragment = new History_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mView= inflater.inflate(R.layout.tab_fragment_3, container, false);
        recyclerView =(RecyclerView) mView.findViewById(R.id.lis_history);
        gethistory();
        return mView;
    }

    public void gethistory()
    {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DocumentHistoryService documentHistoryService = retrofitAPI.create(DocumentHistoryService.class);
            final DocumentHistoryResponse documentHistoryResponse ;
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

                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                            transparentProgressDialog.dismiss();
                            documentHistoryResponses = response.body().getData();
                            onBindAdapter(documentHistoryResponses);

                        }

                        else {

                            String mMessage = apiResponse.status.getMessage().toString();
                           /*// mActivity.showMessagebox(mActivity, mMessage, new View.OnClickListener()
                                {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(mActivity, LoginActivity.class));
                                    mActivity.finish();
                                }
                            }, false);
                        */}
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                }
            });
        }
    }

    private void onBindAdapter(List<DocumentHistoryResponse> documentHistoryResponses) {



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        historyAdapter = new HistoryAdapter(documentHistoryResponses,getActivity());
        recyclerView.setAdapter(historyAdapter);


    }
}
