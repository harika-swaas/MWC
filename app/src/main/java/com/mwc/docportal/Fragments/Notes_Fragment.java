package com.mwc.docportal.Fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.SimpleDividerItemDecoration;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.DMS.Tab_Activity;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;

import java.util.ArrayList;
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
    LinearLayout empty_view;
    TextView notes_empty_txt;
    List<DocumentNotesResponse> documentIdList =  new ArrayList<>();
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
        empty_view = (LinearLayout) mView.findViewById(R.id.empty_view);
        notes_empty_txt = (TextView) mView.findViewById(R.id.notes_empty_txt);
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

                        transparentProgressDialog.dismiss();
                        String message = "";
                        if(response.body().status.getMessage() != null)
                        {
                            message = response.body().status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, response.body().status.getCode())) {
                            documentNotesResponse = response.body().getData();
                            if(documentNotesResponse != null && documentNotesResponse.size() > 0)
                            {
                                empty_view.setVisibility(View.GONE);
                                setAdapterToView();
                            }
                            else
                            {
                                notes_empty_txt.setText("No document notes found");
                                empty_view.setVisibility(View.VISIBLE);
                            }


                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeoutAlert(mActivity);
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

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }



                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {

                            documentIdList.get(position).setMessage(response.body().getData().getMessage());

                            position++;

                            if(documentIdList.size()> position) {
                                getUserNotesDetails(documentIdList.get(position).getNotes_id());

                            }
                            else
                            {
                                notesAdapter = new NotesAdapter(documentIdList, getActivity());
                                recyclerView.setAdapter(notesAdapter);
                                position = 0;
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

        documentIdList = documentNotesResponse;

        if(documentIdList.size() > position)
        {
            getUserNotesDetails(documentIdList.get(position).getNotes_id());
        }

    }

}

