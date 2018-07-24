package com.swaas.mwc.Fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.DocumentHistoryResponse;
import com.swaas.mwc.API.Model.DocumentNotesRequest;
import com.swaas.mwc.API.Model.DocumentNotesResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Service.DocumentNotesService;
import com.swaas.mwc.Adapters.NotesAdapter;
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

public class Notes_Fragment extends android.support.v4.app.Fragment{
    RecyclerView recyclerView;
    List<DocumentNotesResponse> documentNotesResponse;
    NotesAdapter notesAdapter;
    public static Notes_Fragment newInstance() {
        Notes_Fragment fragment = new Notes_Fragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.tab_fragment_2, container, false);
        recyclerView =(RecyclerView)mView.findViewById(R.id.notes);
        getNotes();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        notesAdapter = new NotesAdapter(documentNotesResponse,getActivity());
        recyclerView.setAdapter(notesAdapter);
        return  mView;
            }

    public void getNotes()
    {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DocumentNotesService documentNotesService = retrofitAPI.create(DocumentNotesService.class);
            final DocumentHistoryResponse documentHistoryResponse ;
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

}

