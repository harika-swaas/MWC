package com.swaas.mwc.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swaas.mwc.R;

/**
 * Created by barath on 7/20/2018.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {


    @Override
    public NotesAdapter.NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notesitem, parent, false);
        // set the view's size, margins, paddings and layout parameters
        NotesAdapter.NotesViewHolder vh = new NotesViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(NotesAdapter.NotesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView filename;// init the item view's
        TextView date;
        TextView createdby;
        TextView message;

        public NotesViewHolder(View itemView) {

            super(itemView);
            // get the reference of item view's
            filename = (TextView) itemView.findViewById(R.id.notes_name);
            createdby = (TextView) itemView.findViewById(R.id.notes_creratedby);
            date = (TextView) itemView.findViewById(R.id.notes_date);
            message = (TextView) itemView.findViewById(R.id.notes_message);
        }
    }
}
