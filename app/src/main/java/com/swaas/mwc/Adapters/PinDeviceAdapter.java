package com.swaas.mwc.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.swaas.mwc.API.Model.ListPinDevices;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;

import java.util.List;

public class PinDeviceAdapter extends RecyclerView.Adapter<PinDeviceAdapter.PinDeviceViewHolder> {
    private RadioButton lastCheckedRB = null;
    final Context context;
    List<ListPinDevices> mListPinDevices;


    public PinDeviceAdapter(Activity context, List<ListPinDevices> mListPinDevices) {
        this.context = context;
        this.mListPinDevices = mListPinDevices;
    }

    @Override
    public PinDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        // set the view's size, margins, paddings and layout parameters
        PinDeviceViewHolder vh = new PinDeviceViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(final PinDeviceViewHolder holder, final int position) {
        holder.name.setText(mListPinDevices.get(position).getDevice_type());

        if (mListPinDevices.get(position).getDevice_type().equalsIgnoreCase(context.getResources().getString(R.string.Mobile))) {
            holder.icon.setImageResource(R.mipmap.ic_mobile);
        }

        if (mListPinDevices.get(position).getDevice_type().equalsIgnoreCase(context.getResources().getString(R.string.Email))) {
            holder.icon.setImageResource(R.mipmap.ic_mail);
        }

        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               RadioButton radioButton = (RadioButton) buttonView.findViewById(R.id.user_pin_device_radio_type);
                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                //store the clicked radiobutton
                lastCheckedRB = radioButton;
                if(isChecked){

                    String mUserPinDeviceId = mListPinDevices.get(position).getUser_pin_device_id();
                    PreferenceUtils.setUserPinDeviceId(context,mUserPinDeviceId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListPinDevices.size();
    }

    public class PinDeviceViewHolder extends RecyclerView.ViewHolder {
        TextView name;// init the item view's
        ImageView icon;
        RadioButton radioButton;

        public PinDeviceViewHolder(View itemView) {

            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.user_pin_device_type);
            icon = (ImageView) itemView.findViewById(R.id.user_pin_device_icon);
            radioButton = (RadioButton) itemView.findViewById(R.id.user_pin_device_radio_type);
        }
    }
}
