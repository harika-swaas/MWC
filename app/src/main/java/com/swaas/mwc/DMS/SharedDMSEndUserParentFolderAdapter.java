package com.swaas.mwc.DMS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.GetEndUserSharedParentFoldersResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.ShareEndUserDocumentsRequest;
import com.swaas.mwc.API.Service.ShareEndUserDocumentsService;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by barath on 7/26/2018.
 */

public class SharedDMSEndUserParentFolderAdapter extends RecyclerView.Adapter<SharedDMSEndUserParentFolderAdapter.ViewHolder>{

    final Context context;
    private List<GetEndUserSharedParentFoldersResponse> mGetEndUserSharedParentFoldersResponses;
    private List<GetEndUserSharedParentFoldersResponse> getEndUserSharedParentFoldersResponses;
    AlertDialog mAlertDialog;
    private SharedDMSAdapter.ItemClickListener mClickListener;
    private HashSet<Integer> mSelected;
    MyFolderEndUserAllowedSharedFoldersActivity mActivity;

    public SharedDMSEndUserParentFolderAdapter(List<GetEndUserSharedParentFoldersResponse> mGetEndUserSharedParentFoldersResponses, Activity context) {
        this.context = context;
        this.mGetEndUserSharedParentFoldersResponses = mGetEndUserSharedParentFoldersResponses;
        mSelected = new HashSet<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView imageView, selectedItemIv, moreImage,thumbimage;
        public TextView folder_name,thumbtext;
        public View layout;
        public TextView folder_date;
        SharedDMSAdapter.ViewHolder vh;
        RelativeLayout indicatorParentView;
        LinearLayout parentLayout;
        TextView indicatorTextValue;

        public ViewHolder(View mView) {
            super(mView);
            layout = mView;
            parentLayout = (LinearLayout) mView.findViewById(R.id.list_click);
            folder_name = (TextView) mView.findViewById(R.id.folder_name);
            imageView = (ImageView) mView.findViewById(R.id.folder);
            folder_date = (TextView) mView.findViewById(R.id.folder_date);
            selectedItemIv = (ImageView) mView.findViewById(R.id.selected_item);
            moreImage = (ImageView) mView.findViewById(R.id.more);
            indicatorParentView = (RelativeLayout) mView.findViewById(R.id.nameIndicatorParentView);
            indicatorTextValue = (TextView) mView.findViewById(R.id.indicatorTextValueView);
            thumbimage =(ImageView)mView.findViewById(R.id.thumbnail_image);
            thumbtext=(TextView)mView.findViewById(R.id.thumbnail_text);
            thumbimage.setVisibility(View.INVISIBLE);
            thumbtext.setVisibility(View.INVISIBLE);
            this.itemView.setOnClickListener(this);
            this.itemView.setOnLongClickListener(this);
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
        View v = inflater.inflate(R.layout.file_items, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
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

    public void setClickListener(SharedDMSAdapter.ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);

        boolean onItemLongClick(View view, int position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final GetEndUserSharedParentFoldersResponse resp = mGetEndUserSharedParentFoldersResponses.get(position);

        holder.moreImage.setVisibility(View.GONE);

        if (mGetEndUserSharedParentFoldersResponses != null && mGetEndUserSharedParentFoldersResponses.size() > 0) {
            holder.folder_name.setText(mGetEndUserSharedParentFoldersResponses.get(position).getCategory_name());

            if (position > 0) {
                char poschar = resp.getCategory_name().toUpperCase().charAt(0);
                char prevchar = mGetEndUserSharedParentFoldersResponses.get(position - 1).getCategory_name().toUpperCase().charAt(0);
                if (poschar == prevchar) {
                    holder.indicatorParentView.setVisibility(View.GONE);
                } else {
                    holder.indicatorParentView.setVisibility(View.VISIBLE);
                    holder.indicatorTextValue.setText(String.valueOf(mGetEndUserSharedParentFoldersResponses.get(position).getCategory_name().charAt(0)));
                }
            } else {
                holder.indicatorParentView.setVisibility(View.VISIBLE);
                holder.indicatorTextValue.setText(String.valueOf(mGetEndUserSharedParentFoldersResponses.get(position).getCategory_name().charAt(0)));
            }

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PreferenceUtils.setCategoryId(context,mGetEndUserSharedParentFoldersResponses.get(position).getCategory_id());
                    PreferenceUtils.setWorkspaceId(context,mGetEndUserSharedParentFoldersResponses.get(position).getWorkspace_id());

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.share_alert, null);
                    builder.setView(view);
                    builder.setCancelable(false);

                    Button cancel = (Button) view.findViewById(R.id.cancel_button);
                    Button allow = (Button) view.findViewById(R.id.allow_button);

                    allow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            shareDMSDocuments();
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
        }
    }

    @Override
    public int getItemCount() {
        return mGetEndUserSharedParentFoldersResponses.size();
    }

    public GetEndUserSharedParentFoldersResponse getItemAt(final int position) {
        return mGetEndUserSharedParentFoldersResponses.get(position);
    }

    public void shareDMSDocuments() {

        if (NetworkUtils.isNetworkAvailable(context)){

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

           /* String[] document_ids = new String[0];

            if(mSelectedDocumentList != null){
                for(GetCategoryDocumentsResponse categoryDocumentsResponse : mSelectedDocumentList){
                    List<String> getCategoryDocumentsResponseList = new ArrayList<String>();
                    getCategoryDocumentsResponseList.add(categoryDocumentsResponse.getDocument_version_id());
                    document_ids = getCategoryDocumentsResponseList.toArray(new String[getCategoryDocumentsResponseList.size()]);
                }
            }*/

            final ShareEndUserDocumentsRequest mShareEndUserDocumentsRequest = new ShareEndUserDocumentsRequest(new String[]{PreferenceUtils.getDocument_Id(context)},PreferenceUtils.getWorkspaceId(context), PreferenceUtils.getCategoryId(context));

            String request = new Gson().toJson(mShareEndUserDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final ShareEndUserDocumentsService mShareEndUserDocumentsService = retrofitAPI.create(ShareEndUserDocumentsService.class);
            Call call = mShareEndUserDocumentsService.getSharedEndUserDocuments(params,PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetEndUserSharedParentFoldersResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                String mMessage = apiResponse.status.getMessage().toString();
                                Toast.makeText(context,mMessage,Toast.LENGTH_SHORT).show();
                                context.startActivity(new Intent(context,MyFoldersDMSActivity.class));

                            } else {
                                String mMessage = apiResponse.status.getMessage().toString();
                                Toast.makeText(context,mMessage,Toast.LENGTH_SHORT).show();
                            }

                        } else if (apiResponse.status.getCode() instanceof Double) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

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

}
