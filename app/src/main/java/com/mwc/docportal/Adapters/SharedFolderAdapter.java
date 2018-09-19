package com.mwc.docportal.Adapters;

/**
 * Created by barath on 7/12/2018.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.PdfDocumentResponseModel;
import com.mwc.docportal.API.Model.SharedDocumentResponseModel;
import com.mwc.docportal.API.Model.StopSharingRequestModel;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.DocumentPreviewService;

import com.mwc.docportal.API.Service.EditDocumentPropertiesService;
import com.mwc.docportal.API.Service.EndUserRenameService;
import com.mwc.docportal.API.Service.GetEndUserParentSHaredFoldersService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Common.GlobalVariables;

import com.mwc.docportal.DMS.MyFolderSharedDocuments;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.DMS.NavigationSharedActivity;
import com.mwc.docportal.DMS.Tab_Activity;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Database.OffLine_Files_Repository;
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

public class SharedFolderAdapter extends RecyclerView.Adapter<SharedFolderAdapter.ViewHolder> {

    private Activity context;
    public List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    PdfDocumentResponseModel getDocumentPreviewResponses;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    public List<GetCategoryDocumentsResponse> selectedList = new ArrayList<>();
    private static final int GRID_ITEM = 0;
    private static final int LIST_ITEM = 1;
    String obj = "0";
    String categoryr;
    String document;
    ArrayList<String>document_id= new ArrayList<>();
    ArrayList <String> document_version_id= new ArrayList<>();
    String version_number;
    boolean isSwitchView = true;
    boolean isMultiSelect = false;
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



    public class ViewHolder extends RecyclerView.ViewHolder  {

        public LinearLayout parentLayout;
        public RelativeLayout gridClick, thumbnailLayout;
        public ImageView imageView, selectedItemIv, thumbnailIcon, thumbnailCornerIcon, moreIcon;
        public TextView text, thumbnailText;
        public View layout;
        ViewHolder vh;

        public ViewHolder(View itemView) {
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

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.file_item_grid, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        if (!isSwitchView) {
            return LIST_ITEM;
        } else {
            return GRID_ITEM;
        }
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {


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


            }

            final String name = mGetCategoryDocumentsResponses.get(position).getName();
            holder.text.setText(name);

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isMultiSelect)
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


                        } else if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
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
                            if (!isMultiSelect) {
                                selectedList = new ArrayList<>();
                                isMultiSelect = true;
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

            if(selectedList.contains(mGetCategoryDocumentsResponses.get(position)))
            {
                holder.selectedItemIv.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.selectedItemIv.setVisibility(View.GONE);
            }

            holder.moreIcon.setVisibility(View.VISIBLE);


            if(selectedList.size() > 0 || mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category"))
            {
                holder.moreIcon.setVisibility(View.GONE);
            }

        }

    }

    public void multi_select(int position)
    {

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
        }

        notifyDataSetChanged();

        if (context instanceof NavigationSharedActivity) {
            ((NavigationSharedActivity) context).updateToolbarMenuItems(selectedList);
        }



    }



    public void clearAll()
    {
        selectedList.clear();
        isMultiSelect = false;
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
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
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


        shareView.setVisibility(View.GONE);
        rename_layout.setVisibility(View.GONE);
        move.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);


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
                context.startActivity(intent);
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();

                GlobalVariables.isMoveInitiated = true;
                GlobalVariables.selectedActionName =  "copy";
                Intent intent = new Intent(context, NavigationMyFolderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                if(buttonView.isPressed() == true) {
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
                        offLine_files_repository.deleteAlreadydownloadedFile(mGetCategoryDocumentsResponses.getDocument_version_id());
                        String filepath = offLine_files_repository.getFilePathFromLocalTable(mGetCategoryDocumentsResponses.getDocument_version_id());

                        if(filepath != null && !filepath.isEmpty())
                        {
                            CommonFunctions.deleteFileFromInternalStorage(filepath);
                        }

                        switchButton_download.setChecked(false);
                        mBottomSheetDialog.dismiss();
                    }


                }
            }
        });


    }






    public static Drawable changeDrawableColor(Context context, int icon, int itemFolderColor) {
        Drawable mDrawable = ContextCompat.getDrawable(context, icon).mutate();
        mDrawable.setColorFilter(new PorterDuffColorFilter(itemFolderColor, PorterDuff.Mode.SRC_IN));
        return mDrawable;
    }

}
