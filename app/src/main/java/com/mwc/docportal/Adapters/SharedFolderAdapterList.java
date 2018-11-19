package com.mwc.docportal.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
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
import com.mwc.docportal.API.Model.ColorCodeModel;
import com.mwc.docportal.API.Model.DocumentPreviewRequest;
import com.mwc.docportal.API.Model.EditDocumentPropertiesRequest;
import com.mwc.docportal.API.Model.EditDocumentResponse;
import com.mwc.docportal.API.Model.EndUserRenameRequest;
import com.mwc.docportal.API.Model.GetCategoryDocumentsRequest;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.PdfDocumentResponseModel;
import com.mwc.docportal.API.Model.SharedDocumentResponseModel;
import com.mwc.docportal.API.Model.StopSharingRequestModel;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.DocumentPreviewService;

import com.mwc.docportal.API.Service.GetEndUserParentSHaredFoldersService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.GlobalVariables;
import com.mwc.docportal.DMS.MyFolderSharedDocuments;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.DMS.NavigationSharedActivity;
import com.mwc.docportal.DMS.Tab_Activity;
import com.mwc.docportal.DMS.UploadListActivity;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.OffLine_Files_Repository;
import com.mwc.docportal.Database.PushNotificatoinSettings_Respository;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;
import com.mwc.docportal.pdf.PdfViewActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SharedFolderAdapterList extends RecyclerView.Adapter<SharedFolderAdapterList.ViewHolder> {


    ArrayList<String> doc_id = new ArrayList<String>();
    final Activity context;
    private List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    private List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses;
    AlertDialog mAlertDialog;

    /*boolean isMultiSelect = false;
    List<GetCategoryDocumentsResponse> selectedList = new ArrayList<>();*/
    String objectr;
    String categoryr;
    String parentr;
    String document;
    String version_number;
    ArrayList<String>document_id= new ArrayList<>();
    ArrayList <String> document_version_id= new ArrayList<>();
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    String objectId;
    boolean isFromSecondlevel;
//    int selectedCountValue = 0;

    PdfDocumentResponseModel getDocumentPreviewResponses;
    public SharedFolderAdapterList(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses, Activity context, String objectId, boolean isFromSecondLevel) {
        this.context = context;
        this.mGetCategoryDocumentsResponses = getCategoryDocumentsResponses;
        this.objectId = objectId;
        this.isFromSecondlevel = isFromSecondLevel;
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView, selectedItemIv, thumbnailIcon, thumbnailCornerIcon, imageMore;
        public TextView folder_name, unread_count_txt;
        public View layout;
        public TextView folder_date, thumbnailText;
        ViewHolder vh;
        RelativeLayout indicatorParentView, folderView, thumbnailView,foldernext, list_item_click;
        TextView indicatorTextValue;


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
            foldernext=(RelativeLayout)itemView.findViewById(R.id.folder_rl_2);
            list_item_click = (RelativeLayout) itemView.findViewById(R.id.list_item_click);
            unread_count_txt = (TextView) mView.findViewById(R.id.unread_count);

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

        final GetCategoryDocumentsResponse resp = mGetCategoryDocumentsResponses.get(position);

        if(mGetCategoryDocumentsResponses != null && mGetCategoryDocumentsResponses.size() > 0) {

            PreferenceUtils.setDocumentVersionId(context, mGetCategoryDocumentsResponses.get(position).getDocument_version_id());
            PreferenceUtils.setDocument_Id(context, mGetCategoryDocumentsResponses.get(position).getObject_id());

            setButtonBackgroundColor();

            if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                holder.folderView.setVisibility(View.VISIBLE);
                holder.thumbnailView.setVisibility(View.GONE);
                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String folderColor = mWhiteLabelResponses.get(0).getFolder_Color();
                    int itemFolderColor = Color.parseColor(folderColor);

                    if (folderColor != null) {
                        holder.imageView.setColorFilter(itemFolderColor);
                    } else {
                        holder.imageView.setImageResource(R.mipmap.ic_folder);
                    }
                }

                final String createdDate = mGetCategoryDocumentsResponses.get(position).getCreated_date();
                holder.folder_date.setText("Uploaded on "+createdDate);

                
               /* if(mGetCategoryDocumentsResponses.get(position).getUnread_count() > 0)
                {
                    holder.unread_count_txt.setVisibility(View.VISIBLE);
                    if(mGetCategoryDocumentsResponses.get(position).getUnread_count() > 99)
                    {
                        holder.unread_count_txt.setText("99+");
                    }
                    else
                    {
                        holder.unread_count_txt.setText(String.valueOf(mGetCategoryDocumentsResponses.get(position).getUnread_count()));
                    }
                    holder.folder_name.setTypeface(holder.folder_name.getTypeface(), Typeface.BOLD);
                }
                else {
                    holder.unread_count_txt.setVisibility(View.GONE);
                    holder.folder_name.setTypeface(holder.folder_name.getTypeface(), Typeface.NORMAL);
                }*/


            } else if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
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

                /*if(mGetCategoryDocumentsResponses.get(position).getViewed() != null && mGetCategoryDocumentsResponses.get(position).getViewed().equalsIgnoreCase("No"))
                {
                    holder.folder_name.setTypeface(holder.folder_name.getTypeface(), Typeface.BOLD);
                }
                else {
                    holder.folder_name.setTypeface(holder.folder_name.getTypeface(), Typeface.NORMAL);
                }*/

            }

            final String name = mGetCategoryDocumentsResponses.get(position).getName();
            holder.folder_name.setText(name);


            if(GlobalVariables.sharedDocsSortType.equalsIgnoreCase("name"))
            {
                holder.indicatorParentView.setVisibility(View.VISIBLE);
                if(position > 0){
                    char poschar = resp.getName().toUpperCase().charAt(0);
                    char prevchar = mGetCategoryDocumentsResponses.get(position-1).getName().toUpperCase().charAt(0);
                    if(poschar == prevchar){
                        holder.indicatorParentView.setVisibility(View.GONE);
                    }else{
                        holder.indicatorParentView.setVisibility(View.VISIBLE);
                        holder.indicatorTextValue.setText(String.valueOf(mGetCategoryDocumentsResponses.get(position).getName().charAt(0)));
                    }
                }else{
                    holder.indicatorParentView.setVisibility(View.VISIBLE);
                    holder.indicatorTextValue.setText(String.valueOf(mGetCategoryDocumentsResponses.get(position).getName().charAt(0)));
                }
            }
            else
            {
                holder.indicatorParentView.setVisibility(View.GONE);
            }

            holder.list_item_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GlobalVariables.isMultiSelect)
                    {
                        if(mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                            multi_select(position);
                        }
                    }
                    else {

                        if (mGetCategoryDocumentsResponses.get(position).getType() != null && mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                            PreferenceUtils.setObjectId(context, mGetCategoryDocumentsResponses.get(position).getObject_id());

                            if(GlobalVariables.isMoveInitiated && isFromSecondlevel)
                            {
                                showShareDocumentAlert("Are you sure?", mGetCategoryDocumentsResponses.get(position).getObject_id(),
                                        mGetCategoryDocumentsResponses.get(position).getCategory_id());
                                return;
                            }

                            Intent intent = new Intent(context, NavigationSharedActivity.class);
                            if(objectId.equals("0"))
                            {
                                intent.putExtra("isSecondLevel", true);
                            }
                            else
                            {
                                intent.putExtra("isSecondLevel", false);
                            }
                            intent.putExtra("ObjectId", mGetCategoryDocumentsResponses.get(position).getObject_id());
                            intent.putExtra("CategoryName", mGetCategoryDocumentsResponses.get(position).getName());
                            intent.putExtra("WorkSpaceId", mGetCategoryDocumentsResponses.get(position).getCategory_id());
                            context.startActivity(intent);

                            doc_id.add(mGetCategoryDocumentsResponses.get(position).getObject_id());
                        } else if (mGetCategoryDocumentsResponses.get(position).getType() != null && mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                            if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                                getDocumentPreviews(mGetCategoryDocumentsResponses.get(position));
                            }

                        }
                    }


                }
            });

            holder.list_item_click.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                        if (!GlobalVariables.isMoveInitiated) {
                            if (!GlobalVariables.isMultiSelect) {
                             //   selectedList = new ArrayList<>();
                                GlobalVariables.isMultiSelect = true;
                            }

                            multi_select(position);
                        }
                    }

                    return false;
                }
            });


            holder.imageMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                        openBottomSheetForDocument(mGetCategoryDocumentsResponses.get(position), mGetCategoryDocumentsResponses.get(position).getType(), mGetCategoryDocumentsResponses.get(position).getFiletype(), mGetCategoryDocumentsResponses.get(position).getName());
                        document=mGetCategoryDocumentsResponses.get(position).getObject_id();
                        parentr=mGetCategoryDocumentsResponses.get(position).getParent_id();
                        categoryr = mGetCategoryDocumentsResponses.get(position).getDocument_version_id();
                        document_id.add(document);
                        version_number=mGetCategoryDocumentsResponses.get(position).getVersion_count();
                        PreferenceUtils.saveArrayList(context,document_id,"Key");
                        PreferenceUtils.setDocumentVersionId(context, categoryr);
                        PreferenceUtils.setDocument_Id(context, document);
                        PreferenceUtils.setCategoryId(context,document);
                        document_version_id.add(categoryr);
                    }
                }
            });

            /*if(selectedList.contains(mGetCategoryDocumentsResponses.get(position)))
            {
                holder.selectedItemIv.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.selectedItemIv.setVisibility(View.GONE);
            }*/

            if(mGetCategoryDocumentsResponses.get(position).isSelected() == true)
            {
                holder.selectedItemIv.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.selectedItemIv.setVisibility(View.GONE);
            }

            holder.imageMore.setVisibility(View.VISIBLE);


            if(GlobalVariables.isMultiSelect == true || mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category"))
            {
                holder.imageMore.setVisibility(View.GONE);
            }

        }
    }

    private void showShareDocumentAlert(String message, String object_id, String category_id)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Share");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);
        txtMessage.setText(message);

        Button okButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("Cancel");

        okButton.setText("Share");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();

                if (context instanceof NavigationSharedActivity) {
                    ((NavigationSharedActivity) context).shareEndUserDocuments(object_id, category_id );
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
               /* GlobalVariables.isMoveInitiated = false;
                GlobalVariables.selectedActionName = "";
                GlobalVariables.selectedDocumentsList.clear();

                Intent intent = new Intent(context, NavigationMyFolderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
                context.finish();*/
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void multi_select(int position)
    {
       /* if (selectedList.contains(mGetCategoryDocumentsResponses.get(position))) {
            selectedList.remove(mGetCategoryDocumentsResponses.get(position));
            if (selectedList != null && selectedList.size() == 0) {
                selectedList.clear();
                isMultiSelect = false;
                notifyDataSetChanged();
            }
        } else {
            selectedList.add(mGetCategoryDocumentsResponses.get(position));
        }*/


        if(mGetCategoryDocumentsResponses.get(position).isSelected() == true)
        {
            mGetCategoryDocumentsResponses.get(position).setSelected(false);
            if(GlobalVariables.selectedCountValue > 0)
            {
                GlobalVariables.selectedCountValue--;
            }

            if(GlobalVariables.selectedCountValue == 0)
            {
                GlobalVariables.isMultiSelect = false;
                notifyDataSetChanged();
            }


        }
        else
        {
            mGetCategoryDocumentsResponses.get(position).setSelected(true);
            GlobalVariables.selectedCountValue++;
        }

        notifyDataSetChanged();

        List<GetCategoryDocumentsResponse> selectedUpdateList = new ArrayList<>();

        if(mGetCategoryDocumentsResponses != null && mGetCategoryDocumentsResponses.size() > 0)
        {
            for(GetCategoryDocumentsResponse categoryDocumentsResponse : mGetCategoryDocumentsResponses)
            {
                if(categoryDocumentsResponse.isSelected() == true)
                {
                    selectedUpdateList.add(categoryDocumentsResponse);
                }
            }
        }

        if (context instanceof NavigationSharedActivity) {
            ((NavigationSharedActivity) context).updateToolbarMenuItems(selectedUpdateList);
        }

    }

    public void clearAll()
    {
        if(mGetCategoryDocumentsResponses != null && mGetCategoryDocumentsResponses.size() > 0)
        {
            for(GetCategoryDocumentsResponse categoryDocumentsResponse : mGetCategoryDocumentsResponses)
            {
                if(categoryDocumentsResponse.isSelected() == true)
                {
                    categoryDocumentsResponse.setSelected(false);
                }
            }
        }
        GlobalVariables.selectedCountValue = 0;
        GlobalVariables.isMultiSelect = false;
        notifyDataSetChanged();
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
                                    transparentProgressDialog.dismiss();
                                    getDocumentPreviewResponses = response.body();
                                    String document_preview_url = getDocumentPreviewResponses.getData().getDocumentPdfUrl();

                                    Intent intent = new Intent(context, PdfViewActivity.class);
                                    intent.putExtra("mode",1);
                                    intent.putExtra("url", document_preview_url);
                                    intent.putExtra("documentDetails", categoryDocumentsResponse);
                                    intent.putExtra("IsFromShare", true);
                                    context.startActivity(intent);
                                }

                            } else if (apiResponse.getStatus().getCode() instanceof Double) {
                                transparentProgressDialog.dismiss();
                                double status_value = new Double(apiResponse.getStatus().getCode().toString());

                                if (status_value == 400.0)
                                {
                                    Intent intent = new Intent(context, PdfViewActivity.class);
                                    intent.putExtra("isFrom_Status400",true);
                                    intent.putExtra("documentDetails", categoryDocumentsResponse);
                                    intent.putExtra("IsFromShare", true);
                                    context.startActivity(intent);

                                }
                            }
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

    private void openBottomSheetForDocument(final GetCategoryDocumentsResponse categoryDocumentsResponse, String type, String fileType, String name) {

        View view = ((NavigationSharedActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_document_sort, null);
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


        rename_layout.setVisibility(View.GONE);
        move.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);

        if(categoryDocumentsResponse.getSharetype().equals("1"))
        {
            shareView.setVisibility(View.VISIBLE);
            switchButton_share.setChecked(true);
        }
        else
        {
            shareView.setVisibility(View.GONE);
        }


        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();


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
                    if (isChecked) {
                        switchButton_download.setChecked(true);
                        if (context instanceof NavigationSharedActivity) {
                            ((NavigationSharedActivity) context).getDownloadurlFromServiceSingleDocument(categoryDocumentsResponse);
                        }
                        mBottomSheetDialog.dismiss();

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
                        mBottomSheetDialog.dismiss();
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
                    switchButton_share.setChecked(false);
                   // showWarningMessageAlertForSharingContent(categoryDocumentsResponse);
                    ArrayList<String> documentIdslist = new ArrayList<>();
                    documentIdslist.add(categoryDocumentsResponse.getObject_id());
                    getInternalStoppingSharingContentAPI(categoryDocumentsResponse, documentIdslist, categoryDocumentsResponse.getCategory_id());
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



        doclayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                PreferenceUtils.setDocumentVersionId(context,categoryDocumentsResponse.getDocument_version_id());
                PreferenceUtils.setDocument_Id(context, categoryDocumentsResponse.getObject_id());
                Intent intent = new Intent (context,Tab_Activity.class);
                intent.putExtra("IsFromShared", true);
                intent.putExtra(Constants.DOCUMENT_NAME, categoryDocumentsResponse.getName());
                context.startActivity(intent);
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                GlobalVariables.moveOriginIndex = GlobalVariables.activityCount;

                GlobalVariables.isMoveInitiated = true;
                GlobalVariables.selectedActionName =  "copy";
                Intent intent = new Intent(context, NavigationMyFolderActivity.class);
            //    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("ObjectId", "0");
                context.startActivity(intent);
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


    @Override
    public int getItemCount() {
        return mGetCategoryDocumentsResponses.size();
    }

    public void refreshAdapterToView(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        this.mGetCategoryDocumentsResponses.clear();
        this.mGetCategoryDocumentsResponses.addAll(getCategoryDocumentsResponses);

        notifyDataSetChanged();
    }
    public ArrayList<String> getArrayList(){
        return doc_id;
    }
    public ArrayList<String> setArrayList(int k){
        doc_id.remove(k);
        return doc_id;
    }

    private void showWarningMessageAlertForSharingContent(final GetCategoryDocumentsResponse categoryDocumentsResponse)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Stop Sharing");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

      //  txtMessage.setText(context.getString(R.string.stop_sharing_text));
        AccountSettings accountSettings = new AccountSettings(context);
        String companyName = accountSettings.getCompanyName();
        txtMessage.setText("You are about to share this document with "+ companyName +". Are you sure you wish to proceed?");

        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("Cancel");

        sendPinButton.setText("Share");

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
                getInternalStoppingSharingContentAPI(categoryDocumentsResponse, documentIdslist, categoryDocumentsResponse.getCategory_id());


            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void getInternalStoppingSharingContentAPI(GetCategoryDocumentsResponse categoryDocumentsResponse, ArrayList<String> documentIdslist, String category_id)
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

                        /*mGetCategoryDocumentsResponses.remove(categoryDocumentsResponse);
                        notifyDataSetChanged();*/

                        if(CommonFunctions.isApiSuccess(context, message, response.body().getStatus().getCode()))
                        {
                            mGetCategoryDocumentsResponses.remove(categoryDocumentsResponse);
                            notifyDataSetChanged();

                            if(GlobalVariables.sharedDocumentList != null && GlobalVariables.sharedDocumentList.size() > 0) {

                                for(GetCategoryDocumentsResponse mcategoryDocumentsResponse : GlobalVariables.sharedDocumentList)
                                {
                                    if(mcategoryDocumentsResponse.getDocument_version_id().equalsIgnoreCase(categoryDocumentsResponse.getDocument_version_id()))
                                    {
                                        GlobalVariables.sharedDocumentList.remove(categoryDocumentsResponse);
                                    }
                                }
                            }
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

}
