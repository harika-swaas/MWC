package com.swaas.mwc.DMS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.swaas.mwc.API.Model.GetEndUserSharedParentFoldersResponse;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;

import java.util.HashSet;
import java.util.List;

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

    public SharedDMSEndUserParentFolderAdapter(List<GetEndUserSharedParentFoldersResponse> mGetEndUserSharedParentFoldersResponses, Activity context) {
        this.context = context;
        this.mGetEndUserSharedParentFoldersResponses = mGetEndUserSharedParentFoldersResponses;
        mSelected = new HashSet<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView imageView, selectedItemIv, moreImage;
        public TextView folder_name;
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

}
