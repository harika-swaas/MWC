package com.swaas.mwc.Login;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.swaas.mwc.API.Model.ListPinDevices;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;

import java.util.ArrayList;
import java.util.List;

public class RadioAdapter extends RecyclerView.Adapter<RadioAdapter.MyViewHolder> {
   /* TextView name;// init the item view's
    ImageView icon;
    RadioButton radioButton;*/
    ArrayList list;
    Context context;
    List<ListPinDevices> mListPinDevices;


    public RadioAdapter(Context context, List<ListPinDevices> mListPinDevices) {
        this.context = context;
        this.mListPinDevices = mListPinDevices;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.name.setText(mListPinDevices.get(position).getDevice_name());

        if(mListPinDevices.get(position).getUser_pin_device_id()=="989")
        {
            holder.icon.setImageResource(R.mipmap.ic_mobile);
        }
        else if(mListPinDevices.get(position).getUser_pin_device_id()=="990")
        {
            holder.icon.setImageResource(R.mipmap.ic_mail);
        }
        if(mListPinDevices.get(position).isIs_default()==true)
        {
            holder.radioButton.setChecked(true);
        }
        else {
            holder.radioButton.setChecked(false);
        }
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click


                }


        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;// init the item view's
        ImageView icon;
        RadioButton radioButton;

        public MyViewHolder(View itemView) {

            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.user_pin_device_radio_type);
            icon = (ImageView) itemView.findViewById(R.id.user_pin_device_icon);
            radioButton = (RadioButton) itemView.findViewById(R.id.user_pin_device_type);
        }
    }
}
