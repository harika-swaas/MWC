package com.mwc.docportal.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.InputFilter;
import android.text.InputType;
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
import com.mwc.docportal.API.Model.DeleteEndUserFolderRequest;
import com.mwc.docportal.API.Model.DocumentPreviewRequest;
import com.mwc.docportal.API.Model.DocumentPreviewResponse;
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
import com.mwc.docportal.API.Service.DeleteDocumentService;
import com.mwc.docportal.API.Service.DeleteEndUserFolderService;
import com.mwc.docportal.API.Service.DocumentPreviewService;
import com.mwc.docportal.API.Service.EditDocumentPropertiesService;
import com.mwc.docportal.API.Service.EndUserRenameService;
import com.mwc.docportal.API.Service.GetCategoryDocumentsService;
import com.mwc.docportal.API.Service.GetEndUserParentSHaredFoldersService;
import com.mwc.docportal.Common.CommonFunctions;
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
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;
import com.mwc.docportal.pdf.PdfViewActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DmsAdapterList extends RecyclerView.Adapter<DmsAdapterList.ViewHolder> {

    MyFoldersDMSActivity myFoldersDMSActivity;
    ArrayList<String> doc_id = new ArrayList<String>();
    final Activity context;
    private List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    private List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses;
    AlertDialog mAlertDialog;

  //  boolean isMultiSelect = false;
  //  List<GetCategoryDocumentsResponse> selectedList = new ArrayList<>();
    String objectr;
    String categoryr;
    String parentr;
    String document;
    String version_number;
    ArrayList<String>document_id= new ArrayList<>();

    AlertDialog mBackDialog;

    ArrayList <String> document_version_id= new ArrayList<>();
    ArrayList<String> categoryids = new ArrayList<String>();


    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();

    PdfDocumentResponseModel getDocumentPreviewResponses;
  //  int selectedCountValue = 0;
    public DmsAdapterList(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses, Activity context) {
        this.context = context;
        this.mGetCategoryDocumentsResponses = getCategoryDocumentsResponses;

    }




    // ----------------------
    // Click Listener
    // ----------------------



    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView, selectedItemIv, thumbnailIcon, thumbnailCornerIcon, imageMore;
        public TextView folder_name;
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
          /*  this.itemView.setOnClickListener(this);
            this.itemView.setOnLongClickListener(this);*/
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

            }

            final String name = mGetCategoryDocumentsResponses.get(position).getName();
            holder.folder_name.setText(name);



            if(GlobalVariables.sortType.equalsIgnoreCase("name"))
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
                        if(mGetCategoryDocumentsResponses.get(position).getPermission().isCanViewDocument()) {
                            multi_select(position);
                        }
                    }
                    else {

                        if (mGetCategoryDocumentsResponses.get(position).getType() != null && mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {

                            if(!mGetCategoryDocumentsResponses.get(position).getPermission().isCanViewDocument())
                            {
                                if (context instanceof NavigationMyFolderActivity) {
                                    ((NavigationMyFolderActivity) context).showUnAuthorizedMessageAlert("You are not authorised to perform this action.");
                                }
                                return;
                            }

                            PreferenceUtils.setObjectId(context, mGetCategoryDocumentsResponses.get(position).getObject_id());
                            Intent intent = new Intent(context, NavigationMyFolderActivity.class);
                            intent.putExtra("ObjectId", mGetCategoryDocumentsResponses.get(position).getObject_id());
                            intent.putExtra("CategoryName",mGetCategoryDocumentsResponses.get(position).getName());
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
                    if(mGetCategoryDocumentsResponses.get(position).getPermission().isCanViewDocument()) {
                        if (!GlobalVariables.isMoveInitiated) {
                            if (!GlobalVariables.isMultiSelect) {
                               // selectedList = new ArrayList<>();
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
                    if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                        openBottomSheetForCategory(mGetCategoryDocumentsResponses.get(position), mGetCategoryDocumentsResponses.get(position).getName());
                        objectr = mGetCategoryDocumentsResponses.get(position).getParent_id();
                        categoryr = mGetCategoryDocumentsResponses.get(position).getObject_id();
                        PreferenceUtils.setCategoryId(context,categoryr);
                        document=mGetCategoryDocumentsResponses.get(position).getObject_id();
                        document_id.add(document);
                        PreferenceUtils.saveArrayList(context,document_id,"Key");

                    } else if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
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

            if(mGetCategoryDocumentsResponses.get(position).isSelected() == true)
            {
                holder.selectedItemIv.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.selectedItemIv.setVisibility(View.GONE);
            }

            holder.imageMore.setVisibility(View.VISIBLE);

            if(GlobalVariables.isMultiSelect == true || !mGetCategoryDocumentsResponses.get(position).getPermission().isCanViewDocument() || GlobalVariables.isMoveInitiated)
            {
                holder.imageMore.setVisibility(View.GONE);
            }

        }
    }

    private void multi_select(int position)
    {
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

            if (context instanceof NavigationMyFolderActivity) {
                ((NavigationMyFolderActivity) context).updateToolbarMenuItems(selectedUpdateList);
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

        View view = ((NavigationMyFolderActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_document_sort, null);
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
                    if (isChecked) {

                        switchButton_download.setChecked(true);
                        if (context instanceof NavigationMyFolderActivity) {
                            ((NavigationMyFolderActivity) context).getDownloadurlFromServiceSingleDocument(categoryDocumentsResponse);
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
                    if (!isChecked) {
                        switchButton_share.setChecked(false);
                        showWarningMessageAlertForSharingContent(categoryDocumentsResponse);

                    } else {
                        switchButton_share.setChecked(true);
                        if (context instanceof NavigationMyFolderActivity) {
                            ((NavigationMyFolderActivity) context).showInternalShareAlertMessage();
                        }

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
                if (context instanceof NavigationMyFolderActivity) {
                    ((NavigationMyFolderActivity) context).assigningMoveOriginIndex();
                    ((NavigationMyFolderActivity) context).initiateMoveAction("move");
                }
            }
        });

        doclayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                PreferenceUtils.setDocumentVersionId(context,categoryDocumentsResponse.getDocument_version_id());
                PreferenceUtils.setDocument_Id(context, categoryDocumentsResponse.getObject_id());
                Intent intent = new Intent (context,Tab_Activity.class);
                intent.putExtra("IsFromMyFolder", true);
                intent.putExtra(Constants.DOCUMENT_NAME, categoryDocumentsResponse.getName());
                context.startActivity(intent);
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                if (context instanceof NavigationMyFolderActivity) {
                    ((NavigationMyFolderActivity) context).assigningMoveOriginIndex();
                    ((NavigationMyFolderActivity) context).initiateMoveAction("copy");
                }
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
                String abc = PreferenceUtils.getCategoryId(context);
                if(version_number.equals("0"))
                {
                    delete_historic.setEnabled(false);
                    delete_historic.setTextColor(R.color.grey);
                    delete_all.setEnabled(false);
                    delete_all.setTextColor(R.color.grey);

                }
                categoryids.add(abc);

                mBackDialog = builder.create();
                mBackDialog.show();
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();

                        if (context instanceof NavigationMyFolderActivity) {
                            ((NavigationMyFolderActivity) context).deleteDocumentsService( "0");
                        }
                    }
                });


                delete_historic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();

                        if (context instanceof NavigationMyFolderActivity) {
                            ((NavigationMyFolderActivity) context).deleteDocumentsService( "1");
                        }

                    }
                });

                delete_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();

                        if (context instanceof NavigationMyFolderActivity) {
                            ((NavigationMyFolderActivity) context).deleteDocumentsService( "2");
                        }

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

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(45);
                namer.setFilters(FilterArray);
                namer.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                allow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String folder = namer.getText().toString().trim();

                        if(folder != null && !folder.isEmpty())
                        {
                            renamedocument(categoryr,folder,"","");
                            mAlertDialog.dismiss();
                            mBottomSheetDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(context, context.getString(R.string.newname_txt), Toast.LENGTH_SHORT).show();
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

      //  txtMessage.setText("This action will stop sharing the selected document(s). Company with whom this has been shared will no longer be able to view this document");
        txtMessage.setText(context.getString(R.string.stop_sharing_text));
        Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

        cancelButton.setText("Cancel");

        sendPinButton.setText("Ok");

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

                        if(CommonFunctions.isApiSuccess(context, message, response.body().getStatus().getCode()))
                        {
                            if (context instanceof NavigationMyFolderActivity) {
                                ((NavigationMyFolderActivity) context).resetPageNumber();
                                ((NavigationMyFolderActivity) context).getCategoryDocuments();
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


    private void openBottomSheetForCategory(GetCategoryDocumentsResponse categoryDocumentsResponse, String name) {

        getWhiteLabelProperties();

        View view = ((NavigationMyFolderActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_category_sort, null);

        TextView categoryText = (TextView) view.findViewById(R.id.category_text);
        ImageView categoryImage = (ImageView) view.findViewById(R.id.category_image);
        ImageView moveImage = (ImageView) view.findViewById(R.id.move_image);
        ImageView renameImage = (ImageView) view.findViewById(R.id.rename_image);
        RelativeLayout rename = (RelativeLayout)view.findViewById(R.id.rename1);
        RelativeLayout move = (RelativeLayout)view.findViewById(R.id.move1);
        TextView delete = (TextView) view.findViewById(R.id.delete);
        RelativeLayout share = (RelativeLayout)view.findViewById(R.id.share1);

        List<GetCategoryDocumentsResponse> categoryDocumentlist = new ArrayList<>();
        categoryDocumentlist.add(categoryDocumentsResponse);
        CommonFunctions.setSelectedItems(categoryDocumentlist);


        categoryText.setText(name);
        if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
            String folderColor = mWhiteLabelResponses.get(0).getFolder_Color();
            int itemFolderColor = Color.parseColor(folderColor);

            String itemSelectedColor = mWhiteLabelResponses.get(0).getItem_Selected_Color();
            int selectedColor = Color.parseColor(itemSelectedColor);

            if (folderColor != null) {
                categoryImage.setColorFilter(itemFolderColor);
            } else {
                categoryImage.setImageResource(R.mipmap.ic_folder);
            }

            moveImage.setColorFilter(selectedColor);
            renameImage.setColorFilter(selectedColor);
        }

        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                if (context instanceof NavigationMyFolderActivity) {
                    ((NavigationMyFolderActivity) context).assigningMoveOriginIndex();
                    ((NavigationMyFolderActivity) context).initiateMoveAction("move");
                }

            }
        });

        rename.setOnClickListener(new View.OnClickListener() {
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

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(45);
                namer.setFilters(FilterArray);
                namer.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                allow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String folder = namer.getText().toString().trim();

                        if(folder != null && !folder.isEmpty())
                        {
                            rename(categoryr,folder,objectr);
                            mAlertDialog.dismiss();
                            mBottomSheetDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(context, context.getString(R.string.newname_txt), Toast.LENGTH_SHORT).show();
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



        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.delete_alert, null);
                builder.setView(view);
                builder.setCancelable(false);
                Button delete = (Button)view.findViewById(R.id.delete);
                Button move_to_folder=(Button)view.findViewById(R.id.movefolder);
                Button cancel = (Button)view.findViewById(R.id.canceldel);
                String abc = PreferenceUtils.getCategoryId(context);
                categoryids.add(abc);

                mBackDialog = builder.create();
                mBackDialog.show();


                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavigationMyFolderActivity.mode = 0;
                        mBackDialog.dismiss();


                        if (context instanceof NavigationMyFolderActivity) {
                            ((NavigationMyFolderActivity) context).delete_Folder_DeleteDocuments();
                        }


                    }
                });


                move_to_folder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavigationMyFolderActivity.mode = 1;
                        mBackDialog.dismiss();
                        if (context instanceof NavigationMyFolderActivity) {
                            ((NavigationMyFolderActivity) context).assigningMoveOriginIndex();
                            ((NavigationMyFolderActivity) context).initiateMoveAction("delete");
                        }

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

    }

    public void rename(String object_id,String name,String parentid)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final EndUserRenameRequest endUserRenameRequest = new EndUserRenameRequest(object_id,name,parentid);

            String request = new Gson().toJson(endUserRenameRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final EndUserRenameService endUserRenameService = retrofitAPI.create(EndUserRenameService.class);

            Call call = endUserRenameService.getRename(params, PreferenceUtils.getAccessToken(context));

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
                            if (context instanceof NavigationMyFolderActivity) {
                                ((NavigationMyFolderActivity) context).resetPageNumber();
                                ((NavigationMyFolderActivity) context).getCategoryDocuments();
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
                            if (context instanceof NavigationMyFolderActivity) {
                                ((NavigationMyFolderActivity) context).resetPageNumber();
                                ((NavigationMyFolderActivity) context).getCategoryDocuments();
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
}
