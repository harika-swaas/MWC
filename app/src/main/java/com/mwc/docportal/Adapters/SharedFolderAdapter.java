package com.mwc.docportal.Adapters;

/**
 * Created by barath on 7/12/2018.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.ColorCodeModel;
import com.mwc.docportal.API.Model.DocumentPreviewRequest;

import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.PdfDocumentResponseModel;
import com.mwc.docportal.API.Model.SharedDocumentResponseModel;
import com.mwc.docportal.API.Model.StopSharingRequestModel;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.DocumentPreviewService;

import com.mwc.docportal.API.Service.GetEndUserParentSHaredFoldersService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.GlobalVariables;

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
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SharedFolderAdapter extends RecyclerView.Adapter<SharedFolderAdapter.SharedItemHolder> {

    private Activity context;
    public List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    PdfDocumentResponseModel getDocumentPreviewResponses;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
  //  public List<GetCategoryDocumentsResponse> selectedList = new ArrayList<>();

    String obj = "0";
    String categoryr;
    String document;
    ArrayList<String>document_id= new ArrayList<>();
    ArrayList <String> document_version_id= new ArrayList<>();
    String version_number;
    boolean isSwitchView = true;
 //   boolean isMultiSelect = false;
    AlertDialog mAlertDialog;
    String objectId;

    public SharedFolderAdapter(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses, Activity context, String objectId) {
        this.context = context;
        this.mGetCategoryDocumentsResponses = getCategoryDocumentsResponses;
        this.objectId = objectId;
    }

    private void setButtonBackgroundColor() {

        getWhiteLabelProperities();
    }

    private void getWhiteLabelProperities() {

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



    public class SharedItemHolder extends RecyclerView.ViewHolder  {

        public LinearLayout parentLayout;
        public RelativeLayout gridClick, thumbnailLayout;
        public ImageView imageView, selectedItemIv, thumbnailIcon, thumbnailCornerIcon, moreIcon;
        public TextView text, thumbnailText, unread_Count_txt;
        public View layout;

        public SharedItemHolder(View itemView) {
            super(itemView);
            layout = itemView;
            parentLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout_parent);
            text = (TextView) itemView.findViewById(R.id.folder_name);
            thumbnailText = (TextView) itemView.findViewById(R.id.thumbnail_text);
            imageView = (ImageView) itemView.findViewById(R.id.folder);
            selectedItemIv = (ImageView) itemView.findViewById(R.id.selected_item);
            thumbnailIcon = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            thumbnailCornerIcon = (ImageView) itemView.findViewById(R.id.thumbnail_corner_image);
            gridClick = (RelativeLayout) itemView.findViewById(R.id.grid_click);
            thumbnailLayout = (RelativeLayout) itemView.findViewById(R.id.thumbnail_layout);
            moreIcon = (ImageView) itemView.findViewById(R.id.more_icon);
            unread_Count_txt = (TextView) itemView.findViewById(R.id.unread_count);

        }
    }

    @Override
    public SharedItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.file_item_grid, parent, false);
        SharedItemHolder vh = new SharedItemHolder(v);
        return vh;
    }



    @Override
    public int getItemCount() {
        return mGetCategoryDocumentsResponses.size();
    }

    public boolean toggleItemViewType() {
        isSwitchView = !isSwitchView;
        return isSwitchView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final SharedItemHolder holder, final int position) {

        final GetCategoryDocumentsResponse resp = mGetCategoryDocumentsResponses.get(position);
        if (mGetCategoryDocumentsResponses != null && mGetCategoryDocumentsResponses.size() > 0) {
            PreferenceUtils.setCategoryId(context, mGetCategoryDocumentsResponses.get(position).getParent_id());
            PreferenceUtils.setDocumentVersionId(context, mGetCategoryDocumentsResponses.get(position).getDocument_version_id());
            PreferenceUtils.setDocument_Id(context, mGetCategoryDocumentsResponses.get(position).getObject_id());
            setButtonBackgroundColor();

            if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                holder.imageView.setVisibility(View.VISIBLE);
                holder.thumbnailLayout.setVisibility(View.GONE);
                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String folderColor = mWhiteLabelResponses.get(0).getFolder_Color();
                    int itemFolderColor = Color.parseColor(folderColor);

                    if (folderColor != null) {
                        //  changeDrawableColor(context, R.mipmap.ic_folder, itemFolderColor);
                        holder.imageView.setColorFilter(itemFolderColor);
                    } else {
                        holder.imageView.setImageResource(R.mipmap.ic_folder);
                    }
                }

                if(resp.getUnread_doc_count() > 0)
                {
                    holder.unread_Count_txt.setVisibility(View.VISIBLE);
                    if(resp.getUnread_doc_count() > 99)
                    {
                        holder.unread_Count_txt.setText("99+");
                    }
                    else
                    {
                        holder.unread_Count_txt.setText(String.valueOf(resp.getUnread_doc_count()));
                    }
                    holder.text.setTypeface(holder.text.getTypeface(), Typeface.NORMAL);
                }
                else {
                    holder.unread_Count_txt.setVisibility(View.GONE);
                    holder.text.setTypeface(holder.text.getTypeface(), Typeface.NORMAL);
                }


            } else if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                holder.imageView.setVisibility(View.GONE);
                holder.thumbnailLayout.setVisibility(View.VISIBLE);
                ColorCodeModel colorCodeModel = CommonFunctions.getColorCodesforFileType(mGetCategoryDocumentsResponses.get(position).getFiletype());
                if(colorCodeModel != null)
                {

                    holder.thumbnailIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getPrimaryColor()));
                    holder.thumbnailCornerIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getSecondaryColor()));
                    holder.thumbnailText.setText(colorCodeModel.getFileType());
                }

                if(mGetCategoryDocumentsResponses.get(position).getViewed() != null && mGetCategoryDocumentsResponses.get(position).getViewed().equalsIgnoreCase("No")
                        && mGetCategoryDocumentsResponses.get(position).getSharetype() != null &&  mGetCategoryDocumentsResponses.get(position).getSharetype().equalsIgnoreCase("0"))
                {
                    holder.text.setTypeface(holder.text.getTypeface(), Typeface.BOLD);
                }
                else {
                    holder.text.setTypeface(holder.text.getTypeface(), Typeface.NORMAL);
                }

            }

            final String name = mGetCategoryDocumentsResponses.get(position).getName();
            holder.text.setText(name);



            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (GlobalVariables.isMultiSelect)
                    {
                        if(mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document"))
                        {
                            multi_select(position);
                        }
                    }
                    else {

                        if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                            PreferenceUtils.setObjectId(context, mGetCategoryDocumentsResponses.get(position).getObject_id());
                            obj = mGetCategoryDocumentsResponses.get(position).getObject_id();
                            PreferenceUtils.setParentId(context,obj);

                            //  For Badge count reduced from Root level
                            if(!TextUtils.isEmpty(mGetCategoryDocumentsResponses.get(position).getWorkspace_id()))
                            {
                                PreferenceUtils.setRootWorkspaceid(context, mGetCategoryDocumentsResponses.get(position).getWorkspace_id());
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


                        } else if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document") && !GlobalVariables.isMoveInitiated) {
                            getDocumentPreviews(mGetCategoryDocumentsResponses.get(position));

                        }
                    }

                }
            });

            holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document"))
                    {
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


            holder.moreIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                        openBottomSheetForDocument(mGetCategoryDocumentsResponses.get(position), mGetCategoryDocumentsResponses.get(position).getType(), mGetCategoryDocumentsResponses.get(position).getFiletype(), mGetCategoryDocumentsResponses.get(position).getName());
                        document=mGetCategoryDocumentsResponses.get(position).getObject_id();
                        categoryr = mGetCategoryDocumentsResponses.get(position).getDocument_version_id();
                        document_id.add(document);
                        version_number=mGetCategoryDocumentsResponses.get(position).getVersion_count();
                        PreferenceUtils.saveArrayList(context,document_id,"Key");
                        PreferenceUtils.setDocumentVersionId(context, categoryr);
                        document_version_id.add(categoryr);
                        PreferenceUtils.setDocument_Id(context, document);

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

            holder.moreIcon.setVisibility(View.VISIBLE);


            if(GlobalVariables.isMultiSelect == true || mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category"))
            {
                holder.moreIcon.setVisibility(View.GONE);
            }

        }

    }

    public void multi_select(int position)
    {
       /*
        if (selectedList.contains(mGetCategoryDocumentsResponses.get(position))) {
            selectedList.remove(mGetCategoryDocumentsResponses.get(position));
            if (selectedList != null && selectedList.size() == 0) {
                selectedList.clear();
                isMultiSelect = false;
                notifyDataSetChanged();

            }
        }
        else {
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
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

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
                                    intent.putExtra("IsFromShare", true);
                                    context.startActivity(intent);
                                }

                            } else if (apiResponse.getStatus().getCode() instanceof Double) {
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
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                }
            });
        }
    }

    private void openBottomSheetForDocument(final GetCategoryDocumentsResponse mGetCategoryDocumentsResponses, String type, String fileType, String name) {

        getWhiteLabelProperities();

        View view = ((NavigationSharedActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_document_sort, null);
        RelativeLayout shareView = (RelativeLayout) view.findViewById(R.id.share_layout);
        TextView docText = (TextView) view.findViewById(R.id.doc_text);
        ImageView thumbnailIcon = (ImageView) view.findViewById(R.id.thumbnail_image);
        RelativeLayout docinfo=(RelativeLayout) view.findViewById(R.id.doc_info_layout);
        RelativeLayout copy=(RelativeLayout) view.findViewById(R.id.copy1);
        RelativeLayout move=(RelativeLayout) view.findViewById(R.id.move1);
        ImageView thumbnailCornerIcon = (ImageView) view.findViewById(R.id.thumbnail_corner_image);
        TextView thumbnailText = (TextView) view.findViewById(R.id.thumbnail_text);
        TextView delete= (TextView)view.findViewById(R.id.delete);
        RelativeLayout rename_layout = (RelativeLayout) view.findViewById(R.id.rename_layout);

        final ImageView copyImage = (ImageView) view.findViewById(R.id.copy_image);
        ImageView moveImage = (ImageView) view.findViewById(R.id.move_image);
        ImageView renameImage = (ImageView) view.findViewById(R.id.rename_image);
        ImageView shareImage = (ImageView) view.findViewById(R.id.share_image);
        ImageView availableOfflineImage = (ImageView) view.findViewById(R.id.available_offline_image);
        final SwitchCompat switchButton_download = (SwitchCompat) view.findViewById(R.id.switchButton_download);
        final SwitchCompat switchButton_share = (SwitchCompat) view.findViewById(R.id.switchButton_share);



        List<GetCategoryDocumentsResponse> categoryDocumentlist = new ArrayList<>();
        categoryDocumentlist.add(mGetCategoryDocumentsResponses);
        CommonFunctions.setSelectedItems(categoryDocumentlist);

        rename_layout.setVisibility(View.GONE);
        move.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);

        if(mGetCategoryDocumentsResponses.getSharetype().equals("1"))
        {
            shareView.setVisibility(View.VISIBLE);
            switchButton_share.setChecked(true);
        }
        else
        {
            shareView.setVisibility(View.GONE);
        }



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


        ColorCodeModel colorCodeModel = CommonFunctions.getColorCodesforFileType(fileType);
        if(colorCodeModel != null)
        {
            thumbnailIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getPrimaryColor()));
            thumbnailCornerIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getSecondaryColor()));
            thumbnailText.setText(colorCodeModel.getFileType());
        }


        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
        docinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBottomSheetDialog.dismiss();
                PreferenceUtils.setDocumentVersionId(context,mGetCategoryDocumentsResponses.getDocument_version_id());
                PreferenceUtils.setDocument_Id(context, mGetCategoryDocumentsResponses.getObject_id());

                Intent intent = new Intent (context,Tab_Activity.class);
                intent.putExtra("IsFromShared", true);
                intent.putExtra(Constants.DOCUMENT_NAME, mGetCategoryDocumentsResponses.getName());
                context.startActivity(intent);
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                GlobalVariables.moveOriginIndex = GlobalVariables.activityCount;
                GlobalVariables.isMoveInitiated = true;
                GlobalVariables.selectedActionName =  "share_copy";
                Intent intent = new Intent(context, NavigationMyFolderActivity.class);
                intent.putExtra("ObjectId", "0");
                context.startActivity(intent);

            }
        });



        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
        if (!offLine_files_repository.checkAlreadyDocumentAvailableOrNot(mGetCategoryDocumentsResponses.getDocument_version_id())) {

            switchButton_download.setChecked(true);
        }
        else {
            switchButton_download.setChecked(false);
        }



        switchButton_download.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isClickable() == true) {
                    if (isChecked) {

                        switchButton_download.setChecked(true);
                        if (context instanceof NavigationSharedActivity) {
                            ((NavigationSharedActivity) context).getDownloadurlFromServiceSingleDocument(mGetCategoryDocumentsResponses);
                        }
                        mBottomSheetDialog.dismiss();

                    }
                    else
                    {
                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);

                        String filepath = offLine_files_repository.getFilePathFromLocalTable(mGetCategoryDocumentsResponses.getDocument_version_id());

                        if(filepath != null && !filepath.isEmpty())
                        {
                            CommonFunctions.deleteFileFromInternalStorage(filepath);
                        }
                        offLine_files_repository.deleteAlreadydownloadedFile(mGetCategoryDocumentsResponses.getDocument_version_id());
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
                if(buttonView.isClickable() == true) {
                    mBottomSheetDialog.dismiss();
                    switchButton_share.setChecked(false);
                    showWarningMessageAlertForSharingContent(mGetCategoryDocumentsResponses);
                }
            }
        });

    }


    private void showWarningMessageAlertForSharingContent(GetCategoryDocumentsResponse categoryDocumentsResponse)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText("Stop Sharing");

        TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

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
                getInternalStoppingSharingContentAPI(categoryDocumentsResponse, documentIdslist);


            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void getInternalStoppingSharingContentAPI(GetCategoryDocumentsResponse categoryDocumentsResponse, ArrayList<String> documentIdslist)
    {
        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();


            final StopSharingRequestModel deleteEndUserFolderRequest = new StopSharingRequestModel(documentIdslist,categoryDocumentsResponse.getShare_category_id());

            String request = new Gson().toJson(deleteEndUserFolderRequest);


            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetEndUserParentSHaredFoldersService mGetEndUserParentSHaredstopService = retrofitAPI.create(GetEndUserParentSHaredFoldersService.class);

            Call call = mGetEndUserParentSHaredstopService.getEndUserStopSharedDocuments(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<SharedDocumentResponseModel>() {
                @Override
                public void onResponse(Response<SharedDocumentResponseModel> response, Retrofit retrofit) {
                    transparentProgressDialog.dismiss();
                    if (response.body() != null) {
                        String message = "";
                        if(response.body().getStatus().getMessage() != null)
                        {
                            message = response.body().getStatus().getMessage().toString();
                        }

                        if(CommonFunctions.isApiSuccess(context, message, response.body().getStatus().getCode()))
                        {
                            mGetCategoryDocumentsResponses.remove(categoryDocumentsResponse);
                            notifyDataSetChanged();

                            if(GlobalVariables.sharedDocumentList != null && GlobalVariables.sharedDocumentList.size() > 0) {

                                for(GetCategoryDocumentsResponse mcategoryDocumentsResponse : GlobalVariables.sharedDocumentList)
                                {
                                    if(mcategoryDocumentsResponse.getObject_id() != null && mcategoryDocumentsResponse.getObject_id().equalsIgnoreCase(categoryDocumentsResponse.getObject_id()))
                                    {
                                        GlobalVariables.sharedDocumentList.remove(categoryDocumentsResponse);
                                        break;
                                    }
                                }
                            }

                            if (context instanceof NavigationSharedActivity) {
                                ((NavigationSharedActivity) context).toggleEmptyState();
                            }
                        }

                    }
                    else {
                        CommonFunctions.serverErrorExceptions(context, response.code());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    CommonFunctions.showTimeOutError(context, t);
                }
            });
        }
    }






    public static Drawable changeDrawableColor(Context context, int icon, int itemFolderColor) {
        Drawable mDrawable = ContextCompat.getDrawable(context, icon).mutate();
        mDrawable.setColorFilter(new PorterDuffColorFilter(itemFolderColor, PorterDuff.Mode.SRC_IN));
        return mDrawable;
    }

}
