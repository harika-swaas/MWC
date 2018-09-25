package com.mwc.docportal.GlobalSearch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.ApiResponse;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.ColorCodeModel;
import com.mwc.docportal.API.Model.DeleteDocumentRequest;
import com.mwc.docportal.API.Model.DeleteDocumentResponseModel;
import com.mwc.docportal.API.Model.DeleteEndUserFolderRequest;
import com.mwc.docportal.API.Model.DocumentPreviewRequest;
import com.mwc.docportal.API.Model.DocumentPreviewResponse;
import com.mwc.docportal.API.Model.DownloadDocumentRequest;
import com.mwc.docportal.API.Model.DownloadDocumentResponse;
import com.mwc.docportal.API.Model.EditDocumentPropertiesRequest;
import com.mwc.docportal.API.Model.EditDocumentResponse;
import com.mwc.docportal.API.Model.EndUserRenameRequest;
import com.mwc.docportal.API.Model.GetCategoryDocumentsRequest;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.OfflineFiles;
import com.mwc.docportal.API.Model.PdfDocumentResponseModel;
import com.mwc.docportal.API.Model.SharedDocumentResponseModel;
import com.mwc.docportal.API.Model.StopSharingRequestModel;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.DeleteDocumentService;
import com.mwc.docportal.API.Service.DeleteEndUserFolderService;
import com.mwc.docportal.API.Service.DocumentPreviewService;
import com.mwc.docportal.API.Service.DownloadDocumentService;
import com.mwc.docportal.API.Service.EditDocumentPropertiesService;
import com.mwc.docportal.API.Service.EndUserRenameService;
import com.mwc.docportal.API.Service.GetCategoryDocumentsService;
import com.mwc.docportal.API.Service.GetEndUserParentSHaredFoldersService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.FileDownloadManager;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.DMS.MyFolderActivity;
import com.mwc.docportal.DMS.MyFolderCopyActivity;
import com.mwc.docportal.DMS.MyFolderSharedDocuments;
import com.mwc.docportal.DMS.MyFoldersDMSActivity;
import com.mwc.docportal.DMS.MyfolderDeleteActivity;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.DMS.NavigationSharedActivity;
import com.mwc.docportal.DMS.Tab_Activity;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.OffLine_Files_Repository;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.FTL.WebviewLoaderTermsActivity;
import com.mwc.docportal.Fragments.ItemNavigationFolderFragment;
import com.mwc.docportal.Login.DocumentPreview;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;
import com.mwc.docportal.Utils.DateHelper;
import com.mwc.docportal.pdf.PdfViewActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class GlobalSearchAdapter extends RecyclerView.Adapter<GlobalSearchAdapter.ViewHolder> {

    Activity context;
    private List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;

    AlertDialog mBackDialog;

    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();

    PdfDocumentResponseModel getDocumentPreviewResponses;
    AlertDialog mAlertDialog;

    public GlobalSearchAdapter(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses, Activity context) {
        this.context = context;
        this.mGetCategoryDocumentsResponses = getCategoryDocumentsResponses;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView, thumbnailIcon, thumbnailCornerIcon, imageMore, offline_download;
        public TextView folder_name, folder_path;
        public View layout;
        public TextView folder_date, thumbnailText;
        ViewHolder vh;
        RelativeLayout folderView, thumbnailView,foldernext, list_item_click;



        public ViewHolder(View mView) {

            super(mView);
            layout = mView;

            folder_name = (TextView) mView.findViewById(R.id.folder_name);
            imageView = (ImageView) mView.findViewById(R.id.folder);
            imageMore = (ImageView) mView.findViewById(R.id.more);
            folder_date = (TextView) mView.findViewById(R.id.folder_date);
            folderView = (RelativeLayout) mView.findViewById(R.id.folder_layout);
            thumbnailView = (RelativeLayout) mView.findViewById(R.id.thumbnail_layout);
            thumbnailIcon = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            thumbnailCornerIcon = (ImageView) itemView.findViewById(R.id.thumbnail_corner_image);
            thumbnailText = (TextView) itemView.findViewById(R.id.thumbnail_text);
            foldernext=(RelativeLayout)itemView.findViewById(R.id.folder_rl_2);
            list_item_click = (RelativeLayout) itemView.findViewById(R.id.list_item_click);
            folder_path = (TextView) itemView.findViewById(R.id.folder_path);
            offline_download = (ImageView) mView.findViewById(R.id.offline_download);
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.global_search_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if(mGetCategoryDocumentsResponses != null && mGetCategoryDocumentsResponses.size() > 0) {

            PreferenceUtils.setDocumentVersionId(context, mGetCategoryDocumentsResponses.get(position).getDocument_version_id());
            PreferenceUtils.setDocument_Id(context, mGetCategoryDocumentsResponses.get(position).getObject_id());

            setButtonBackgroundColor();

           if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                holder.folderView.setVisibility(View.GONE);
                holder.thumbnailView.setVisibility(View.VISIBLE);
                holder.thumbnailCornerIcon.setVisibility(View.VISIBLE);
                ColorCodeModel colorCodeModel = CommonFunctions.getColorCodesforFileType(mGetCategoryDocumentsResponses.get(position).getFiletype());
                if(colorCodeModel != null) {

                    holder.thumbnailIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getPrimaryColor()));
                    holder.thumbnailCornerIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getSecondaryColor()));

                    holder.thumbnailText.setText(colorCodeModel.getFileType());

                }

                final String createdDate = mGetCategoryDocumentsResponses.get(position).getCreated_date();
                holder.folder_date.setText("Uploaded on "+createdDate);
                holder.folder_path.setText(mGetCategoryDocumentsResponses.get(position).getDoc_status() + mGetCategoryDocumentsResponses.get(position).getFile_path());

            }

            final String name = mGetCategoryDocumentsResponses.get(position).getName();
            holder.folder_name.setText(name);

            holder.imageMore.setVisibility(View.VISIBLE);

            OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
            if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(mGetCategoryDocumentsResponses.get(position).getDocument_version_id())) {
                holder.offline_download.setVisibility(View.VISIBLE);
            }
            else {
                holder.offline_download.setVisibility(View.INVISIBLE);
            }

            holder.list_item_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                       if (mGetCategoryDocumentsResponses.get(position).getType() != null && mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                            if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                                getDocumentPreviews(mGetCategoryDocumentsResponses.get(position));
                            }

                        }

                }
            });


            holder.imageMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                     if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                        openBottomSheetForDocument(mGetCategoryDocumentsResponses.get(position), mGetCategoryDocumentsResponses.get(position).getType(), mGetCategoryDocumentsResponses.get(position).getFiletype(), mGetCategoryDocumentsResponses.get(position).getName());
                        String document=mGetCategoryDocumentsResponses.get(position).getObject_id();
                        String categoryr = mGetCategoryDocumentsResponses.get(position).getDocument_version_id();
                        PreferenceUtils.setDocumentVersionId(context, categoryr);
                        PreferenceUtils.setDocument_Id(context, document);
                        PreferenceUtils.setCategoryId(context,document);

                    }
                }
            });



        }
    }





    private void getDocumentPreviews(final GetCategoryDocumentsResponse categoryDocumentsResponse) {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final DocumentPreviewRequest mDocumentPreviewRequest = new DocumentPreviewRequest(categoryDocumentsResponse.getDocument_version_id());

            String request = new Gson().toJson(mDocumentPreviewRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final DocumentPreviewService mDocumentPreviewService = retrofitAPI.create(DocumentPreviewService.class);
            Call call = mDocumentPreviewService.getDocumentPreviews(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<PdfDocumentResponseModel>() {
                @Override
                public void onResponse(Response<PdfDocumentResponseModel> response, Retrofit retrofit) {
                    PdfDocumentResponseModel apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        String message = "";
                        if(apiResponse.getStatus().getMessage() != null)
                        {
                            message = apiResponse.getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(context, message, apiResponse.getStatus().getCode())) {

                            if (apiResponse.getStatus().getCode() instanceof Boolean) {
                                if (apiResponse.getStatus().getCode() == Boolean.FALSE) {
                                    getDocumentPreviewResponses = response.body();
                                    String document_preview_url = getDocumentPreviewResponses.getData().getDocumentPdfUrl();

                                    Intent intent = new Intent(context, PdfViewActivity.class);
                                    intent.putExtra("mode",1);
                                    intent.putExtra("url", document_preview_url);
                                    intent.putExtra("documentDetails", categoryDocumentsResponse);
                                    context.startActivity(intent);
                                }

                            } else if (apiResponse.getStatus().getCode() instanceof Double) {
                                double status_value = new Double(apiResponse.getStatus().getCode().toString());

                                if (status_value == 400.0)
                                {
                                    Intent intent = new Intent(context, PdfViewActivity.class);
                                    intent.putExtra("isFrom_Status400",true);
                                    intent.putExtra("documentDetails", categoryDocumentsResponse);
                                    context.startActivity(intent);

                                }
                            }

                        }

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
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

    private void openBottomSheetForDocument(final GetCategoryDocumentsResponse categoryDocumentsResponse, String type, String fileType, String name) {

        View view = ((GlobalSearchActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_document_sort, null);
        RelativeLayout shareView = (RelativeLayout)view.findViewById(R.id.share_layout);
        RelativeLayout doclayout = (RelativeLayout)view.findViewById(R.id.doc_info_layout);
        TextView docText = (TextView) view.findViewById(R.id.doc_text);
        ImageView thumbnailIcon = (ImageView) view.findViewById(R.id.thumbnail_image);
        ImageView thumbnailCornerIcon = (ImageView) view.findViewById(R.id.thumbnail_corner_image);
        TextView thumbnailText = (TextView) view.findViewById(R.id.thumbnail_text);
        RelativeLayout copy=(RelativeLayout) view.findViewById(R.id.copy1);
        RelativeLayout move=(RelativeLayout) view.findViewById(R.id.move1);
        ImageView copyImage = (ImageView) view.findViewById(R.id.copy_image);
        ImageView moveImage = (ImageView) view.findViewById(R.id.move_image);
        ImageView renameImage = (ImageView) view.findViewById(R.id.rename_image);
        ImageView shareImage = (ImageView) view.findViewById(R.id.share_image);
        ImageView availableOfflineImage = (ImageView) view.findViewById(R.id.available_offline_image);
        TextView delete = (TextView)view.findViewById(R.id.delete);
        RelativeLayout rename_layout = (RelativeLayout) view.findViewById(R.id.rename_layout);


        final SwitchCompat switchButton_download = (SwitchCompat) view.findViewById(R.id.switchButton_download);
        final SwitchCompat switchButton_share = (SwitchCompat) view.findViewById(R.id.switchButton_share);

        List<GetCategoryDocumentsResponse> categoryDocumentlist = new ArrayList<>();
        categoryDocumentlist.add(categoryDocumentsResponse);
        CommonFunctions.setSelectedItems(categoryDocumentlist);


        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        if(categoryDocumentsResponse.getIs_shared().equals("1"))
        {
            switchButton_share.setChecked(true);
        }
        else
        {
            switchButton_share.setChecked(false);
        }


        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
        if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(categoryDocumentsResponse.getDocument_version_id())) {

            switchButton_download.setChecked(true);
        }
        else {
            switchButton_download.setChecked(false);
        }

        switchButton_download.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed() == true) {
                    mBottomSheetDialog.dismiss();
                    if (isChecked) {
                        switchButton_download.setChecked(true);
                        getDownloadurlFromServiceSingleDocument(categoryDocumentsResponse);
                    }
                    else
                    {
                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                        String filepath = offLine_files_repository.getFilePathFromLocalTable(categoryDocumentsResponse.getDocument_version_id());

                        if(filepath != null && !filepath.isEmpty())
                        {
                            CommonFunctions.deleteFileFromInternalStorage(filepath);
                        }
                        offLine_files_repository.deleteAlreadydownloadedFile(categoryDocumentsResponse.getDocument_version_id());

                        switchButton_download.setChecked(false);
                    }


                }
            }
        });

        switchButton_share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(buttonView.isPressed() == true) {
                    mBottomSheetDialog.dismiss();
                    if (!isChecked) {
                        switchButton_share.setChecked(true);
                        showWarningMessageAlertForSharingContent(categoryDocumentsResponse);
                    } else {
                        switchButton_share.setChecked(false);
                        GlobalVariables.isMoveInitiated = true;
                        GlobalVariables.selectedActionName =  "share";
                        Intent intent = new Intent(context, NavigationSharedActivity.class);
                        intent.putExtra("ObjectId", "0");
                        context.startActivity(intent);
                    }


                }

            }
        });



        if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
            String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            int selectedColor = Color.parseColor(itemSelectedColor);

            copyImage.setColorFilter(selectedColor);
            renameImage.setColorFilter(selectedColor);
            moveImage.setColorFilter(selectedColor);
            shareImage.setColorFilter(selectedColor);
            availableOfflineImage.setColorFilter(selectedColor);
        }

        docText.setText(name);

        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();

                GlobalVariables.searchKey = GlobalSearchActivity.searchingData;
                initiateMoveAction("move");

            }
        });

        doclayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                PreferenceUtils.setDocumentVersionId(context,categoryDocumentsResponse.getDocument_version_id());
                PreferenceUtils.setDocument_Id(context, categoryDocumentsResponse.getObject_id());
                Intent intent = new Intent (context,Tab_Activity.class);
                if(categoryDocumentsResponse.getDoc_status().equalsIgnoreCase("shared"))
                {
                    intent.putExtra("IsFromShared", true);
                }
                context.startActivity(intent);
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();

                GlobalVariables.searchKey = GlobalSearchActivity.searchingData;
                initiateMoveAction("copy");

            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v)
            {
                mBottomSheetDialog.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.delete_document, null);
                builder.setView(view);
                builder.setCancelable(false);
                Button delete = (Button)view.findViewById(R.id.delete);
                Button delete_historic=(Button)view.findViewById(R.id.movefolder);
                Button delete_all =(Button)view.findViewById(R.id.deleteall);
                Button cancel = (Button)view.findViewById(R.id.canceldel);

                if(categoryDocumentsResponse.getVersion_count().equals("0"))
                {
                    delete_historic.setEnabled(false);
                    delete_historic.setTextColor(R.color.grey);
                    delete_all.setEnabled(false);
                    delete_all.setTextColor(R.color.grey);

                }


                mBackDialog = builder.create();
                mBackDialog.show();
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();

                        deleteDocumentsService("0");
                    }
                });


                delete_historic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();

                        deleteDocumentsService("1");

                    }
                });

                delete_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();

                        deleteDocumentsService("2");

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();

                    }
                });


            }




        });

        rename_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.rename_alert, null);
                builder.setView(view);
                builder.setCancelable(false);

                Button cancel = (Button) view.findViewById(R.id.cancel_b);
                Button allow = (Button) view.findViewById(R.id.allow);
                final EditText namer = (EditText) view.findViewById(R.id.edit_username1);
                allow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String folder = namer.getText().toString().trim();

                        if(folder != null && !folder.isEmpty())
                        {
                            renamedocument(categoryDocumentsResponse.getDocument_version_id(),folder,"","");
                            mAlertDialog.dismiss();
                            mBottomSheetDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();
                        mBottomSheetDialog.dismiss();
                    }
                });

                mAlertDialog = builder.create();
                mAlertDialog.show();


            }
        });



        ColorCodeModel colorCodeModel = CommonFunctions.getColorCodesforFileType(fileType);
        if(colorCodeModel != null)
        {

            thumbnailIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getPrimaryColor()));
            thumbnailCornerIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getSecondaryColor()));
            thumbnailText.setText(colorCodeModel.getFileType());
        }



    }

    private void initiateMoveAction(String actionName)
    {
        GlobalVariables.isMoveInitiated = true;
        GlobalVariables.selectedActionName =  actionName;
        Intent intent = new Intent(context, NavigationMyFolderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("ObjectId", "0");
        context.startActivity(intent);
    }

    private void showWarningMessageAlertForSharingContent(final GetCategoryDocumentsResponse categoryDocumentsResponse)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Warning");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText("This action will stop sharing the selected document(s). Company with whom this has been shared will no longer be able to view this document");

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

                ArrayList<String> documentIdslist = new ArrayList<>();
                documentIdslist.add(categoryDocumentsResponse.getObject_id());
                getInternalStoppingSharingContentAPI(documentIdslist, categoryDocumentsResponse.getCategory_id());


            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }


    private void getInternalStoppingSharingContentAPI(ArrayList<String> documentIdslist, String category_id)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();


            final StopSharingRequestModel deleteEndUserFolderRequest = new StopSharingRequestModel(documentIdslist,category_id);

            String request = new Gson().toJson(deleteEndUserFolderRequest);


            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetEndUserParentSHaredFoldersService mGetEndUserParentSHaredstopService = retrofitAPI.create(GetEndUserParentSHaredFoldersService.class);

            Call call = mGetEndUserParentSHaredstopService.getEndUserStopSharedDocuments(params, PreferenceUtils.getAccessToken(context));

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

                        CommonFunctions.isApiSuccess(context, message, response.body().getStatus().getCode());

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }



    public void renamedocument(String object_id,String name,String doc_created,String auth)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            ArrayList<EditDocumentResponse> documentResponses = new ArrayList<>();
            final EditDocumentPropertiesRequest editDocumentPropertiesRequest = new EditDocumentPropertiesRequest(object_id,name,doc_created,auth, documentResponses);

            String request = new Gson().toJson(editDocumentPropertiesRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final EditDocumentPropertiesService editDocumentPropertiesService = retrofitAPI.create(EditDocumentPropertiesService.class);

            Call call = editDocumentPropertiesService.getRenameDocument(params, PreferenceUtils.getAccessToken(context));

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

                        if(CommonFunctions.isApiSuccess(context, message, apiResponse.status.getCode())) {
                            Toast.makeText(context, "Document renamed successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }




    @Override
    public int getItemCount() {
        return mGetCategoryDocumentsResponses.size();
    }


    public void getDownloadurlFromServiceSingleDocument(GetCategoryDocumentsResponse documentsResponse)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {
            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();
            final DownloadDocumentService downloadDocumentService = retrofitAPI.create(DownloadDocumentService.class);

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            //DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(PreferenceUtils.getDocumentVersionId(this));
            List<String> strlist = new ArrayList<>();
            strlist.add(documentsResponse.getObject_id());
            DownloadDocumentRequest downloadDocumentRequest = new DownloadDocumentRequest(strlist, documentsResponse.getIs_shared());
            final String request = new Gson().toJson(downloadDocumentRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            Call call = downloadDocumentService.download(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ApiResponse<DownloadDocumentResponse>>() {
                @Override
                public void onResponse(Response<ApiResponse<DownloadDocumentResponse>> response, Retrofit retrofit) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();
                        String message = "";
                        if(apiResponse.status.getMessage() != null)
                        {
                            message = apiResponse.status.getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(context, message, apiResponse.status.getCode())) {
                            DownloadDocumentResponse downloadDocumentResponse = response.body().getData();

                            String downloaded_url = downloadDocumentResponse.getData();

                            String access_Token = PreferenceUtils.getAccessToken(context);

                            byte[] encodeValue = Base64.encode(access_Token.getBytes(), Base64.DEFAULT);
                            String base64AccessToken = new String(encodeValue);

                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                StrictMode.ThreadPolicy policy =  new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                            }

                            documentsResponse.setDownloadUrl(downloaded_url+"&token="+base64AccessToken);
                            List<GetCategoryDocumentsResponse> downloadingList = new ArrayList<>();
                            downloadingList.add(documentsResponse);
                            getDownloadManagerForDownloading(downloadingList);

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

    private void showSessionExpiryAlert(String mMessage)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Alert");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

        txtMessage.setText(mMessage);

        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setVisibility(View.GONE);

        sendPinButton.setText("OK");

        sendPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                AccountSettings accountSettings = new AccountSettings(context);
                accountSettings.deleteAll();
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }



    private void getDownloadManagerForDownloading(List<GetCategoryDocumentsResponse> downloadingUrlDataList)
    {
        LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
        transparentProgressDialog.show();
        for (final GetCategoryDocumentsResponse digitalAsset : downloadingUrlDataList) {
            if (!TextUtils.isEmpty(digitalAsset.getDownloadUrl())) {
                FileDownloadManager fileDownloadManager = new FileDownloadManager(context);
                fileDownloadManager.setFileTitle(digitalAsset.getName());
                fileDownloadManager.setDownloadUrl(digitalAsset.getDownloadUrl());
                fileDownloadManager.setDigitalAssets(digitalAsset);
                fileDownloadManager.setmFileDownloadListener(new FileDownloadManager.FileDownloadListener() {
                    @Override
                    public void fileDownloadSuccess(String path) {

                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                        if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(digitalAsset.getDocument_version_id())) {
                            String filepath = offLine_files_repository.getFilePathFromLocalTable(digitalAsset.getDocument_version_id());
                            if(filepath != null && !filepath.isEmpty())
                            {
                                CommonFunctions.deleteFileFromInternalStorage(filepath);
                            }
                            offLine_files_repository.deleteAlreadydownloadedFile(digitalAsset.getDocument_version_id());
                            insertIntoOffLineFilesTable(digitalAsset, path);
                        }
                        else
                        {
                            insertIntoOffLineFilesTable(digitalAsset, path);
                        }

                        transparentProgressDialog.dismiss();

                    }

                    @Override
                    public void fileDownloadFailure() {

                    }
                });
                fileDownloadManager.downloadTheFile();
            }
        }

    }


    private void insertIntoOffLineFilesTable(GetCategoryDocumentsResponse digitalAsset, String path)
    {
        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
        OfflineFiles offlineFilesModel = new OfflineFiles();
        offlineFilesModel.setDocumentId(digitalAsset.getObject_id());
        offlineFilesModel.setDocumentName(digitalAsset.getName());
        offlineFilesModel.setDocumentVersionId(digitalAsset.getDocument_version_id());
        offlineFilesModel.setDownloadDate(DateHelper.getCurrentDate());
        offlineFilesModel.setFilename(digitalAsset.getName());
        offlineFilesModel.setFilePath(path);
        offlineFilesModel.setFiletype(digitalAsset.getFiletype());
        offlineFilesModel.setFileSize(digitalAsset.getFilesize());
        offlineFilesModel.setVersionNumber(digitalAsset.getVersion_number());
        offlineFilesModel.setSource(digitalAsset.getDoc_status());

        offLine_files_repository.InsertOfflineFilesData(offlineFilesModel);
    }



    public void deleteDocumentsService(String deleteMode)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();



            ArrayList<DeleteDocumentRequest> docsList = new ArrayList<>();

            for(GetCategoryDocumentsResponse category : GlobalVariables.selectedDocumentsList)
            {
                if (category.getType().equalsIgnoreCase("document"))
                {
                    ArrayList<String> documentVersionIdList = new ArrayList<>();
                    documentVersionIdList.add(category.getDocument_version_id());
                    DeleteDocumentRequest deleteDocumentRequest= new DeleteDocumentRequest();
                    deleteDocumentRequest.setDoc_id(category.getObject_id());
                    deleteDocumentRequest.setDoc_version_ids(documentVersionIdList);
                    deleteDocumentRequest.setMode(deleteMode);

                    docsList.add(deleteDocumentRequest);

                }

            }

            final DeleteDocumentRequest.DeleteDocRequest deleteDocRequest = new DeleteDocumentRequest.DeleteDocRequest(docsList);

            String request = new Gson().toJson(deleteDocRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final DeleteDocumentService deleteDocumentService = retrofitAPI.create(DeleteDocumentService.class);

            Call call = deleteDocumentService.delete_eu_document(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<DeleteDocumentResponseModel>() {
                @Override
                public void onResponse(Response<DeleteDocumentResponseModel> response, Retrofit retrofit) {
                    DeleteDocumentResponseModel apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        String message = "";
                        if(apiResponse.getStatus().getMessage() != null)
                        {
                            message = apiResponse.getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(context, message, apiResponse.getStatus().getCode())) {
                            Toast.makeText(context, "Document deleted successfully", Toast.LENGTH_SHORT).show();

                            if(deleteMode.equals("0"))
                            {
                                for(GetCategoryDocumentsResponse category : GlobalVariables.selectedDocumentsList)
                                {
                                    if (category.getType().equalsIgnoreCase("document"))
                                    {
                                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                                        String filepath = offLine_files_repository.getFilePathFromLocalTable(category.getDocument_version_id());
                                        if(filepath != null && !filepath.isEmpty())
                                        {
                                            CommonFunctions.deleteFileFromInternalStorage(filepath);
                                        }

                                        offLine_files_repository.deleteAlreadydownloadedFile(category.getDocument_version_id());

                                    }

                                }
                            }
                            else if(deleteMode.equals("1"))
                            {
                                for(GetCategoryDocumentsResponse category : GlobalVariables.selectedDocumentsList)
                                {
                                    if (category.getType().equalsIgnoreCase("document"))
                                    {
                                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
                                        List<OfflineFiles> offlineFileList = offLine_files_repository.getFilePathFromLocalTableBasedUponCondition(category.getDocument_version_id(),
                                                category.getObject_id());

                                        if(offlineFileList != null && offlineFileList.size() > 0)
                                        {
                                            for(OfflineFiles offlineFilesModel : offlineFileList)
                                            {
                                                CommonFunctions.deleteFileFromInternalStorage(offlineFilesModel.getFilePath());
                                            }

                                        }

                                        offLine_files_repository.deleteAlreadydownloadedFileBasedUPonCondition(category.getDocument_version_id(), category.getObject_id());

                                    }

                                }
                            }
                            else if(deleteMode.equals("2"))
                            {
                                for(GetCategoryDocumentsResponse category : GlobalVariables.selectedDocumentsList)
                                {
                                    if (category.getType().equalsIgnoreCase("document"))
                                    {
                                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);

                                        List<OfflineFiles> offlineFileList = offLine_files_repository.getFilePathFromLocalTableBasedOnVersionId(category.getObject_id());

                                        if(offlineFileList != null && offlineFileList.size() > 0)
                                        {
                                            for(OfflineFiles offlineFilesModel : offlineFileList)
                                            {
                                                CommonFunctions.deleteFileFromInternalStorage(offlineFilesModel.getFilePath());
                                            }

                                        }

                                        offLine_files_repository.deleteAlreadydownloadedFileBasedOnVersionId(category.getObject_id());

                                    }
                                }
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();

                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }


}
