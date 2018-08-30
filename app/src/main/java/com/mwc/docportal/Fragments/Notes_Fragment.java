package com.mwc.docportal.Fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.DocumentNotesRequest;
import com.mwc.docportal.API.Model.DocumentNotesResponse;
import com.mwc.docportal.API.Model.GetUserNotesDetailsRequest;
import com.mwc.docportal.API.Model.GetUserNotesDetailsResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Service.DocumentNotesService;
import com.mwc.docportal.API.Service.GetUserNotesDetailsService;
import com.mwc.docportal.Adapters.NotesAdapter;
import com.mwc.docportal.Common.SimpleDividerItemDecoration;
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

public class Notes_Fragment extends android.support.v4.app.Fragment {
    RecyclerView recyclerView;
    List<DocumentNotesResponse> documentNotesResponse;
    NotesAdapter notesAdapter;
    int position = 0,listItem = 1;
    Tab_Activity mActivity;
    public static Notes_Fragment newInstance() {
        Notes_Fragment fragment = new Notes_Fragment();
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
        View mView = inflater.inflate(R.layout.tab_fragment_2, container, false);
        recyclerView = (RecyclerView) mView.findViewById(R.id.notes_view);
        getNotes();
        setHasOptionsMenu(true);

        return mView;
    }

    public void getNotes() {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DocumentNotesService documentNotesService = retrofitAPI.create(DocumentNotesService.class);
            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(getActivity());
            transparentProgressDialog.show();
            DocumentNotesRequest documentNotesRequest = new DocumentNotesRequest(PreferenceUtils.getDocument_Id(getActivity()));
            final String request = new Gson().toJson(documentNotesRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = documentNotesService.getdocumentnotes(params, PreferenceUtils.getAccessToken(getActivity()));

            call.enqueue(new Callback<ListPinDevicesResponse<DocumentNotesResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<DocumentNotesResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                            transparentProgressDialog.dismiss();
                            documentNotesResponse = response.body().getData();
                           // getUserNotesDetails();
                            setAdapterToView();

                        } else {

                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                }
            });
        }
    }

    private void getUserNotesDetails(String notesId) {

        if (NetworkUtils.isNetworkAvailable(getActivity())) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final GetUserNotesDetailsService userNotesDetailsService = retrofitAPI.create(GetUserNotesDetailsService.class);

            GetUserNotesDetailsRequest userNotesDetailsRequest = new GetUserNotesDetailsRequest(Integer.parseInt(notesId));
            final String request = new Gson().toJson(userNotesDetailsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = userNotesDetailsService.getUserNotesDetails(params, PreferenceUtils.getAccessToken(getActivity()));

            call.enqueue(new Callback<BaseApiResponse<GetUserNotesDetailsResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<GetUserNotesDetailsResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                            if(documentNotesResponse.size() > position ){
                                documentNotesResponse.get(position).setMessage(response.body().getData().getMessage());
                                position++;
                                getUserNotesDetails(documentNotesResponse.get(position-1).getNotes_id());
                               // listItem++;
                            }else{
                                notesAdapter = new NotesAdapter(documentNotesResponse, getActivity());
                                recyclerView.setAdapter(notesAdapter);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                }
            });
        }
    }

    private void setAdapterToView() {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity().getApplicationContext()));

        for(DocumentNotesResponse response : documentNotesResponse){
            getUserNotesDetails(response.getNotes_id());
            break;
        }

    }

}

