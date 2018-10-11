package com.mwc.docportal.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.ColorCodeModel;
import com.mwc.docportal.API.Model.DocumentPreviewRequest;
import com.mwc.docportal.API.Model.DocumentPreviewResponse;
import com.mwc.docportal.API.Model.ExternalShareResponseModel;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.OfflineFiles;
import com.mwc.docportal.API.Model.SharedDocumentResponseModel;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.DocumentPreviewService;
import com.mwc.docportal.API.Service.ShareEndUserDocumentsService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Login.DocumentPreview;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.UserProfileActivity;
import com.mwc.docportal.pdf.PdfViewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class OffLineFilesListAdapter extends RecyclerView.Adapter<OffLineFilesListAdapter.ViewHolder> {

    final Activity context;
    private List<OfflineFiles> offLineFileListData;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    AlertDialog mAlertDialog;

    public OffLineFilesListAdapter(List<OfflineFiles> getCategoryDocumentsResponses, Activity context) {
        this.context = context;
        this.offLineFileListData = getCategoryDocumentsResponses;


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView imageView, selectedItemIv, thumbnailIcon, thumbnailCornerIcon, imageMore;
        public TextView folder_name;
        public View layout;
        public TextView folder_date, thumbnailText;
        ViewHolder vh;
        RelativeLayout indicatorParentView, folderView, thumbnailView;
        TextView indicatorTextValue;
        LinearLayout itemClick;

        public ViewHolder(View mView) {

            super(mView);
            layout = mView;

            folder_name = (TextView) mView.findViewById(R.id.folder_name);
            imageView = (ImageView) mView.findViewById(R.id.folder);
            imageMore = (ImageView) mView.findViewById(R.id.more);
            folder_date = (TextView) mView.findViewById(R.id.folder_date);
            selectedItemIv = (ImageView) itemView.findViewById(R.id.selected_item);
            indicatorParentView = (RelativeLayout) mView.findViewById(R.id.nameIndicatorParentView);
            folderView = (RelativeLayout) mView.findViewById(R.id.folder_layout);
            thumbnailView = (RelativeLayout) mView.findViewById(R.id.thumbnail_layout);
            indicatorTextValue = (TextView) mView.findViewById(R.id.indicatorTextValueView);
            thumbnailIcon = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            thumbnailCornerIcon = (ImageView) itemView.findViewById(R.id.thumbnail_corner_image);
            thumbnailText = (TextView) itemView.findViewById(R.id.thumbnail_text);
            itemClick = (LinearLayout) itemView.findViewById(R.id.list_click);
            this.itemView.setOnClickListener(this);
            this.itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
           /* if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());*/
        }

        @Override
        public boolean onLongClick(View view)
        {

            return false;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.file_items, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final OfflineFiles resp = offLineFileListData.get(position);

        if(offLineFileListData != null && offLineFileListData.size() > 0) {


            setButtonBackgroundColor();

            if (offLineFileListData.get(position).getFiletype() != null) {
                holder.folderView.setVisibility(View.GONE);
                holder.thumbnailView.setVisibility(View.VISIBLE);

                ColorCodeModel colorCodeModel = CommonFunctions.getColorCodesforFileType(offLineFileListData.get(position).getFiletype());
                if(colorCodeModel != null)
                {
                    holder.thumbnailIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getPrimaryColor()));
                    holder.thumbnailCornerIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getSecondaryColor()));
                    holder.thumbnailText.setText(colorCodeModel.getFileType());
                }

            }

            final String name = offLineFileListData.get(position).getFilename();
            holder.folder_name.setText(name);

            final String createdDate = offLineFileListData.get(position).getDownloadDate();
            holder.folder_date.setText(offLineFileListData.get(position).getFiletype()+", "+
                    offLineFileListData.get(position).getFileSize()+" MB, Offline on "+createdDate+ ", Source: "+ offLineFileListData.get(position).getSource());
            holder.folder_date.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);

            if(position > 0){
                char poschar = resp.getFilename().toUpperCase().charAt(0);
                char prevchar = offLineFileListData.get(position-1).getFilename().toUpperCase().charAt(0);
                if(poschar == prevchar){
                    holder.indicatorParentView.setVisibility(View.GONE);
                }else{
                    holder.indicatorParentView.setVisibility(View.VISIBLE);
                    holder.indicatorTextValue.setText(String.valueOf(offLineFileListData.get(position).getFilename().charAt(0)));
                }
            }else{
                holder.indicatorParentView.setVisibility(View.VISIBLE);
                holder.indicatorTextValue.setText(String.valueOf(offLineFileListData.get(position).getFilename().charAt(0)));
            }


            holder.imageMore.setVisibility(View.GONE);


            holder.itemClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(offLineFileListData.get(position).getFiletype() != null && offLineFileListData.get(position).getFiletype().equalsIgnoreCase("pdf"))
                    {
                        GetCategoryDocumentsResponse categoryDocumentsResponse = new GetCategoryDocumentsResponse();
                        categoryDocumentsResponse.setName(offLineFileListData.get(position).getFilename());
                        categoryDocumentsResponse.setDocument_version_id(offLineFileListData.get(position).getDocumentVersionId());
                        categoryDocumentsResponse.setFiletype(offLineFileListData.get(position).getFiletype());
                        categoryDocumentsResponse.setFilesize(offLineFileListData.get(position).getFileSize());
                        categoryDocumentsResponse.setObject_id(offLineFileListData.get(position).getDocumentId());

                        Intent intent = new Intent(context, PdfViewActivity.class);
                        intent.putExtra("mode",0);
                        intent.putExtra("url", offLineFileListData.get(position).getFilePath());
                        intent.putExtra("documentDetails",categoryDocumentsResponse);
                        intent.putExtra("IsFromOffline", true);
                        context.startActivity(intent);

                    }
                    else if(offLineFileListData.get(position).getFiletype() != null && !offLineFileListData.get(position).getFiletype().equalsIgnoreCase("pdf"))
                    {
                        if(offLineFileListData.get(position).getDocumentVersionId() != null && !offLineFileListData.get(position).getDocumentVersionId().isEmpty())
                        {
                         //   showWarningAlertForSharingContent(offLineFileListData.get(position), offLineFileListData.get(position).getFilename(), offLineFileListData.get(position).getDocumentVersionId());

                            String[] mimetypes = {"image/*", "application/*|text/*"};

                            String imagePath = offLineFileListData.get(position).getFilePath();

                            File imageFileToShare = new File(imagePath);

                            Uri uri = Uri.fromFile(imageFileToShare);

                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("*/*");
                            String shareBody = "";
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, offLineFileListData.get(position).getFilename());
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            sharingIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            Intent.createChooser(sharingIntent,"Share via");
                            context.startActivity(sharingIntent);
                        }

                    }

                }
            });
        }
    }

    public void showWarningAlertForSharingContent(OfflineFiles offlineFiles, final String filename, final String documentVersionId)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Warning");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

      //  txtMessage.setText("You are sharing this document to external contacts. Please aware that document security will not be carried over to the recipient");
        txtMessage.setText(context.getString(R.string.external_sharing_text));
        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("CANCEL");

        sendPinButton.setText("OK");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        sendPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                if (NetworkUtils.checkIfNetworkAvailable(context))
                {
                    getExternalSharingContentAPI(offlineFiles, filename, documentVersionId);
                }
                else
                {
                    String[] mimetypes = {"image/*", "application/*|text/*"};

                    String imagePath = offlineFiles.getFilePath();

                    File imageFileToShare = new File(imagePath);

                    Uri uri = Uri.fromFile(imageFileToShare);

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("*/*");
                    String shareBody = "";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, filename);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    sharingIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    Intent.createChooser(sharingIntent,"Share via");
                    context.startActivity(sharingIntent);
                }

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    public void getExternalSharingContentAPI(OfflineFiles offlineFiles, final String filename, final String versionId)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final ExternalShareResponseModel externalShareResponseModel = new ExternalShareResponseModel("external_sharing", versionId);


          //  ExternalShareResponseModel = new GetCategoryDocumentsRequest(Integer.parseInt(obj), "list", "category", "1", "0");

            String request = new Gson().toJson(externalShareResponseModel);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final ShareEndUserDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);

            Call call = mGetCategoryDocumentsService.getSharedDocumentDetails(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<SharedDocumentResponseModel>() {
                @Override
                public void onResponse(Response<SharedDocumentResponseModel> response, Retrofit retrofit) {

                    if (response != null) {

                        transparentProgressDialog.dismiss();
                        String message = "";
                        if(response.body().getStatus().getMessage() != null)
                        {
                            message = response.body().getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(context, message, response.body().getStatus().getCode()))
                        {
                            String[] mimetypes = {"image/*", "application/*|text/*"};

                            String imagePath = offlineFiles.getFilePath();

                            File imageFileToShare = new File(imagePath);

                            Uri uri = Uri.fromFile(imageFileToShare);

                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("*/*");
                            String shareBody = "";
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, filename);
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            sharingIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            Intent.createChooser(sharingIntent,"Share via");
                            context.startActivity(sharingIntent);
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeoutAlert(context);
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }




    private void setButtonBackgroundColor() {

        getWhiteLabelProperties();
    }

    private void getWhiteLabelProperties() {

        AccountSettings accountSettings = new AccountSettings(context);
        accountSettings.SetWhiteLabelCB(new AccountSettings.GetWhiteLabelCB() {
            @Override
            public void getWhiteLabelSuccessCB(List<WhiteLabelResponse> whiteLabelResponses) {
                if (whiteLabelResponses != null && whiteLabelResponses.size() > 0) {
                    mWhiteLabelResponses = whiteLabelResponses;
                }
            }

            @Override
            public void getWhiteLabelFailureCB(String message) {

            }
        });
        accountSettings.getWhiteLabelProperties();
    }



    @Override
    public int getItemCount() {
        return offLineFileListData.size();
    }


}
