

/**
 * Created by barath on 8/4/2018.
 */
package com.mwc.docportal.DMS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.GetCategoryDocumentsResponse;
import com.mwc.docportal.API.Model.GetEndUserAllowedSharedFoldersRequest;
import com.mwc.docportal.API.Model.GetEndUserCategoriesResponse;
import com.mwc.docportal.API.Model.GetEndUserSharedParentFoldersResponse;
import com.mwc.docportal.API.Model.ListPinDevicesResponse;
import com.mwc.docportal.API.Service.GetEndUserAllowedSharedFoldersService;
import com.mwc.docportal.Dialogs.LoadingProgressDialog;
import com.mwc.docportal.Login.LoginActivity;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.Constants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class MoveDmsAdapter extends RecyclerView.Adapter<MoveDmsAdapter.ViewHolder> {

    final Context context;
    String parent_id;
    private List<GetEndUserCategoriesResponse> mGetEndUserCategoriesResponse;
    List<GetEndUserCategoriesResponse> mSelectedDocumentList;
    AlertDialog mAlertDialog;
    private ItemClickListener mClickListener;
    private HashSet<Integer> mSelected;
    String id=null;

    public MoveDmsAdapter(List<GetEndUserCategoriesResponse>mGetEndUserCategoriesResponse, List<GetEndUserCategoriesResponse> mSelectedDocumentList, Activity context) {
        this.context = context;
        this.mGetEndUserCategoriesResponse = mGetEndUserCategoriesResponse;
        this.mSelectedDocumentList = mSelectedDocumentList;
        mSelected = new HashSet<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView imageView, selectedItemIv, moreImage;
        public TextView folder_name;
        public View layout;
        public TextView folder_date;
        ViewHolder vh;
        RelativeLayout indicatorParentView;
        RelativeLayout imageFile;
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
            imageFile =(RelativeLayout) mView.findViewById(R.id.thumbnail_layout);
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

    public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);


        boolean onItemLongClick(View view, int position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final GetEndUserCategoriesResponse resp = mGetEndUserCategoriesResponse.get(position);

        holder.imageFile.setVisibility(View.GONE);
        holder.moreImage.setVisibility(View.GONE);

        if (mGetEndUserCategoriesResponse != null && mGetEndUserCategoriesResponse.size() > 0) {
            holder.folder_name.setText(mGetEndUserCategoriesResponse.get(position).getCategory_name());

            if (position > 0) {
                char poschar = resp.getCategory_name().toUpperCase().charAt(0);
                char prevchar = mGetEndUserCategoriesResponse.get(position - 1).getCategory_name().toUpperCase().charAt(0);
                if (poschar == prevchar) {
                    holder.indicatorParentView.setVisibility(View.GONE);
                } else {
                    holder.indicatorParentView.setVisibility(View.VISIBLE);
                    holder.indicatorTextValue.setText(String.valueOf(mGetEndUserCategoriesResponse.get(position).getCategory_name().charAt(0)));
                }
            } else {
                holder.indicatorParentView.setVisibility(View.VISIBLE);
                holder.indicatorTextValue.setText(String.valueOf(mGetEndUserCategoriesResponse.get(position).getCategory_name().charAt(0)));
            }

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id = String.valueOf(mGetEndUserCategoriesResponse.get(position).getCategory_id());

                    Intent mIntent = new Intent(context, MyFolderActivity.class);
                    Intent intent = new Intent("custom-message");
                    mIntent.putExtra("abc",id);
                    parent_id= mGetEndUserCategoriesResponse.get(position).getCategory_id();
                    PreferenceUtils.setParentId(context,id);
/*



                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
*/
                    context.startActivity(mIntent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mGetEndUserCategoriesResponse.size();
    }

    private void getEndUserParentSharedFolders(String workspace_id, String category_id) {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetEndUserAllowedSharedFoldersRequest mGetEndUserAllowedSharedFoldersRequest = new GetEndUserAllowedSharedFoldersRequest(workspace_id, category_id);

            String request = new Gson().toJson(mGetEndUserAllowedSharedFoldersRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetEndUserAllowedSharedFoldersService mGetEndUserAllowedSharedFoldersService = retrofitAPI.create(GetEndUserAllowedSharedFoldersService.class);

            Call call = mGetEndUserAllowedSharedFoldersService.getEndUserAllowedSharedFolders(params, PreferenceUtils.getAccessToken(context));

            call.enqueue(new Callback<ListPinDevicesResponse<GetEndUserCategoriesResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetEndUserCategoriesResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                mSelectedDocumentList = response.body().getData();

                                refreshAdapterToView(mSelectedDocumentList);
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

    public void refreshAdapterToView(List<GetEndUserCategoriesResponse> getEndUserSharedParentFoldersResponses) {

        this.mGetEndUserCategoriesResponse.clear();
        notifyDataSetChanged();
    }

    public GetEndUserCategoriesResponse getItemAt(final int position) {
        return mGetEndUserCategoriesResponse.get(position);
    }
}


