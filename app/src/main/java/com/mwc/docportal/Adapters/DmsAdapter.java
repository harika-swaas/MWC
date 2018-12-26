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
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.mwc.docportal.API.Model.BaseApiResponseStatus;
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
import com.mwc.docportal.Common.GlobalVariables;

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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DmsAdapter extends RecyclerView.Adapter<DmsAdapter.ViewHolder> {
    private ArrayList<String> doc_id = new ArrayList<String>();
    private Activity context;
    List<GetCategoryDocumentsResponse> paginationList = new ArrayList<>();
    ArrayList<String> categoryids = new ArrayList<String>();
    public List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses = new ArrayList<>();
    PdfDocumentResponseModel getDocumentPreviewResponses;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    AlertDialog mAlertDialog;
   // private ItemClickListener mClickListener;

    private HashSet<Integer> mSelected;
 //   public List<GetCategoryDocumentsResponse> selectedList = new ArrayList<>();

    int pageNumber=1;
    int totalPage=1;
    String obj = "0";
    String objectr;
    String categoryr;
    String parentr;
    String document;
    ArrayList<String>document_id= new ArrayList<>();
    ArrayList <String> document_version_id= new ArrayList<>();
    String version_number;

    static Boolean isTouched = false;
    AlertDialog mBackDialog;
    boolean isSwitchView = true;
    int index=0;

 //   boolean isMultiSelect = false;

    private boolean mIsLoadingFooterAdded = false;
    List<GetCategoryDocumentsResponse> downloadingUrlDataList = new ArrayList<>();
  //  int selectedCountValue = 0;

    public DmsAdapter(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses, Activity context) {
        this.context = context;
        this.mGetCategoryDocumentsResponses = getCategoryDocumentsResponses;


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

    /*public void toggleSelection(int pos) {
        if (mSelected.contains(pos))
            mSelected.remove(pos);
        else
            mSelected.add(pos);
        notifyItemChanged(pos);
    }

    public void selectRange(int start, int end, boolean selected) {
        for (int i = start; i <= end; i++) {
            if (selected)
                mSelected.add(i);
            else
                mSelected.remove(i);
        }
        notifyItemRangeChanged(start, end - start + 1);
    }

    public HashSet<Integer> getSelection() {
        return mSelected;
    }*/

   /* public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        boolean onItemLongClick(View view, int position);
    }*/


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
          /*  this.parentLayout.setOnClickListener(this);
            this.parentLayout.setOnLongClickListener(this);*/
        }
/*
        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());


        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null)
                return mClickListener.onItemLongClick(view, getAdapterPosition());
            return false;
        }*/
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.file_item_grid, parent, false);
        ViewHolder vh = new ViewHolder(v);
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

                    if (GlobalVariables.isMultiSelect)
                    {
                        if(mGetCategoryDocumentsResponses.get(position).getPermission().isCanViewDocument()) {
                            multi_select(position);
                        }
                    }
                    else {

                        if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {

                            if(!mGetCategoryDocumentsResponses.get(position).getPermission().isCanViewDocument())
                            {
                                if (context instanceof NavigationMyFolderActivity) {
                                    ((NavigationMyFolderActivity) context).showUnAuthorizedMessageAlert("You are not authorised to perform this action.");
                                }
                                return;
                            }

                            PreferenceUtils.setObjectId(context, mGetCategoryDocumentsResponses.get(position).getObject_id());
                            obj = mGetCategoryDocumentsResponses.get(position).getObject_id();
                            PreferenceUtils.setParentId(context,obj);
                           /* MyFoldersDMSActivity.sort.setText("Name");
                            ItemNavigationFolderFragment.sortByName = true;*/
                           Intent intent = new Intent(context, NavigationMyFolderActivity.class);
                           intent.putExtra("ObjectId", mGetCategoryDocumentsResponses.get(position).getObject_id());
                           intent.putExtra("CategoryName", mGetCategoryDocumentsResponses.get(position).getName());
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
                    if(mGetCategoryDocumentsResponses.get(position).getPermission().isCanViewDocument()) {
                        if (!GlobalVariables.isMoveInitiated) {
                            if (!GlobalVariables.isMultiSelect) {
                              //  selectedList = new ArrayList<>();
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
                    if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                        openBottomSheetForCategory(mGetCategoryDocumentsResponses.get(position), mGetCategoryDocumentsResponses.get(position).getName());
                        objectr = mGetCategoryDocumentsResponses.get(position).getParent_id();
                        categoryr = mGetCategoryDocumentsResponses.get(position).getObject_id();
                        PreferenceUtils.setCategoryId(context,categoryr);
                        document=mGetCategoryDocumentsResponses.get(position).getObject_id();
                        document_id.add(document);
                        document_version_id.add(PreferenceUtils.getDocumentVersionId(context));

                        PreferenceUtils.saveArrayList(context,document_id,"Key");

                    } else if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
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

            /*if(selectedList.contains(mGetCategoryDocumentsResponses.get(position)))
            {
                holder.selectedItemIv.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.selectedItemIv.setVisibility(View.GONE);
            }*/

            holder.moreIcon.setVisibility(View.VISIBLE);

            if(GlobalVariables.isMultiSelect == true || !mGetCategoryDocumentsResponses.get(position).getPermission().isCanViewDocument())
            {
                holder.moreIcon.setVisibility(View.GONE);
            }
        }
    }

    public void multi_select(int position)
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
                                    transparentProgressDialog.dismiss();
                                    getDocumentPreviewResponses = response.body();
                                    String document_preview_url = getDocumentPreviewResponses.getData().getDocumentPdfUrl();


                                    Intent intent = new Intent(context, PdfViewActivity.class);
                                    intent.putExtra("mode",1);
                                    intent.putExtra("url", document_preview_url);
                                    intent.putExtra("documentDetails", categoryDocumentsResponse);
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

        View view = ((NavigationMyFolderActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_document_sort, null);
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
                intent.putExtra("IsFromMyFolder", true);
                intent.putExtra(Constants.DOCUMENT_NAME, mGetCategoryDocumentsResponses.getName());
                context.startActivity(intent);
            }
        });

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

        if(mGetCategoryDocumentsResponses.getIs_shared().equals("1"))
        {
            switchButton_share.setChecked(true);
        }
        else
        {
            switchButton_share.setChecked(false);
        }


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
                        if (context instanceof NavigationMyFolderActivity) {
                            ((NavigationMyFolderActivity) context).getDownloadurlFromServiceSingleDocument(mGetCategoryDocumentsResponses);
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
                    if (!isChecked) {
                      showWarningMessageAlertForSharingContent(mGetCategoryDocumentsResponses, switchButton_share);
                    } else {
                        switchButton_share.setChecked(true);
                       /* if (context instanceof NavigationMyFolderActivity) {
                            ((NavigationMyFolderActivity) context).showInternalShareAlertMessage();
                        }*/

                        CommonFunctions.checkMultipleSharedLevel(context, true);

                      /*  GlobalVariables.isMoveInitiated = true;
                        GlobalVariables.selectedActionName =  "share";
                        Intent intent = new Intent(context, NavigationSharedActivity.class);
                        intent.putExtra("ObjectId", "0");
                        context.startActivity(intent);*/

                    }


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
                            ((NavigationMyFolderActivity) context).deleteDocumentsService("1");
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
    }



    private void showWarningMessageAlertForSharingContent(final GetCategoryDocumentsResponse mGetCategoryDocumentsResponses, SwitchCompat switchButton_share)
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
                documentIdslist.add(mGetCategoryDocumentsResponses.getObject_id());
                getInternalStoppingSharingContentAPI(documentIdslist, mGetCategoryDocumentsResponses.getCategory_id(), switchButton_share);


            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void getInternalStoppingSharingContentAPI(ArrayList<String> documentIdslist, String category_id, SwitchCompat switchButton_share)
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
                    transparentProgressDialog.dismiss();
                    if (response != null) {

                        String message = "";
                        if(response.body().getStatus().getMessage() != null)
                        {
                            message = response.body().getStatus().getMessage().toString();
                        }

                      if(CommonFunctions.isApiSuccess(context, message, response.body().getStatus().getCode()))
                      {
                          switchButton_share.setChecked(false);
                          if (context instanceof NavigationMyFolderActivity) {
                              ((NavigationMyFolderActivity) context).resetPageNumber();
                              ((NavigationMyFolderActivity) context).getCategoryDocuments();
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


    private void openBottomSheetForCategory(GetCategoryDocumentsResponse getCategoryDocumentsResponse, String name) {

        getWhiteLabelProperities();

        View view = ((NavigationMyFolderActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_category_sort, null);

        TextView categoryText = (TextView) view.findViewById(R.id.category_text);
        ImageView categoryImage = (ImageView) view.findViewById(R.id.category_image);
        ImageView moveImage = (ImageView) view.findViewById(R.id.move_image);
        ImageView renameImage = (ImageView) view.findViewById(R.id.rename_image);
        RelativeLayout rename= (RelativeLayout) view.findViewById(R.id.rename1);
        RelativeLayout move_layout = (RelativeLayout) view.findViewById(R.id.move1);
        TextView delete= (TextView)view.findViewById(R.id.delete);

        List<GetCategoryDocumentsResponse> categoryDocumentlist = new ArrayList<>();
        categoryDocumentlist.add(getCategoryDocumentsResponse);
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

        move_layout.setOnClickListener(new View.OnClickListener() {
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
    public void renamedocument(String object_id, String name, String doc_created, String auth)
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
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

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

                    }else {
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
                    transparentProgressDialog.dismiss();
                    if (apiResponse != null) {

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


    public ArrayList<String> getArrayList(){
        return doc_id;
    }
    public ArrayList<String> setArrayList(int k){
    //    doc_id.remove(k);
        return doc_id;
    }


}
