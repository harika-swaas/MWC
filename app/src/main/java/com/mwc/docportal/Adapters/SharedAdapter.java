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
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.APIResponseModel;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.DocumentPreviewRequest;
import com.mwc.docportal.API.Model.DocumentPreviewResponse;
import com.mwc.docportal.API.Model.EndUserRenameRequest;
import com.mwc.docportal.API.Model.GetCategoryDocumentsRequest;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.GetSharedCategoryDocumentsRequest;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Model.LoginResponse;
import com.mwc.docportal.API.Model.WhiteLabelResponse;
import com.mwc.docportal.API.Service.DocumentPreviewService;
import com.mwc.docportal.API.Service.EndUserRenameService;
import com.mwc.docportal.API.Service.GetCategoryDocumentsService;
import com.mwc.docportal.DMS.MyFolderActivity;
import com.mwc.docportal.DMS.MyFolderSharedDocuments;
import com.mwc.docportal.DMS.MyFoldersDMSActivity;
import com.mwc.docportal.Database.AccountSettings;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Fragments.ItemNavigationFolderFragment;
import com.mwc.docportal.Fragments.ItemNavigationSharedFragment;
import com.mwc.docportal.Login.DocumentPreview;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;

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

public class SharedAdapter extends RecyclerView.Adapter<SharedAdapter.ViewHolder> {
    ArrayList<String> doc_id = new ArrayList<String>();
    private Context context;
    List<GetCategoryDocumentsResponse> paginationList = new ArrayList<>();
    private List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses = new ArrayList<>();
    DocumentPreviewResponse getDocumentPreviewResponses;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    AlertDialog mAlertDialog;
    private ItemClickListener mClickListener;


    private HashSet<Integer> mSelected;
    List<GetCategoryDocumentsResponse> selectedList;
    int lastItemPosition ;
    private static final int GRID_ITEM = 0;
    private static final int LIST_ITEM = 1;
    int pageNumber=1;
    int totalPage=1;
    String obj = "0";
    String objectr;
    String categoryr;
    Fragment fragment;


    boolean isSwitchView = true;

    private boolean mIsLoadingFooterAdded = false;

    public SharedAdapter(List<GetCategoryDocumentsResponse>  getCategoryDocumentsResponses, List<GetCategoryDocumentsResponse> selectedList, Activity context, Fragment fragment) {
        this.context = context;
        this.mGetCategoryDocumentsResponses = getCategoryDocumentsResponses;
        this.selectedList = selectedList;
        mSelected = new HashSet<>();
        this.fragment = fragment;

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

    public void toggleSelection(int pos) {
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
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        boolean onItemLongClick(View view, int position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public LinearLayout parentLayout;
        public RelativeLayout gridClick, thumbnailLayout;
        public ImageView imageView, selectedItemIv, thumbnailIcon, thumbnailCornerIcon, moreIcon;
        public TextView text, thumbnailText;
        public View layout;
        ViewHolder vh;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parent_layout1);
            text = (TextView) itemView.findViewById(R.id.folder_name);
            thumbnailText = (TextView) itemView.findViewById(R.id.thumbnail_text);
            imageView = (ImageView) itemView.findViewById(R.id.folder);
            selectedItemIv = (ImageView) itemView.findViewById(R.id.selected_item);
            thumbnailIcon = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            thumbnailCornerIcon = (ImageView) itemView.findViewById(R.id.thumbnail_corner_image);
            gridClick = (RelativeLayout) itemView.findViewById(R.id.grid_click);
            thumbnailLayout = (RelativeLayout) itemView.findViewById(R.id.thumbnail_layout);
            moreIcon = (ImageView) itemView.findViewById(R.id.more_icon);
            this.parentLayout.setOnClickListener(this);
            this.parentLayout.setOnLongClickListener(this);
        }

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


         /*   APIResponseModel.Data data=dataArrayList.get(0);
            List<APIResponseModel.Document> documentList=dataArrayList.get(0).getDocuments();

            for (APIResponseModel.Document document :documentList){
                PreferenceUtils.setDocumentVersionId(context, document.getDocument_version_id());

            }*/

         if(mGetCategoryDocumentsResponses.get(position).getDocument_version_id() != null)
         {
             PreferenceUtils.setDocumentVersionId(context, mGetCategoryDocumentsResponses.get(position).getDocument_version_id());
         }

            if(mGetCategoryDocumentsResponses.get(position).getObject_id() != null)
            {
                PreferenceUtils.setDocument_Id(context, mGetCategoryDocumentsResponses.get(position).getObject_id());
            }

            setButtonBackgroundColor();

            if (mGetCategoryDocumentsResponses.get(position).getType() != null &&
                    mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
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
            } else if (mGetCategoryDocumentsResponses.get(position).getType() != null &&
                    mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                holder.imageView.setVisibility(View.GONE);
                holder.thumbnailLayout.setVisibility(View.VISIBLE);
                if (mGetCategoryDocumentsResponses.get(position).getType() != null && mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("pdf")) {
                    //holder.imageView.setImageResource(R.mipmap.ic_pdf);
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pdf_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pdf_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("xlsx") ||
                        mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("xls") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("xlsm")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("csv")) {
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xls_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xls_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("doc") ||
                        mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("docx") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("docm")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("gdoc") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("keynote")) {
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_doc_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_doc_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("ppt") ||
                        mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("pptx") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("pps")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("ai")) {
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_ppt_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_ppt_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("xml") ||
                        mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("log") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("zip")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("rar") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("zipx")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("mht")) {
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("xml") ||
                        mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("log") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("rtf") ||
                        mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("txt") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("epub")) {
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("msg") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("dot") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("odt")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("ott")) {
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_msg_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_msg_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("pages")) {
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pages_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pages_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("pub") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("ods")) {
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pub_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pub_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("gif") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("jpeg")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("jpg") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("png") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("bmp")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("tif") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("tiff") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("eps")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("svg") || (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("odp")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("otp"))) {
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_gif_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_gif_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("avi")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("flv") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("mpeg") ||
                        mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("mpg") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("swf") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("wmv")) {
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_avi_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_avi_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("mp3")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("wav") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("wma")) {
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_mp3_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_mp3_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else {
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_default_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_default_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                }
            }

            final String name = mGetCategoryDocumentsResponses.get(position).getName();
            holder.text.setText(name);

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGetCategoryDocumentsResponses.get(position).getType() != null &&
                            mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                        PreferenceUtils.setObjectId(context, mGetCategoryDocumentsResponses.get(position).getObject_id());
                        obj = mGetCategoryDocumentsResponses.get(position).getObject_id();
                        if(context instanceof MyFoldersDMSActivity){
                            ((ItemNavigationSharedFragment)fragment).getSharedCategoryDocuments(obj);
                        }

                        //   myFoldersDMSActivity.getCategoryDocuments(obj,String.valueOf(pageNumber));
                        doc_id.add(mGetCategoryDocumentsResponses.get(position).getObject_id());
                    }
                }
            });

            holder.thumbnailLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDocumentPreviews(mGetCategoryDocumentsResponses.get(position).getDocument_version_id());
                }
            });

            holder.moreIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGetCategoryDocumentsResponses.get(position).getType() != null &&
                            mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                        openBottomSheetForCategory(mGetCategoryDocumentsResponses.get(position).getName());
                        objectr = mGetCategoryDocumentsResponses.get(position).getParent_id();
                        categoryr = mGetCategoryDocumentsResponses.get(position).getObject_id();

                    } else if (mGetCategoryDocumentsResponses.get(position).getType() != null &&
                            mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                        openBottomSheetForDocument(mGetCategoryDocumentsResponses.get(position).getType(), mGetCategoryDocumentsResponses.get(position).getFiletype(), mGetCategoryDocumentsResponses.get(position).getName());
                    }
                }

            });

            if (mSelected.contains(position)) {
                holder.selectedItemIv.setVisibility(View.VISIBLE);
            } else {
                holder.selectedItemIv.setVisibility(View.GONE);
            }
        }
    }

    private void getDocumentPreviews(String document_version_id) {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final DocumentPreviewRequest mDocumentPreviewRequest = new DocumentPreviewRequest(document_version_id);

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

                               /* Intent mIntent = new Intent(context, PDFViewer.class);
                                mIntent.putExtra(Constants.DOCUMENTPDFURL, document_preview_url);
                                context.startActivity(mIntent);*/
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

    private void openBottomSheetForDocument(String type, String fileType, String name) {

        getWhiteLabelProperities();

        View view = ((MyFoldersDMSActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_document_sort, null);
        RelativeLayout shareView = (RelativeLayout) view.findViewById(R.id.share_layout);
        RelativeLayout rename_layout = (RelativeLayout) view.findViewById(R.id.rename_layout);
        RelativeLayout move_layout =  (RelativeLayout) view.findViewById(R.id.move1);
        RelativeLayout copy_layout =  (RelativeLayout) view.findViewById(R.id.copy1);

        TextView delete = (TextView) view.findViewById(R.id.delete);
        TextView docText = (TextView) view.findViewById(R.id.doc_text);
        ImageView thumbnailIcon = (ImageView) view.findViewById(R.id.thumbnail_image);
        ImageView thumbnailCornerIcon = (ImageView) view.findViewById(R.id.thumbnail_corner_image);
        TextView thumbnailText = (TextView) view.findViewById(R.id.thumbnail_text);

        final ImageView copyImage = (ImageView) view.findViewById(R.id.copy_image);
        ImageView moveImage = (ImageView) view.findViewById(R.id.move_image);
        ImageView renameImage = (ImageView) view.findViewById(R.id.rename_image);
        ImageView shareImage = (ImageView) view.findViewById(R.id.share_image);


        ImageView availableOfflineImage = (ImageView) view.findViewById(R.id.available_offline_image);

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
        shareView.setVisibility(View.GONE);
        rename_layout.setVisibility(View.GONE);
        move_layout.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);

        if (fileType.equalsIgnoreCase("pdf")) {
            //holder.imageView.setImageResource(R.mipmap.ic_pdf);
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pdf_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pdf_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("xlsx") ||
                fileType.equalsIgnoreCase("xls") || fileType.equalsIgnoreCase("xlsm")
                || fileType.equalsIgnoreCase("csv")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xls_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xls_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("doc") ||
                fileType.equalsIgnoreCase("docx") || fileType.equalsIgnoreCase("docm")
                || fileType.equalsIgnoreCase("gdoc") || fileType.equalsIgnoreCase("keynote")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_doc_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_doc_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("ppt") ||
                fileType.equalsIgnoreCase("pptx") || fileType.equalsIgnoreCase("pps")
                || fileType.equalsIgnoreCase("ai")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_ppt_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_ppt_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("xml") ||
                fileType.equalsIgnoreCase("log") || fileType.equalsIgnoreCase("zip")
                || fileType.equalsIgnoreCase("rar") || fileType.equalsIgnoreCase("zipx")
                || fileType.equalsIgnoreCase("mht")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("xml") ||
                fileType.equalsIgnoreCase("log") || fileType.equalsIgnoreCase("rtf") ||
                fileType.equalsIgnoreCase("txt") || fileType.equalsIgnoreCase("epub")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("msg") || fileType.equalsIgnoreCase("dot") || fileType.equalsIgnoreCase("odt")
                || fileType.equalsIgnoreCase("ott")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_msg_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_msg_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("pages")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pages_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pages_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("pub") || fileType.equalsIgnoreCase("ods")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pub_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pub_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("gif") || fileType.equalsIgnoreCase("jpeg")
                || fileType.equalsIgnoreCase("jpg") || fileType.equalsIgnoreCase("png") || fileType.equalsIgnoreCase("bmp")
                || fileType.equalsIgnoreCase("tif") || fileType.equalsIgnoreCase("tiff") || fileType.equalsIgnoreCase("eps")
                || fileType.equalsIgnoreCase("svg") || fileType.equalsIgnoreCase("odp")
                || fileType.equalsIgnoreCase("otp")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_gif_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_gif_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("avi")
                || fileType.equalsIgnoreCase("flv") || fileType.equalsIgnoreCase("mpeg") ||
                fileType.equalsIgnoreCase("mpg") || fileType.equalsIgnoreCase("swf") || fileType.equalsIgnoreCase("wmv")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_avi_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_avi_corner_color));
            thumbnailText.setText(fileType);
        } else if (fileType.equalsIgnoreCase("mp3")
                || fileType.equalsIgnoreCase("wav") || fileType.equalsIgnoreCase("wma")) {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_mp3_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_mp3_corner_color));
            thumbnailText.setText(fileType);
        } else {
            thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_default_color));
            thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_default_corner_color));
            thumbnailText.setText(fileType);
        }

        final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context,MyFolderSharedDocuments.class);
                mIntent.putExtra(Constants.OBJ, (Serializable) selectedList);
                context.startActivity(mIntent);
            }
        });

        moveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyFolderActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private void openBottomSheetForCategory(String name) {

        getWhiteLabelProperities();

        View view = ((MyFoldersDMSActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_category_sort, null);

        TextView categoryText = (TextView) view.findViewById(R.id.category_text);
        ImageView categoryImage = (ImageView) view.findViewById(R.id.category_image);
        ImageView moveImage = (ImageView) view.findViewById(R.id.move_image);
        ImageView renameImage = (ImageView) view.findViewById(R.id.rename_image);
        RelativeLayout rename= (RelativeLayout) view.findViewById(R.id.rename1);
        rename.setVisibility(View.GONE);

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

                        rename(categoryr,folder,objectr);
                        mAlertDialog.dismiss();
                        mBottomSheetDialog.dismiss();
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
                                //  refreshAdapterToView(getCategoryDocumentsResponses);
                                getSubCategoryDocuments(objectr);

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

    public void getSubCategoryDocuments(final String object_id) {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetSharedCategoryDocumentsRequest mGetCategoryDocumentsRequest = new GetSharedCategoryDocumentsRequest(object_id);

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);
            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);
            Call call = mGetCategoryDocumentsService.getSharedCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<APIResponseModel>() {
                @Override
                public void onResponse(Response<APIResponseModel> response, Retrofit retrofit) {

                    if (response != null) {

                        transparentProgressDialog.dismiss();
                        if (response.body().getStatus().getCode() instanceof Boolean) {

                            if (response.body().getStatus().getCode() == Boolean.FALSE) {

                                List<APIResponseModel.Category> categoryList = response.body().getData().getCategories();
                                List<APIResponseModel.Document>documentList=response.body().getData().getDocuments();

                                //     List<GetCategoryDocumentsResponse> getCategoryDocumentsResponseList=new ArrayList<>();
                                List<GetCategoryDocumentsResponse> getCategoryDocumentsResponseListTemp=new ArrayList<>();

                                if (categoryList != null && categoryList.size() > 0){
                                    for (APIResponseModel.Category category:categoryList){
                                        GetCategoryDocumentsResponse getCategoryDocumentsResponse=new GetCategoryDocumentsResponse();
                                        getCategoryDocumentsResponse.setObject_id(category.getCategoryId());
                                        getCategoryDocumentsResponse.setName(category.getCategoryName());
                                        getCategoryDocumentsResponse.setType(category.getType());
                                        mGetCategoryDocumentsResponses.add(getCategoryDocumentsResponse);

                                    }
                                }

                                if (documentList != null && documentList.size()>0){
                                    for (APIResponseModel.Document  document:documentList){
                                        GetCategoryDocumentsResponse getCategoryDocumentsResponse=new GetCategoryDocumentsResponse();
                                        getCategoryDocumentsResponse.setObject_id(document.getDocument_id());
                                        getCategoryDocumentsResponse.setDocument_version_id(document.getDocument_version_id());
                                        getCategoryDocumentsResponse.setName(document.getName());
                                        getCategoryDocumentsResponse.setFiletype(document.getFiletype());
                                        getCategoryDocumentsResponse.setFilesize(document.getFilesize());
                                        getCategoryDocumentsResponse.setType(document.getType());
                                        getCategoryDocumentsResponse.setShared_date(document.getShared_date());
                                        getCategoryDocumentsResponseListTemp.add(getCategoryDocumentsResponse);
                                    }
                                }


                                mGetCategoryDocumentsResponses.addAll(getCategoryDocumentsResponseListTemp);


                                refreshAdapterToView(getCategoryDocumentsResponses);



                            }

                        } else if (response.body().getStatus().getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = response.body().getStatus().getMessage().toString();

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
        doc_id.remove(k);
        return doc_id;
    }

    public void refreshAdapterToView(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        this.mGetCategoryDocumentsResponses.clear();
        this.mGetCategoryDocumentsResponses.addAll(getCategoryDocumentsResponses);
        //getSubCategoryDocuments(objectr,"1");

        notifyDataSetChanged();
    }
}
