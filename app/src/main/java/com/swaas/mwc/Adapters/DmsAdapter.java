package com.swaas.mwc.Adapters;

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
import com.swaas.mwc.API.Model.ApiResponse;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.BaseApiResponseStatus;
import com.swaas.mwc.API.Model.ColorCodeModel;
import com.swaas.mwc.API.Model.DeleteDocumentRequest;
import com.swaas.mwc.API.Model.DeleteDocumentResponseModel;
import com.swaas.mwc.API.Model.DeleteEndUserFolderRequest;
import com.swaas.mwc.API.Model.DocumentPreviewRequest;
import com.swaas.mwc.API.Model.DocumentPreviewResponse;
import com.swaas.mwc.API.Model.DownloadDocumentRequest;
import com.swaas.mwc.API.Model.DownloadDocumentResponse;

import com.swaas.mwc.API.Model.EditDocumentPropertiesRequest;
import com.swaas.mwc.API.Model.EndUserRenameRequest;
import com.swaas.mwc.API.Model.GetCategoryDocumentsRequest;
import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.LoginResponse;
import com.swaas.mwc.API.Model.SharedDocumentResponseModel;
import com.swaas.mwc.API.Model.StopSharingRequestModel;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.API.Service.DeleteDocumentService;
import com.swaas.mwc.API.Service.DeleteEndUserFolderService;
import com.swaas.mwc.API.Service.DocumentPreviewService;
import com.swaas.mwc.API.Service.DownloadDocumentService;

import com.swaas.mwc.API.Service.EditDocumentPropertiesService;
import com.swaas.mwc.API.Service.EndUserRenameService;
import com.swaas.mwc.API.Service.GetCategoryDocumentsService;
import com.swaas.mwc.API.Service.GetEndUserParentSHaredFoldersService;
import com.swaas.mwc.Common.CommonFunctions;
import com.swaas.mwc.Common.GlobalVariables;
import com.swaas.mwc.DMS.MyFolderActivity;

import com.swaas.mwc.DMS.MyFolderCategoryActivity;
import com.swaas.mwc.DMS.MyFolderCopyActivity;
import com.swaas.mwc.DMS.MyFolderSharedDocuments;
import com.swaas.mwc.DMS.MyFoldersDMSActivity;
import com.swaas.mwc.DMS.MyfolderDeleteActivity;
import com.swaas.mwc.DMS.NavigationMyFolderActivity;
import com.swaas.mwc.DMS.Tab_Activity;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Database.OffLine_Files_Repository;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Fragments.ItemNavigationFolderFragment;
import com.swaas.mwc.Login.DocumentPreview;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;
import com.swaas.mwc.pdf.PdfViewActivity;

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

public class DmsAdapter extends RecyclerView.Adapter<DmsAdapter.ViewHolder> {
    private ArrayList<String> doc_id = new ArrayList<String>();
    private Context context;
    List<GetCategoryDocumentsResponse> paginationList = new ArrayList<>();
    ArrayList<String> categoryids = new ArrayList<String>();
    public List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses = new ArrayList<>();
   DocumentPreviewResponse getDocumentPreviewResponses;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    AlertDialog mAlertDialog;
   // private ItemClickListener mClickListener;
    MyFoldersDMSActivity myFoldersDMSActivity;

    private HashSet<Integer> mSelected;
    public List<GetCategoryDocumentsResponse> selectedList = new ArrayList<>();
    int lastItemPosition ;
    private static final int GRID_ITEM = 0;
    private static final int LIST_ITEM = 1;
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

    boolean isMultiSelect = false;

    private boolean mIsLoadingFooterAdded = false;
    List<GetCategoryDocumentsResponse> downloadingUrlDataList = new ArrayList<>();

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
                        multi_select(position);

                    }
                    else {

                        if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                            PreferenceUtils.setObjectId(context, mGetCategoryDocumentsResponses.get(position).getObject_id());
                            obj = mGetCategoryDocumentsResponses.get(position).getObject_id();
                            PreferenceUtils.setParentId(context,obj);
                           /* MyFoldersDMSActivity.sort.setText("Name");
                            ItemNavigationFolderFragment.sortByName = true;*/
                           Intent intent = new Intent(context, NavigationMyFolderActivity.class);
                           intent.putExtra("ObjectId", mGetCategoryDocumentsResponses.get(position).getObject_id());
                           context.startActivity(intent);
                         //  context.overridePendingTransition(0, 0);

                           /* if (context instanceof NavigationMyFolderActivity) {
                                ((NavigationMyFolderActivity) context).getCategoryDocuments(obj, String.valueOf(pageNumber));
                            }*/
                           /* if(PreferenceUtils.getBackButtonList(context,"key")!=null)
                            {
                            doc_id = PreferenceUtils.getBackButtonList(context,"key");
                            }

                            else
                            {
                                doc_id.add(obj);
                            PreferenceUtils.setBackButtonList(context,doc_id,"key");
                            }*/

                        } else if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                            getDocumentPreviews(mGetCategoryDocumentsResponses.get(position));

                        }
                    }

                }
            });

            holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!GlobalVariables.isMoveInitiated) {
                        if (!isMultiSelect) {
                            selectedList = new ArrayList<>();
                            isMultiSelect = true;
                        }

                        multi_select(position);
                    }
                    return false;
                }
            });

          /*  holder.thumbnailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDocumentPreviews(mGetCategoryDocumentsResponses.get(position));
                }
            });*/


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

            if(selectedList.contains(mGetCategoryDocumentsResponses.get(position)))
            {
                holder.selectedItemIv.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.selectedItemIv.setVisibility(View.GONE);
            }

            if(selectedList != null && selectedList.size() > 0)
            {
                holder.moreIcon.setVisibility(View.GONE);
            }
            else
            {
                holder.moreIcon.setVisibility(View.VISIBLE);
            }


         /*   if (selectedList.contains(mGetCategoryDocumentsResponses.get(position))) {
                holder.selectedItemIv.setVisibility(View.VISIBLE);
            } else {
                holder.selectedItemIv.setVisibility(View.GONE);
            }*/
        }
        else
        {
            myFoldersDMSActivity.emptyText.setVisibility(View.VISIBLE);
        }
    }

    public void multi_select(int position)
    {

      //  if(!GlobalVariables.isMoveInitiated) {

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

            if (context instanceof NavigationMyFolderActivity) {
                ((NavigationMyFolderActivity) context).updateToolbarMenuItems(selectedList);
            }


      //      CommonFunctions.assignSelectedList(selectedList);
     //   }

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

            call.enqueue(new Callback<BaseApiResponse<DocumentPreviewResponse>>() {
                @Override
                public void onResponse(Response<BaseApiResponse<DocumentPreviewResponse>> response, Retrofit retrofit) {
                    BaseApiResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                getDocumentPreviewResponses = response.body().getData();
                                String document_preview_url = getDocumentPreviewResponses.getDocument_pdf_url();


                                Intent intent = new Intent(context, PdfViewActivity.class);
                                intent.putExtra("mode",1);
                                intent.putExtra("url", document_preview_url);
                                intent.putExtra("documentDetails", categoryDocumentsResponse);
                                context.startActivity(intent);
                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            context.startActivity(new Intent(context, DocumentPreview.class));
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

                    PreferenceUtils.setDocumentVersionId(context,mGetCategoryDocumentsResponses.getDocument_version_id());
                    PreferenceUtils.setDocument_Id(context, mGetCategoryDocumentsResponses.getObject_id());

                Intent intent = new Intent (context,Tab_Activity.class);
                context.startActivity(intent);
            }
        });
      /*  shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context,MyFolderSharedDocuments.class);
                mIntent.putExtra(Constants.OBJ, (Serializable) selectedList);
                context.startActivity(mIntent);
            }
        });*/

        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (context instanceof NavigationMyFolderActivity) {
                    ((NavigationMyFolderActivity) context).initiateMoveAction("move");
                }

            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (context instanceof NavigationMyFolderActivity) {
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
                if(buttonView.isPressed() == true) {
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

        switchButton_share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(buttonView.isPressed() == true) {

                    if (!isChecked) {
                        switchButton_share.setChecked(true);
                        showWarningMessageAlertForSharingContent(mGetCategoryDocumentsResponses);
                        mBottomSheetDialog.dismiss();

                    } else {
                        switchButton_share.setChecked(false);
                        List<GetCategoryDocumentsResponse> selectedList = new ArrayList<>();
                        selectedList.add(mGetCategoryDocumentsResponses);

                        PreferenceUtils.setCategoryId(context, mGetCategoryDocumentsResponses.getCategory_id());
                        PreferenceUtils.setDocument_Id(context, mGetCategoryDocumentsResponses.getObject_id());

                        Intent mIntent = new Intent(context,MyFolderSharedDocuments.class);
                        mIntent.putExtra(Constants.OBJ, (Serializable) selectedList);
                        context.startActivity(mIntent);
                        mBottomSheetDialog.dismiss();
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
                            ((NavigationMyFolderActivity) context).deleteDocumentsService(document, document_version_id, "0");
                        }

                    }
                });


                delete_historic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();

                        if (context instanceof NavigationMyFolderActivity) {
                            ((NavigationMyFolderActivity) context).deleteDocumentsService(document, document_version_id, "1");
                        }

                    }
                });

                delete_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBackDialog.dismiss();

                        if (context instanceof NavigationMyFolderActivity) {
                            ((NavigationMyFolderActivity) context).deleteDocumentsService(document, document_version_id, "2");
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
    }

    private void showWarningMessageAlertForSharingContent(final GetCategoryDocumentsResponse mGetCategoryDocumentsResponses)
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
                documentIdslist.add(mGetCategoryDocumentsResponses.getObject_id());
                getInternalStoppingSharingContentAPI(documentIdslist, mGetCategoryDocumentsResponses.getCategory_id());


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

                        if (response.body().getStatus().getCode() instanceof Boolean) {
                            if (response.body().getStatus().getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();



                            }

                        } else if (response.body().getStatus().getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = response.body().getStatus().getMessage().toString();

                            Object obj = 401.0;
                            if(obj.equals(401.0)) {
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

            final EditDocumentPropertiesRequest editDocumentPropertiesRequest = new EditDocumentPropertiesRequest(object_id,name,doc_created,auth);

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

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();

                                if (context instanceof NavigationMyFolderActivity) {
                                    ((NavigationMyFolderActivity) context).resetPageNumber();
                                    ((NavigationMyFolderActivity) context).getCategoryDocuments();
                                }
                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                            builder.setView(view);
                            builder.setCancelable(false);

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
                                    context.startActivity(new Intent(context, LoginActivity.class));
                                }
                            });

                            mAlertDialog = builder.create();
                            mAlertDialog.show();
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

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                if (context instanceof NavigationMyFolderActivity) {
                                    ((NavigationMyFolderActivity) context).resetPageNumber();
                                    ((NavigationMyFolderActivity) context).getCategoryDocuments();
                                }
                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                            builder.setView(view);
                            builder.setCancelable(false);

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
                                    context.startActivity(new Intent(context, LoginActivity.class));
                                }
                            });

                            mAlertDialog = builder.create();
                            mAlertDialog.show();
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


    public static Drawable changeDrawableColor(Context context, int icon, int itemFolderColor) {
        Drawable mDrawable = ContextCompat.getDrawable(context, icon).mutate();
        mDrawable.setColorFilter(new PorterDuffColorFilter(itemFolderColor, PorterDuff.Mode.SRC_IN));
        return mDrawable;
    }

    public void getSubCategoryDocuments(final String object_id, final String page) {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(object_id, "list", "category", "1", "0");

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);
            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);
            Call call = mGetCategoryDocumentsService.getCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(context),page);

            call.enqueue(new Callback<ListPinDevicesResponse<GetCategoryDocumentsResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetCategoryDocumentsResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();
                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();

                                getCategoryDocumentsResponses = response.body().getData();

                                //     getCategoryDocumentsResponses = response.body().getData();
                                //     getCategoryDocumentsResponses.addAll(paginationList);

                                //   paginationList.add(getCategoryDocumentsResponses);

                           /*     if (object_id.equals("0")){
                                    ActionBar actionBar = myFoldersDMSActivity.getSupportActionBar();
                                    if (actionBar != null) {
                                        actionBar.setDisplayHomeAsUpEnabled(false);
                                    }
                                    fragment.getView().findViewById(R.id.menu_camera_item).setVisibility(View.GONE);
                                    fragment.getView().findViewById(R.id.menu_camera_video_item).setVisibility(View.GONE);
                                    fragment.getView().findViewById(R.id.menu_upload_item).setVisibility(View.GONE);
                                }
                                else
                                {
                                    ActionBar actionBar = myFoldersDMSActivity.getSupportActionBar();
                                    if (actionBar != null) {
                                        actionBar.setDisplayHomeAsUpEnabled(false);
                                    }                                    fragment.getView().findViewById(R.id.menu_camera_item).setVisibility(View.VISIBLE);
                                    fragment.getView().findViewById(R.id.menu_camera_video_item).setVisibility(View.VISIBLE);
                                    fragment.getView().findViewById(R.id.menu_upload_item).setVisibility(View.VISIBLE);
                                }*/


/*

                                totalPage = Integer.parseInt(response.headers().get("X-Pagination-Page-Count"));


                                if(pageNumber+ 1<=totalPage)
                                {
                                  getSubCategoryDocuments(obj,String.valueOf(pageNumber+1));
                                  pageNumber=pageNumber+1;
                                }
*/

                                refreshAdapterToView(getCategoryDocumentsResponses);
                                //    paginationList.clear();
/*
                                while(pageCount!=null&&Integer.parseInt(pageCount)>1){
                                    getSubCategoryDocuments(obj,pageCount);
                                    refreshAdapterToView(getCategoryDocumentsResponses);

                                }
*/


                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                            builder.setView(view);
                            builder.setCancelable(false);

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
                                    context.startActivity(new Intent(context, LoginActivity.class));
                                }
                            });

                            mAlertDialog = builder.create();
                            mAlertDialog.show();
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
    public ArrayList<String> getArrayList(){
        return doc_id;
    }
    public ArrayList<String> setArrayList(int k){
    //    doc_id.remove(k);
        return doc_id;
    }

    public void refreshAdapterToView(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        this.mGetCategoryDocumentsResponses.clear();
        this.mGetCategoryDocumentsResponses.addAll(getCategoryDocumentsResponses);
        //getSubCategoryDocuments(parentr,"1");

        notifyDataSetChanged();
    }
}
