package com.swaas.mwc.Fragments;

/**
 * Created by barath on 7/19/2018.
 */


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.DocumentPropertiesRequest;
import com.swaas.mwc.API.Model.DocumentPropertiesResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Service.DocumentPropertiesService;
import com.swaas.mwc.DMS.Tab_Activity;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.MessageDialog;
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

public class Properties_Fragment extends Fragment{
    Tab_Activity  mActivity;
    MessageDialog messageDialog;
    TextView filename,name,version,type,size,uploaded_date,author,created_date,search_tags;
    public static Properties_Fragment newInstance() {
        Properties_Fragment fragment = new Properties_Fragment();
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

            View mView= inflater.inflate(R.layout.tab_fragment_1, container, false);

            filename= (TextView) mView.findViewById(R.id.filename);
            name= (TextView) mView.findViewById(R.id.name);
            version= (TextView) mView.findViewById(R.id.version);
            type= (TextView) mView.findViewById(R.id.type);
            size= (TextView) mView.findViewById(R.id.size);
            uploaded_date= (TextView) mView.findViewById(R.id.uploaded_date);
            author= (TextView) mView.findViewById(R.id.author);
            created_date= (TextView) mView.findViewById(R.id.creation_date);
            search_tags= (TextView) mView.findViewById(R.id.tags);
            getdocumentdetails();
            /*intiaizeViews();*/
            return mView;


        }

        /*public void intiaizeViews(){

                filename= (TextView) mView.findViewById(R.id.textView18);
                name= (TextView) mView.findViewById(R.id.textView12);
                version= (TextView) mView.findViewById(R.id.filename);
                type= (TextView) mView.findViewById(R.id.textView10);
                size= (TextView) mView.findViewById(R.id.textView14);
                uploaded_date= (TextView) mView.findViewById(R.id.textView15);
                author= (TextView) mView.findViewById(R.id.textView16);
                created_date= (TextView) mView.findViewById(R.id.textView13);
                search_tags= (TextView) mView.findViewById(R.id.textView11);
        }
*/
    public void getdocumentdetails(){

        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DocumentPropertiesService documentPropertiesService = retrofitAPI.create(DocumentPropertiesService.class);
            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(getActivity());
            transparentProgressDialog.show();

            DocumentPropertiesRequest documentPropertiesRequest = new DocumentPropertiesRequest(PreferenceUtils.getDocumentVersionId(getActivity()));
            String request = new Gson().toJson(documentPropertiesRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = documentPropertiesService.getdocumentprop(params, PreferenceUtils.getAccessToken(getActivity()));

            call.enqueue(new Callback<ListPinDevicesResponse<DocumentPropertiesResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<DocumentPropertiesResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();

                                List<DocumentPropertiesResponse> documentPropertiesResponse = response.body().getData();

                                filename.setText(String.valueOf(documentPropertiesResponse.get(0).getFilename()));
                                name.setText(String.valueOf(documentPropertiesResponse.get(0).getDocument_name()));
                                author.setText(String.valueOf(documentPropertiesResponse.get(0).getAuthor()));
                                size.setText(String.valueOf(documentPropertiesResponse.get(0).getFilesize()));
                                type.setText(String.valueOf(documentPropertiesResponse.get(0).getFiletype()));
                                uploaded_date.setText(String.valueOf(documentPropertiesResponse.get(0).getUploaded_date()));
                                version.setText(String.valueOf(documentPropertiesResponse.get(0).getVersion_number()));
                                created_date.setText(String.valueOf(documentPropertiesResponse.get(0).getCreation_date()));
                                String tags = String.valueOf(documentPropertiesResponse.get(0).getTag());

                                for(int i = 0; i<tags.length() ; i++)
                                {

                                }
                                search_tags.setText(documentPropertiesResponse.get(0).getTag());
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
    public void showMessagebox(Context context, String message, View.OnClickListener onClickListener, boolean isCancelable) {
        initMessageDialog(context);
        messageDialog.showDialog(context, message, onClickListener, isCancelable);
    }
    private void initMessageDialog(Context context) {
        if (messageDialog == null) {
            messageDialog = new MessageDialog(context);
        }
    }
}
