package com.mwc.docportal.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mwc.docportal.API.Model.DocumentHistoryResponse;
import com.mwc.docportal.R;
import com.mwc.docportal.Utils.DateHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by barath on 7/20/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>{

    List<DocumentHistoryResponse> responseList = new ArrayList<>();
    Context context;

    public HistoryAdapter(List<DocumentHistoryResponse> responseList, Activity context) {
        this.responseList = responseList;
        this.context = context;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.history_item, parent, false);
        HistoryViewHolder vh = new HistoryViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {

        if(responseList != null && responseList.size() > 0){
            DocumentHistoryResponse documentHistoryResponse = responseList.get(position);

            holder.filename.setText(documentHistoryResponse.getFilename());
            if(documentHistoryResponse.getDoc_created_date() == null || documentHistoryResponse.getDoc_created_date().isEmpty())
            {
                holder.date.setText("Uploaded on (Unknown)");
            }
            else
            {
                holder.date.setText("Uploaded on " + documentHistoryResponse.getDoc_created_date());
            }
            holder.version.setText("Version Number " + documentHistoryResponse.getVersion_number());
        }
    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView filename;// init the item view's
        TextView version;
        TextView date;

        public HistoryViewHolder(View itemView) {

            super(itemView);
            // get the reference of item view's
            filename = (TextView) itemView.findViewById(R.id.his_filename);
            version = (TextView) itemView.findViewById(R.id.his_version);
            date = (TextView) itemView.findViewById(R.id.his_date);
        }
    }
}
