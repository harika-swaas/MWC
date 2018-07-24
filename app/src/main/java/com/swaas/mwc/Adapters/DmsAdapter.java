package com.swaas.mwc.Adapters;

/**
 * Created by barath on 7/12/2018.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.swaas.mwc.API.Model.GetCategoryDocumentsRequest;
import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Model.WhiteLabelResponse;
import com.swaas.mwc.API.Service.GetCategoryDocumentsService;
import com.swaas.mwc.Database.AccountSettings;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;

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

    private Context context;
    private List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    private List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses;
    List<WhiteLabelResponse> mWhiteLabelResponses = new ArrayList<>();
    AlertDialog mAlertDialog;
    private ItemClickListener mClickListener;

    private HashSet<Integer> mSelected;

    private static final int GRID_ITEM = 0;
    private static final int LIST_ITEM = 1;

    boolean isSwitchView = true;

    public DmsAdapter(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses, Activity context){
        this.context = context;
        this.mGetCategoryDocumentsResponses = getCategoryDocumentsResponses;
        mSelected = new HashSet<>();
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

    public HashSet<Integer> getSelection()
    {
        return mSelected;
    }

    public void setClickListener(ItemClickListener itemClickListener)
    {
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
        boolean onItemLongClick(View view, int position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        public RelativeLayout gridClick;
        public ImageView imageView, selectedItemIv;
        public TextView text;
        public View layout;
        ViewHolder vh;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView;
            text = (TextView) itemView.findViewById(R.id.folder_name);
            imageView = (ImageView) itemView.findViewById(R.id.folder);
            selectedItemIv = (ImageView) itemView.findViewById(R.id.selected_item);
            gridClick = (RelativeLayout) itemView.findViewById(R.id.grid_click);
            this.itemView.setOnClickListener(this);
            this.itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view)
        {
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
    public int getItemViewType (int position) {
        if (!isSwitchView){
            return LIST_ITEM;
        }else{
            return GRID_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mGetCategoryDocumentsResponses.size();
    }

    public boolean toggleItemViewType () {
        isSwitchView = !isSwitchView;
        return isSwitchView;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (mGetCategoryDocumentsResponses != null && mGetCategoryDocumentsResponses.size() > 0) {
            PreferenceUtils.setDocumentVersionId(context,mGetCategoryDocumentsResponses.get(position).getDocument_version_id());
            PreferenceUtils.setDocument_Id(context,mGetCategoryDocumentsResponses.get(position).getObject_id());
            setButtonBackgroundColor();

            if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {

                if (mWhiteLabelResponses != null && mWhiteLabelResponses.size() > 0) {
                    String folderColor = mWhiteLabelResponses.get(0).getFolder_Color();
                    int itemFolderColor = Color.parseColor(folderColor);

                    if(folderColor != null){
                        changeDrawableColor(context,R.mipmap.ic_folder, itemFolderColor);
                    } else {
                        holder.imageView.setImageResource(R.mipmap.ic_folder);
                    }
                }

            } else if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("document")) {
                if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("pdf")) {
                    holder.imageView.setImageResource(R.mipmap.ic_pdf);
                } else if (mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("xlsx") ||
                        mGetCategoryDocumentsResponses.get(position).getFiletype().equalsIgnoreCase("xls")) {
                    holder.imageView.setImageResource(R.mipmap.ic_excel);
                } else {
                    holder.imageView.setImageResource(R.mipmap.ic_excel);
                }
            }

            final String name = mGetCategoryDocumentsResponses.get(position).getName();
            holder.text.setText(name);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGetCategoryDocumentsResponses.get(position).getType().equalsIgnoreCase("category")) {
                        PreferenceUtils.setObjectId(context,mGetCategoryDocumentsResponses.get(position).getObject_id());
                        getSubCategoryDocuments(mGetCategoryDocumentsResponses.get(position).getObject_id());
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

    public static Drawable changeDrawableColor(Context context, int icon, int itemFolderColor) {
        Drawable mDrawable = ContextCompat.getDrawable(context, icon).mutate();
        mDrawable.setColorFilter(new PorterDuffColorFilter(itemFolderColor, PorterDuff.Mode.SRC_IN));
        return mDrawable;
    }

    private void getSubCategoryDocuments(String object_id) {

        if (NetworkUtils.isNetworkAvailable(context)) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(context);
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(Integer.parseInt(object_id), "list", "category", "1", "0");

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);
            Call call = mGetCategoryDocumentsService.getCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(context));

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

    public void refreshAdapterToView(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        this.mGetCategoryDocumentsResponses.clear();
        this.mGetCategoryDocumentsResponses.addAll(getCategoryDocumentsResponses);
        notifyDataSetChanged();
    }
}
