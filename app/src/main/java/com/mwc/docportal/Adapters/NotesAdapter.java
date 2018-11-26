package com.mwc.docportal.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mwc.docportal.API.Model.BaseApiResponse;
import com.mwc.docportal.API.Model.DocumentNotesResponse;
import com.mwc.docportal.API.Model.GetUserNotesDetailsRequest;
import com.mwc.docportal.API.Model.GetUserNotesDetailsResponse;
import com.mwc.docportal.API.Service.GetUserNotesDetailsService;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Network.NetworkUtils;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Retrofit.RetrofitAPIBuilder;
import com.mwc.docportal.Utils.DateHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by barath on 7/20/2018.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    List<DocumentNotesResponse> responseList = new ArrayList<>();
    Activity context;
    String message;

    public NotesAdapter(List<DocumentNotesResponse> responseList, Activity context) {
        this.responseList = responseList;
        this.context = context;
    }

    @Override
    public NotesAdapter.NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notesitem, parent, false);
        // set the view's size, margins, paddings and layout parameters
        NotesAdapter.NotesViewHolder vh = new NotesViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(NotesAdapter.NotesViewHolder holder, int position) {

        if (responseList != null && responseList.size() > 0) {
            DocumentNotesResponse documentNotesResponse = responseList.get(position);
            PreferenceUtils.setNotesId(context,documentNotesResponse.getNotes_id());
            holder.filename.setText(documentNotesResponse.getSubject());
            holder.date.setText("on " + DateHelper.getDisplayFormatDateMonth(documentNotesResponse.getDate(), "dd/MM/yyyy"));
            holder.createdby.setText(documentNotesResponse.getCreatedby());
        //    getUserNotesDetails(documentNotesResponse.getNotes_id());
            holder.msg.setText(documentNotesResponse.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return responseList.size();
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView filename;// init the item view's
        TextView date;
        TextView createdby;
        TextView msg;

        public NotesViewHolder(View itemView) {

            super(itemView);
            // get the reference of item view's
            filename = (TextView) itemView.findViewById(R.id.notes_name);
            createdby = (TextView) itemView.findViewById(R.id.notes_creratedby);
            date = (TextView) itemView.findViewById(R.id.notes_date);
            msg = (TextView) itemView.findViewById(R.id.notes_message);
        }
    }
}
