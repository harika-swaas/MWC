package com.swaas.mwc.Adapters;

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

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.ApiResponse;
import com.swaas.mwc.API.Model.BaseApiResponse;
import com.swaas.mwc.API.Model.DeleteDocumentRequest;
import com.swaas.mwc.API.Model.DeleteEndUserFolderRequest;
import com.swaas.mwc.API.Model.DocumentPreviewRequest;
import com.swaas.mwc.API.Model.DocumentPreviewResponse;
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
import com.swaas.mwc.API.Service.EditDocumentPropertiesService;
import com.swaas.mwc.API.Service.EndUserRenameService;
import com.swaas.mwc.API.Service.GetCategoryDocumentsService;
import com.swaas.mwc.API.Service.GetEndUserParentSHaredFoldersService;
import com.swaas.mwc.DMS.MyFolderActivity;
import com.swaas.mwc.DMS.MyFolderCopyActivity;
import com.swaas.mwc.DMS.MyFolderSharedDocuments;
import com.swaas.mwc.DMS.MyFoldersDMSActivity;
import com.swaas.mwc.DMS.MyfolderDeleteActivity;
import com.swaas.mwc.DMS.Tab_Activity;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Database.OffLine_Files_Repository;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.FTL.WebviewLoaderTermsActivity;
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

public class DmsAdapterList extends RecyclerView.Adapter<DmsAdapterList.ViewHolder> {

    MyFoldersDMSActivity myFoldersDMSActivity;
    ArrayList<String> doc_id = new ArrayList<String>();
    final Context context;
    private List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    private List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses;
    AlertDialog mAlertDialog;

    boolean isMultiSelect = false;
    List<GetCategoryDocumentsResponse> selectedList = new ArrayList<>();
    String objectr;
    String categoryr;
    String parentr;
    String document;
    String version_number;
    ArrayList<String>document_id= new ArrayList<>();
    Fragment fragment;
    AlertDialog mBackDialog;
    int mode = 0;
    ArrayList <String> document_version_id= new ArrayList<>();
    ArrayList<String> categoryids = new ArrayList<String>();


    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();

    DocumentPreviewResponse getDocumentPreviewResponses;
    String pageCount = "1";
    public DmsAdapterList(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses, Activity context, Fragment fragment) {
        this.context = context;
        this.mGetCategoryDocumentsResponses = getCategoryDocumentsResponses;
        this.fragment = fragment;
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
            } else if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                holder.folderView.setVisibility(View.GONE);
                holder.thumbnailView.setVisibility(View.VISIBLE);
                holder.thumbnailCornerIcon.setVisibility(View.VISIBLE);
                if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("pdf")) {
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
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("ai")){
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_ppt_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_ppt_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("xml") ||
                        mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("log") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("zip")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("rar") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("zipx")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("mht")){
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("xml") ||
                        mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("log") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("rtf") ||
                        mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("txt") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("epub")){
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("msg") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("dot") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("odt")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("ott")){
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_msg_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_msg_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("pages")){
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pages_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pages_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("pub") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("ods")){
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pub_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pub_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("gif") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("jpeg")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("jpg") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("png") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("bmp")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("tif") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("tiff") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("eps")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("svg") || (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("odp")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("otp"))){
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_gif_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_gif_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("avi")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("flv") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("mpeg") ||
                        mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("mpg") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("swf") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("wmv")){
                    holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_avi_color));
                    holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_avi_corner_color));
                    holder.thumbnailText.setText(mGetCategoryDocumentsResponses.get(position).getFiletype());
                }  else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("mp3")
                        || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("wav") || mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("wma")){
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
            holder.folder_name.setText(name);

            final String createdDate = mGetCategoryDocumentsResponses.get(position).getCreated_date();
            holder.folder_date.setText(createdDate);

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

            /*if (!TextUtils.isEmpty(mGetCategoryDocumentsResponses.get(position).getName())) {
                holder.indicatorParentView.setVisibility(View.VISIBLE);
                holder.indicatorTextValue.setText(String.valueOf(mGetCategoryDocumentsResponses.get(position).getName().charAt(0)));
            }
            else {
                holder.indicatorParentView.setVisibility(View.GONE);
            }*/

            holder.foldernext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGetCategoryDocumentsResponses.get(position).getType() != null && mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                        getSubCategoryDocuments(mGetCategoryDocumentsResponses.get(position).getObject_id(),pageCount);
                        doc_id = PreferenceUtils.getBackButtonList(context,"key");
                        doc_id.add(mGetCategoryDocumentsResponses.get(position).getObject_id());
                        PreferenceUtils.setBackButtonList(context,doc_id,"key");
                    }
                    else if (mGetCategoryDocumentsResponses.get(position).getType() != null && mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document"))
                    {
                        if(mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document"))
                        {
                            getDocumentPreviews(mGetCategoryDocumentsResponses.get(position));
                        }

                    }
                }
            });


            holder.list_item_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isMultiSelect)
                    {
                        multi_select(position);

                    }
                    else {

                        if (mGetCategoryDocumentsResponses.get(position).getType() != null && mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                            PreferenceUtils.setObjectId(context, mGetCategoryDocumentsResponses.get(position).getObject_id());
                            getSubCategoryDocuments(mGetCategoryDocumentsResponses.get(position).getObject_id(), pageCount);

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

                    if (!isMultiSelect) {
                        selectedList = new ArrayList<>();
                        isMultiSelect = true;
                    }

                    multi_select(position);


                    return false;
                }
            });


           /* holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGetCategoryDocumentsResponses.get(position).getType() != null && mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                        getSubCategoryDocuments(mGetCategoryDocumentsResponses.get(position).getObject_id(),pageCount);
                        doc_id = PreferenceUtils.getBackButtonList(context,"key");
                        doc_id.add(mGetCategoryDocumentsResponses.get(position).getObject_id());
                        PreferenceUtils.setBackButtonList(context,doc_id,"key");
                    }
                    else if (mGetCategoryDocumentsResponses.get(position).getType() != null && mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document"))
                    {
                        if(mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document"))
                        {
                            getDocumentPreviews(mGetCategoryDocumentsResponses.get(position).getDocument_version_id(), mGetCategoryDocumentsResponses.get(position).getName());
                        }

                    }
                }
            });

            holder.thumbnailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDocumentPreviews(mGetCategoryDocumentsResponses.get(position).getDocument_version_id(), mGetCategoryDocumentsResponses.get(position).getName());
                }
            });
*/
            holder.imageMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                        openBottomSheetForCategory(mGetCategoryDocumentsResponses.get(position).getName());
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
                holder.imageMore.setVisibility(View.GONE);
            }
            else
            {
                holder.imageMore.setVisibility(View.VISIBLE);
            }

        }
    }

    private void multi_select(int position)
    {
        if (selectedList.contains(mGetCategoryDocumentsResponses.get(position)))
        {
            selectedList.remove(mGetCategoryDocumentsResponses.get(position));
            if(selectedList != null && selectedList.size() == 0)
            {
                selectedList.clear();
                isMultiSelect = false;
                notifyDataSetChanged();
            }
        }
        else
        {
            selectedList.add(mGetCategoryDocumentsResponses.get(position));
        }


        notifyDataSetChanged();

        if (context instanceof MyFoldersDMSActivity) {
            ((ItemNavigationFolderFragment) fragment).updateToolbarMenuItems(selectedList);
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

        View view = ((MyFoldersDMSActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_document_sort, null);
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


        final SwitchCompat switchButton_download = (SwitchCompat) view.findViewById(R.id.switchButton_download);
        final SwitchCompat switchButton_share = (SwitchCompat) view.findViewById(R.id.switchButton_share);


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
                        if (context instanceof MyFoldersDMSActivity) {
                            ((ItemNavigationFolderFragment) fragment).getDownloadurlFromService(categoryDocumentsResponse.getDocument_version_id());
                        }
                        mBottomSheetDialog.dismiss();

                    }
                    else
                    {
                        OffLine_Files_Repository offLine_files_repository = new OffLine_Files_Repository(context);
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

                    if (!isChecked) {
                        switchButton_share.setChecked(true);
                        showWarningMessageAlertForSharingContent(categoryDocumentsResponse);
                        mBottomSheetDialog.dismiss();

                    } else {
                        switchButton_share.setChecked(false);
                        List<GetCategoryDocumentsResponse> selectedList = new ArrayList<>();
                        selectedList.add(categoryDocumentsResponse);

                        PreferenceUtils.setCategoryId(context, categoryDocumentsResponse.getCategory_id());
                        PreferenceUtils.setDocument_Id(context, categoryDocumentsResponse.getObject_id());

                        Intent mIntent = new Intent(context,MyFolderSharedDocuments.class);
                        mIntent.putExtra(Constants.OBJ, (Serializable) selectedList);
                        context.startActivity(mIntent);
                        mBottomSheetDialog.dismiss();
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
                Intent intent = new Intent(context, MyFolderActivity.class);
                context.startActivity(intent);
            }
        });

        doclayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent (context,Tab_Activity.class);
                context.startActivity(intent);
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyFolderCopyActivity.class);
                context.startActivity(intent);
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v)
            {
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
                        if (NetworkUtils.isNetworkAvailable(context)) {

                            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
                            transparentProgressDialog.show();
                            DeleteDocumentRequest deleteDocumentRequest= new DeleteDocumentRequest();
                            deleteDocumentRequest.setDoc_id(document);
                            deleteDocumentRequest.setDoc_version_ids(document_version_id);
                            deleteDocumentRequest.setMode("0");
                            DeleteDocumentRequest docs = deleteDocumentRequest;
                            final DeleteDocumentRequest.DeleteDocRequest deleteDocRequest = new DeleteDocumentRequest.DeleteDocRequest(new DeleteDocumentRequest[]{docs});

                            String request = new Gson().toJson(deleteDocRequest);

                            //Here the json data is add to a hash map with key data
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("data", request);

                            final DeleteDocumentService deleteDocumentService = retrofitAPI.create(DeleteDocumentService.class);

                            Call call = deleteDocumentService.delete_eu_document(params, PreferenceUtils.getAccessToken(context));

                            call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                                @Override
                                public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                                    ListPinDevicesResponse apiResponse = response.body();
                                    if (apiResponse != null) {

                                        transparentProgressDialog.dismiss();

                                        if (apiResponse.status.getCode() instanceof Boolean) {
                                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                                transparentProgressDialog.dismiss();
                                                getSubCategoryDocuments(parentr,"1");
                                                mBackDialog.dismiss();
                                                // refreshAdapterToView(getCategoryDocumentsResponses);
                                            }

                                        } else if (apiResponse.status.getCode() instanceof Integer) {
                                            transparentProgressDialog.dismiss();
                                            mBackDialog.dismiss();
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
                                    mBackDialog.dismiss();
                                    Log.d("PinDevice error", t.getMessage());
                                }
                            });
                        }


                    }
                });


                delete_historic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (NetworkUtils.isNetworkAvailable(context)) {

                            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
                            transparentProgressDialog.show();
                            DeleteDocumentRequest deleteDocumentRequest= new DeleteDocumentRequest();
                            deleteDocumentRequest.setDoc_id(document);
                            deleteDocumentRequest.setDoc_version_ids(document_version_id);
                            deleteDocumentRequest.setMode("1");
                            DeleteDocumentRequest docs = deleteDocumentRequest;
                            final DeleteDocumentRequest.DeleteDocRequest deleteDocRequest = new DeleteDocumentRequest.DeleteDocRequest(new DeleteDocumentRequest[]{docs});

                            String request = new Gson().toJson(deleteDocRequest);

                            //Here the json data is add to a hash map with key data
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("data", request);

                            final DeleteDocumentService deleteDocumentService = retrofitAPI.create(DeleteDocumentService.class);

                            Call call = deleteDocumentService.delete_eu_document(params, PreferenceUtils.getAccessToken(context));

                            call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                                @Override
                                public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                                    ListPinDevicesResponse apiResponse = response.body();
                                    if (apiResponse != null) {

                                        transparentProgressDialog.dismiss();

                                        if (apiResponse.status.getCode() instanceof Boolean) {
                                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                                transparentProgressDialog.dismiss();
                                                getSubCategoryDocuments(parentr,"1");
                                                mBackDialog.dismiss();
                                                // refreshAdapterToView(getCategoryDocumentsResponses);
                                            }

                                        } else if (apiResponse.status.getCode() instanceof Integer) {
                                            transparentProgressDialog.dismiss();
                                            mBackDialog.dismiss();
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
                                    mBackDialog.dismiss();
                                    Log.d("PinDevice error", t.getMessage());
                                }
                            });
                        }



                    }
                });

                delete_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetworkUtils.isNetworkAvailable(context)) {

                            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
                            transparentProgressDialog.show();
                            DeleteDocumentRequest deleteDocumentRequest= new DeleteDocumentRequest();
                            deleteDocumentRequest.setDoc_id(document);
                            deleteDocumentRequest.setDoc_version_ids(document_version_id);
                            deleteDocumentRequest.setMode("2");
                            DeleteDocumentRequest docs = deleteDocumentRequest;
                            final DeleteDocumentRequest.DeleteDocRequest deleteDocRequest = new DeleteDocumentRequest.DeleteDocRequest(new DeleteDocumentRequest[]{docs});

                            String request = new Gson().toJson(deleteDocRequest);

                            //Here the json data is add to a hash map with key data
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("data", request);

                            final DeleteDocumentService deleteDocumentService = retrofitAPI.create(DeleteDocumentService.class);

                            Call call = deleteDocumentService.delete_eu_document(params, PreferenceUtils.getAccessToken(context));

                            call.enqueue(new Callback<ListPinDevicesResponse<LoginResponse>>() {
                                @Override
                                public void onResponse(Response<ListPinDevicesResponse<LoginResponse>> response, Retrofit retrofit) {
                                    ListPinDevicesResponse apiResponse = response.body();
                                    if (apiResponse != null) {

                                        transparentProgressDialog.dismiss();

                                        if (apiResponse.status.getCode() instanceof Boolean) {
                                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                                transparentProgressDialog.dismiss();
                                                getSubCategoryDocuments(parentr,"1");
                                                mBackDialog.dismiss();
                                                // refreshAdapterToView(getCategoryDocumentsResponses);
                                            }

                                        } else if (apiResponse.status.getCode() instanceof Integer) {
                                            transparentProgressDialog.dismiss();
                                            mBackDialog.dismiss();
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
                                    mBackDialog.dismiss();
                                    Log.d("PinDevice error", t.getMessage());
                                }
                            });
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

        renameImage.setOnClickListener(new View.OnClickListener() {
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

                        renamedocument(categoryr,folder,"","");
                        mAlertDialog.dismiss();

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog.dismiss();

                    }
                });

                mAlertDialog = builder.create();
                mAlertDialog.show();


            }
        });
        if (fileType.equalsIgnoreCase("pdf")) {
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



        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context,MyFolderSharedDocuments.class);
                mIntent.putExtra(Constants.OBJ, (Serializable) selectedList);
                context.startActivity(mIntent);
            }
        });
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


    private void openBottomSheetForCategory(String name) {

        getWhiteLabelProperties();

        View view = ((MyFoldersDMSActivity) context).getLayoutInflater().inflate(R.layout.bottom_sheet_category_sort, null);

        TextView categoryText = (TextView) view.findViewById(R.id.category_text);
        ImageView categoryImage = (ImageView) view.findViewById(R.id.category_image);
        ImageView moveImage = (ImageView) view.findViewById(R.id.move_image);
        ImageView renameImage = (ImageView) view.findViewById(R.id.rename_image);
        RelativeLayout rename = (RelativeLayout)view.findViewById(R.id.rename1);
        RelativeLayout move = (RelativeLayout)view.findViewById(R.id.move1);
        TextView delete = (TextView) view.findViewById(R.id.delete);
        RelativeLayout share = (RelativeLayout)view.findViewById(R.id.share1);


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
                Intent intent = new Intent(context,MyFolderActivity.class);
                String object = "0";
                intent.putExtra(object,"0");
                context.startActivity(intent);
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



        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        if (NetworkUtils.isNetworkAvailable(context)) {

                            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

                            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
                            transparentProgressDialog.show();

                            final DeleteEndUserFolderRequest deleteEndUserFolderRequest = new DeleteEndUserFolderRequest(categoryids,"0");

                            String request = new Gson().toJson(deleteEndUserFolderRequest);

                            //Here the json data is add to a hash map with key data
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("data", request);

                            final DeleteEndUserFolderService deleteEndUserFolderService = retrofitAPI.create(DeleteEndUserFolderService.class);

                            Call call = deleteEndUserFolderService.delete_eu_folder(params, PreferenceUtils.getAccessToken(context));

                            call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
                                @Override
                                public void onResponse(Response<ApiResponse<LoginResponse>> response, Retrofit retrofit) {
                                    ApiResponse apiResponse = response.body();
                                    if (apiResponse != null) {

                                        transparentProgressDialog.dismiss();

                                        if (apiResponse.status.getCode() == Boolean.FALSE) {
                                            transparentProgressDialog.dismiss();
                                            getSubCategoryDocuments(parentr,"1");
                                            mBackDialog.dismiss();
                                            // refreshAdapterToView(getCategoryDocumentsResponses);
                                        }
                                        else {
                                            transparentProgressDialog.dismiss();
                                            String mMessage = apiResponse.status.getMessage().toString();
                                            mBackDialog.dismiss();
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
                                    mBackDialog.dismiss();
                                    transparentProgressDialog.dismiss();
                                    Log.d("PinDevice error", t.getMessage());
                                }
                            });
                        }


                    }
                });


                move_to_folder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mode = 1;
                        PreferenceUtils.setDelete(context,categoryids,"key");
                        Intent intent = new Intent(context,MyfolderDeleteActivity.class);
                        context.startActivity(intent);

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

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                getSubCategoryDocuments(objectr,"1");
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


    public void renamedocument(String object_id,String name,String doc_created,String auth)
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
                                getSubCategoryDocuments(parentr,"1");
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



    public void getSubCategoryDocuments(String object_id,String page) {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(object_id,"list","category","1","0");

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
                                refreshAdapterToView(getCategoryDocumentsResponses);

                                if(response.headers() != null)
                                {
                                    String paging =  response.headers().get("X-Pagination-Page-Count");
                                    pageCount = paging;


/*
                                    getCategoryDocumentsResponses.add(getCategoryDocumentsResponses.size()-1, (GetCategoryDocumentsResponse) mGetCategoryDocumentsResponses);
*/


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
