package com.swaas.mwc.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swaas.mwc.API.Model.DocumentHistoryResponse;
import com.swaas.mwc.R;

import java.util.List;

/**
 * Created by barath on 7/20/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>{

    List<DocumentHistoryResponse> responseList;
    Context context;

    public HistoryAdapter(List<DocumentHistoryResponse> responseList, Context context){
        this.responseList = responseList;
        this.context = context;
    }



    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vv = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,parent,false);
       // View v = inflater.inflate(R.layout.history_item, parent, false);
        HistoryViewHolder vh = new HistoryViewHolder(vv);
        return vh;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {

        DocumentHistoryResponse documentHistoryResponse = responseList.get(position);
        holder.filename.setText(documentHistoryResponse.getFilename());
        holder.date.setText(documentHistoryResponse.getDoc_created_date());
        holder.version.setText(documentHistoryResponse.getVersion_number());

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
