package com.swaas.mwc.Adapters;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swaas.mwc.R;

public class DmsAdapterList extends RecyclerView.Adapter<DmsAdapterList.ViewHolder> {
    private List<String> values;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        public TextView text;
        public View layout;
        public TextView text1;
        com.swaas.mwc.Adapters.DmsAdapter.ViewHolder vh;
        public ViewHolder(View v) {
            super(v);
            layout = v;
            text = (TextView) v.findViewById(R.id.folder_name);
            imageView = (ImageView) v.findViewById(R.id.folder);
            text1 = (TextView)v.findViewById(R.id.folder_date);
        }
    }

    public void add(int position, String item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DmsAdapterList(List<String>myDataset) {
        values = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {


        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());


        View v =
                inflater.inflate(R.layout.file_items, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
        // set the view's size, margins, paddings and layout parameters
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String name = values.get(position);
        holder.text.setText(name);
        holder.imageView.setImageResource(R.mipmap.ic_folder);
        holder.text1.setText("Date Created");
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}
