package com.mwc.docportal.Fragments;

/**
 * Created by barath on 7/19/2018.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.DocumentPropertiesRequest;
import com.mwc.docportal.API.Model.DocumentPropertiesResponse;
import com.mwc.docportal.API.Model.EditDocumentPropertiesRequest;
import com.mwc.docportal.API.Model.EditDocumentResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Service.DocumentPropertiesService;
import com.mwc.docportal.API.Service.EditDocumentPropertiesService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.DMS.Tab_Activity;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.MessageDialog;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.pdf.PdfViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.mwc.docportal.DMS.Tab_Activity.isFromShared;
import static com.mwc.docportal.DMS.Tab_Activity.isNameEditAvailable;

public class Properties_Fragment extends Fragment{
    Tab_Activity  mActivity;
    MessageDialog messageDialog;
    TextView filename,name,version,type,size,uploaded_date,author,created_date,search_tags;
    EditText name_edtText;
    MenuItem editButton;
    List<DocumentPropertiesResponse> documentPropertiesResponse;
    public static Properties_Fragment newInstance() {
        Properties_Fragment fragment = new Properties_Fragment();
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
            name_edtText = (EditText) mView.findViewById(R.id.name_edtText);

            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(45);
            name_edtText.setFilters(FilterArray);
            name_edtText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            name_edtText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            getdocumentdetails();


            name_edtText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });
            setHasOptionsMenu(true);
            return mView;


        }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
                        transparentProgressDialog.dismiss();
                        String message = "";
                        if(response.body().status.getMessage() != null)
                        {
                            message = response.body().status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, response.body().status.getCode())) {
                            documentPropertiesResponse = response.body().getData();

                            filename.setText(String.valueOf(documentPropertiesResponse.get(0).getFilename()));
                            name.setText(String.valueOf(documentPropertiesResponse.get(0).getDocument_name()));
                            author.setText(String.valueOf(documentPropertiesResponse.get(0).getAuthor()));
                            size.setText(String.valueOf(documentPropertiesResponse.get(0).getFilesize())+" MB");
                            type.setText(String.valueOf(documentPropertiesResponse.get(0).getFiletype()));
                            uploaded_date.setText(String.valueOf(documentPropertiesResponse.get(0).getUploaded_date()));
                            version.setText(String.valueOf(documentPropertiesResponse.get(0).getVersion_number()));
                            created_date.setText(String.valueOf(documentPropertiesResponse.get(0).getCreation_date()));


                            StringBuilder sb = new StringBuilder();
                            try {
                                JSONArray jsonObj = new JSONArray(documentPropertiesResponse.get(0).getTag());

                                String prefix = "";
                                for (int i = 0; i < jsonObj.length(); i++) {
                                    JSONObject objString = jsonObj.getJSONObject(i);
                                    sb.append(prefix);
                                    prefix = ", ";
                                    sb.append(objString.getString("text"));
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                            search_tags.setText(sb.toString());
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
    public void showMessagebox(Context context, String message, View.OnClickListener onClickListener, boolean isCancelable) {
        initMessageDialog(context);
        messageDialog.showDialog(context, message, onClickListener, isCancelable);
    }
    private void initMessageDialog(Context context) {
        if (messageDialog == null) {
            messageDialog = new MessageDialog(context);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_property, menu);

        editButton = menu.findItem(R.id.edit_item);
        if(isFromShared)
        {
            editButton.setVisible(false);
        }



        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_item:
                editDocumentName();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void editDocumentName()
    {
        if(editButton.getTitle().toString().equalsIgnoreCase("EDIT"))
        {
            if(name.getText().toString() != null && !name.getText().toString().isEmpty())
            {
                name.setVisibility(View.GONE);
                name_edtText.setVisibility(View.VISIBLE);
                name_edtText.setText(name.getText().toString());
                editButton.setTitle("DONE");

            }

        }
        else if(editButton.getTitle().toString().equalsIgnoreCase("DONE")) {

            if(name_edtText.getText().toString() != null && !name_edtText.getText().toString().isEmpty()) {

                name.setVisibility(View.VISIBLE);
                name.setText(name_edtText.getText().toString());
                name_edtText.setVisibility(View.GONE);
                editButton.setTitle("EDIT");
                editDocumentDetails(PreferenceUtils.getDocumentVersionId(mActivity),name_edtText.getText().toString());
            }
            else
            {
                Toast.makeText(mActivity, "Please enter name", Toast.LENGTH_SHORT).show();
            }

        }


    }

    private void editDocumentDetails(String documentVersionId, String renameData)
    {
        if (NetworkUtils.isNetworkAvailable(mActivity)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(mActivity);
            transparentProgressDialog.show();

            ArrayList<EditDocumentResponse> tagArrayList = new ArrayList<>();


            try {
                JSONArray jsonObj = new JSONArray(documentPropertiesResponse.get(0).getTag());
                for (int i = 0; i < jsonObj.length(); i++) {
                    JSONObject objString = jsonObj.getJSONObject(i);
                    EditDocumentResponse editDocumentResponse = new EditDocumentResponse();
                    editDocumentResponse.setText(objString.getString("text"));
                    tagArrayList.add(editDocumentResponse);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
            final EditDocumentPropertiesRequest editDocumentPropertiesRequest = new EditDocumentPropertiesRequest(documentVersionId,renameData,
                    documentPropertiesResponse.get(0).getCreation_date(),documentPropertiesResponse.get(0).getAuthor(),tagArrayList);

            String request = new Gson().toJson(editDocumentPropertiesRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final EditDocumentPropertiesService editDocumentPropertiesService = retrofitAPI.create(EditDocumentPropertiesService.class);

            Call call = editDocumentPropertiesService.getRenameDocument(params, PreferenceUtils.getAccessToken(mActivity));

            call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(mActivity, message, apiResponse.status.getCode())) {

                            if(isNameEditAvailable)
                            {
                                PreferenceUtils.setDocumentName(getActivity(), renameData);
                            }

                            ((Tab_Activity) getActivity())
                                    .getSupportActionBar().setTitle(renameData);
                            getdocumentdetails();
                        }

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeoutAlert(mActivity);
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }

    }


}
